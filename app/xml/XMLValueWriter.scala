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

import play.twirl.api.utils.StringEscapeUtils

//trait XMLValueWriter[A] {
//  def writeValue(a: A): String
//}
//
//object XMLValueWriter {
//
//  def apply[A](implicit ev: XMLValueWriter[A]): XMLValueWriter[A] = ev
//
//  implicit class XMLValueWriterOps[A](val a: A) extends AnyVal {
//
//    def asXmlText(implicit ev: XMLValueWriter[A]): String =
//      ev.writeValue(a)
//  }
//
//  implicit val stringXmlValueWriter: XMLValueWriter[String] = string => StringEscapeUtils.escapeXml11(string)
//
//  implicit val intXmlValueWriter: XMLValueWriter[Int] = int => int.toString.asXmlText
//
//}
