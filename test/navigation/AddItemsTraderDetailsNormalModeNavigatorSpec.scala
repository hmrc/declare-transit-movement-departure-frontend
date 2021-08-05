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
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.routes
import controllers.addItems.traderDetails.{routes => traderRoutes}
import generators.Generators
import models.{CheckMode, CommonAddress, Index, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems.traderDetails._

class AddItemsTraderDetailsNormalModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
  val navigator = new AddItemsTraderDetailsNavigator

  "Add Items section" - {

    "in normal mode" - {

      "Trader Details" - {
        //Consignor
        "must go from ConsignorEoriKnown to" - {
          "ConsignorEoriNumber when true" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .remove(AddConsignorPage).success.value
                  .remove(AddConsigneePage).success.value
                  .set(TraderDetailsConsignorEoriKnownPage(index), true).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
            }
          }

          "ConsignorName when false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage(index), false).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
            }
          }
        }

        "must go from ConsignorEoriNumber to ConsignorName" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorEoriNumberPage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(answers.lrn, index, NormalMode))
          }
        }

        "must go from ConsignorName to ConsignorAddress" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorNamePage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(answers.lrn, index, NormalMode))
          }
        }

        "must go from ConsignorAddress to" - {
          "Consignee's Eori when there is no Consignee for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(AddConsigneePage)(false)

                navigator
                  .nextPage(TraderDetailsConsignorAddressPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.lrn, index, NormalMode))
            }
          }

          "Package type when there is a Consignee for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(AddConsigneePage)(true)

                navigator
                  .nextPage(TraderDetailsConsignorAddressPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.lrn, index, Index(0), NormalMode))
            }
          }
        }

        "must go from ConsigneeEoriKnown to" - {
          "ConsigneeEoriNumber when true" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsigneeEoriKnownPage(index), true).success.value
                navigator
                  .nextPage(TraderDetailsConsigneeEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
            }
          }

          "ConsigneeName when false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsigneeEoriKnownPage(index), false).success.value
                  .remove(TraderDetailsConsigneeNamePage(index)).success.value
                navigator
                  .nextPage(TraderDetailsConsigneeEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
            }
          }

        }

        "must go from ConsigneeEoriNumber to Consignee Name" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsigneeEoriNumberPage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(answers.lrn, index, NormalMode))
          }
        }

        "must go from ConsigneeName to ConsigneeAddress" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsigneeNamePage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsigneeAddressController.onPageLoad(answers.lrn, index, NormalMode))
          }
        }

        "must go from ConsigneeAddress to Package Type" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsigneeAddressPage(index), NormalMode, answers)
                .mustBe(routes.PackageTypeController.onPageLoad(answers.lrn, index, Index(0), NormalMode))
          }
        }
      }

    }
  }

  "in check mode" - {

    //Trader details
    "Trader Details" - {
      //Consignor
      "must go from ConsignorEoriKnown to" - {
        "ConsignorEoriNumber when true and EoriNumber is empty" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(TraderDetailsConsignorEoriKnownPage(index), true).success.value
                .remove(TraderDetailsConsignorEoriNumberPage(index)).success.value
              navigator
                .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
          }
        }

        "ConsignorName when false and consignorName is empty" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(TraderDetailsConsignorEoriKnownPage(index), false).success.value
                .remove(TraderDetailsConsignorNamePage(index)).success.value
              navigator
                .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
          }
        }

        "Items CYA when true and EoriNumber is answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(TraderDetailsConsignorEoriKnownPage(index), true).success.value
                .set(TraderDetailsConsignorEoriNumberPage(index), eoriNumber.value).success.value
              navigator
                .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
          }
        }

        "Items CYA when false and ConsignorName is answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(TraderDetailsConsignorEoriKnownPage(index), false).success.value
                .set(TraderDetailsConsignorNamePage(index), "name").success.value
              navigator
                .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
          }
        }
      }

      "must go from ConsignorEoriNumber to" - {
        "Items CYA if Consignor Name is populated" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .set(TraderDetailsConsignorNamePage(index), "Davey Jones").success.value
              navigator
                .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, userAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.lrn, index))
          }
        }

        "Consignor Name if Consignor Name is not populated" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .remove(TraderDetailsConsignorNamePage(index)).success.value
              navigator
                .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, userAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(userAnswers.lrn, index, CheckMode))
          }
        }
      }

      "must go from ConsignorName to" - {
        "ConsignorAddress when Address is empty" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .remove(TraderDetailsConsignorAddressPage(index)).success.value
              navigator
                .nextPage(TraderDetailsConsignorNamePage(index), CheckMode, userAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(userAnswers.lrn, index, CheckMode))
          }
        }

        "Items CYA when Address is Populated" in {
          forAll(arbitrary[UserAnswers], arbitrary[CommonAddress]) {
            (answers, address) =>
              val userAnswers = answers
                .set(TraderDetailsConsignorAddressPage(index), address).success.value
              navigator
                .nextPage(TraderDetailsConsignorNamePage(index), CheckMode, userAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
          }
        }
      }

      "must go from ConsignorAddress to" - {
        "Items CYA" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorAddressPage(index), CheckMode, answers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
          }
        }
      }

      //Consignee
      "must go from ConsigneeEoriKnown to" - {
        "ConsigneeEoriNumber when true and ConsigneeEoriNumber is empty" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(TraderDetailsConsigneeEoriKnownPage(index), true).success.value
                .remove(TraderDetailsConsigneeEoriNumberPage(index)).success.value
              navigator
                .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
          }
        }

        "ConsigneeName when false and ConsigneeName is empty" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(TraderDetailsConsigneeEoriKnownPage(index), false).success.value
                .remove(TraderDetailsConsigneeNamePage(index)).success.value
              navigator
                .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.lrn, index, CheckMode))
          }
        }

        "Items CYA when true and ConsigneeEoriNumber is populated" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(TraderDetailsConsigneeEoriKnownPage(index), true).success.value
                .set(TraderDetailsConsigneeEoriNumberPage(index), eoriNumber.value).success.value
              navigator
                .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
          }

        }

        "Items CYA when false and ConsigneeName is populated" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(TraderDetailsConsigneeEoriKnownPage(index), false).success.value
                .set(TraderDetailsConsigneeNamePage(index), "value").success.value
              navigator
                .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
          }
        }
      }

      "must go from ConsigneeEoriNumber to" - {
        "Items CYA if Consignee Name is populated" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .unsafeSetVal(TraderDetailsConsigneeNamePage(index))("Davey Jones")

              navigator
                .nextPage(TraderDetailsConsigneeEoriNumberPage(index), CheckMode, userAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.lrn, index))
          }
        }

        "Consignee Name if Consignee Name is not populated" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .unsafeRemove(TraderDetailsConsigneeNamePage(index))

              navigator
                .nextPage(TraderDetailsConsigneeEoriNumberPage(index), CheckMode, userAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(userAnswers.lrn, index, CheckMode))
          }
        }
      }

      "must go from ConsigneeName to" - {
        "ConsigneeAddress when address is empty" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .remove(TraderDetailsConsigneeAddressPage(index)).success.value
              navigator
                .nextPage(TraderDetailsConsigneeNamePage(index), CheckMode, userAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeAddressController.onPageLoad(userAnswers.lrn, index, CheckMode))
          }
        }

        "Items CYA when address is populated" in {
          forAll(arbitrary[UserAnswers], arbitrary[CommonAddress]) {
            (answers, address) =>
              val userAnswers = answers
                .set(TraderDetailsConsigneeAddressPage(index), address).success.value
              navigator
                .nextPage(TraderDetailsConsigneeNamePage(index), CheckMode, userAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
          }
        }
      }

      "must go from ConsigneeAddress to" - {
        "Items  CYA" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsigneeAddressPage(index), CheckMode, answers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
          }
        }
      }

      // format: on
    }
  }
}
