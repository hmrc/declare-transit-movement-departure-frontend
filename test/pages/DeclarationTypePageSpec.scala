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

package pages

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import derivable.DeriveNumberOfGuarantees
import models.{DeclarationType, GuaranteeType, Index}
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}

class DeclarationTypeSpec extends PageBehaviours with SpecBase with UserAnswersSpecHelper {

  "DeclarationTypePage" - {

    beRetrievable[DeclarationType](DeclarationTypePage)

    beSettable[DeclarationType](DeclarationTypePage)

    beRemovable[DeclarationType](DeclarationTypePage)

    "Clear down all guarantees if choosing T1,T2 or T2F and Guarantee type B exists" in {
      val preChangeUserAnswers = emptyUserAnswers
        .unsafeSetVal(GuaranteeTypePage(Index(0)))(GuaranteeType.TIR)
        .unsafeSetVal(GuaranteeReferencePage(Index(0)))("REF1")
        .unsafeSetVal(GuaranteeTypePage(Index(1)))(GuaranteeType.GuaranteeWaiver)
        .unsafeSetVal(GuaranteeReferencePage(Index(1)))("REF3")
        .unsafeSetVal(LiabilityAmountPage(Index(1)))("1000")
        .unsafeSetVal(AccessCodePage(Index(1)))("Star")
        .unsafeSetVal(GuaranteeTypePage(Index(2)))(GuaranteeType.GuaranteeWaiverByAgreement)
        .unsafeSetVal(GuaranteeReferencePage(Index(2)))("REF2")

      preChangeUserAnswers.get(DeriveNumberOfGuarantees).value mustBe 3

      val declarationType = Gen.oneOf(DeclarationType.Option1, DeclarationType.Option2, DeclarationType.Option3).sample.value

      val userAnswers = preChangeUserAnswers.set(DeclarationTypePage, declarationType).success.value

      userAnswers.get(DeriveNumberOfGuarantees).value mustBe 0
    }

    "Keep guarantees if choosing T1,T2 or T2F and Guarantee type B exists" in {
      val preChangeUserAnswers = emptyUserAnswers
        .unsafeSetVal(GuaranteeTypePage(Index(0)))(GuaranteeType.GuaranteeWaiver)
        .unsafeSetVal(GuaranteeReferencePage(Index(0)))("REF3")
        .unsafeSetVal(LiabilityAmountPage(Index(0)))("1000")
        .unsafeSetVal(AccessCodePage(Index(0)))("Star")
        .unsafeSetVal(GuaranteeTypePage(Index(1)))(GuaranteeType.GuaranteeWaiverByAgreement)
        .unsafeSetVal(GuaranteeReferencePage(Index(1)))("REF2")

      preChangeUserAnswers.get(DeriveNumberOfGuarantees).value mustBe 2

      val declarationType = Gen.oneOf(DeclarationType.Option1, DeclarationType.Option2, DeclarationType.Option3).sample.value

      val userAnswers = preChangeUserAnswers.set(DeclarationTypePage, declarationType).success.value

      userAnswers.get(DeriveNumberOfGuarantees).value mustBe 2
    }

    "Keep guarantees if choosing TIR and Guarantee type B exists" in {
      val preChangeUserAnswers = emptyUserAnswers
        .unsafeSetVal(GuaranteeTypePage(Index(0)))(GuaranteeType.TIR)
        .unsafeSetVal(GuaranteeReferencePage(Index(0)))("REF1")
        .unsafeSetVal(GuaranteeTypePage(Index(1)))(GuaranteeType.GuaranteeWaiver)
        .unsafeSetVal(GuaranteeReferencePage(Index(1)))("REF3")
        .unsafeSetVal(LiabilityAmountPage(Index(1)))("1000")
        .unsafeSetVal(AccessCodePage(Index(1)))("Star")
        .unsafeSetVal(GuaranteeTypePage(Index(2)))(GuaranteeType.GuaranteeWaiverByAgreement)
        .unsafeSetVal(GuaranteeReferencePage(Index(2)))("REF2")

      preChangeUserAnswers.get(DeriveNumberOfGuarantees).value mustBe 3

      val userAnswers = preChangeUserAnswers.set(DeclarationTypePage, DeclarationType.Option4).success.value

      userAnswers.get(DeriveNumberOfGuarantees).value mustBe 3
    }

    "Remove guarantees if choosing TIR and Guarantee type B doesn't exist" in {
      val preChangeUserAnswers = emptyUserAnswers
        .unsafeSetVal(GuaranteeTypePage(Index(0)))(GuaranteeType.GuaranteeWaiver)
        .unsafeSetVal(GuaranteeReferencePage(Index(0)))("REF3")
        .unsafeSetVal(LiabilityAmountPage(Index(0)))("1000")
        .unsafeSetVal(AccessCodePage(Index(0)))("Star")
        .unsafeSetVal(GuaranteeTypePage(Index(1)))(GuaranteeType.GuaranteeWaiverByAgreement)
        .unsafeSetVal(GuaranteeReferencePage(Index(1)))("REF2")

      preChangeUserAnswers.get(DeriveNumberOfGuarantees).value mustBe 2

      val userAnswers = preChangeUserAnswers.set(DeclarationTypePage, DeclarationType.Option4).success.value

      userAnswers.get(DeriveNumberOfGuarantees).value mustBe 0
    }
  }
}
