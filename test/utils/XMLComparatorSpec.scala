/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

import org.scalatest.Suite

import scala.xml.{Node, NodeSeq}

trait XMLComparatorSpec {
  self: Suite =>

  case class XmlValue(node: Path, value: String) {
    override def toString: String = s"$node - $value"
  }

  sealed trait Path {
    def parent: Path
  }

  case class RootPath(self: String) extends Path {
    override def toString: String = self

    override def parent: Path = copy()
  }

  case class ChildPath(parent: Path, self: String) extends Path {
    override def toString: String = s"$parent / $self"
  }

  implicit private class NodeOps(actual: Node) {

    def flatter(root: Path = RootPath("__")): Seq[XmlValue] =
      if (actual.child.nonEmpty) {
        actual.child.flatMap {
          case x if x.label == "#PCDATA" => Seq(XmlValue(root, x.text))
          case x if x.child.isEmpty      => Seq(XmlValue(ChildPath(root, x.label), ""))
          case x                         => x.flatter(ChildPath(root, x.label))
        }
      } else {
        Nil
      }
  }

  implicit class NodeSeqEq(actual: NodeSeq) {

    private lazy val actualFields: Seq[XmlValue] = actual.flatMap(
      x => x.flatter(RootPath(x.label))
    )
    private lazy val actualFieldNodes: Seq[Path] = actualFields.map(_.node)

    private lazy val fieldsMissingInExpected: Seq[Path] => Seq[Path] = actualFieldNodes diff
    private lazy val fieldsMissingInActual: Seq[Path] => Seq[Path]   = _ diff actualFieldNodes

    private lazy val getCommonFields: XmlValue => Seq[XmlValue] = xml => actualFields.filter(_.node == xml.node)

    private lazy val fieldsWithDifferentValues: Seq[XmlValue] => Seq[String] = expectedFields =>
      actualFields
        .collect {
          case actualField
              if (getCommonFields(actualField).length > 1 || expectedFields.count(_.node == actualField.node) > 1)
                && (getCommonFields(actualField).nonEmpty && expectedFields.count(_.node == actualField.node) > 0) =>
            val fields            = expectedFields.filter(_.node == actualField.node)
            val actualFieldLength = getCommonFields(actualField).length
            fields.length match {
              case x if x != actualFieldLength =>
                Some(s"Array field ${Console.CYAN}${actualField.node}${Console.RED} occurred '$actualFieldLength' time(s) and expected '$x' time(s) ")
              case `actualFieldLength` if getCommonFields(actualField) != fields =>
                Some(
                  s"Array field ${Console.CYAN}${actualField.node}${Console.RED} did not full match all fields in expected xml:" +
                    s"\n    actual: ${getCommonFields(actualField).map(_.value).mkString(", ")}" +
                    s"\n    expected: ${fields.map(_.value).mkString(", ")}"
                )
              case _ => None
            }
          case actualField =>
            expectedFields.find(_.node == actualField.node).flatMap {
              field =>
                if (field.value == actualField.value) {
                  None
                } else {
                  Some(s"For field ${Console.CYAN}${actualField.node}${Console.RED} actual '${actualField.value}' did not equal expected '${field.value}'")
                }
            }
        }
        .flatten
        .distinct

    def xmlMustEqual(expected: NodeSeq): Unit = {
      lazy val expectedFields = expected.flatMap(
        x => x.flatter(RootPath(x.label))
      )
      lazy val expectedFieldNodes  = expectedFields.map(_.node)
      lazy val missingFromActual   = fieldsMissingInActual(expectedFieldNodes)
      lazy val missingFromExpected = fieldsMissingInExpected(expectedFieldNodes)

      lazy val incorrectValues = fieldsWithDifferentValues(expectedFields)

      lazy val incorrectString =
        if (incorrectValues.isEmpty) ""
        else
          s"\n(${incorrectValues.length}) Fields with incorrect values:" +
            s"\n  ${incorrectValues.mkString("\n  ")}"
      lazy val missingFromExpectedString =
        if (missingFromExpected.isEmpty) ""
        else
          s"\n(${missingFromExpected.length}) Fields missing from expected: " +
            s"\n  ${missingFromExpected.mkString("\n  ")} "
      lazy val missingFromActualString =
        if (missingFromActual.isEmpty) ""
        else
          s"\n(${missingFromActual.length}) Fields missing from actual: " +
            s"\n  ${missingFromActual.mkString("\n  ")}"

      lazy val mismatchedPositionFields = expectedFields
        .zip(actualFields)
        .flatMap {
          x =>
            if (x._1.value != x._2.value) {
              Some(s"ACTUAL: ${Console.CYAN}${x._2}${Console.RED} EXPECTED: ${Console.CYAN}${x._1}${Console.RED}")
            } else {
              None
            }
        }
        .mkString("\n", "\n", "\n")

      if (expected.toString() != actual.toString()) {

        val failureMessage = if (s"$incorrectString$missingFromActualString$missingFromExpectedString".trim.nonEmpty) {
          s"$incorrectString $missingFromActualString $missingFromExpectedString"
        } else {
          s"\nThe following fields are in different positions in the XML: $mismatchedPositionFields"
        }
        fail(s"The XMLs tested didn't match each other $failureMessage")
      }
    }
  }

}
