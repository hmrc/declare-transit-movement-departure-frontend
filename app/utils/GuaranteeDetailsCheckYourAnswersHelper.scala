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

import controllers.guaranteeDetails.routes
import models._
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class GuaranteeDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def defaultAmount(index: Index): Option[Row] =
    userAnswers.get(DefaultAmountPage(index)) map {
      answer =>
        Row(
          key   = Key(msg"defaultAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(yesOrNo(answer)),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = routes.DefaultAmountController.onPageLoad(lrn, index, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"defaultAmount.checkYourAnswersLabel")),
              attributes         = Map("id" -> "change-default-amount")
            )
          )
        )
    }

  def guaranteeType(index: Index): Option[Row] = userAnswers.get(GuaranteeTypePage(index)) map {
    answer =>
      val gtName = GuaranteeType.getId(answer.toString)
      Row(
        key   = Key(msg"guaranteeType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(msg"guaranteeType.$gtName"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.GuaranteeTypeController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"guaranteeType.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-guarantee-type")
          )
        )
      )
  }

  def accessCode(index: Index): Option[Row] = userAnswers.get(AccessCodePage(index)) map {
    _ =>
      Row(
        key   = Key(msg"accessCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"••••"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AccessCodeController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"accessCode.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-access-code")
          )
        )
      )
  }

  def otherReference(index: Index): Option[Row] = userAnswers.get(OtherReferencePage(index)) map {
    answer =>
      Row(
        key   = Key(msg"otherReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.OtherReferenceController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"otherReference.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-other-reference")
          )
        )
      )
  }

  def liabilityAmount(index: Index): Option[Row] =
    userAnswers.get(LiabilityAmountPage(index)) map {
      answer =>
        val displayAmount = answer match {
          case x if x.trim.nonEmpty => lit"$answer"
          case _                    => msg"guaranteeDetailsCheckYourAnswers.defaultLiabilityAmount"
        }

        Row(
          key   = Key(msg"liabilityAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(lit"$answer"),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = routes.LiabilityAmountController.onPageLoad(lrn, index, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"liabilityAmount.checkYourAnswersLabel")),
              attributes         = Map("id" -> "change-liability-amount")
            )
          )
        )
    }

  def guaranteeReference(index: Index): Option[Row] = userAnswers.get(GuaranteeReferencePage(index)) map {
    answer =>
      Row(
        key   = Key(msg"guaranteeReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.GuaranteeReferenceController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"guaranteeReference.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-guarantee-reference")
          )
        )
      )
  }

  def guaranteeRows(index: Index): Option[Row] =
    userAnswers.get(GuaranteeTypePage(index)).map {
      answer =>
        Row(
          key   = Key(msg"guaranteeType.${GuaranteeType.getId(answer.toString)}"),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.change",
              href               = routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(userAnswers.id, index).url,
              visuallyHiddenText = Some(msg"addAnotherGuarantee.guarantee.change.hidden".withArgs(msg"${GuaranteeType.getId(answer.toString)}")),
              attributes         = Map("id" -> s"""change-guarantee-${index.display}""")
            ),
            Action(
              content            = msg"site.delete",
              href               = routes.ConfirmRemoveGuaranteeController.onPageLoad(userAnswers.id, index).url,
              visuallyHiddenText = Some(msg"addAnotherGuarantee.guarantee.delete.hidden".withArgs(msg"${GuaranteeType.getId(answer.toString)}")),
              attributes         = Map("id" -> s"""remove-guarantee-${index.display}""")
            )
          )
        )
    }

  def lrn: LocalReferenceNumber = userAnswers.id
}
