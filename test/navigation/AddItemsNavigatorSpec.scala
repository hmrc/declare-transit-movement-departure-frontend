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
import controllers.addItems.containers.{routes => containerRoutes}
import controllers.addItems.previousReferences.{routes => previousReferenceRoutes}
import controllers.addItems.routes
import controllers.addItems.specialMentions.{routes => specialMentionsRoutes}
import controllers.addItems.traderDetails.{routes => traderRoutes}
import controllers.{routes => mainRoutes}
import generators.Generators
import models.reference.PackageType
import models.{CheckMode, CommonAddress, Index, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import pages.addItems.containers._
import pages.addItems.traderDetails._
import pages.safetyAndSecurity.{AddCommercialReferenceNumberAllItemsPage, AddTransportChargesPaymentMethodPage}
import queries.{ContainersQuery, _}

class AddItemsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
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
              .set(AddTotalNetMassPage(index), true).success.value
              .remove(TotalNetMassPage(index)).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.TotalNetMassController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      "must go from add total net mass page to IsCommodityCodeKnownPage if the answer is 'No'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false).success.value
              .remove(TotalNetMassPage(index)).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), NormalMode, updatedAnswers)
              .mustBe(routes.IsCommodityCodeKnownController.onPageLoad(answers.id, index, NormalMode))
        }
      }

      //Commodity Code to Trader Details

      "must go from IsCommodityCodeKnownPage" - {
        "when the answer is 'No' to" - {
          "Consignor's Eori when there is no Consignor for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
                  .unsafeSetVal(AddConsignorPage)(false)
                  
                navigator
                  .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "Consignee's Eori when there is a Consignor for all items and no Consignee for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
                  .unsafeSetVal(AddConsignorPage)(true)
                  .unsafeSetVal(AddConsigneePage)(false)

                navigator
                  .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "Package type when there is  a Consignor for all items and a Consignee for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
                  .unsafeSetVal(AddConsignorPage)(true)
                  .unsafeSetVal(AddConsigneePage)(true)

                navigator
                  .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, Index(0), NormalMode))
            }
          }

        }

        "when the answer is 'Yes'" - {
          "to CommodityCode" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(IsCommodityCodeKnownPage(index), true).success.value
                navigator
                  .nextPage(IsCommodityCodeKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.CommodityCodeController.onPageLoad(answers.id, index, NormalMode))
            }
          }
        }
      }
      

      "must go from CommodityCodePage to" - {
        "Consignor's Eori when there is no Consignor for all items" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(false)

              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Consignee's Eori when there is a Consignor for all items and no Consignee for all items" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(AddConsigneePage)(false)

              navigator
                .nextPage(CommodityCodePage(index), NormalMode, updatedAnswers)
                .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "Package type when there is  a Consignor for all items and a Consignee for all items" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(AddConsigneePage)(true)

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
          "ConsignorEoriNumber when true" in {
            forAll(arbitrary[UserAnswers]) { 
              answers =>
                val updatedAnswers = answers
                  .remove(AddConsignorPage).success.value
                  .remove(AddConsigneePage).success.value
                  .set(TraderDetailsConsignorEoriKnownPage(index), true).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }

          "ConsignorName when false" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(TraderDetailsConsignorEoriKnownPage(index), false).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriKnownPage(index), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
        }

        "must go from ConsignorEoriNumber to ConsignorName" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorEoriNumberPage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from ConsignorName to ConsignorAddress" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsignorNamePage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from ConsignorAddress to" - {
          "Consignee's Eori when there is no Consignee for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(AddConsigneePage)(false)

                navigator
                  .nextPage(CommonAddItemsAddressPage(index, "traderDetailsConsignorAddress"), NormalMode, updatedAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "Package type when there is a Consignee for all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(AddConsigneePage)(true)

                navigator
                  .nextPage(CommonAddItemsAddressPage(index, "traderDetailsConsignorAddress"), NormalMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, Index(0), NormalMode))
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
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.id, index, NormalMode))
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
                  .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, NormalMode))
            }
          }
          
        }

        "must go from ConsigneeEoriNumber to Consignee Name" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TraderDetailsConsigneeEoriNumberPage(index), NormalMode, answers)
                .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(answers.id, index, NormalMode))
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

        "must go from ConsigneeAddress to Package Type" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(CommonAddItemsAddressPage(index, "traderDetailsConsigneeAddress"), NormalMode, answers)
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
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
        }

        "HowManyPackages" - {

          "must go to DeclareMark when PackageType code isn't bulk or unpacked" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType], arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(HowManyPackagesPage(index, index), howManyPackages).success.value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(HowManyPackagesPage(index, index), howManyPackages).success.value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(HowManyPackagesPage(index, index), howManyPackages).success.value

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
                  .set(DeclareNumberOfPackagesPage(index, index), true).success.value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
          "must go to AddMark if answer is 'No' and PackageType is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(DeclareNumberOfPackagesPage(index, index), false).success.value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
          "must go to TotalPieces if answer is 'No' and PackageType is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(DeclareNumberOfPackagesPage(index, index), false).success.value

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
                  .set(TotalPackagesPage, totalPieces).success.value

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
                  .set(AddMarkPage(index, index), true).success.value

                navigator
                  .nextPage(AddMarkPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }
          "must go to AddAnotherPackage if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), false).success.value

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
                  .set(DeclareMarkPage(index, index), declareMark).success.value

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
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(AddAnotherPackagePage(index), true).success.value

                val nextPackageIndex = Index(index.position + 1)

                navigator
                  .nextPage(AddAnotherPackagePage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, nextPackageIndex, NormalMode))
            }
          }

          "when no is answered must go to" - {

            "Add items CYA when no containers used selected" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers
                    .set(ContainersUsedPage, false).success.value
                    .set(AddAnotherPackagePage(itemIndex), false).success.value
                    .remove(ContainersQuery(itemIndex, containerIndex)).success.value
                  navigator
                    .nextPage(AddAnotherPackagePage(itemIndex), NormalMode, updatedAnswers)
                    .mustBe(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(updatedAnswers.id, itemIndex, NormalMode))
              }
            }

            "containerNumber when no containers exist" in {
              forAll(arbitrary[UserAnswers]) {
                answers =>
                  val updatedAnswers = answers
                    .set(ContainersUsedPage, true).success.value
                    .set(AddAnotherPackagePage(itemIndex), false).success.value
                    .remove(ContainersQuery(itemIndex, containerIndex)).success.value
                navigator
                  .nextPage(AddAnotherPackagePage(itemIndex), NormalMode, updatedAnswers)
                  .mustBe(containerRoutes.ContainerNumberController.onPageLoad(updatedAnswers.id, itemIndex, containerIndex, NormalMode))
              }
            }

            "addAnotherContainer when containers already exist" in {
              forAll(arbitrary[UserAnswers], arbitrary[String]) {
                (answers, containerNumber) =>
                  val updatedAnswers = answers
                    .set(ContainersUsedPage, true).success.value
                    .set(AddAnotherPackagePage(itemIndex), false).success.value
                    .set(ContainerNumberPage(itemIndex, containerIndex), containerNumber).success.value
                  navigator
                    .nextPage(AddAnotherPackagePage(itemIndex), NormalMode, updatedAnswers)
                    .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswers.id, itemIndex, NormalMode))

              }
            }
          }
        }

        "containerNumber" - {
          "must go from containerNumber to addAnotherContainer" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, containerNumber) =>
                val updatedAnswers = answers
                  .set(ContainerNumberPage(itemIndex, containerIndex), containerNumber).success.value
                navigator
                  .nextPage(ContainerNumberPage(itemIndex, containerIndex), NormalMode, updatedAnswers)
                  .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswers.id, itemIndex, NormalMode))
            }
          }
        }

        "RemovePackage" - {

          "must go to AddAnotherPackage page when 'No' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(AddAnotherPackagePage(index), true).success.value
                  .set(RemovePackagePage(index), false).success.value
                navigator
                  .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go to AddAnotherPackage page when 'Yes' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(AddAnotherPackagePage(index), true).success.value
                  .set(RemovePackagePage(index), true).success.value
                navigator
                  .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go to PackageType page when 'Yes' is selected and all the packages are removed" in {
            val updatedAnswers = emptyUserAnswers
              .remove(PackagesQuery(index, index)).success.value
              .set(RemovePackagePage(index), true).success.value
            navigator
              .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
              .mustBe(routes.PackageTypeController.onPageLoad(updatedAnswers.id, index, index, NormalMode))
          }
        }

      }

      "previous references journey" - {

        "must go from 'add administrative reference' page to 'reference type' page when selected 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .remove(PreviousReferencesQuery(index)).success.value
                .set(AddAdministrativeReferencePage(index), true).success.value

              navigator
                .nextPage(AddAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, referenceIndex, NormalMode))
          }
        }

        "must go from 'add administrative reference' page to 'user selected yes for safety and security' page when selected 'No'" ignore {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddAdministrativeReferencePage(index), false).success.value

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
                  .remove(PreviousReferencesQuery(index)).success.value
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), true).success.value

                navigator
                  .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), NormalMode, updatedAnswer)
                  .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go to ReferenceType page when user selects 'No'" ignore {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .remove(PreviousReferencesQuery(index)).success.value
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), false).success.value

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
                  .set(AddAnotherItemPage, true).success.value
                  .set(ItemDescriptionPage(index), "test").success.value

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
                  .set(ItemDescriptionPage(index), "item1").success.value
                  .set(ItemDescriptionPage(Index(1)), "item2").success.value
                  .set(AddAnotherItemPage, true).success.value
                  .set(ConfirmRemoveItemPage, false).success.value
                navigator
                  .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.id))
            }
          }

          "AddAnotherItem page when 'Yes' is selected and there are more than one item" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(ItemDescriptionPage(index), "item1").success.value
                  .set(ItemDescriptionPage(Index(1)), "item2").success.value
                  .set(ConfirmRemoveItemPage, true).success.value
                navigator
                  .nextPage(ConfirmRemoveItemPage, NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.id))
            }
          }

          "ItemDescription page when 'Yes' is selected and when all the items are removed" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = emptyUserAnswers
                  .remove(ItemsQuery(index)).success.value
                  .set(ConfirmRemoveItemPage, true).success.value
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
                  .set(ReferenceTypePage(index, referenceIndex), "T1").success.value
                  .set(ReferenceTypePage(index, Index(1)), "T1").success.value
                  .set(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), true).success.value
                navigator
                  .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), NormalMode, updatedAnswer)
                  .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go to reference type page when there are no previous references" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .remove(PreviousReferencesQuery(index)).success.value
                  .set(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), true).success.value

                navigator
                  .nextPage(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), NormalMode, updatedAnswer)
                  .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, index, NormalMode))
            }
          }

          "must go from ConfirmRemoveContainerPage to AddAnotherContainer page when there is 1 container left" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .set(ContainerNumberPage(index, containerIndex),"").success.value
                navigator
                  .nextPage(ConfirmRemoveContainerPage(index, containerIndex), NormalMode, updatedAnswer)
                  .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswer.id, index, NormalMode))
            }
          }

          "must go from ConfirmRemoveContainerPage to ContainerNumber page when there are no containers left" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .remove(ContainersQuery(index, containerIndex)).success.value
                navigator
                  .nextPage(ConfirmRemoveContainerPage(index, containerIndex), NormalMode, updatedAnswer)
                  .mustBe(containerRoutes.ContainerNumberController.onPageLoad(updatedAnswer.id, index, Index(0), NormalMode))
            }
          }

          "must go from AddAnotherContainerPage to AddSpecialMentionController" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(AddAnotherContainerPage(index), NormalMode, answers)
                  .mustBe(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(answers.id, index, NormalMode))
            }
          }

          "must go from add administrative reference page to reference type page when selected 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .remove(PreviousReferencesQuery(index)).success.value
                  .set(AddAdministrativeReferencePage(index), true).success.value
                navigator
                  .nextPage(AddAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                  .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(updatedAnswers.id, index, referenceIndex, NormalMode))
            }
          }

          "must go from add administrative reference page to transport charges page when selected 'No' and not using same method of payment across all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAdministrativeReferencePage(index), false).success.value
                  .set(AddSecurityDetailsPage, true).success.value
                  .set(AddTransportChargesPaymentMethodPage, false).success.value
                navigator
                  .nextPage(AddAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                  .mustBe(controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(updatedAnswers.id,itemIndex, NormalMode))
            }
          }

          "must go from add administrative reference page to CYA page when selected 'No' and also selected 'No' for add safety and security" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAdministrativeReferencePage(index), false).success.value
                  .set(AddSecurityDetailsPage, false).success.value
                navigator
                  .nextPage(AddAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }

          "must go from add administrative reference page to Commercial Reference page when selected 'No' and also selected 'Yes' for add safety and security" +
            " and selected 'Yes' for add transport charges method of payment and 'No' for add commercial reference number across all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAdministrativeReferencePage(index), false).success.value
                  .set(AddSecurityDetailsPage, true).success.value
                  .set(AddTransportChargesPaymentMethodPage, true).success.value
                  .set(AddCommercialReferenceNumberAllItemsPage, false).success.value
                navigator
                  .nextPage(AddAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                  .mustBe(controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(updatedAnswers.id, itemIndex, NormalMode))
            }
          }

          "must go from add administrative reference page to Add Dangerous Goods page when selected 'No' and also selected 'Yes' for add safety and security" +
            " and selected 'Yes' for add transport charges method of payment and 'No' for add commercial reference number across all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAdministrativeReferencePage(index), false).success.value
                  .set(AddSecurityDetailsPage, true).success.value
                  .set(AddTransportChargesPaymentMethodPage, true).success.value
                  .set(AddCommercialReferenceNumberAllItemsPage, true).success.value
                navigator
                  .nextPage(AddAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                  .mustBe(controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(updatedAnswers.id, itemIndex, NormalMode))
            }
          }


          "must go from AddAnotherPreviousAdministrativeReferencePage to transport charges page when selected 'No' and not using same method of payment across all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), false).success.value
                  .set(AddSecurityDetailsPage, true).success.value
                  .set(AddTransportChargesPaymentMethodPage, false).success.value
                navigator
                  .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                  .mustBe(controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(updatedAnswers.id,itemIndex, NormalMode))
            }
          }

          "must go from AddAnotherPreviousAdministrativeReferencePage to CYA page when selected 'No' and also selected 'No' for add safety and security" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), false).success.value
                  .set(AddSecurityDetailsPage, false).success.value
                navigator
                  .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
            }
          }

          "must go from AddAnotherPreviousAdministrativeReferencePage to Commercial Reference page when selected 'No' and also selected 'Yes' for add safety and security" +
            " and selected 'Yes' for add transport charges method of payment and 'No' for add commercial reference number across all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), false).success.value
                  .set(AddSecurityDetailsPage, true).success.value
                  .set(AddTransportChargesPaymentMethodPage, true).success.value
                  .set(AddCommercialReferenceNumberAllItemsPage, false).success.value
                navigator
                  .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                  .mustBe(controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(updatedAnswers.id, itemIndex, NormalMode))
            }
          }

          "must go from AddAnotherPreviousAdministrativeReferencePage to Add Dangerous Goods page when selected 'No' and also selected 'Yes' for add safety and security" +
            " and selected 'Yes' for add transport charges method of payment and 'No' for add commercial reference number across all items" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), false).success.value
                  .set(AddSecurityDetailsPage, true).success.value
                  .set(AddTransportChargesPaymentMethodPage, true).success.value
                  .set(AddCommercialReferenceNumberAllItemsPage, true).success.value
                navigator
                  .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                  .mustBe(controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(updatedAnswers.id, itemIndex, NormalMode))
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
              .set(AddTotalNetMassPage(index), true).success.value
              .remove(TotalNetMassPage(index)).success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.TotalNetMassController.onPageLoad(answers.id, index, CheckMode))
        }
      }

      "must go from add total net mass page to CYA page if the answer is 'Yes' and previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), true).success.value
              .set(TotalNetMassPage(index), "100.123").success.value
            navigator
              .nextPage(AddTotalNetMassPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from add total net mass page to CYA page if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddTotalNetMassPage(index), false).success.value
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
              .set(IsCommodityCodeKnownPage(index), true).success.value
              .set(CommodityCodePage(index), "111111").success.value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

      "must go from IsCommodityCodeKnownPage to CommodityCodePage if the answer is 'Yes' and no previous answer exists" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), true).success.value
              .remove(CommodityCodePage(index)).success.value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.CommodityCodeController.onPageLoad(answers.id, index, CheckMode))
        }
      }

      "must go from IsCommodityCodeKnownPage to CYA if the answer is 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(IsCommodityCodeKnownPage(index), false).success.value
              .remove(CommodityCodePage(index)).success.value
            navigator
              .nextPage(IsCommodityCodeKnownPage(index), CheckMode, updatedAnswers)
              .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
        }
      }

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
                  .mustBe(traderRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(updatedAnswers.id, index, CheckMode))
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
                  .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
            }
          }

          "Consignor Name if Consignor Name is not populated" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers
                  .remove(TraderDetailsConsignorNamePage(index)).success.value
                navigator
                  .nextPage(TraderDetailsConsignorEoriNumberPage(index), CheckMode, userAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorNameController.onPageLoad(userAnswers.id, index, CheckMode))
            }
          }
        }

        "must go from ConsignorName to" - {
          "ConsignorAddress when Address is empty" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers
                  .remove(CommonAddItemsAddressPage(index, "traderDetailsConsignorAddress")).success.value
                navigator
                  .nextPage(TraderDetailsConsignorNamePage(index), CheckMode, userAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsignorAddressController.onPageLoad(userAnswers.id, index, CheckMode))
            }
          }

          "Items CYA when Address is Populated" in {
            forAll(arbitrary[UserAnswers], arbitrary[CommonAddress]) {
              (answers, address) =>
                val userAnswers = answers
                  .set(CommonAddItemsAddressPage(index, "traderDetailsConsignorAddress"), address).success.value
                navigator
                  .nextPage(TraderDetailsConsignorNamePage(index), CheckMode, userAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
            }
          }
        }

        "must go from ConsignorAddress to" - {
          "Items CYA" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(CommonAddItemsAddressPage(index, "traderDetailsConsignorAddress"), CheckMode, answers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
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
                  .mustBe(traderRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(updatedAnswers.id, index, CheckMode))
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
                  .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(updatedAnswers.id, index, CheckMode))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
            }
          }

          "Consignee Name if Consignee Name is not populated" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers
                  .unsafeRemove(TraderDetailsConsigneeNamePage(index))

                navigator
                  .nextPage(TraderDetailsConsigneeEoriNumberPage(index), CheckMode, userAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeNameController.onPageLoad(userAnswers.id, index, CheckMode))
            }
          }
        }

        "must go from ConsigneeName to" - {
          "ConsigneeAddress when address is empty" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers
                  .remove(CommonAddItemsAddressPage(index, "traderDetailsConsigneeAddress")).success.value
                navigator
                  .nextPage(TraderDetailsConsigneeNamePage(index), CheckMode, userAnswers)
                  .mustBe(traderRoutes.TraderDetailsConsigneeAddressController.onPageLoad(userAnswers.id, index, CheckMode))
            }
          }
          
          "Items CYA when address is populated" in {
            forAll(arbitrary[UserAnswers], arbitrary[CommonAddress]) {
              (answers, address) =>
                val userAnswers = answers
                  .set(CommonAddItemsAddressPage(index, "traderDetailsConsigneeAddress"), address).success.value
                navigator
                  .nextPage(TraderDetailsConsigneeNamePage(index), CheckMode, userAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
            }
          }
        }

        "must go from ConsigneeAddress to" - {
          "Items  CYA" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(CommonAddItemsAddressPage(index, "traderDetailsConsigneeAddress"), CheckMode, answers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
            }
          }
        }
      }

      "PackageJourney" - {

        "PackageType" - {

          "must go to HowManyPackages when PackageType code isn't bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to Add Marks when PackageType code is bulk" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
        }
        "HowManyPackages" - {

          "must go to DeclareMark when PackageType code isn't bulk or unpacked" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType], arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(HowManyPackagesPage(index, index), howManyPackages).success.value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(HowManyPackagesPage(index, index), howManyPackages).success.value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(HowManyPackagesPage(index, index), howManyPackages).success.value

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
                  .set(DeclareNumberOfPackagesPage(index, index), true).success.value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
          "must go to AddMark if answer is 'No' and PackageType is bulk" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(DeclareNumberOfPackagesPage(index, index), false).success.value

                navigator
                  .nextPage(DeclareNumberOfPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
          "must go to TotalPieces if answer is 'No' and PackageType is unpacked" in {
            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(DeclareNumberOfPackagesPage(index, index), false).success.value

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
                  .set(TotalPackagesPage, totalPieces).success.value

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
                  .set(AddMarkPage(index, index), true).success.value

                navigator
                  .nextPage(AddMarkPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.id, index, index, CheckMode))
            }
          }
          "must go to CheckYourAnswers if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), false).success.value

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
                  .set(DeclareMarkPage(index, index), declareMark).success.value

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
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(AddAnotherPackagePage(index), true).success.value

                val nextPackageIndex = Index(index.position + 1)

                navigator
                  .nextPage(AddAnotherPackagePage(index), CheckMode, updatedAnswers)
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.id, index, nextPackageIndex, CheckMode))
            }
          }
          "must go to CheckYourAnswers if'No' and there are containers and containers not used" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, container) =>
                val updatedAnswers = answers
                  .set(ContainersUsedPage, true).success.value
                  .set(AddAnotherPackagePage(itemIndex), false).success.value
                  .set(ContainerNumberPage(itemIndex, containerIndex), container).success.value
                navigator
                  .nextPage(AddAnotherPackagePage(itemIndex), CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, itemIndex))
            }
          }

          "must go to CheckYourAnswers if'No' and there are containers" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, container) =>
                val updatedAnswers = answers
                  .set(AddAnotherPackagePage(itemIndex), false).success.value
                  .set(ContainerNumberPage(itemIndex, containerIndex), container).success.value
                navigator
                  .nextPage(AddAnotherPackagePage(itemIndex), CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, itemIndex))
            }
          }


          "must go to ContainerNumber(0, 0) if'No' and there are NO containers" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(ContainersUsedPage, true).success.value
                  .set(AddAnotherPackagePage(itemIndex), false).success.value
                  .remove(ContainersQuery(itemIndex, containerIndex)).success.value
                navigator
                  .nextPage(AddAnotherPackagePage(itemIndex), CheckMode, updatedAnswers)
                  .mustBe(containerRoutes.ContainerNumberController.onPageLoad(answers.id, itemIndex, containerIndex, CheckMode))
            }
          }

        }
      }

      "containerNumber" - {
        "must go from containerNumber to addAnotherContainer" in {
          forAll(arbitrary[UserAnswers], arbitrary[String]) {
            (answers, containerNumber) =>
              val updatedAnswers = answers
                .set(ContainerNumberPage(itemIndex, containerIndex), containerNumber).success.value
              navigator
                .nextPage(ContainerNumberPage(itemIndex, containerIndex), CheckMode, updatedAnswers)
                .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswers.id, itemIndex, CheckMode))
          }
        }
      }

      "previous references journey" - {
        "must go from add administrative reference page to CYA page when selected 'No' and also selected 'No' for add safety and security" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddAdministrativeReferencePage(index), false).success.value
                .set(AddSecurityDetailsPage, false).success.value
              navigator
                .nextPage(AddAdministrativeReferencePage(index), CheckMode, updatedAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.id, index))
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

        "must go to Reference Type page when user selects 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers
                .remove(PreviousReferencesQuery(index)).success.value
                .set(AddAnotherPreviousAdministrativeReferencePage(index), true).success.value

              navigator
                .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), CheckMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.id, index, index, CheckMode))
          }
        }

        "must go to CYA page when user selects 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers
                .remove(PreviousReferencesQuery(index)).success.value
                .set(AddAnotherPreviousAdministrativeReferencePage(index), false).success.value

              navigator
                .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), CheckMode, updatedAnswer)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.id, index))
          }
        }

        "RemovePackage" - {

          "must go to AddAnotherPackage page when 'No' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(AddAnotherPackagePage(index), true).success.value
                  .set(RemovePackagePage(index), false).success.value
                navigator
                  .nextPage(RemovePackagePage(index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, CheckMode))
            }
          }

          "must go to AddAnotherPackage page when 'Yes' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(AddAnotherPackagePage(index), true).success.value
                  .set(RemovePackagePage(index), true).success.value
                navigator
                  .nextPage(RemovePackagePage(index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.id, index, CheckMode))
            }
          }

          "must go to PackageType page when 'Yes' is selected and all the packages are removed" in {
            val updatedAnswers = emptyUserAnswers
              .remove(PackagesQuery(index, index)).success.value
              .set(RemovePackagePage(index), true).success.value
            navigator
              .nextPage(RemovePackagePage(index), CheckMode, updatedAnswers)
              .mustBe(routes.PackageTypeController.onPageLoad(updatedAnswers.id, index, index, CheckMode))
          }
        }

        "must go from ConfirmRemoveContainerPage to AddAnotherContainer page when there is 1 container left" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers
                .set(ContainerNumberPage(index, containerIndex),"").success.value
              navigator
                .nextPage(ConfirmRemoveContainerPage(index, containerIndex), CheckMode, updatedAnswer)
                .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswer.id, index, CheckMode))
          }
        }

        "must go from ConfirmRemoveContainerPage to ContainerNumber page when there are no containers left" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers
                .remove(ContainersQuery(index, containerIndex)).success.value
              navigator
                .nextPage(ConfirmRemoveContainerPage(index, containerIndex), CheckMode, updatedAnswer)
                .mustBe(containerRoutes.ContainerNumberController.onPageLoad(updatedAnswer.id, index, Index(0), CheckMode))
          }
        }
      }
    }
  }
  // format: on
}
