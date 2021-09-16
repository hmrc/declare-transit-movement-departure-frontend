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

import controllers.movementDetails.routes
import models.{CheckMode, RepresentativeCapacity, UserAnswers}
import pages.generalInformation._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

class MovementDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def preLodgeDeclarationPage: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = PreLodgeDeclarationPage,
    formatAnswer = yesOrNo,
    prefix = "preLodgeDeclaration",
    id = Some("change-pre-lodge-declaration"),
    call = routes.PreLodgeDeclarationController.onPageLoad(lrn, CheckMode)
  )

  def representativeCapacity: Option[Row] = getAnswerAndBuildRow[RepresentativeCapacity](
    page = RepresentativeCapacityPage,
    formatAnswer = x => msg"representativeCapacity.$x",
    prefix = "representativeCapacity",
    id = Some("change-representative-capacity"),
    call = routes.RepresentativeCapacityController.onPageLoad(lrn, CheckMode)
  )

  def representativeName: Option[Row] = getAnswerAndBuildRow[String](
    page = RepresentativeNamePage,
    formatAnswer = x => lit"$x",
    prefix = "representativeName",
    id = Some("change-representative-name"),
    call = routes.RepresentativeNameController.onPageLoad(lrn, CheckMode)
  )

  def containersUsedPage: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = ContainersUsedPage,
    formatAnswer = yesOrNo,
    prefix = "containersUsed",
    id = Some("change-containers-used"),
    call = routes.ContainersUsedPageController.onPageLoad(lrn, CheckMode)
  )

  def declarationForSomeoneElse: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = DeclarationForSomeoneElsePage,
    formatAnswer = yesOrNo,
    prefix = "declarationForSomeoneElse",
    id = Some("change-declaration-for-someone-else"),
    call = routes.DeclarationForSomeoneElseController.onPageLoad(lrn, CheckMode)
  )

  def declarationPlace: Option[Row] = getAnswerAndBuildRow[String](
    page = DeclarationPlacePage,
    formatAnswer = x => lit"$x",
    prefix = "declarationPlace",
    id = Some("change-declaration-place"),
    call = routes.DeclarationPlaceController.onPageLoad(lrn, CheckMode)
  )
}
