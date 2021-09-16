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
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage, TIRGuaranteeReferencePage}
import pages.routeDetails.DestinationOfficePage
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

class GuaranteeDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def defaultAmount(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = DefaultAmountPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "defaultAmount",
    id = Some("change-default-amount"),
    call = routes.DefaultAmountController.onPageLoad(lrn, index, CheckMode)
  )

  def guaranteeType(index: Index): Option[Row] =
    (userAnswers.get(DeclarationTypePage), index) match {
      case (Some(Option4), Index(0)) =>
        None
      case _ =>
        getAnswerAndBuildRow[GuaranteeType](
          page = GuaranteeTypePage(index),
          formatAnswer = guaranteeType => msg"guaranteeType.${GuaranteeType.getId(guaranteeType.toString)}",
          prefix = "guaranteeType",
          id = Some("change-guarantee-type"),
          call = routes.GuaranteeTypeController.onPageLoad(lrn, index, CheckMode)
        )
    }

  def accessCode(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = AccessCodePage(index),
    formatAnswer = formatAsMasked,
    prefix = "accessCode",
    id = Some("change-access-code"),
    call = routes.AccessCodeController.onPageLoad(lrn, index, CheckMode)
  )

  def otherReference(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = OtherReferencePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "otherReference",
    id = Some("change-other-reference"),
    call = routes.OtherReferenceController.onPageLoad(lrn, index, CheckMode)
  )

  def tirLiabilityAmount(index: Index): Option[Row] =
    (userAnswers.get(DeclarationTypePage), index) match {
      case (Some(Option4), Index(0)) =>
        getAnswerAndBuildRow[String](
          page = LiabilityAmountPage(index),
          formatAnswer = formatAsLiteral,
          prefix = "liabilityAmount",
          id = Some("change-liability-amount"),
          call = routes.OtherReferenceLiabilityAmountController.onPageLoad(lrn, index, CheckMode)
        )
      case _ =>
        None
    }

  def liabilityAmount(index: Index): Option[Row] =
    (userAnswers.get(OfficeOfDeparturePage), userAnswers.get(DestinationOfficePage), userAnswers.get(GuaranteeTypePage(index))) match {
      case (Some(officeOfDeparture), Some(destinationOffice), Some(guaranteeType)) if guaranteeReferenceRoute.contains(guaranteeType) =>
        val displayAmount = userAnswers.get(LiabilityAmountPage(index)) match {
          case Some(value) if value.trim.nonEmpty => lit"$value"
          case _                                  => msg"guaranteeDetailsCheckYourAnswers.defaultLiabilityAmount"
        }

        val call = if (officeOfDeparture.countryId.code == "GB" && destinationOffice.countryId.code == "GB") {
          routes.LiabilityAmountController.onPageLoad(lrn, index, CheckMode)
        } else {
          routes.OtherReferenceLiabilityAmountController.onPageLoad(lrn, index, CheckMode)
        }

        Some(
          buildRow(
            prefix = "liabilityAmount",
            content = displayAmount,
            id = Some("change-liability-amount"),
            call = call
          )
        )

      case _ =>
        None
    }

  def guaranteeReference(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = GuaranteeReferencePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "guaranteeReference",
    id = Some("change-guarantee-reference"),
    call = routes.GuaranteeReferenceController.onPageLoad(lrn, index, CheckMode)
  )

  def tirGuaranteeReference(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TIRGuaranteeReferencePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "tirGuaranteeReference",
    id = Some("change-tir-guarantee-reference"),
    call = routes.TIRGuaranteeReferenceController.onPageLoad(lrn, index, CheckMode)
  )

  def guaranteeRow(index: Index, isTir: Boolean)(implicit messages: Messages): Option[Row] =
    if (isTir) {
      if (index.position == 0) {
        getAnswerAndBuildValuelessRow[String](
          page = TIRGuaranteeReferencePage(index),
          formatAnswer = formatAsSelf,
          id = Some(s"change-tir-carnet-${index.display}"),
          call = routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(lrn, index)
        )
      } else {
        getAnswerAndBuildRemovableRow[String](
          page = TIRGuaranteeReferencePage(index),
          formatAnswer = formatAsSelf,
          id = s"tir-carnet-${index.display}",
          changeCall = routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(lrn, index),
          removeCall = routes.ConfirmRemoveGuaranteeController.onPageLoad(lrn, index)
        )
      }
    } else {
      getAnswerAndBuildRemovableRow[GuaranteeType](
        page = GuaranteeTypePage(index),
        formatAnswer = guaranteeType => msg"guaranteeType.${GuaranteeType.getId(guaranteeType.toString)}".resolve,
        id = s"guarantee-${index.display}",
        changeCall = routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(lrn, index),
        removeCall = routes.ConfirmRemoveGuaranteeController.onPageLoad(lrn, index)
      )
    }
}
