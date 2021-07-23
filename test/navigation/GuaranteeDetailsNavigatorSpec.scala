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
import controllers.guaranteeDetails.{routes => guaranteeDetailsRoute}
import generators.Generators
import models.DeclarationType.{Option2, Option4}
import models.GuaranteeType._
import models.reference.{CountryCode, CustomsOffice}
import models.{CheckMode, GuaranteeType, Index, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{guaranteeDetails, _}
import pages.guaranteeDetails._
import play.api.libs.json.{JsObject, JsPath}

class GuaranteeDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  // format: off
  val customsOffice1: CustomsOffice = CustomsOffice("officeId", "someName", CountryCode("GB"), None)
  val customsOffice2: CustomsOffice = CustomsOffice("officeId", "someName", CountryCode("DE"), None)

  val navigator = new GuaranteeDetailsNavigator
  "GuaranteeDetailsNavigator" - {
    "in normal mode" - {
      "must go from GuaranteeTypePage to GuaranteeReferenceNumberPage when user selects" - {
        "GuaranteeWaiver" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage(index), GuaranteeWaiver).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "ComprehensiveGuarantee" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage(index), ComprehensiveGuarantee).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "IndividualGuarantee" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage(index), IndividualGuarantee).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "FlatRateVoucher" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage(index), FlatRateVoucher).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.lrn, (index), NormalMode))
          }
        }

        "IndividualGuaranteeMultiple" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers.set(GuaranteeTypePage(index), IndividualGuaranteeMultiple).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }
      }

      "to OtherReferencePage when user selects" - {
        "CashDepositGuarantee" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage(index)).success.value
                .set(GuaranteeTypePage(index), CashDepositGuarantee).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "GuaranteeNotRequired" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage(index)).success.value
                .set(GuaranteeTypePage(index), GuaranteeNotRequired).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "GuaranteeWaivedRedirect" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage(index)).success.value
                .set(GuaranteeTypePage(index), GuaranteeWaivedRedirect).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "GuaranteeWaiverByAgreement" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage(index)).success.value
                .set(GuaranteeTypePage(index), GuaranteeWaiverByAgreement).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "GuaranteeWaiverSecured" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers = answers
                .remove(GuaranteeReferencePage(index)).success.value
                .set(GuaranteeTypePage(index), GuaranteeWaiverSecured).success.value
              navigator
                .nextPage(GuaranteeTypePage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }
      }

      "From TIRGuaranteeReferencePage to AddAnotherGuarantee" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(guaranteeDetails.TIRGuaranteeReferencePage(index), "test").success.value

            navigator
              .nextPage(TIRGuaranteeReferencePage(index), NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.AddAnotherGuaranteeController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "From GuaranteeReferencePage to LiabilityAmountPage when both 'OfficeOfDeparture' and 'DestinationOffice' are in GB" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeReferencePage(index), "test").success.value
              .set(OfficeOfDeparturePage, customsOffice1).success.value
              .set(DestinationOfficePage, customsOffice1).success.value
            navigator
              .nextPage(GuaranteeReferencePage(index), NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.LiabilityAmountController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }
      }

      "From GuaranteeReferencePage to OtherReferenceLiabilityAmountPage when 'DestinationOffice' is not in GB" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeReferencePage(index), "test").success.value
              .set(OfficeOfDeparturePage, customsOffice1).success.value
              .set(DestinationOfficePage, customsOffice2).success.value
            navigator
              .nextPage(GuaranteeReferencePage(index), NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.OtherReferenceLiabilityAmountController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }
      }

      "From GuaranteeReferencePage to OtherReferenceLiabilityAmountPage when 'OfficeOfDeparture' is not in GB" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeReferencePage(index), "test").success.value
              .set(OfficeOfDeparturePage, customsOffice2).success.value
              .set(DestinationOfficePage, customsOffice1).success.value
            navigator
              .nextPage(GuaranteeReferencePage(index), NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.OtherReferenceLiabilityAmountController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }
      }

      "From Liability Amount Page" - {
        "to AccessCodePage" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers: UserAnswers =
                answers
                  .set(GuaranteeTypePage(index), GuaranteeWaiver).success.value
                  .set(GuaranteeReferencePage(index), "test").success.value
                  .set(LiabilityAmountPage(index), "100.12").success.value
              navigator
                .nextPage(LiabilityAmountPage(index), NormalMode, updatedAnswers)
                .mustBe(guaranteeDetailsRoute.AccessCodeController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }
      }
    }

    "From Default Amount Page" - {
      "to Other Reference Liability amount page if the answer is NO" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(DefaultAmountPage(index), false).success.value
            navigator
              .nextPage(DefaultAmountPage(index), NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.OtherReferenceLiabilityAmountController.onPageLoad(answers.lrn, index, NormalMode))
        }
      }

      "to Access code page if the answer is YES" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(DefaultAmountPage(index), true).success.value
            navigator
              .nextPage(DefaultAmountPage(index), NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.AccessCodeController.onPageLoad(answers.lrn, index, NormalMode))
        }
      }
    }

    "From AccessCodeController to CYA" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers: UserAnswers = answers.set(AccessCodePage(index), "1234").success.value
          navigator
            .nextPage(AccessCodePage(index), NormalMode, updatedAnswers)
            .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }

    "From OtherDetailsLiabilityAmount" - {
      "to AccessCode page when an amount greater than '0' is entered" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers.set(LiabilityAmountPage(index), "1234").success.value
            navigator
              .nextPage(OtherReferenceLiabilityAmountPage(index), NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.AccessCodeController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }
      }

      "to Default Amount page when an amount of '0' is entered" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers.set(LiabilityAmountPage(index), "0").success.value
            navigator
              .nextPage(OtherReferenceLiabilityAmountPage(index), NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.DefaultAmountController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
        }
      }
    }

    "From OtherReference to CheckYourAnswers" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers: UserAnswers = answers.set(OtherReferencePage(index), "1234").success.value
          navigator
            .nextPage(OtherReferencePage(index), NormalMode, updatedAnswers)
            .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }

    "From AddAnotherGuaranteePage" - {
      "to GuaranteeTypePage for next guarantee in the array when yes is selected and declaration type is not TIR" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(DeclarationTypePage, Option2).success.value
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(LiabilityAmountPage(index), "12345").success.value
              .set(AccessCodePage(index), "1234").success.value
              .set(AddAnotherGuaranteePage, true).success.value
            navigator
              .nextPage(AddAnotherGuaranteePage, NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeTypeController.onPageLoad(updatedAnswers.lrn, Index(1), NormalMode))
        }
      }

      "to TIRGuaranteeReference page for next guarantee in the array when yes is selected and declaration type is TIR" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(DeclarationTypePage, Option4).success.value
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(LiabilityAmountPage(index), "12345").success.value
              .set(AccessCodePage(index), "1234").success.value
              .set(AddAnotherGuaranteePage, true).success.value
            navigator
              .nextPage(AddAnotherGuaranteePage, NormalMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.TIRGuaranteeReferenceController.onPageLoad(updatedAnswers.lrn, Index(1), NormalMode))
        }
      }

      "to TaskListPage when no is selected" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(DeclarationTypePage, Option2).success.value
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(LiabilityAmountPage(index), "12345").success.value
              .set(AccessCodePage(index), "1234").success.value
              .set(AddAnotherGuaranteePage, false).success.value
            navigator
              .nextPage(AddAnotherGuaranteePage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.DeclarationSummaryController.onPageLoad(updatedAnswers.lrn))
        }
      }
      "to TaskListPage when number of guarantees is 9" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(GuaranteeTypePage(Index(1)), GuaranteeType.FlatRateVoucher).success.value
              .set(GuaranteeTypePage(Index(2)), GuaranteeType.CashDepositGuarantee).success.value
              .set(GuaranteeTypePage(Index(3)), GuaranteeType.ComprehensiveGuarantee).success.value
              .set(GuaranteeTypePage(Index(4)), GuaranteeType.GuaranteeNotRequired).success.value
              .set(GuaranteeTypePage(Index(5)), GuaranteeType.GuaranteeWaivedRedirect).success.value
              .set(GuaranteeTypePage(Index(6)), GuaranteeType.GuaranteeWaiverByAgreement).success.value
              .set(GuaranteeTypePage(Index(7)), GuaranteeType.IndividualGuaranteeMultiple).success.value
              .set(GuaranteeTypePage(Index(8)), GuaranteeType.IndividualGuarantee).success.value
              .set(LiabilityAmountPage(index), "12345").success.value
              .set(AccessCodePage(index), "1234").success.value
              .set(AddAnotherGuaranteePage, true).success.value
            navigator
              .nextPage(AddAnotherGuaranteePage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.DeclarationSummaryController.onPageLoad(updatedAnswers.lrn))
        }
      }
    }
  }

  "in Checkmode" - {

    "must go from Guarantee Type page to" - {
      "OtherReferencePage when user selects 3,5,6,7 or A and guaranteeReference had been set" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeReferencePage(index), "1234").success.value
              .set(GuaranteeTypePage(index), CashDepositGuarantee).success.value
              .remove(OtherReferencePage(index)).success.value
            navigator
              .nextPage(GuaranteeTypePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.OtherReferenceController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }

      "to CYA page when user selects 3,5,6,7 or A and previously had selected GuaranteeWaivedRedirect" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .remove(GuaranteeReferencePage(index)).success.value
              .set(OtherReferencePage(index), "test").success.value
              .set(GuaranteeTypePage(index), GuaranteeWaivedRedirect).success.value

            navigator
              .nextPage(GuaranteeTypePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }

      "to CYA page when user selects 0,1,2,4,or 9 and previously had selected GuaranteeWaiver" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .remove(OtherReferencePage(index)).success.value
              .set(GuaranteeReferencePage(index), "test").success.value
              .set(GuaranteeTypePage(index), GuaranteeWaiver).success.value

            navigator
              .nextPage(GuaranteeTypePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }

      "to GuaranteeReference page when user changes answer from 4 to 0,1,2 or 9 and the cleanup removed previous answer" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .remove(GuaranteeReferencePage(index)).success.value
              .set(AccessCodePage(index), "1111").success.value
              .set(GuaranteeTypePage(index), GuaranteeWaiver).success.value
            navigator
              .nextPage(GuaranteeTypePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }


      "to GuaranteedReference page when user changes answer from  0,1,2 or 9 to 4 and the cleanup removed previous answer" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeTypePage(index), FlatRateVoucher).success.value
              .remove(GuaranteeReferencePage(index)).success.value

            navigator
              .nextPage(GuaranteeTypePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeReferenceController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }
    }

    "From OtherReferencePage" - {
      "to CYA " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(OtherReferencePage(index), "test").success.value
              .set(LiabilityAmountPage(index), "1").success.value
            navigator
              .nextPage(OtherReferencePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }
    }

    "From TIRGuaranteeReferencePage" - {

      "to AddAnotherGuaranteeReference" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(guaranteeDetails.TIRGuaranteeReferencePage(index), "125678901234567").success.value

            navigator
              .nextPage(guaranteeDetails.TIRGuaranteeReferencePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.AddAnotherGuaranteeController.onPageLoad(updatedAnswers.lrn))
        }
      }
    }

    "From GuaranteeReferencePage" - {
      "to CYA if liability amount and access code exists" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(OtherReferencePage(index), "125678901234567").success.value
              .set(LiabilityAmountPage(index), "1").success.value
              .set(AccessCodePage(index), "1111").success.value
            navigator
              .nextPage(GuaranteeReferencePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }
      "to CYA if other liability amount and access code exists" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(OtherReferencePage(index), "125678901234567").success.value
              .set(LiabilityAmountPage(index), "1").success.value
              .set(AccessCodePage(index), "1111").success.value
            navigator
              .nextPage(GuaranteeReferencePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }
      "to AccessCodePage when no access code exists but liability amount exists" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeReferencePage(index), "test").success.value
              .set(LiabilityAmountPage(index), "1").success.value
              .remove(AccessCodePage(index)).success.value
            navigator
              .nextPage(GuaranteeReferencePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.AccessCodeController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }

      "to LiabilityAmount page when no liability amount exist and offices of departure and destination are both GB" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeReferencePage(index), "test").success.value
              .set(OfficeOfDeparturePage, customsOffice1).success.value
              .set(DestinationOfficePage, customsOffice1).success.value
              .remove(LiabilityAmountPage(index)).success.value
            navigator
              .nextPage(GuaranteeReferencePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.LiabilityAmountController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }

      "to OtherLiabilityAmount page when no otherliability amount exists and at least one of the offices of departure and destination is not GB" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeReferencePage(index), "test").success.value
              .set(OfficeOfDeparturePage, customsOffice2).success.value
              .set(DestinationOfficePage, customsOffice1).success.value
              .remove(LiabilityAmountPage(index)).success.value
            navigator
              .nextPage(GuaranteeReferencePage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.OtherReferenceLiabilityAmountController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }
    }

    "From LiabilityAmountPage" - {
      "to CYA if access code exists" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(LiabilityAmountPage(index), "100.00").success.value
              .set(AccessCodePage(index), "1111").success.value
            navigator
              .nextPage(LiabilityAmountPage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }
      "to access code if access code does not exist" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeTypePage(index), GuaranteeWaiver).success.value
              .set(LiabilityAmountPage(index), "100.00").success.value
              .remove(AccessCodePage(index)).success.value
            navigator
              .nextPage(LiabilityAmountPage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.AccessCodeController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }
    }

    "From AccessCodePage to CYA" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers: UserAnswers = answers
            .set(GuaranteeTypePage(index), GuaranteeWaiver).success.value
            .set(GuaranteeReferencePage(index), "12345678901234567").success.value
            .set(LiabilityAmountPage(index), "1").success.value
            .set(AccessCodePage(index), "1111").success.value
          navigator
            .nextPage(GuaranteeReferencePage(index), CheckMode, updatedAnswers)
            .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
      }
    }

    "From DefaultAmountPage" - {
      "to CYA when Yes is selected and access code is present" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(DefaultAmountPage(index), true).success.value
              .set(AccessCodePage(index), "1111").success.value
            navigator
              .nextPage(DefaultAmountPage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }
      "to AccessCode page when Yes is selected and access code is not present" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(DefaultAmountPage(index), true).success.value
              .remove(AccessCodePage(index)).success.value
            navigator
              .nextPage(DefaultAmountPage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.AccessCodeController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }
      "to OtherReferenceLiabilityAmountPage when No is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers.set(DefaultAmountPage(index), false).success.value
            navigator
              .nextPage(DefaultAmountPage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.OtherReferenceLiabilityAmountController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
        }
      }
    }

    "From OtherReferenceLiabilityAmountPage" - {
      "to CYA when a valid amount is input and  access code exists" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(LiabilityAmountPage(index), "1,23").success.value
              .set(AccessCodePage(index), "1111").success.value
            navigator
              .nextPage(OtherReferenceLiabilityAmountPage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
        }
      }
      "to access code page when a valid amount is input and no access code exists" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(LiabilityAmountPage(index), "1,23").success.value
              .remove(AccessCodePage(index)).success.value
            navigator
              .nextPage(OtherReferenceLiabilityAmountPage(index), CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.AccessCodeController.onPageLoad(updatedAnswers.lrn, index, CheckMode))

        }
      }
    }

    "From ConfirmRemoveGuaranteePage" - {
      "to AddAnotherGuaranteePage if yes is selected" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(ConfirmRemoveGuaranteePage, true).success.value
            navigator
              .nextPage(ConfirmRemoveGuaranteePage, NormalMode, updatedAnswers)
              .mustBe(controllers.guaranteeDetails.routes.AddAnotherGuaranteeController.onPageLoad(updatedAnswers.lrn))
        }
      }
      "to AddAnotherGuaranteePage if no is selected" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(GuaranteeTypePage(Index(1)), GuaranteeType.FlatRateVoucher).success.value
              .set(GuaranteeTypePage(Index(2)), GuaranteeType.FlatRateVoucher).success.value
              .set(ConfirmRemoveGuaranteePage, false).success.value
            navigator
              .nextPage(ConfirmRemoveGuaranteePage, NormalMode, updatedAnswers)
              .mustBe(controllers.guaranteeDetails.routes.AddAnotherGuaranteeController.onPageLoad(updatedAnswers.lrn))
        }
      }
      "to GuaranteeTypePage if yes is selected and there are no more guarantees left" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val guaranteesBlock: QuestionPage[JsObject] = new QuestionPage[JsObject] {
              override def path: JsPath = JsPath \ "guarantees"
            }

            val updatedAnswers: UserAnswers = answers.remove(guaranteesBlock).success.value
              .set(ConfirmRemoveGuaranteePage, true).success.value
            navigator
              .nextPage(ConfirmRemoveGuaranteePage, NormalMode, updatedAnswers)
              .mustBe(controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(updatedAnswers.lrn, Index(0), NormalMode))
        }
      }
    }

    "From AddAnotherGuaranteePage" - {
      "to GuaranteeTypePage for next guarantee in the array when yes is selected and declaration type is not TIR" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(DeclarationTypePage, Option2).success.value
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(LiabilityAmountPage(index), "12345").success.value
              .set(AccessCodePage(index), "1234").success.value
              .set(AddAnotherGuaranteePage, true).success.value
            navigator
              .nextPage(AddAnotherGuaranteePage, CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.GuaranteeTypeController.onPageLoad(updatedAnswers.lrn, Index(1), NormalMode))
        }
      }

      "to TIRGuaranteeReference page for next guarantee in the array when yes is selected and declaration type is TIR" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(DeclarationTypePage, Option4).success.value
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(LiabilityAmountPage(index), "12345").success.value
              .set(AccessCodePage(index), "1234").success.value
              .set(AddAnotherGuaranteePage, true).success.value
            navigator
              .nextPage(AddAnotherGuaranteePage, CheckMode, updatedAnswers)
              .mustBe(guaranteeDetailsRoute.TIRGuaranteeReferenceController.onPageLoad(updatedAnswers.lrn, Index(1), NormalMode))
        }
      }

      "to TaskListPage when no is selected" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(DeclarationTypePage, Option2).success.value
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(LiabilityAmountPage(index), "12345").success.value
              .set(AccessCodePage(index), "1234").success.value
              .set(AddAnotherGuaranteePage, false).success.value
            navigator
              .nextPage(AddAnotherGuaranteePage, CheckMode, updatedAnswers)
              .mustBe(controllers.routes.DeclarationSummaryController.onPageLoad(updatedAnswers.lrn))
        }
      }
      "to TaskListPage when number of guarantees is 9" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers: UserAnswers = answers
              .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher).success.value
              .set(GuaranteeTypePage(Index(1)), GuaranteeType.FlatRateVoucher).success.value
              .set(GuaranteeTypePage(Index(2)), GuaranteeType.CashDepositGuarantee).success.value
              .set(GuaranteeTypePage(Index(3)), GuaranteeType.ComprehensiveGuarantee).success.value
              .set(GuaranteeTypePage(Index(4)), GuaranteeType.GuaranteeNotRequired).success.value
              .set(GuaranteeTypePage(Index(5)), GuaranteeType.GuaranteeWaivedRedirect).success.value
              .set(GuaranteeTypePage(Index(6)), GuaranteeType.GuaranteeWaiverByAgreement).success.value
              .set(GuaranteeTypePage(Index(7)), GuaranteeType.IndividualGuaranteeMultiple).success.value
              .set(GuaranteeTypePage(Index(8)), GuaranteeType.IndividualGuarantee).success.value
              .set(LiabilityAmountPage(index), "12345").success.value
              .set(AccessCodePage(index), "1234").success.value
              .set(AddAnotherGuaranteePage, true).success.value
            navigator
              .nextPage(AddAnotherGuaranteePage, CheckMode, updatedAnswers)
              .mustBe(controllers.routes.DeclarationSummaryController.onPageLoad(updatedAnswers.lrn))
        }
      }
    }
    // format: on
  }
}
