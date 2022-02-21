/*
 * Copyright 2022 HM Revenue & Customs
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
import controllers.goodsSummary.{routes => goodsSummaryRoute}
import generators.Generators
import models.ProcedureType.{Normal, Simplified}
import models.domain.SealDomain
import models.{CheckMode, Index, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.generalInformation.PreLodgeDeclarationPage
import queries.SealsQuery

import java.time.LocalDate

class GoodsSummaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new GoodsSummaryNavigator
  // format: off
  "GoodsSummaryNavigator" - {

    "in Normal Mode" - {

      "must go from Loading Place page to AuthorisedLocationCodePage when on Simplified journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ProcedureTypePage, Simplified).toOption.value

            navigator
              .nextPage(LoadingPlacePage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AuthorisedLocationCodeController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from Loading Place page to session expired when no selection for ProcedureTypePage" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            lazy val updatedAnswers = answers.remove(ProcedureTypePage).toOption.value

            navigator
              .nextPage(LoadingPlacePage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
        }
      }

      "must go from Loading Place page to AddCustomsApprovedLocationPage when on Normal journey and selected No for PreLodgeDeclaration" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(ProcedureTypePage, Normal).toOption.value
              .set(PreLodgeDeclarationPage, false).toOption.value

            navigator
              .nextPage(LoadingPlacePage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddCustomsApprovedLocationController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from Loading Place page to Add Agreed Location of Goods Page when on Normal journey and selected Yes for PreLodgeDeclaration" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(ProcedureTypePage, Normal).toOption.value
              .set(PreLodgeDeclarationPage, true).toOption.value

            navigator
              .nextPage(LoadingPlacePage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddAgreedLocationOfGoodsController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from Loading Place page to session expired when on Normal journey and no selection for PreLodgeDeclaration" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(ProcedureTypePage, Normal).toOption.value
              .remove(PreLodgeDeclarationPage).toOption.value

            navigator
              .nextPage(LoadingPlacePage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
        }
      }

      "must go from AuthorisedLocationCodePage to ControlResultDateLimitPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AuthorisedLocationCodePage, "test").toOption.value

            navigator
              .nextPage(AuthorisedLocationCodePage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.ControlResultDateLimitController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from AddCustomsApprovedLocationPage to CustomsApprovedLocationPage when answer is Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddCustomsApprovedLocationPage, true).toOption.value

            navigator
              .nextPage(AddCustomsApprovedLocationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.CustomsApprovedLocationController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }



      "must go from Agreed Location Of Goods Page to to Add seals page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AgreedLocationOfGoodsPage, "test").toOption.value

            navigator
              .nextPage(AgreedLocationOfGoodsPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }


      "must go from Add Agreed Location of Goods Page" - {
        "to Agreed location of goods page when user selects 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddAgreedLocationOfGoodsPage, true).toOption.value

              navigator
                .nextPage(AddAgreedLocationOfGoodsPage, NormalMode, updatedAnswers)
                .mustBe(goodsSummaryRoute.AgreedLocationOfGoodsController.onPageLoad(updatedAnswers.lrn, NormalMode))
          }
        }
        "to Add Seals page when user selects 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddAgreedLocationOfGoodsPage, false).toOption.value

              navigator
                .nextPage(AddAgreedLocationOfGoodsPage, NormalMode, updatedAnswers)
                .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.lrn, NormalMode))
          }
        }
      }

      "must go from AddCustomsApprovedLocationPage to AddSealsPage when answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddCustomsApprovedLocationPage, false).toOption.value

            navigator
              .nextPage(AddCustomsApprovedLocationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddAgreedLocationOfGoodsController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from CustomsResultDateLimitPage to AddSealsPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val date           = LocalDate.now
            val updatedAnswers = answers.set(ControlResultDateLimitPage, date).toOption.value

            navigator
              .nextPage(ControlResultDateLimitPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from CustomsApprovedLocationPage to AddSealsPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(CustomsApprovedLocationPage, "test").success.value

            navigator
              .nextPage(CustomsApprovedLocationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from SealsInformationPage to SealsIdDetails when answer is Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(SealsInformationPage, true).toOption.value

            navigator
              .nextPage(SealsInformationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedAnswers.lrn, sealIndex, NormalMode))
        }
      }

      "must go from SealsInformationPage to GoodsSummaryCheckYourAnswersPage when answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(SealsInformationPage, false).toOption.value

            navigator
              .nextPage(SealsInformationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from SealsInformationPage to CYA when answer is undefined" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            lazy val updatedAnswers = answers.remove(SealsInformationPage).toOption.value

            navigator
              .nextPage(SealsInformationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from SealIdDetailsPage to SealsInformationPage when submitted" in {
        forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
          (answers, seal) =>
            val updatedAnswers = answers.set(SealIdDetailsPage(sealIndex), seal).success.value

            navigator
              .nextPage(SealIdDetailsPage(sealIndex), NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealsInformationController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from ConfirmRemoveSealPage to SealsInformationPage when submitted" in {
        forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
          (userAnswers, seal) =>
            val updatedAnswers = userAnswers.set(SealIdDetailsPage(sealIndex), seal).success.value

            navigator
              .nextPage(ConfirmRemoveSealPage(), NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealsInformationController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from AddSealsPage to " - {
        "SealIdDetailsController(1) when 'true' is selected and they have no seals" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val updatedUserAnswers = userAnswers
                .remove(SealIdDetailsPage(sealIndex))
                .success
                .value
                .set(AddSealsPage, true)
                .success
                .value

              navigator
                .nextPage(AddSealsPage, NormalMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedUserAnswers.lrn, sealIndex, NormalMode))
          }
        }

        "SealIdDetailsController(2) when 'true' is selected and they already have a seal" in {
          val seal2 = Index(1)
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            (userAnswers, seal) =>
              val updatedUserAnswers = userAnswers
                .set(SealIdDetailsPage(sealIndex), seal)
                .success
                .value
                .set(AddSealsPage, true)
                .success
                .value

              navigator
                .nextPage(AddSealsPage, NormalMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedUserAnswers.lrn, seal2, NormalMode))
          }
        }

        "AddSealsLaterController when 'false' is selected and they don't have existing seals" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val updatedUserAnswers = userAnswers
                .remove(SealIdDetailsPage(sealIndex))
                .success
                .value
                .set(AddSealsPage, false)
                .success
                .value

              navigator
                .nextPage(AddSealsPage, NormalMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.AddSealsLaterController.onPageLoad(updatedUserAnswers.lrn, NormalMode))
          }
        }

        "ConfirmRemoveSealsController when 'false' is selected and they have existing seals" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            (userAnswers, seal) =>
              val updatedUserAnswers = userAnswers
                .set(SealIdDetailsPage(sealIndex), seal).success.value
                .set(AddSealsPage, false).success.value

              navigator
                .nextPage(AddSealsPage, NormalMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.ConfirmRemoveSealsController.onPageLoad(updatedUserAnswers.lrn, NormalMode))
          }
        }

        "SealsInformationController when we already have 10 seals" in {
          forAll(arbitrary[UserAnswers], Gen.listOfN(10, arbitrary[SealDomain])) {
            (userAnswers, seals) =>
            val updateAnswers = userAnswers
              .set(SealsQuery(), seals).success.value
              .set(AddSealsPage, true).success.value

            navigator
              .nextPage(AddSealsPage, NormalMode, updateAnswers)
              .mustBe(goodsSummaryRoute.SealsInformationController.onPageLoad(updateAnswers.lrn, NormalMode))
          }

        }
      }
    }

    "in Check Mode" - {

      "must go from Loading Place page to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
                       navigator
              .nextPage(LoadingPlacePage , CheckMode, answers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from AuthLocationCodePage to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AuthorisedLocationCodePage, "test code").success.value

            navigator
              .nextPage(AuthorisedLocationCodePage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from AddCustomsApprovedLocation to AddAgreedLocationOfGoods page when selecting No when AgreedLocationOfGoodsPage is empty" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCustomsApprovedLocationPage, false).toOption.value
              .remove(AgreedLocationOfGoodsPage).success.value

            navigator
              .nextPage(AddCustomsApprovedLocationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddAgreedLocationOfGoodsController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }
      }

      "must go from AddCustomsApprovedLocation to CheckYourAnswersPage page when selecting No when AgreedLocationOfGoodsPage is empty" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCustomsApprovedLocationPage, false).toOption.value
              .set(AgreedLocationOfGoodsPage, "test").success.value

            navigator
              .nextPage(AddCustomsApprovedLocationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from AddCustomsApprovedLocation to CustomsApprovedLocation when selecting Yes and CustomsApprovedLocation has data" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddCustomsApprovedLocationPage, true)
              .toOption
              .value
              .remove(CustomsApprovedLocationPage)
              .success
              .value

            navigator
              .nextPage(AddCustomsApprovedLocationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.CustomsApprovedLocationController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }
      }

      "must go from ControlResultDateLimitPage to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val date           = LocalDate.now
            val updatedAnswers = answers.set(ControlResultDateLimitPage, date).success.value

            navigator
              .nextPage(ControlResultDateLimitPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from CustomsApprovedLocation page to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(CustomsApprovedLocationPage, "test data").success.value

            navigator
              .nextPage(CustomsApprovedLocationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from AddSealsLaterPage to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(AddSealsLaterPage, CheckMode, answers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from SealIdDetailsPage to SealsInformationPage when submitted" in {
        forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
          (answers, seal) =>
            val updatedAnswers = answers.set(SealIdDetailsPage(sealIndex), seal).success.value

            navigator
              .nextPage(SealIdDetailsPage(sealIndex), CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealsInformationController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }
      }

      "must go from SealsInformationPage to SealsIdDetails when answer is Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(SealsInformationPage, true).toOption.value

            navigator
              .nextPage(SealsInformationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedAnswers.lrn, sealIndex, CheckMode))
        }
      }

      "must go from SealsInformationPage to GoodsSummaryCheckYourAnswersPage when answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(SealsInformationPage, false).toOption.value

            navigator
              .nextPage(SealsInformationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from SealsInformationPage to CYA when answer is undefined" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            lazy val updatedAnswers = answers.remove(SealsInformationPage).toOption.value

            navigator
              .nextPage(SealsInformationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from ConfirmRemoveSealsPage to" - {
        "GoodSummaryCYA when True is selected" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ConfirmRemoveSealsPage, true).toOption.value

              navigator
                .nextPage(ConfirmRemoveSealsPage, CheckMode, updatedAnswers)
                .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
          }
        }

        "AddSeals when False is selected" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ConfirmRemoveSealsPage, false).toOption.value

              navigator
                .nextPage(ConfirmRemoveSealsPage, CheckMode, updatedAnswers)
                .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.lrn, CheckMode))
          }
        }
      }

      "must go from AddAgreedLocationOfGoodsPage" - {
        "to Check your answers page when answer is 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val updatedUserAnswers = userAnswers
                .set(AddAgreedLocationOfGoodsPage, false)
                .success
                .value

              navigator
                .nextPage(AddAgreedLocationOfGoodsPage, CheckMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedUserAnswers.lrn))
          }
        }
        "to Check your answers page when answer is 'Yes' and an answer exists for Agreed Location of Goods" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val updatedUserAnswers = userAnswers
                .set(AddAgreedLocationOfGoodsPage, true)
                .success
                .value
                .set(AgreedLocationOfGoodsPage, "test").success.value

              navigator
                .nextPage(AddAgreedLocationOfGoodsPage, CheckMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedUserAnswers.lrn))
          }
        }

        "to Agreed Location Of Goods Page when answer is 'Yes' and no answer exists for Agreed Location of Goods" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val updatedUserAnswers = userAnswers
                .set(AddAgreedLocationOfGoodsPage, true)
                .success
                .value
                .remove(AgreedLocationOfGoodsPage).success.value

              navigator
                .nextPage(AddAgreedLocationOfGoodsPage, CheckMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.AgreedLocationOfGoodsController.onPageLoad(updatedUserAnswers.lrn, CheckMode))
          }
        }
      }

      "go from AddSealsPage to " - {
        "SealIdDetailsController(1) when 'true' is selected and they have no seals" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val updatedUserAnswers = userAnswers
                .remove(SealIdDetailsPage(sealIndex))
                .success
                .value
                .set(AddSealsPage, true)
                .success
                .value

              navigator
                .nextPage(AddSealsPage, CheckMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedUserAnswers.lrn, sealIndex, CheckMode))
          }
        }

        "GoodsSummaryCheckYourAnswersController when 'true' is selected and they already have a seal" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            (userAnswers, seal) =>
              val updatedUserAnswers = userAnswers
                .set(SealIdDetailsPage(sealIndex), seal)
                .success
                .value
                .set(AddSealsPage, true)
                .success
                .value

              navigator
                .nextPage(AddSealsPage, CheckMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedUserAnswers.lrn))
          }
        }

        "AddSealsLaterController when 'false' is selected and they don't have existing seals" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val updatedUserAnswers = userAnswers
                .remove(SealIdDetailsPage(sealIndex))
                .success
                .value
                .set(AddSealsPage, false)
                .success
                .value

              navigator
                .nextPage(AddSealsPage, CheckMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.AddSealsLaterController.onPageLoad(updatedUserAnswers.lrn, CheckMode))
          }
        }

        "ConfirmRemoveSealsController when 'false' is selected and they have existing seals" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            (userAnswers, seal) =>
              val updatedUserAnswers = userAnswers
                .set(SealIdDetailsPage(sealIndex), seal)
                .success
                .value
                .set(AddSealsPage, false)
                .success
                .value

              navigator
                .nextPage(AddSealsPage, CheckMode, updatedUserAnswers)
                .mustBe(goodsSummaryRoute.ConfirmRemoveSealsController.onPageLoad(updatedUserAnswers.lrn, CheckMode))
          }
        }
      }

      "Must go from Agreed Location Of Goods Page to Check Your Answers Page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AgreedLocationOfGoodsPage, "test").toOption.value

            navigator
              .nextPage(AgreedLocationOfGoodsPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "Must go from ConfirmRemoveSeals page" - {

        "to CYA when answer is Yes" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.set(ConfirmRemoveSealsPage, true).toOption.value

              navigator
                .nextPage(ConfirmRemoveSealsPage, CheckMode, updatedAnswers)
                .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
          }
        }

        "to Add Seals Page when answer is No" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.set(ConfirmRemoveSealsPage, false).toOption.value

              navigator
                .nextPage(ConfirmRemoveSealsPage, CheckMode, updatedAnswers)
                .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.lrn, CheckMode))
          }
        }
      }
    }
  }
  // format: on
}
