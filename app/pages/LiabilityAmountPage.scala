/*
 * Copyright 2023 HM Revenue & Customs
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

package pages

import models.{Index, UserAnswers}
import play.api.libs.json.JsPath
import queries.Constants.guarantees

import scala.util.Try

case class LiabilityAmountPage(index: Index) extends QuestionPage[String] {

  override def path: JsPath = JsPath \ guarantees \ index.position \ toString

  override def toString: String = "liabilityAmount"

  override def cleanup(value: Option[String], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(x) if x.nonEmpty     => userAnswers.remove(DefaultAmountPage(index))
      case Some(x) if x.trim.isEmpty => userAnswers.remove(LiabilityAmountPage(index))
      case _                         => super.cleanup(value, userAnswers)
    }
}
