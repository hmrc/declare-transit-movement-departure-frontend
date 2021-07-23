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
import models.DeclarationType.Option4
import models.GuaranteeType.guaranteeReferenceRoute
import models._
import models.reference.CountryCode
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage, TIRGuaranteeReferencePage}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class GuaranteeDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def defaultAmount(index: Index): Option[Row] =
    userAnswers.get(DefaultAmountPage(index)) map {
      answer =>
        Row(
          key = Key(msg"defaultAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(yesOrNo(answer)),
          actions = List(
            Action(
              content = msg"site.edit",
              href = routes.DefaultAmountController.onPageLoad(lrn, index, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"defaultAmount.checkYourAnswersLabel")),
              attributes = Map("id" -> "change-default-amount")
            )
          )
        )
    }

  def guaranteeType(index: Index): Option[Row] = userAnswers.get(GuaranteeTypePage(index)) flatMap {
    answer =>
      val gtName = GuaranteeType.getId(answer.toString)

      (userAnswers.get(DeclarationTypePage), index) match {
        case (Some(Option4), Index(0)) => None
        case _ =>
          Some(
            Row(
              key = Key(msg"guaranteeType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"guaranteeType.$gtName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.GuaranteeTypeController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"guaranteeType.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-guarantee-type")
                )
              )
            )
          )
      }
  }

  def accessCode(index: Index): Option[Row] = userAnswers.get(AccessCodePage(index)) map {
    _ =>
      Row(
        key = Key(msg"accessCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"••••"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.AccessCodeController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"accessCode.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-access-code")
          )
        )
      )
  }

  def otherReference(index: Index): Option[Row] = userAnswers.get(OtherReferencePage(index)) map {
    answer =>
      Row(
        key = Key(msg"otherReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.OtherReferenceController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"otherReference.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-other-reference")
          )
        )
      )
  }

  def tirLiabilityAmount(index: Index): Option[Row] =
    (userAnswers.get(DeclarationTypePage), userAnswers.get(LiabilityAmountPage(index)), index) match {
      case (Some(Option4), Some(value), Index(0)) =>
        Some(
          Row(
            key = Key(msg"liabilityAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
            value = Value(lit"$value"),
            actions = List(
              Action(
                content = msg"site.edit",
                href = routes.OtherReferenceLiabilityAmountController.onPageLoad(lrn, index, CheckMode).url,
                visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"liabilityAmount.checkYourAnswersLabel")),
                attributes = Map("id" -> "change-liability-amount")
              )
            )
          )
        )
      case _ => None
    }

  def liabilityAmount(index: Index): Option[Row] =
    (userAnswers.get(OfficeOfDeparturePage), userAnswers.get(DestinationOfficePage), userAnswers.get(GuaranteeTypePage(index))) match {
      case (Some(officeOfDeparture), Some(destinationOffice), Some(guaranteeType)) if guaranteeReferenceRoute.contains(guaranteeType) =>
        val displayAmount = userAnswers.get(LiabilityAmountPage(index)) match {
          case Some(value) if value.trim.nonEmpty => lit"$value"
          case _                                  => msg"guaranteeDetailsCheckYourAnswers.defaultLiabilityAmount"
        }

        val href = if (officeOfDeparture.countryId == CountryCode("GB") && destinationOffice.countryId == CountryCode("GB")) {
          routes.LiabilityAmountController.onPageLoad(lrn, index, CheckMode).url
        } else {
          routes.OtherReferenceLiabilityAmountController.onPageLoad(lrn, index, CheckMode).url
        }

        Some(
          Row(
            key = Key(msg"liabilityAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
            value = Value(displayAmount),
            actions = List(
              Action(
                content = msg"site.edit",
                href = href,
                visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"liabilityAmount.checkYourAnswersLabel")),
                attributes = Map("id" -> "change-liability-amount")
              )
            )
          )
        )
      case _ => None
    }

  def guaranteeReference(index: Index): Option[Row] = userAnswers.get(GuaranteeReferencePage(index)) map {
    answer =>
      Row(
        key = Key(msg"guaranteeReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.GuaranteeReferenceController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"guaranteeReference.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-guarantee-reference")
          )
        )
      )
  }

  def tirGuaranteeReference(index: Index): Option[Row] = userAnswers.get(TIRGuaranteeReferencePage(index)) map {
    answer =>
      Row(
        key = Key(msg"tirGuaranteeReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.TIRGuaranteeReferenceController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"tirGuaranteeReference.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-tir-guarantee-reference")
          )
        )
      )
  }

  def guaranteeRows(index: Index, isTir: Boolean): Option[Row] =
    if (isTir) {
      userAnswers.get(TIRGuaranteeReferencePage(index)).map {
        answer =>
          Row(
            key = Key(msg"$answer"),
            value = Value(lit""),
            actions = List(
              Action(
                content = msg"site.change",
                href = routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(userAnswers.lrn, index).url,
                visuallyHiddenText = Some(msg"addAnotherGuarantee.guarantee.change.hidden".withArgs(msg"$answer")),
                attributes = Map("id" -> s"""change-tir-carnet-${index.display}""")
              )
            ) ++ {
              if (index.position != 0) {
                List(
                  Action(
                    content = msg"site.delete",
                    href = routes.ConfirmRemoveGuaranteeController.onPageLoad(userAnswers.lrn, index).url,
                    visuallyHiddenText = Some(msg"addAnotherGuarantee.guarantee.delete.hidden".withArgs(msg"$answer")),
                    attributes = Map("id" -> s"""remove-tir-carnet-${index.display}""")
                  )
                )
              } else {
                List.empty
              }
            }
          )
      }
    } else {
      userAnswers.get(GuaranteeTypePage(index)).map {
        answer =>
          Row(
            key = Key(msg"guaranteeType.${GuaranteeType.getId(answer.toString)}"),
            value = Value(lit""),
            actions = List(
              Action(
                content = msg"site.change",
                href = routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(userAnswers.lrn, index).url,
                visuallyHiddenText = Some(msg"addAnotherGuarantee.guarantee.change.hidden".withArgs(msg"${GuaranteeType.getId(answer.toString)}")),
                attributes = Map("id" -> s"""change-guarantee-${index.display}""")
              ),
              Action(
                content = msg"site.delete",
                href = routes.ConfirmRemoveGuaranteeController.onPageLoad(userAnswers.lrn, index).url,
                visuallyHiddenText = Some(msg"addAnotherGuarantee.guarantee.delete.hidden".withArgs(msg"${GuaranteeType.getId(answer.toString)}")),
                attributes = Map("id" -> s"""remove-guarantee-${index.display}""")
              )
            )
          )
      }
    }

  def lrn: LocalReferenceNumber = userAnswers.lrn
}
