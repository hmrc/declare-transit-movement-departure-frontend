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

import base.SpecBase
import controllers.addItems.previousReferences.{routes => previousReferenceRoutes}
import controllers.addItems.routes
import controllers.addItems.traderDetails.{routes => traderRoutes}
import controllers.{routes => mainRoutes}
import generators.Generators
import models.reference.{CountryCode, PackageType}
import models.{CheckMode, DeclarationType, Index, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import pages.addItems.traderDetails._
import queries.{ItemsQuery, PackagesQuery, PreviousReferencesQuery}

class AddItemsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new AddItemsNavigator

  "Add Items section" - {

    "in normal mode" - {

      "must go from item description page to total gross mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemDescriptionPage(index), NormalMode, answers)
              .mustBe(routes.ItemTotalGrossMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from total gross mass page to add total net mass page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemTotalGrossMassPage(index), NormalMode, answers)
              .mustBe(routes.AddTotalNetMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from add total net mass page to total net mass page if the answer is 'Yes' and no answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true)
              .success
              .value
              .remove(TotalNetMassPage(index))
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.TotalNetMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from add total net mass page to IsCommodityCodeKnownPage if the answer is 'No'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false)
              .success
              .value
              .remove(TotalNetMassPage(index))
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.IsCommodityCodeKnownController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      //Commodity Code to Trader Details

      "must go from IsCommodityCodeKnownPage if the answer is 'No' to" - {

        "Do you know the consignor eori page when Consignor and Consignee is not the same for all item" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsCommodityCodeKnownPage(index), false)
                .success
                .value
                .set(ConsignorForAllItemsPage, false)
                .success
                .value
                .set(AddConsignorPage, false)
                .success
                .value
                .set(ConsigneeForAllItemsPage, false)
                .success
                .value
                .set(AddConsigneePage, false)
                .success
                .value
              navigator
                .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Do you know the consignee eori page when there are no answers in Header Trader Details" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsCommodityCodeKnownPage(index), false)
                .success
                .value
                .remove(ConsignorForAllItemsPage)
                .success
                .value
                .remove(AddConsignorPage)
                .success
                .value
                .remove(ConsigneeForAllItemsPage)
                .success
                .value
                .remove(AddConsigneePage)
                .success
                .value
              navigator
                .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Do you know the consignee eori page when Consignor is the same but Consignee is not for all items" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsCommodityCodeKnownPage(index), false)
                .success
                .value
                .set(ConsignorForAllItemsPage, true)
                .success
                .value
                .remove(AddConsignorPage)
                .success
                .value
                .set(ConsigneeForAllItemsPage, false)
                .success
                .value
                .set(AddConsigneePage, false)
                .success
                .value
              navigator
                .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Do you know the consignee eori page when there is a Consignor for all but Consignee is not for all items" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsCommodityCodeKnownPage(index), false)
                .success
                .value
                .set(ConsignorForAllItemsPage, false)
                .success
                .value
                .set(AddConsignorPage, true)
                .success
                .value
                .set(ConsigneeForAllItemsPage, false)
                .success
                .value
                .set(AddConsigneePage, false)
                .success
                .value
              navigator
                .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Package Type page when there is a Consignor for all and Consignee is the same for all items" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsCommodityCodeKnownPage(index), false)
                .success
                .value
                .set(ConsignorForAllItemsPage, false)
                .success
                .value
                .set(AddConsignorPage, true)
                .success
                .value
                .set(ConsigneeForAllItemsPage, true)
                .success
                .value
                .remove(AddConsigneePage)
                .success
                .value
              navigator
                .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, Index(0), NormalMode))
          }
        }

        "Package Type page when Consignor and Consignee is the same for all items" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(IsCommodityCodeKnownPage(index), false)
                .success
                .value
                .set(ConsignorForAllItemsPage, true)
                .success
                .value
                .remove(AddConsignorPage)
                .success
                .value
                .set(ConsigneeForAllItemsPage, true)
                .success
                .value
                .remove(AddConsigneePage)
                .success
                .value
              navigator
                .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, Index(0), NormalMode))
          }
        }
      }

      "must go from IsCommodityCodeKnownPage to CommodityCodePage if the answer is 'Yes'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true)
              .success
              .value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.CommodityCodeController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from CommodityCodePage to" - {

        "Do you know the consignor eori page when Consignor and Consignee is not the same for all items" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ConsignorForAllItemsPage, false)
                .success
                .value
                .set(AddConsignorPage, false)
                .success
                .value
                .set(ConsigneeForAllItemsPage, false)
                .success
                .value
                .set(AddConsigneePage, false)
                .success
                .value
              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Do you know the consignor eori page when there are no answers in Header Trader Details" ignore {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .remove(ConsignorForAllItemsPage)
                .success
                .value
                .remove(AddConsignorPage)
                .success
                .value
                .remove(ConsigneeForAllItemsPage)
                .success
                .value
                .remove(AddConsigneePage)
                .success
                .value
              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Do you know the consignee eori page when Consignor is the same but Consignee is not for all items" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ConsignorForAllItemsPage, true)
                .success
                .value
                .remove(AddConsignorPage)
                .success
                .value
                .set(ConsigneeForAllItemsPage, false)
                .success
                .value
                .set(AddConsigneePage, false)
                .success
                .value
              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Do you know the consignee eori page when there is a Consignor for all but Consignee is not for all items" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ConsignorForAllItemsPage, false)
                .success
                .value
                .set(AddConsignorPage, true)
                .success
                .value
                .set(ConsigneeForAllItemsPage, false)
                .success
                .value
                .set(AddConsigneePage, false)
                .success
                .value
              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Package Type page when there is a Consignor for all and Consignee is the same for all items" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ConsignorForAllItemsPage, false)
                .success
                .value
                .set(AddConsignorPage, true)
                .success
                .value
                .set(ConsigneeForAllItemsPage, true)
                .success
                .value
                .remove(AddConsigneePage)
                .success
                .value
              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, Index(0), NormalMode))
          }
        }

        "Package Type page when Consignor and Consignee is the same for all items" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ConsignorForAllItemsPage, true)
                .success
                .value
                .remove(AddConsignorPage)
                .success
                .value
                .set(ConsigneeForAllItemsPage, true)
                .success
                .value
                .remove(AddConsigneePage)
                .success
                .value
              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, Index(0), NormalMode))
          }
        }
      }

      //Trader details
      "Trader Details" - {
        //Consignor
        "must go from ConsignorEoriKnown to" - {
          "ConsignorEoriNumber when true" ignore {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage(index), true)
                  .success
                  .value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
          "ConsignorName when false" ignore {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage(index), false)
                  .success
                  .value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
          //TODO: Add more specs for consignorEoriKnown navigation
        }

        "must go from ConsignorEoriNumber to" - {
          "TraderDetailsConsigneeEoriKnownController when Consignee for all, and Consignee is user are 'False'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(ConsigneeForAllItemsPage, false)
                  .success
                  .value
                  .set(AddConsigneePage, false)
                  .success
                  .value
                navigator
                  .nextPage(TraderDetailsConsignorEoriNumberPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
            }
          }
          "TraderDetailsConsigneeEoriKnownController when Header Consignee questions not answered" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .remove(ConsigneeForAllItemsPage)
                  .success
                  .value
                  .remove(AddConsigneePage)
                  .success
                  .value
                navigator
                  .nextPage(TraderDetailsConsignorEoriNumberPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
            }
          }
          //TODO: Add more specs for consignorEoriNumber navigation
        }

        "must go from ConsignorName to ConsignorAddress" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorNamePage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from ConsignorAddress to AddItemsSameConsigneeForAllItems" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(ConsignorForAllItemsPage, false)
                .success
                .value
                .set(AddConsignorPage, false)
                .success
                .value
                .set(ConsigneeForAllItemsPage, false)
                .success
                .value
                .set(AddConsigneePage, false)
                .success
                .value
              navigator
                .nextPage(TraderDetailsConsignorAddressPage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        //Consignee
        "must go from AddItemsSameConsigneeForAllItems to" - {
          "PackageType when All items same Consignor and Consignee true " in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage(index), true)
                  .success
                  .value
                  .set(AddItemsSameConsigneeForAllItemsPage(index), true)
                  .success
                  .value
                navigator
                  .nextPage(AddItemsSameConsigneeForAllItemsPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(updatedAnswers.id, index, Index(0), NormalMode))
            }
          }

          "AddItems CYA when AddItemsSameConsignorForAllItems is false" in {
            (forAll(arbitrary[UserAnswers], arbitrary[Boolean])) {
              (answers, addItemsSameConsigneeForAllItems) =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsignorForAllItemsPage(index), false)
                  .success
                  .value
                  .set(AddItemsSameConsigneeForAllItemsPage(index), addItemsSameConsigneeForAllItems)
                  .success
                  .value
                navigator
                  .nextPage(AddItemsSameConsigneeForAllItemsPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }

          "AddItems CYA when AddItemsSameConsignorForAllItems is true but AddItemsSameConsigneeForAllItems is false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddItemsSameConsigneeForAllItemsPage(index), false)
                  .success
                  .value
                navigator
                  .nextPage(AddItemsSameConsigneeForAllItemsPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }
        }

        "must go from ConsigneeEoriKnown to" - {
          "ConsigneeEoriNumber when true" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsigneeEoriKnownPage(index), true)
                  .success
                  .value
                navigator
                  .nextPage(TraderDetailsConsigneeEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }

          "ConsigneeName when false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsigneeEoriKnownPage(index), false)
                  .success
                  .value
                navigator
                  .nextPage(TraderDetailsConsigneeEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
        }

        "must go from ConsigneeEoriNumber to Package Type" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsigneeEoriNumberPage(index), NormalMode, answers)
                .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, Index(0), NormalMode))
          }
        }

        "must go from ConsigneeName to ConsigneeAddress" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsigneeNamePage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsigneeAddressController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from ConsigneeAddress to ItemsCYA" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsigneeAddressPage(index), NormalMode, answers)
                .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, Index(0), NormalMode))
          }
        }
      }

      "PackageJourney" - {

        "PackageType" - {

          "must go to HowManyPackages when PackageType code isn't bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to DeclareNumberOfPackages when PackageType code is bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkOrUnpackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.DeclareNumberOfPackagesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
        }

        "HowManyPackages" - {

          "must go to DeclareMark when PackageType code isn't bulk or unpacked" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType], arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

        }

        "DeclareNumberOfPackages" - {
          "must go to HowManyPackages if answer is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(DeclareNumberOfPackagesPage(index, index), true)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
          "must go to AddMark if answer is 'No' and PackageType is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(DeclareNumberOfPackagesPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
          "must go to TotalPieces if answer is 'No' and PackageType is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(DeclareNumberOfPackagesPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

        }

        "TotalPieces" - {
          "must go to AddMark" in {
            forAll(arbitrary[UserAnswers], arbitrary[Int]) {
              (answers, totalPieces) =>
                val updatedAnswers = answers
                  .set(TotalPackagesPage, totalPieces)
                  .success
                  .value

                navigator
                  .nextPage(TotalPiecesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
        }

        "AddMark" - {
          "must go to DeclareMark if answers is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), true)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
          "must go to AddAnotherPackage if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, NormalMode))
            }
          }
        }

        "DeclareMark" - {
          "must go to AddAnotherPackage" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, declareMark) =>
                val updatedAnswers = answers
                  .set(DeclareMarkPage(index, index), declareMark)
                  .success
                  .value

                navigator
                  .nextPage(DeclareMarkPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, NormalMode))
            }
          }
        }

        "AddAnotherPackage" - {
          "must go to PackageType if the answer is 'Yes' and increment package index" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(AddAnotherPackagePage(index), true)
                  .success
                  .value

                val nextPackageIndex = Index(index.position + 1)

                navigator
                  .nextPage(AddAnotherPackagePage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, nextPackageIndex, NormalMode))
            }
          }
        }

        "RemovePackage" - {

          "must go to AddAnotherPackage page when 'No' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(AddAnotherPackagePage(index), true)
                  .success
                  .value
                  .set(RemovePackagePage(index), false)
                  .success
                  .value
                navigator
                  .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go to AddAnotherPackage page when 'Yes' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(AddAnotherPackagePage(index), true)
                  .success
                  .value
                  .set(RemovePackagePage(index), true)
                  .success
                  .value
                navigator
                  .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go to PackageType page when 'Yes' is selected and all the packages are removed" in {
            val updatedAnswers = emptyUserAnswers
              .remove(PackagesQuery(index, index))
              .success
              .value
              .set(RemovePackagePage(index), true)
              .success
              .value
            navigator
              .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
              .mustBe(routes.PackageTypeController.onPageLoad(updatedAnswers.id, index, index, NormalMode))
          }
        }

      }

      "previous references journey" - {
        "must go from add another document page to add administrative reference page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(DeclarationTypePage, DeclarationType.Option1)
                .success
                .value
                .set(CountryOfDispatchPage, CountryCode("UK"))
                .success
                .value

              navigator
                .nextPage(DummyPage(index, referenceIndex), NormalMode, updatedAnswers)
                .mustBe(previousReferenceRoutes.AddAdministrativeReferenceController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from add another document page to reference type page" - {
          "when declaration type is T2 and dispatch country is non-EU" in {
            val dispatchCountries =
              Seq(CountryCode("AD"), CountryCode("IS"), CountryCode("LI"), CountryCode("NO"), CountryCode("SM"), CountryCode("SJ"), CountryCode("CH"))
            for (countryCode <- dispatchCountries) {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers
                    .set(DeclarationTypePage, DeclarationType.Option2)
                    .success
                    .value
                    .set(CountryOfDispatchPage, countryCode)
                    .success
                    .value

                  navigator
                    .nextPage(DummyPage(index, referenceIndex), NormalMode, updatedAnswers)
                    .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, referenceIndex, NormalMode))
              }
            }
          }
        }

        "must go from 'add administrative reference' page to 'reference type' page when selected 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .remove(PreviousReferencesQuery(index))
                .success
                .value
                .set(AddAdministrativeReferencePage(index), true)
                .success
                .value

              navigator
                .nextPage(AddAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, referenceIndex, NormalMode))
          }
        }

        "must go from 'add administrative reference' page to 'user selected yes for safety and security' page when selected 'No'" ignore {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddAdministrativeReferencePage(index), false)
                .success
                .value

              navigator
                .nextPage(AddAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                .mustBe(???) // TODO need to replace with  user selected yes for safety and security
          }
        }

        "must go from 'reference-type page' to 'previous reference' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ReferenceTypePage(index, referenceIndex), NormalMode, answers)
                .mustBe(previousReferenceRoutes.PreviousReferenceController.onPageLoad(answers.id, index, referenceIndex, NormalMode))
          }
        }

        "must go from 'previous reference' page to 'add extra information' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(PreviousReferencePage(index, referenceIndex), NormalMode, answers)
                .mustBe(previousReferenceRoutes.AddExtraInformationController.onPageLoad(answers.id, index, referenceIndex, NormalMode))
          }
        }

        "must go from 'add extra information' page to 'extra information' page on selecting 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), true).success.value

              navigator
                .nextPage(AddExtraInformationPage(index, referenceIndex), NormalMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.ExtraInformationController.onPageLoad(answers.id, index, referenceIndex, NormalMode))
          }
        }

        "must go from 'add extra information' page to 'Add another reference' page on selecting 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), false).success.value

              navigator
                .nextPage(AddExtraInformationPage(index, referenceIndex), NormalMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from 'extra information' page to 'Add another reference' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(ExtraInformationPage(index, referenceIndex), "text").success.value

              navigator
                .nextPage(ExtraInformationPage(index, referenceIndex), NormalMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "AddAnotherPreviousAdministrativeReferencePage" - {
          "must go to ReferenceType page when user selects 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .remove(PreviousReferencesQuery(index))
                  .success
                  .value
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), true)
                  .success
                  .value

                navigator
                  .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), NormalMode, updatedAnswer)
                  .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to ReferenceType page when user selects 'No'" ignore {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .remove(PreviousReferencesQuery(index))
                  .success
                  .value
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), false)
                  .success
                  .value

                navigator
                  .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), NormalMode, updatedAnswer)
                  .mustBe(???) //TODO must got to safety and security journey
            }
          }

        }

        "must go from AddAnotherItem page to" - {

          "ItemDescription page if the answer is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers.set(AddAnotherItemPage, false).success.value
                navigator
                  .nextPage(AddAnotherItemPage, NormalMode, updatedAnswer)
                  .mustBe(mainRoutes.DeclarationSummaryController.onPageLoad(answers.id))
            }
          }

          "task list page if the answer is 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .set(AddAnotherItemPage, true)
                  .success
                  .value
                  .set(ItemDescriptionPage(index), "test")
                  .success
                  .value

                navigator
                  .nextPage(AddAnotherItemPage, NormalMode, updatedAnswer)
                  .mustBe(routes.ItemDescriptionController.onPageLoad(answers.id, Index(1), NormalMode))
            }
          }
        }

        "must go from ConfirmRemoveItem page to " - {

          "AddAnotherItem page when 'No' is selected and there are more than one item" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(ItemDescriptionPage(index), "item1")
                  .success
                  .value
                  .set(ItemDescriptionPage(Index(1)), "item2")
                  .success
                  .value
                  .set(AddAnotherItemPage, true)
                  .success
                  .value
                  .set(ConfirmRemoveItemPage, false)
                  .success
                  .value
                navigator
                  .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.id))
            }
          }

          "AddAnotherItem page when 'Yes' is selected and there are more than one item" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(ItemDescriptionPage(index), "item1")
                  .success
                  .value
                  .set(ItemDescriptionPage(Index(1)), "item2")
                  .success
                  .value
                  .set(ConfirmRemoveItemPage, true)
                  .success
                  .value
                navigator
                  .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.id))
            }
          }

          "ItemDescription page when 'Yes' is selected and when all the items are removed" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = emptyUserAnswers
                  .remove(ItemsQuery(index))
                  .success
                  .value
                  .set(ConfirmRemoveItemPage, true)
                  .success
                  .value
                navigator
                  .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                  .mustBe(routes.ItemDescriptionController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
        }

        "ConfirmRemovePreviousAdministrativeReference page" - {
          "must go to AddAnotherPreviousAdministrativeReference page" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .set(ReferenceTypePage(index, referenceIndex), "T1")
                  .success
                  .value
                  .set(ReferenceTypePage(index, Index(1)), "T1")
                  .success
                  .value
                  .set(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), true)
                  .success
                  .value
                navigator
                  .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), NormalMode, updatedAnswer)
                  .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go to reference type page when there are no previous references" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .remove(PreviousReferencesQuery(index))
                  .success
                  .value
                  .set(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), true)
                  .success
                  .value

                navigator
                  .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), NormalMode, updatedAnswer)
                  .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
        }
      }
    }

    "in check mode" - {

      "must go from item description page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ItemDescriptionPage(index), CheckMode, answers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from total grass mass page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ItemTotalGrossMassPage(index), "100").success.value
            navigator
              .nextPage(ItemTotalGrossMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from total net mass page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(TotalNetMassPage(index), "100").success.value
            navigator
              .nextPage(TotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from add total net mass page to total net mass page if the answer is 'Yes' and no previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true)
              .success
              .value
              .remove(TotalNetMassPage(index))
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.TotalNetMassController.onPageLoad(answers.id, index, CheckMode))
        }
      }

      "must go from add total net mass page to CYA page if the answer is 'Yes' and previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true)
              .success
              .value
              .set(TotalNetMassPage(index), "100.123")
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from add total net mass page to CYA page if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false)
              .success
              .value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from commodity code page page to Check Your Answers" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CommodityCodePage(index), CheckMode, answers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from IsCommodityCodeKnownPage to CYA page if the answer is 'Yes' and previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true)
              .success
              .value
              .set(CommodityCodePage(index), "111111")
              .success
              .value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from IsCommodityCodeKnownPage to CommodityCodePage if the answer is 'Yes' and no previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true)
              .success
              .value
              .remove(CommodityCodePage(index))
              .success
              .value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.CommodityCodeController.onPageLoad(answers.id, index, CheckMode))
        }
      }

      "must go from IsCommodityCodeKnownPage to CYA if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), false)
              .success
              .value
              .remove(CommodityCodePage(index))
              .success
              .value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

