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

import controllers.guaranteeDetails.routes
import models._
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class GuaranteeDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def defaultAmount: Option[Row] =
    if (userAnswers.get(LiabilityAmountPage).isEmpty && userAnswers.get(OtherReferenceLiabilityAmountPage).isEmpty) {
      userAnswers.get(DefaultAmountPage) map {

        answer =>
          val useDefault = if (answer) {
            "Yes"
          } else {
            "No"
          }
          Row(
            key   = Key(msg"defaultAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
            value = Value(lit"$useDefault"),
            actions = List(
              Action(
                content            = msg"site.edit",
                href               = routes.DefaultAmountController.onPageLoad(lrn, CheckMode).url,
                visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"defaultAmount.checkYourAnswersLabel"))
              )
            )
          )
      }
    } else {
      None
    }

  def otherReferenceliabilityAmount: Option[Row] = userAnswers.get(OtherReferenceLiabilityAmountPage) map {
    answer =>
      Row(
        key   = Key(msg"liabilityAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.OtherReferenceLiabilityAmountController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"liabilityAmount.checkYourAnswersLabel"))
          )
        )
      )
  }

  def guaranteeType: Option[Row] = userAnswers.get(GuaranteeTypePage) map {
    answer =>
      val gtName = GuaranteeType.getId(answer.toString)
      Row(
        key   = Key(msg"guaranteeType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(msg"guaranteeType.$gtName"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.GuaranteeTypeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"guaranteeType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def accessCode: Option[Row] = userAnswers.get(AccessCodePage) map {
    answer =>
      Row(
        key   = Key(msg"accessCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"••••"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AccessCodeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"accessCode.checkYourAnswersLabel"))
          )
        )
      )
  }

  def otherReference: Option[Row] = userAnswers.get(OtherReferencePage) map {
    answer =>
      Row(
        key   = Key(msg"otherReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.OtherReferenceController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"otherReference.checkYourAnswersLabel"))
          )
        )
      )
  }

  def liabilityAmount: Option[Row] =
    userAnswers.get(LiabilityAmountPage) map {
      answer =>
        val displayAmount = answer match {
          case x if x.trim.nonEmpty => lit"$answer"
          case _                    => msg"guaranteeDetailsCheckYourAnswers.defaultLiabilityAmount"
        }

        Row(
          key   = Key(msg"liabilityAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(displayAmount),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = routes.LiabilityAmountController.onPageLoad(lrn, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"liabilityAmount.checkYourAnswersLabel"))
            )
          )
        )
    }

  def guaranteeReference: Option[Row] = userAnswers.get(GuaranteeReferencePage) map {
    answer =>
      Row(
        key   = Key(msg"guaranteeReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.GuaranteeReferenceController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"guaranteeReference.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id
}
