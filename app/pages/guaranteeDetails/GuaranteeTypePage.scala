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

package pages.guaranteeDetails

import models.GuaranteeType._
import models.{GuaranteeType, Index, UserAnswers}
import pages._
import play.api.libs.json.JsPath
import queries.Constants.guarantees

import scala.util.Try

case class GuaranteeTypePage(index: Index) extends QuestionPage[GuaranteeType] {

  override def path: JsPath = JsPath \ guarantees \ index.position \ toString

  override def toString: String = "guaranteeType"

  override def cleanup(value: Option[GuaranteeType], userAnswers: UserAnswers): Try[UserAnswers] =
    (value, userAnswers.get(OtherReferencePage(index)), userAnswers.get(GuaranteeReferencePage(index))) match {

      case (Some(GuaranteeWaiver) | Some(ComprehensiveGuarantee) | Some(IndividualGuarantee) | Some(IndividualGuaranteeMultiple), _, Some(grnNumber))
          if grnNumber.length > 17 =>
        userAnswers.remove(GuaranteeReferencePage(index))

      case (Some(GuaranteeWaiver) | Some(ComprehensiveGuarantee) | Some(IndividualGuarantee) | Some(FlatRateVoucher) | Some(IndividualGuaranteeMultiple),
            Some(_),
            _
          ) =>
        userAnswers.remove(OtherReferencePage(index))

      case (Some(CashDepositGuarantee) | Some(GuaranteeNotRequired) | Some(GuaranteeWaivedRedirect) | Some(GuaranteeWaiverByAgreement) |
            Some(GuaranteeWaiverSecured),
            None,
            _
          ) =>
        userAnswers
          .remove(GuaranteeReferencePage(index))
          .flatMap(_.remove(LiabilityAmountPage(index)))
          .flatMap(_.remove(AccessCodePage(index)))
          .flatMap(_.remove(DefaultAmountPage(index)))

      case _ => super.cleanup(value, userAnswers)

    }

}
