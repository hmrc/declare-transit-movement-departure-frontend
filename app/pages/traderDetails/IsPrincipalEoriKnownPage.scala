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

package pages.traderDetails

import models.ProcedureType.Simplified
import models.UserAnswers
import pages.{ClearAllAddItems, ProcedureTypePage, QuestionPage}
import play.api.libs.json.JsPath

import scala.util.{Success, Try}

case object IsPrincipalEoriKnownPage extends QuestionPage[Boolean] with ClearAllAddItems[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "isPrincipalEoriKnown"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    val cleanedUpUserAnswers = value match {
      case Some(false) => userAnswers.remove(WhatIsPrincipalEoriPage)
      case Some(true) =>
        userAnswers.get(ProcedureTypePage) match {
          case Some(Simplified) =>
            userAnswers
              .remove(PrincipalNamePage)
              .flatMap(_.remove(PrincipalAddressPage))
          case _ => Success(userAnswers)
        }
      case _ => Success(userAnswers)
    }

    cleanedUpUserAnswers
      .flatMap(
        updatedUserAnswers => super.cleanup(value, updatedUserAnswers)
      )
  }
}
