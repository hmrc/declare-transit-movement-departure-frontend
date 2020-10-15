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

package pages.guaranteeDetails

import models.GuaranteeType._
import models.{GuaranteeType, UserAnswers}
import pages._
import play.api.libs.json.JsPath

import scala.util.Try

case object GuaranteeTypePage extends QuestionPage[GuaranteeType] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "guaranteeType"

  override def cleanup(value: Option[GuaranteeType], userAnswers: UserAnswers): Try[UserAnswers] =
    (value, userAnswers.get(AccessCodePage)) match {

      case (Some(GuaranteeWaiver) | Some(ComprehensiveGuarantee) | Some(IndividualGuarantee) | Some(IndividualGuaranteeMultiple), Some(_))
          if userAnswers.get(GuaranteeReferencePage).toString.length > 17 =>
        userAnswers.remove(GuaranteeReferencePage)

      case (Some(GuaranteeWaiver) | Some(ComprehensiveGuarantee) | Some(IndividualGuarantee) | Some(FlatRateVoucher) | Some(IndividualGuaranteeMultiple),
            Some(_)) =>
        userAnswers.remove(OtherReferencePage)

      case (Some(GuaranteeWaiver) | Some(ComprehensiveGuarantee) | Some(IndividualGuarantee) | Some(FlatRateVoucher) | Some(IndividualGuaranteeMultiple),
            None) =>
        userAnswers
          .remove(OtherReferencePage)
          .flatMap(_.remove(OtherReferenceLiabilityAmountPage))

      case (_, Some(_)) =>
        userAnswers
          .remove(GuaranteeReferencePage)
          .flatMap(_.remove(LiabilityAmountPage))
          .flatMap(_.remove(AccessCodePage))
          .flatMap(_.remove(DefaultAmountPage))

      case (_, None) =>
        userAnswers.remove(GuaranteeReferencePage)

    }

}
