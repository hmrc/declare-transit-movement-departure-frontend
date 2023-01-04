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

package utils

import controllers.movementDetails.routes
import models.{Mode, RepresentativeCapacity, UserAnswers}
import pages.generalInformation._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

class MovementDetailsCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode) extends CheckYourAnswersHelper(userAnswers) {

  def preLodgeDeclarationPage: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = PreLodgeDeclarationPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "preLodgeDeclaration",
    id = Some("change-pre-lodge-declaration"),
    call = routes.PreLodgeDeclarationController.onPageLoad(lrn, mode)
  )

  def representativeCapacity: Option[Row] = getAnswerAndBuildRow[RepresentativeCapacity](
    page = RepresentativeCapacityPage,
    formatAnswer = representativeCapacity => msg"representativeCapacity.$representativeCapacity",
    prefix = "representativeCapacity",
    id = Some("change-representative-capacity"),
    call = routes.RepresentativeCapacityController.onPageLoad(lrn, mode)
  )

  def representativeName: Option[Row] = getAnswerAndBuildRow[String](
    page = RepresentativeNamePage,
    formatAnswer = formatAsLiteral,
    prefix = "representativeName",
    id = Some("change-representative-name"),
    call = routes.RepresentativeNameController.onPageLoad(lrn, mode)
  )

  def containersUsedPage: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = ContainersUsedPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "containersUsed",
    id = Some("change-containers-used"),
    call = routes.ContainersUsedController.onPageLoad(lrn, mode)
  )

  def declarationForSomeoneElse: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = DeclarationForSomeoneElsePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "declarationForSomeoneElse",
    id = Some("change-declaration-for-someone-else"),
    call = routes.DeclarationForSomeoneElseController.onPageLoad(lrn, mode)
  )

  def declarationPlace: Option[Row] = getAnswerAndBuildRow[String](
    page = DeclarationPlacePage,
    formatAnswer = formatAsLiteral,
    prefix = "declarationPlace",
    id = Some("change-declaration-place"),
    call = routes.DeclarationPlaceController.onPageLoad(lrn, mode)
  )
}