//      //Trader details
//      "Trader Details" - {
//        //Consignor
//        "must go from addItemsSameConsignorForAllItems to" - {
//          "AddItemsSameConsigneeForAllItems when true" in {
//            forAll(arbitrary[UserAnswers]) {
//              answers =>
//                val updatedAnswers = answers
//                  .set(AddItemsSameConsignorForAllItemsPage(index), true)
//                  .success
//                  .value
//                navigator
//                  .nextPage(AddItemsSameConsignorForAllItemsPage(index), CheckMode, updatedAnswers)
//                  .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(updatedAnswers.id, index, CheckMode))
//            }
//          }
//
//          "ConsignorEoriKnown when false and ConsignorEoriKnown is empty" in {
//            forAll(arbitrary[UserAnswers]) {
//              answers =>
//                val updatedAnswers = answers
//                  .set(AddItemsSameConsignorForAllItemsPage(index), false)
//                  .success
//                  .value
//                  .remove(TraderDetailsConsignorEoriKnownPage(index))
//                  .success
//                  .value
//                navigator
//                  .nextPage(AddItemsSameConsignorForAllItemsPage(index), CheckMode, updatedAnswers)
//                  .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(updatedAnswers.id, index, CheckMode))
//            }
//          }
//
//          "Items CYA when false and ConsignorEoriKnown is answered" in {
//            forAll(arbitrary[UserAnswers]) {
//              answers =>
//                val updatedAnswers = answers
//                  .set(AddItemsSameConsignorForAllItemsPage(index), false)
//                  .success
//                  .value
//                  .set(TraderDetailsConsignorEoriNumberPage(index), eoriNumber.value)
//                  .success
//                  .value
//                navigator
//                  .nextPage(AddItemsSameConsignorForAllItemsPage(index), CheckMode, updatedAnswers)
//                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
//            }
//          }
//
//          "must go from ConsignorEoriKnown to" - {
//            "ConsignorEoriNumber when true and EoriNumber is empty" in {
//              forAll(arbitrary[UserAnswers]) {
//                answers =>
//                  val updatedAnswers = answers
//                    .set(TraderDetailsConsignorEoriKnownPage(index), true)
//                    .success
//                    .value
//                    .remove(TraderDetailsConsignorEoriNumberPage(index))
//                    .success
//                    .value
//                  navigator
//                    .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
//                    .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.id, index, CheckMode))
//              }
//            }
//
//            "Items CYA when true and EoriNumber is answered" in {
//              forAll(arbitrary[UserAnswers]) {
//                answers =>
//                  val updatedAnswers = answers
//                    .set(TraderDetailsConsignorEoriKnownPage(index), true)
//                    .success
//                    .value
//                    .set(TraderDetailsConsignorEoriNumberPage(index), eoriNumber.value)
//                    .success
//                    .value
//                  navigator
//                    .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
//                    .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
//              }
//            }
//
//            "ConsignorName when false and consignorName is empty" in {
//              forAll(arbitrary[UserAnswers]) {
//                answers =>
//                  val updatedAnswers = answers
//                    .set(TraderDetailsConsignorEoriKnownPage(index), false)
//                    .success
//                    .value
//                    .remove(TraderDetailsConsignorNamePage(index))
//                    .success
//                    .value
//                  navigator
//                    .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
//                    .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
//              }
//            }
//
//            "Items CYA when false and ConsignorName is answered" in { //todo: recheck this logic when we merge with packages
//              forAll(arbitrary[UserAnswers]) {
//                answers =>
//                  val updatedAnswers = answers
//                    .set(TraderDetailsConsignorEoriKnownPage(index), false)
//                    .success
//                    .value
//                    .set(TraderDetailsConsignorNamePage(index), "name")
//                    .success
//                    .value
//                  navigator
//                    .nextPage(TraderDetailsConsignorEoriKnownPage(index), CheckMode, updatedAnswers)
//                    .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
//              }
//            }
//          }
//
//          "must go from ConsignorEoriNumber to Items CYA" in {
//            forAll(arbitrary[UserAnswers]) {
//              answers =>
//                navigator
//                  .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, answers)
//                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
//            }
//          }
//
//          "must go from ConsignorName to" - {
//            "ConsignorAddress when Address is empty" in {
//              forAll(arbitrary[UserAnswers]) {
//                answers =>
//                  val userAnswers = answers
//                    .remove(TraderDetailsConsignorAddressPage(index))
//                    .success
//                    .value
//                  navigator
//                    .nextPage(TraderDetailsConsignorNamePage(index), CheckMode, userAnswers)
//                    .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(userAnswers.id, index, CheckMode))
//              }
//            }
//
//            //              "Items CYA when Address is Populated" ignore {
//            //                forAll(arbitrary[UserAnswers]) {
//            //                  answers =>
//            //                    val userAnswers = answers
//            //                      .set(TraderDetailsConsignorAddressPage(index), "address").success.value //todo: move to correct model when page completed
//            //                    navigator
//            //                      .nextPage(TraderDetailsConsignorNamePage(index), CheckMode, userAnswers)
//            //                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
//            //                }
//            //              }
//
//            "must go from ConsignorAddress to Items CYA" in {
//              forAll(arbitrary[UserAnswers]) {
//                answers =>
//                  navigator
//                    .nextPage(TraderDetailsConsignorAddressPage(index), CheckMode, answers)
//                    .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
//              }
//            }
//          }
//
//          //Consignee
//          "must go from AddItemsSameConsigneeForAllItems to" - {
//            "PackageType when All items same Consignor and Consignee true " in { //todo: move to correct model when page completed
//              forAll(arbitrary[UserAnswers]) {
//                answers =>
//                  val updatedAnswers = answers
//                    .set(AddItemsSameConsignorForAllItemsPage(index), true)
//                    .success
//                    .value
//                    .set(AddItemsSameConsigneeForAllItemsPage(index), true)
//                    .success
//                    .value
//                  navigator
//                    .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, updatedAnswers)
//                    .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(updatedAnswers.id, index, CheckMode))
//              }
//            }
//
//            "AddItems CYA when AddItemsSameConsignorForAllItems is false" in {
//              (forAll(arbitrary[UserAnswers], arbitrary[Boolean])) {
//                (answers, addItemsSameConsigneeForAllItems) =>
//                  val updatedAnswers = answers
//                    .set(AddItemsSameConsignorForAllItemsPage(index), false)
//                    .success
//                    .value
//                    .set(AddItemsSameConsigneeForAllItemsPage(index), addItemsSameConsigneeForAllItems)
//                    .success
//                    .value
//                  navigator
//                    .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, updatedAnswers)
//                    .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
//              }
//            }
//
//            "AddItems CYA when AddItemsSameConsignorForAllItems is true but AddItemsSameConsigneeForAllItems is false" in {
//              forAll(arbitrary[UserAnswers]) {
//                answers =>
//                  val updatedAnswers = answers
//                    .set(AddItemsSameConsigneeForAllItemsPage(index), false)
//                    .success
//                    .value
//                  navigator
//                    .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, updatedAnswers)
//                    .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
//              }
//            }
//          }
//
//            "must go from ConsigneeEoriKnown to" - {
//              "ConsigneeEoriNumber when true and ConsigneeEoriNumber is empty" in {
//                forAll(arbitrary[UserAnswers]) {
//                  answers =>
//                    val updatedAnswers = answers
//                      .set(TraderDetailsConsigneeEoriKnownPage(index), true)
//                      .success
//                      .value
//                      .remove(TraderDetailsConsigneeEoriNumberPage(index))
//                      .success
//                      .value
//                    navigator
//                      .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
//                      .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.id, index, CheckMode))
//                }
//              }
//
//              "Items CYA when true and ConsigneeEoriNumber is populated" in {
//                forAll(arbitrary[UserAnswers]) {
//                  answers =>
//                    val updatedAnswers = answers
//                      .set(TraderDetailsConsigneeEoriKnownPage(index), true)
//                      .success
//                      .value
//                      .set(TraderDetailsConsigneeEoriNumberPage(index), eoriNumber.value)
//                      .success
//                      .value
//                    navigator
//                      .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
//                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
//                }
//
//              }
//
//              "ConsigneeName when false and ConsigneeName is empty" in {
//                forAll(arbitrary[UserAnswers]) {
//                  answers =>
//                    val updatedAnswers = answers
//                      .set(TraderDetailsConsigneeEoriKnownPage(index), false)
//                      .success
//                      .value
//                      .remove(TraderDetailsConsigneeNamePage(index))
//                      .success
//                      .value
//                    navigator
//                      .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
//                      .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
//                }
//              }
//
//              "Items CYA when false and ConsigneeName is empty" in {
//                forAll(arbitrary[UserAnswers]) {
//                  answers =>
//                    val updatedAnswers = answers
//                      .set(TraderDetailsConsigneeEoriKnownPage(index), false)
//                      .success
//                      .value
//                      .remove(TraderDetailsConsigneeNamePage(index))
//                      .success
//                      .value
//                    navigator
//                      .nextPage(TraderDetailsConsigneeEoriKnownPage(index), CheckMode, updatedAnswers)
//                      .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
//                }
//              }
//            }
//
//              "must go from ConsigneeEoriNumber to" - { //todo: Check logic when packages added
//                "CYA" in {
//                  forAll(arbitrary[UserAnswers]) {
//                    answers =>
//                      navigator
//                        .nextPage(TraderDetailsConsigneeEoriNumberPage(index), CheckMode, answers)
//                        .mustBe(routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(answers.id, index, CheckMode))
//                  }
//                }
//              }
//
//              "must go from ConsigneeName to" - {
//                "ConsigneeAddress when address is empty" in {
//                  forAll(arbitrary[UserAnswers]) {
//                    answers =>
//                      val userAnswers = answers
//                        .remove(TraderDetailsConsigneeAddressPage(index))
//                        .success
//                        .value
//                      navigator
//                        .nextPage(TraderDetailsConsigneeNamePage(index), CheckMode, userAnswers)
//                        .mustBe(traderRoutes.TraderDetailsConsigneeAddressController.onPageLoad(userAnswers.id, index, CheckMode))
//                  }
//                }
//
//                "Items CYA when address is populated" ignore {
//                  forAll(arbitrary[UserAnswers]) {
//                    answers =>
//                      val userAnswers = answers
//                        .set(TraderDetailsConsigneeAddressPage(index), "address").success.value //todo: change to correct model
//                      navigator
//                        .nextPage(TraderDetailsConsigneeNamePage(index), CheckMode, userAnswers)
//                        .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
//                  }
//                }
//              }
//
//              "must go from ConsigneeAddress to ItemsCYA" in {
//                forAll(arbitrary[UserAnswers]) {
//                  answers =>
//                    navigator
//                      .nextPage(TraderDetailsConsigneeAddressPage(index), CheckMode, answers)
//                      .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
//                }
//              }
//            }
//          }

      "PackageJourney" - {

        "PackageType" - {

          "must go to HowManyPackages when PackageType code isn't bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to DeclareNumberOfPackages when PackageType code is bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkOrUnpackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.DeclareNumberOfPackagesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
        }
        "HowManyPackages" - {

          "must go to DeclareMark when PackageType code isn't bulk or unpacked" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType], arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(HowManyPackagesPage(index, index), howManyPackages)
                  .success
                  .value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

        }

        "DeclareNumberOfPackages" - {
          "must go to HowManyPackages if answer is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(DeclareNumberOfPackagesPage(index, index), true)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
          "must go to AddMark if answer is 'No' and PackageType is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(DeclareNumberOfPackagesPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
          "must go to TotalPieces if answer is 'No' and PackageType is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(DeclareNumberOfPackagesPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

        }

        "TotalPieces" - {
          "must go to AddMark" in {
            forAll(arbitrary[UserAnswers], arbitrary[Int]) {
              (answers, totalPieces) =>
                val updatedAnswers = answers
                  .set(TotalPackagesPage, totalPieces)
                  .success
                  .value

                navigator
                  .nextPage(TotalPiecesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
        }

        "AddMark" - {
          "must go to DeclareMark if answers is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), true)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
          "must go to CheckYourAnswers if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), false)
                  .success
                  .value

                navigator
                  .nextPage(AddMarkPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
            }
          }
        }

        "DeclareMark" - {
          "must go to CheckYourAnswers" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, declareMark) =>
                val updatedAnswers = answers
                  .set(DeclareMarkPage(index, index), declareMark)
                  .success
                  .value

                navigator
                  .nextPage(DeclareMarkPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
            }
          }
        }

        "AddAnotherPackage" - {
          "must go to PackageType if the answer is 'Yes' and increment package index" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(AddAnotherPackagePage(index), true)
                  .success
                  .value

                val nextPackageIndex = Index(index.position + 1)

                navigator
                  .nextPage(AddAnotherPackagePage(index), CheckMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, nextPackageIndex, CheckMode))
            }
          }
          "must go to CheckYourAnswers if'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAnotherPackagePage(index), false)
                  .success
                  .value

                navigator
                  .nextPage(AddAnotherPackagePage(index), CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
            }
          }
        }
      }

      "previous references journey" - {
        "must go from add administrative reference page to CYA page when selected 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.set(AddAdministrativeReferencePage(index), false).success.value
              navigator
                .nextPage(AddAdministrativeReferencePage(index), CheckMode, updatedAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
          }
        }

        "must go from add administrative reference page to reference type page when selected 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .remove(PreviousReferencesQuery(index))
                .success
                .value
                .set(AddAdministrativeReferencePage(index), true)
                .success
                .value
              navigator
                .nextPage(AddAdministrativeReferencePage(index), CheckMode, updatedAnswers)
                .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, referenceIndex, CheckMode))
          }
        }

        "must go from add administrative reference page to safety and security page when selected 'No' and mode is NormalMode" ignore {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.set(AddAdministrativeReferencePage(index), true).success.value
              navigator
                .nextPage(AddAdministrativeReferencePage(index), CheckMode, updatedAnswers)
                .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, referenceIndex, CheckMode)) //TODO must got to first page of safety n security
          }
        }

        "must go from 'reference-type page' to 'previous reference' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ReferenceTypePage(index, referenceIndex), CheckMode, answers)
                .mustBe(previousReferenceRoutes.PreviousReferenceController.onPageLoad(answers.id, index, referenceIndex, CheckMode))
          }
        }

        "must go from 'previous reference' page to 'add extra information' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(PreviousReferencePage(index, referenceIndex), CheckMode, answers)
                .mustBe(previousReferenceRoutes.AddExtraInformationController.onPageLoad(answers.id, index, referenceIndex, CheckMode))
          }
        }

        "must go from 'add extra information' page to 'extra information' page on selecting 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), true).success.value

              navigator
                .nextPage(AddExtraInformationPage(index, referenceIndex), CheckMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.ExtraInformationController.onPageLoad(answers.id, index, referenceIndex, CheckMode))
          }
        }

        "must go from 'add extra information' page to 'Add another reference' page on selecting 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), false).success.value

              navigator
                .nextPage(AddExtraInformationPage(index, referenceIndex), CheckMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.id, index, CheckMode))
          }
        }

        "must go from 'extra information' page to 'Add another reference' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(ExtraInformationPage(index, referenceIndex), "text").success.value

              navigator
                .nextPage(ExtraInformationPage(index, referenceIndex), CheckMode, updatedAnswer)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
          }
        }

        "RemovePackage" - {

          "must go to AddAnotherPackage page when 'No' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(AddAnotherPackagePage(index), true)
                  .success
                  .value
                  .set(RemovePackagePage(index), false)
                  .success
                  .value
                navigator
                  .nextPage(RemovePackagePage(index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, CheckMode))
            }
          }

          "must go to AddAnotherPackage page when 'Yes' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(PackageTypePage(index, index), packageType)
                  .success
                  .value
                  .set(AddAnotherPackagePage(index), true)
                  .success
                  .value
                  .set(RemovePackagePage(index), true)
                  .success
                  .value
                navigator
                  .nextPage(RemovePackagePage(index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, CheckMode))
            }
          }

          "must go to PackageType page when 'Yes' is selected and all the packages are removed" in {
            val updatedAnswers = emptyUserAnswers
              .remove(PackagesQuery(index, index))
              .success
              .value
              .set(RemovePackagePage(index), true)
              .success
              .value
            navigator
              .nextPage(RemovePackagePage(index), CheckMode, updatedAnswers)
              .mustBe(routes.PackageTypeController.onPageLoad(updatedAnswers.id, index, index, CheckMode))
          }
        }
      }
    }
  }
}
