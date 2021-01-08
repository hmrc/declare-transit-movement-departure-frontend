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

import com.lucidchart.open.xtract.{ParseError, ParseFailure, ParseSuccess, XmlReader}

object BinaryToBooleanXMLReader {

  case class BinaryToBooleanXMLReaderError(message: String) extends ParseError

  implicit val xmlBinaryToBooleanReads: XmlReader[Boolean] = {
    xml =>
      xml.text match {
        case "1"   => ParseSuccess(true)
        case "0"   => ParseSuccess(false)
        case value => ParseFailure(BinaryToBooleanXMLReaderError(s"Failed to parse the following to boolean: $value"))
      }
  }
}
