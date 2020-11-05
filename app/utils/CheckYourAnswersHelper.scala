/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.format.DateTimeFormatter

import controllers.addItems.specialMentions.{routes => specialMentionRoutes}
import controllers.routes
import controllers.addItems.containers.{routes => containerRoutes}
import models.{CheckMode, Index, LocalReferenceNumber, UserAnswers}
import pages._
import pages.addItems.containers.{AddAnotherContainerPage, ContainerNumberPage}
import pages.addItems.specialMentions.{
  AddAnotherSpecialMentionPage,
  AddSpecialMentionPage,
  RemoveSpecialMentionPage,
  SpecialMentionAdditionalInfoPage,
  SpecialMentionTypePage
}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def removeSpecialMention(itemIndex: Index): Option[Row] = userAnswers.get(RemoveSpecialMentionPage(itemIndex)) map {
    answer =>
      Row(
        key   = Key(msg"removeSpecialMention.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = specialMentionRoutes.RemoveSpecialMentionController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"removeSpecialMention.checkYourAnswersLabel"))
          )
        )
      )
  }

  //TODO: These should be moved
  def addAnotherSpecialMention: Option[Row] = userAnswers.get(AddAnotherSpecialMentionPage) map {
    answer =>
      Row(
        key   = Key(msg"addAnotherSpecialMention.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = specialMentionRoutes.AddAnotherSpecialMentionController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addAnotherSpecialMention.checkYourAnswersLabel"))
          )
        )
      )
  }

  def specialMentionAdditionalInfo: Option[Row] = userAnswers.get(SpecialMentionAdditionalInfoPage) map {
    answer =>
      Row(
        key   = Key(msg"specialMentionAdditionalInfo.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = specialMentionRoutes.SpecialMentionAdditionalInfoController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"specialMentionAdditionalInfo.checkYourAnswersLabel"))
          )
        )
      )
  }

  def specialMentionType: Option[Row] = userAnswers.get(SpecialMentionTypePage) map {
    answer =>
      Row(
        key   = Key(msg"specialMentionType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = specialMentionRoutes.SpecialMentionTypeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"specialMentionType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addSpecialMention(itemIndex: Index): Option[Row] = userAnswers.get(AddSpecialMentionPage(itemIndex)) map {
    answer =>
      Row(
        key   = Key(msg"addSpecialMention.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = specialMentionRoutes.AddSpecialMentionController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSpecialMention.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addAnotherContainer(itemIndex: Index): Option[Row] = userAnswers.get(AddAnotherContainerPage) map {
    answer =>
      Row(
        key   = Key(msg"addAnotherContainer.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = containerRoutes.AddAnotherContainerController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addAnotherContainer.checkYourAnswersLabel"))
          )
        )
      )
  }

  def containerNumber(itemIndex: Index): Option[Row] = userAnswers.get(ContainerNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"containerNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = containerRoutes.ContainerNumberController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"containerNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addSecurityDetails: Option[Row] = userAnswers.get(AddSecurityDetailsPage) map {
    answer =>
      Row(
        key   = Key(msg"addSecurityDetails.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSecurityDetailsController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSecurityDetails.checkYourAnswersLabel")),
            attributes         = Map("id" -> s"""change-add-security-details""")
          )
        )
      )
  }

  def procedureType: Option[Row] = userAnswers.get(ProcedureTypePage) map {
    answer =>
      Row(
        key   = Key(msg"procedureType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(msg"procedureType.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ProcedureTypeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"procedureType.checkYourAnswersLabel")),
            attributes         = Map("id" -> s"""change-procedure-type""")
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id

}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
