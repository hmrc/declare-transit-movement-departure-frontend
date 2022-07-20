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

package xml

//import base.SpecBase
//import xml.XMLValueWriter._
//
//class XMLValueWriterSpec extends SpecBase {
//
//  "inserts string as a value of an xml node" in {
//
//    val sut = "testString"
//
//    val result = <testNode>{sut.asXmlText}</testNode>
//
//    result mustEqual <testNode>testString</testNode>
//  }
//
//  "inserts an Int as a value of an xml node" in {
//
//    val sut = 1
//
//    val result = <testNode>{sut.asXmlText}</testNode>
//
//    result mustEqual <testNode>1</testNode>
//  }
//
//  "inserts uses the XMLValueWriter for an object to turn it into a string value" in {
//
//    case class IntWord(a: String)
//    case class IntWordMapper(x: Int, y: IntWord)
//
//    implicit val xMLValueWriterIntWord: XMLValueWriter[IntWord] =
//      o => o.a.asXmlText
//
//    implicit val xMLValueWriterIntWordMapper: XMLValueWriter[IntWordMapper] =
//      o => o.x.toString.asXmlText + " in words is " + o.y.asXmlText
//
//    val sut = IntWordMapper(1, IntWord("one"))
//
//    val result = <testNode>{sut.asXmlText}</testNode>
//
//    result mustEqual <testNode>1 in words is one</testNode>
//  }
//
//}
