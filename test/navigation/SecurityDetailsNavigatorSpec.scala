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

package navigation

import base.SpecBase
import generators.Generators
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.securityDetails._
import controllers.addItems.securityDetails._
import pages.safetyAndSecurity.{
  AddCircumstanceIndicatorPage,
  AddCommercialReferenceNumberAllItemsPage,
  AddSafetyAndSecurityConsigneePage,
  AddSafetyAndSecurityConsignorPage,
  CircumstanceIndicatorPage
}

class SecurityDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new SecurityDetailsNavigator

  "In Normal mode" - {

    "Must go from TransportChargesPage to CommercialReferencePage when AddCommercialReferenceNumberAllItemsPage answer is No" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddCommercialReferenceNumberAllItemsPage, false)
            .success
            .value
          navigator
            .nextPage(TransportChargesPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.CommercialReferenceNumberController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
      }
    }

    "Must go from TransportChargesPage to CommercialReferencePage when AddCommercialReferenceNumberAllItemsPage is not completed" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .remove(AddCommercialReferenceNumberAllItemsPage)
            .success
            .value
          navigator
            .nextPage(TransportChargesPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.CommercialReferenceNumberController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
      }
    }

    "Must go from TransportChargesPage to AddsDangerousGoodsCodePage when AddCommercialReferenceNumberAllItemsPage answer is Yes" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddCommercialReferenceNumberAllItemsPage, true)
            .success
            .value
          navigator
            .nextPage(TransportChargesPage(index), NormalMode, updatedAnswers)
            .mustBe(routes.AddDangerousGoodsCodeController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
      }
    }

    "Must go from CommercialReferenceNumberPage to AddDangerousGoodsCodePage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(CommercialReferenceNumberPage(index), NormalMode, answers)
            .mustBe(routes.AddDangerousGoodsCodeController.onPageLoad(answers.lrn, index, NormalMode))
      }
    }

    "Must go from AddDangerousGoodsCodePage" - {
      "to AddItemsCheckYourAnswersPage when user selects 'No' and consignee and consignor are same for all items" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorPage, true)
              .success
              .value
              .set(AddSafetyAndSecurityConsigneePage, true)
              .success
              .value
              .set(AddDangerousGoodsCodePage(index), false)
              .success
              .value

            navigator
              .nextPage(AddDangerousGoodsCodePage(index), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }

      "to SecurityConsigneesEoriPage when user selects 'No' and consignor is same for all items but consignee isn't and user selects 'E' for circumstance indicator" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorPage, true)
              .success
              .value
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value
              .set(CircumstanceIndicatorPage, "E")
              .success
              .value
              .set(AddDangerousGoodsCodePage(index), false)
              .success
              .value

            navigator
              .nextPage(AddDangerousGoodsCodePage(index), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.traderSecurityDetails.routes.SecurityConsigneeEoriController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }
      }

      "to AddSecurityConsigneesEoriPage when user selects 'No' and consignor is same for all items but consignee isn't and user does not select 'E' for circumstance indicator" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorPage, true)
              .success
              .value
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value
              .set(CircumstanceIndicatorPage, "B")
              .success
              .value
              .set(AddDangerousGoodsCodePage(index), false)
              .success
              .value

            navigator
              .nextPage(AddDangerousGoodsCodePage(index), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.traderSecurityDetails.routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }
      }

      "to AddSecurityConsigneesEoriPage when user selects 'No' and consignor is same for all items but consignee isn't and user selects 'No' to add circumstance indicator" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorPage, true)
              .success
              .value
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value
              .set(AddCircumstanceIndicatorPage, false)
              .success
              .value
              .set(AddDangerousGoodsCodePage(index), false)
              .success
              .value

            navigator
              .nextPage(AddDangerousGoodsCodePage(index), NormalMode, updatedAnswers)
              .mustBe(controllers.addItems.traderSecurityDetails.routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }
      }

      "to DangerousGoodsCodePage when user selects 'Yes'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddDangerousGoodsCodePage(index), true)
              .success
              .value
            navigator
              .nextPage(AddDangerousGoodsCodePage(index), NormalMode, updatedAnswers)
              .mustBe(routes.DangerousGoodsCodeController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }
      }
    }

    "Must go from DangerousGoodsCodePage to AddItemsCheckYourAnswersPage and user selected the same consignor and consignee for all items" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddSafetyAndSecurityConsignorPage, true)
            .success
            .value
            .set(AddSafetyAndSecurityConsigneePage, true)
            .success
            .value
          navigator
            .nextPage(DangerousGoodsCodePage(index), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }

    "to SecurityConsigneesEoriPage when user selects 'No' and consignor is same for all items but consignee isn't and user selects 'E' for circumstance indicator" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(DangerousGoodsCodePage(index), "testCode")
            .success
            .value
            .set(AddSafetyAndSecurityConsignorPage, true)
            .success
            .value
            .set(AddSafetyAndSecurityConsigneePage, false)
            .success
            .value
            .set(CircumstanceIndicatorPage, "E")
            .success
            .value
          navigator
            .nextPage(DangerousGoodsCodePage(index), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.traderSecurityDetails.routes.SecurityConsigneeEoriController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
      }
    }

    "to SecurityConsigneesEoriPage when user selects 'No' and consignor is same for all items but consignee isn't and user does not select 'E' for circumstance indicator" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(DangerousGoodsCodePage(index), "testCode")
            .success
            .value
            .set(AddSafetyAndSecurityConsignorPage, true)
            .success
            .value
            .set(AddSafetyAndSecurityConsigneePage, false)
            .success
            .value
            .set(CircumstanceIndicatorPage, "B")
            .success
            .value
          navigator
            .nextPage(DangerousGoodsCodePage(index), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.traderSecurityDetails.routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
      }
    }

    "to SecurityConsigneesEoriPage when user selects 'No' and consignor is same for all items but consignee isn't and user selects 'No' to add circumstance indicator" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(DangerousGoodsCodePage(index), "testCode")
            .success
            .value
            .set(AddSafetyAndSecurityConsignorPage, true)
            .success
            .value
            .set(AddSafetyAndSecurityConsigneePage, false)
            .success
            .value
            .set(AddCircumstanceIndicatorPage, false)
            .success
            .value
          navigator
            .nextPage(DangerousGoodsCodePage(index), NormalMode, updatedAnswers)
            .mustBe(controllers.addItems.traderSecurityDetails.routes.AddSecurityConsigneesEoriController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
      }
    }
  }
  "In CheckMode" - {

    "Must go from TransportChargesPage to AddItemsCheckYourAnswersPage" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          navigator
            .nextPage(TransportChargesPage(index), CheckMode, answers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
      }
    }
  }

  "Must go from CommercialReferenceNumberPage to AddItemsCheckYourAnswersPage" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        navigator
          .nextPage(CommercialReferenceNumberPage(index), CheckMode, answers)
          .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
    }
  }

  "Must go from AddDangerousGoodsCodePage" - {
    "to AddItemsCheckYourAnswersPage when user selects 'No'" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(AddDangerousGoodsCodePage(index), false)
            .success
            .value
          navigator
            .nextPage(AddDangerousGoodsCodePage(index), CheckMode, updatedAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }

    "to AddItemsCheckYourAnswersPage when user selects 'Yes' and an answer for DangerousGoodsCodePage already exists" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(DangerousGoodsCodePage(index), "test")
            .success
            .value
            .set(AddDangerousGoodsCodePage(index), true)
            .success
            .value

          navigator
            .nextPage(AddDangerousGoodsCodePage(index), CheckMode, updatedAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }

    "to DangerousGoodsCodePage when user selects 'Yes' and no answer for DangerousGoodsCodePage already exists" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .remove(DangerousGoodsCodePage(index))
            .success
            .value
            .set(AddDangerousGoodsCodePage(index), true)
            .success
            .value

          navigator
            .nextPage(AddDangerousGoodsCodePage(index), CheckMode, updatedAnswers)
            .mustBe(routes.DangerousGoodsCodeController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
      }
    }
  }
}
