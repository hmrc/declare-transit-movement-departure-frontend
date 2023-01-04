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

import derivable.DeriveNumberOfGuarantees
import models.{DeclarationType, GuaranteeType, Index, UserAnswers}
import pages.guaranteeDetails.GuaranteeTypePage
import play.api.libs.json.JsPath
import queries.GuaranteesQuery

import scala.util.{Success, Try}

case object DeclarationTypePage extends QuestionPage[DeclarationType] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "declarationType"

  override def cleanup(value: Option[DeclarationType], userAnswers: UserAnswers): Try[UserAnswers] = {
    import DeclarationType.{Option1, Option2, Option3, Option4}

    val amountOfGuarantees = userAnswers.get(DeriveNumberOfGuarantees).getOrElse(0)

    lazy val removeAllGuarantees: Try[UserAnswers] = (0 until userAnswers.get(DeriveNumberOfGuarantees).getOrElse(0)).foldLeft(Try(userAnswers))(
      (ua, _) => ua.flatMap(_.remove(GuaranteesQuery(Index(0))))
    )

    lazy val findTirGuaranteeType: Option[Int] = (0 until amountOfGuarantees).find {
      index =>
        userAnswers.get(GuaranteeTypePage(Index(index))).contains(GuaranteeType.TIR)
    }

    value match {
      case Some(Option1 | Option2 | Option3) =>
        findTirGuaranteeType match {
          case Some(_) => removeAllGuarantees
          case None    => Success(userAnswers)
        }

      case Some(Option4) =>
        findTirGuaranteeType match {
          case Some(_) => Success(userAnswers)
          case None    => removeAllGuarantees
        }

      case _ => Success(userAnswers)
    }
  }
}
