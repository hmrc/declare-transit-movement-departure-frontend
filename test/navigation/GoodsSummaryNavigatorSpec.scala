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

package navigation

import java.time.LocalDate

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
import queries.SealsQuery

class GoodsSummaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new GoodsSummaryNavigator
  // format: off
  "GoodsSummaryNavigator" - {

    "in Normal Mode" - {

      "must go from DeclarePackagesPage to TotalPackagesPage when user selects Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(DeclarePackagesPage, true).toOption.value

            navigator
              .nextPage(DeclarePackagesPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.TotalPackagesController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from DeclarePackagesPage to TotalGrossMassPage when user selects No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(DeclarePackagesPage, false).toOption.value

            navigator
              .nextPage(DeclarePackagesPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.TotalGrossMassController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from TotalPackagesPage to TotalGrossMassPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(TotalPackagesPage, 1).toOption.value

            navigator
              .nextPage(TotalPackagesPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.TotalGrossMassController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from TotalGrossMassPage to AuthorisedLocationCodePage when on Simplified journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ProcedureTypePage, Simplified).toOption.value

            navigator
              .nextPage(TotalGrossMassPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AuthorisedLocationCodeController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from TotalGrossMassPage to AddCustomsApprovedLocationPage when on Normal journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ProcedureTypePage, Normal).toOption.value

            navigator
              .nextPage(TotalGrossMassPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddCustomsApprovedLocationController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from AuthorisedLocationCodePage to ControlResultDateLimitPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AuthorisedLocationCodePage, "test").toOption.value

            navigator
              .nextPage(AuthorisedLocationCodePage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.ControlResultDateLimitController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from AddCustomsApprovedLocationPage to CustomsApprovedLocationPage when answer is Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddCustomsApprovedLocationPage, true).toOption.value

            navigator
              .nextPage(AddCustomsApprovedLocationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.CustomsApprovedLocationController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from AddCustomsApprovedLocationPage to AddSealsPage when answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddCustomsApprovedLocationPage, false).toOption.value

            navigator
              .nextPage(AddCustomsApprovedLocationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from CustomsResultDateLimitPage to AddSealsPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val date           = LocalDate.now
            val updatedAnswers = answers.set(ControlResultDateLimitPage, date).toOption.value

            navigator
              .nextPage(ControlResultDateLimitPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from CustomsApprovedLocationPage to AddSealsPage when submitted" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(CustomsApprovedLocationPage, "test").success.value

            navigator
              .nextPage(CustomsApprovedLocationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from SealsInformationPage to SealsIdDetails when answer is Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(SealsInformationPage, true).toOption.value

            navigator
              .nextPage(SealsInformationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedAnswers.id, sealIndex, NormalMode))
        }
      }

      "must go from SealsInformationPage to GoodsSummaryCheckYourAnswersPage when answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(SealsInformationPage, false).toOption.value

            navigator
              .nextPage(SealsInformationPage, NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from SealIdDetailsPage to SealsInformationPage when submitted" in {
        forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
          (answers, seal) =>
            val updatedAnswers = answers.set(SealIdDetailsPage(sealIndex), seal).success.value

            navigator
              .nextPage(SealIdDetailsPage(sealIndex), NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealsInformationController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from ConfirmRemoveSealPage to SealsInformationPage when submitted" in {
        forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
          (userAnswers, seal) =>
            val updatedAnswers = userAnswers.set(SealIdDetailsPage(sealIndex), seal).success.value

            navigator
              .nextPage(ConfirmRemoveSealPage(), NormalMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealsInformationController.onPageLoad(updatedAnswers.id, NormalMode))
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
                .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedUserAnswers.id, sealIndex, NormalMode))
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
                .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedUserAnswers.id, seal2, NormalMode))
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
                .mustBe(goodsSummaryRoute.AddSealsLaterController.onPageLoad(updatedUserAnswers.id, NormalMode))
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
                .mustBe(goodsSummaryRoute.ConfirmRemoveSealsController.onPageLoad(updatedUserAnswers.id, NormalMode))
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
              .mustBe(goodsSummaryRoute.SealsInformationController.onPageLoad(updateAnswers.id, NormalMode))
          }

        }
      }
    }

    "in Check Mode" - {

      "must go from DeclarePackagesPage to TotalPackagesPage when selecting Yes and TotalPackages has no data" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(DeclarePackagesPage, true).toOption.value
              .remove(TotalPackagesPage).success.value

            navigator
              .nextPage(DeclarePackagesPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.TotalPackagesController.onPageLoad(updatedAnswers.id, CheckMode))
        }
      }

      "must go from DeclarePackagesPage to CheckYourAnswersPage when selecting Yes and TotalPackages has data" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(DeclarePackagesPage, true)
              .toOption
              .value
              .set(TotalPackagesPage, 1)
              .success
              .value

            navigator
              .nextPage(DeclarePackagesPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from DeclarePackagesPage to CheckYourAnswersPage when selecting No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(DeclarePackagesPage, false)
              .toOption
              .value
              .remove(TotalPackagesPage)
              .success
              .value

            navigator
              .nextPage(DeclarePackagesPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from TotalGrossMage to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(TotalGrossMassPage, "100").success.value

            navigator
              .nextPage(TotalGrossMassPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from AuthLocationCodePage to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AuthorisedLocationCodePage, "test code").success.value

            navigator
              .nextPage(AuthorisedLocationCodePage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from AddCustomsApprovedLocation to CheckYourAnswers page when selecting No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddCustomsApprovedLocationPage, false).toOption.value

            navigator
              .nextPage(AddCustomsApprovedLocationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
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
              .mustBe(goodsSummaryRoute.CustomsApprovedLocationController.onPageLoad(updatedAnswers.id, CheckMode))
        }
      }

      "must go from ControlResultDateLimitPage to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val date           = LocalDate.now
            val updatedAnswers = answers.set(ControlResultDateLimitPage, date).success.value

            navigator
              .nextPage(ControlResultDateLimitPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from CustomsApprovedLocation page to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(CustomsApprovedLocationPage, "test data").success.value

            navigator
              .nextPage(CustomsApprovedLocationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from AddSealsLaterPage to CheckYourAnswersPage " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(AddSealsLaterPage, CheckMode, answers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from SealIdDetailsPage to SealsInformationPage when submitted" in {
        forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
          (answers, seal) =>
            val updatedAnswers = answers.set(SealIdDetailsPage(sealIndex), seal).success.value

            navigator
              .nextPage(SealIdDetailsPage(sealIndex), CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealsInformationController.onPageLoad(updatedAnswers.id, CheckMode))
        }
      }

      "must go from SealsInformationPage to SealsIdDetails when answer is Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(SealsInformationPage, true).toOption.value

            navigator
              .nextPage(SealsInformationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedAnswers.id, sealIndex, CheckMode))
        }
      }

      "must go from SealsInformationPage to GoodsSummaryCheckYourAnswersPage when answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(SealsInformationPage, false).toOption.value

            navigator
              .nextPage(SealsInformationPage, CheckMode, updatedAnswers)
              .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
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
                .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
          }
        }

        "AddSeals when False is selected" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ConfirmRemoveSealsPage, false).toOption.value

              navigator
                .nextPage(ConfirmRemoveSealsPage, CheckMode, updatedAnswers)
                .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.id, CheckMode))
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
                .mustBe(goodsSummaryRoute.SealIdDetailsController.onPageLoad(updatedUserAnswers.id, sealIndex, CheckMode))
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
                .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedUserAnswers.id))
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
                .mustBe(goodsSummaryRoute.AddSealsLaterController.onPageLoad(updatedUserAnswers.id, CheckMode))
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
                .mustBe(goodsSummaryRoute.ConfirmRemoveSealsController.onPageLoad(updatedUserAnswers.id, CheckMode))
          }
        }
      }

      "Must go from ConfirmRemoveSeals page" - {

        "to CYA when answer is Yes" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.set(ConfirmRemoveSealsPage, true).toOption.value

              navigator
                .nextPage(ConfirmRemoveSealsPage, CheckMode, updatedAnswers)
                .mustBe(goodsSummaryRoute.GoodsSummaryCheckYourAnswersController.onPageLoad(updatedAnswers.id))
          }
        }

        "to Add Seals Page when answer is No" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.set(ConfirmRemoveSealsPage, false).toOption.value

              navigator
                .nextPage(ConfirmRemoveSealsPage, CheckMode, updatedAnswers)
                .mustBe(goodsSummaryRoute.AddSealsController.onPageLoad(updatedAnswers.id, CheckMode))
          }
        }
      }
    }
  }
  // format: on
}
