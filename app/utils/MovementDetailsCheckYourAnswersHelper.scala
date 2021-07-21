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
import models.{CheckMode, LocalReferenceNumber, UserAnswers}
import pages._
import pages.movementDetails.PreLodgeDeclarationPage
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class MovementDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def preLodgeDeclarationPage: Option[Row] = userAnswers.get(PreLodgeDeclarationPage) map {
    answer =>
      Row(
        key = Key(msg"preLodgeDeclaration.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.PreLodgeDeclarationController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"preLodgeDeclaration.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-pre-lodge-declaration""")
          )
        )
      )
  }

  def representativeCapacity: Option[Row] = userAnswers.get(RepresentativeCapacityPage) map {
    answer =>
      Row(
        key = Key(msg"representativeCapacity.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(msg"representativeCapacity.$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.RepresentativeCapacityController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"representativeCapacity.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-representative-capacity""")
          )
        )
      )
  }

  def representativeName: Option[Row] = userAnswers.get(RepresentativeNamePage) map {
    answer =>
      Row(
        key = Key(msg"representativeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.RepresentativeNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"representativeName.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-representative-name""")
          )
        )
      )
  }

  def containersUsedPage: Option[Row] = userAnswers.get(ContainersUsedPage) map {
    answer =>
      Row(
        key = Key(msg"containersUsed.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.ContainersUsedPageController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"containersUsed.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-containers-used""")
          )
        )
      )
  }

  def declarationForSomeoneElse: Option[Row] = userAnswers.get(DeclarationForSomeoneElsePage) map {
    answer =>
      Row(
        key = Key(msg"declarationForSomeoneElse.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.DeclarationForSomeoneElseController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declarationForSomeoneElse.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-declaration-for-someone-else""")
          )
        )
      )
  }

  def declarationPlace: Option[Row] = userAnswers.get(DeclarationPlacePage) map {
    answer =>
      Row(
        key = Key(msg"declarationPlace.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.DeclarationPlaceController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declarationPlace.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-declaration-place""")
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.lrn
}
