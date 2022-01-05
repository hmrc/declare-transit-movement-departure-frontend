/*
 * Copyright 2022 HM Revenue & Customs
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

import cats.data.NonEmptyList
import com.lucidchart.open.xtract.XmlReader.seq
import com.lucidchart.open.xtract.{ParseError, XmlReader}

object NonEmptyListXMLReader {

  case class NonEmptyListXMLReaderParseFailure(message: String) extends ParseError

  def xmlNonEmptyListReads[A](implicit xmlReader: XmlReader[A]): XmlReader[NonEmptyList[A]] =
    XmlReader
      .of(seq[A].atLeast(1))
      .collect(
        NonEmptyListXMLReaderParseFailure("Failed to parse to NonEmptyList due to empty list")
      ) {
        case result => NonEmptyList(result.head, result.tail.toList)
      }
}
