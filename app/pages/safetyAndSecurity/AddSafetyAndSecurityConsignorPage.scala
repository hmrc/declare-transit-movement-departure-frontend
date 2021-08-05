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

package pages.safetyAndSecurity

import models.UserAnswers
import pages.{ClearAllAddItems, QuestionPage}
import play.api.libs.json.JsPath

import scala.util.{Success, Try}

case object AddSafetyAndSecurityConsignorPage extends QuestionPage[Boolean] with ClearAllAddItems[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "addSafetyAndSecurityConsignor"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    val cleanedUpUserAnswers = value match {
      case Some(false) =>
        userAnswers
          .remove(AddSafetyAndSecurityConsignorEoriPage)
          .flatMap(_.remove(SafetyAndSecurityConsignorEoriPage))
          .flatMap(_.remove(SafetyAndSecurityConsignorNamePage))
          .flatMap(_.remove(SafetyAndSecurityConsignorAddressPage))
      case _ => Success(userAnswers)
    }

    cleanedUpUserAnswers
      .flatMap(
        updatedUserAnswers => super.cleanup(value, updatedUserAnswers)
      )
  }
}
