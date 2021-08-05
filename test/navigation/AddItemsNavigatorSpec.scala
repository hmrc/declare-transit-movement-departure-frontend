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

      "PackageJourney" - {

        "PackageType" - {

          "must go to HowManyPackages when PackageType code isn't bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.lrn, index, index, NormalMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.lrn, index, index, NormalMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.lrn, index, index, NormalMode))
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
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.lrn, index, index, NormalMode))
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
                  .mustBe(routes.AddMarkController.onPageLoad(answers.lrn, index, index, NormalMode))
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
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.lrn, index, index, NormalMode))
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
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.lrn, index, index, NormalMode))
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
                  .mustBe(routes.AddMarkController.onPageLoad(answers.lrn, index, index, NormalMode))
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
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.lrn, index, index, NormalMode))
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
                  .mustBe(routes.AddMarkController.onPageLoad(answers.lrn, index, index, NormalMode))
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
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.lrn, index, index, NormalMode))
            }
          }
          "must go to AddAnotherPackage if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), false).success.value

                navigator
                  .nextPage(AddMarkPage(index, index), NormalMode, updatedAnswers)
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.lrn, index, NormalMode))
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
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.lrn, index, NormalMode))
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
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.lrn, index, nextPackageIndex, NormalMode))
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
                    .mustBe(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
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
                  .mustBe(containerRoutes.ContainerNumberController.onPageLoad(updatedAnswers.lrn, itemIndex, containerIndex, NormalMode))
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
                    .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))

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
                  .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
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
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.lrn, index, NormalMode))
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
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.lrn, index, NormalMode))
            }
          }

          "must go to PackageType page when 'Yes' is selected and all the packages are removed" in {
            val updatedAnswers = emptyUserAnswers
              .remove(PackagesQuery(index, index)).success.value
              .set(RemovePackagePage(index), true).success.value
            navigator
              .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
              .mustBe(routes.PackageTypeController.onPageLoad(updatedAnswers.lrn, index, index, NormalMode))
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
                .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.lrn, index, referenceIndex, NormalMode))
          }
        }

        "must go from 'add administrative reference' page to AddItemsCheckYourAnswers Page when 'user selected 'No' " in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .set(AddAdministrativeReferencePage(index), false).success.value
                .set(AddSecurityDetailsPage, false).success.value

              navigator
                .nextPage(AddAdministrativeReferencePage(index), NormalMode, updatedAnswers)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
          }
        }

        "must go from 'reference-type page' to 'previous reference' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ReferenceTypePage(index, referenceIndex), NormalMode, answers)
                .mustBe(previousReferenceRoutes.PreviousReferenceController.onPageLoad(answers.lrn, index, referenceIndex, NormalMode))
          }
        }

        "must go from 'previous reference' page to 'add extra information' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(PreviousReferencePage(index, referenceIndex), NormalMode, answers)
                .mustBe(previousReferenceRoutes.AddExtraInformationController.onPageLoad(answers.lrn, index, referenceIndex, NormalMode))
          }
        }

        "must go from 'add extra information' page to 'extra information' page on selecting 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), true).success.value

              navigator
                .nextPage(AddExtraInformationPage(index, referenceIndex), NormalMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.ExtraInformationController.onPageLoad(answers.lrn, index, referenceIndex, NormalMode))
          }
        }

        "must go from 'add extra information' page to 'Add another reference' page on selecting 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), false).success.value

              navigator
                .nextPage(AddExtraInformationPage(index, referenceIndex), NormalMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.lrn, index, NormalMode))
          }
        }

        "must go from 'extra information' page to 'Add another reference' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(ExtraInformationPage(index, referenceIndex), "text").success.value

              navigator
                .nextPage(ExtraInformationPage(index, referenceIndex), NormalMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.lrn, index, NormalMode))
          }
        }

        "AddAnotherPreviousAdministrativeReferencePage" - {
          "must go to ReferenceType page when user selects 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .remove(PreviousReferencesQuery(index)).success.value
                  .set(AddSecurityDetailsPage, true).success.value
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), true).success.value

                navigator
                  .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), NormalMode, updatedAnswer)
                  .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.lrn, index, index, NormalMode))
            }
          }

          "must go to CYA page when user selects 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .set(AddAnotherPreviousAdministrativeReferencePage(index), false).success.value
                  .set(AddSecurityDetailsPage, false).success.value

                navigator
                  .nextPage(AddAnotherPreviousAdministrativeReferencePage(index), NormalMode, updatedAnswer)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswer.lrn, index))
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
                  .mustBe(mainRoutes.DeclarationSummaryController.onPageLoad(answers.lrn))
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
                  .mustBe(routes.ItemDescriptionController.onPageLoad(answers.lrn, Index(1), NormalMode))
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
                  .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.lrn))
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
                  .mustBe(routes.AddAnotherItemController.onPageLoad(updatedAnswers.lrn))
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
                  .mustBe(routes.ItemDescriptionController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
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
                  .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.lrn, index, NormalMode))
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
                  .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.lrn, index, index, NormalMode))
            }
          }

          "must go from ConfirmRemoveContainerPage to AddAnotherContainer page when there is 1 container left" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .set(ContainerNumberPage(index, containerIndex),"").success.value
                navigator
                  .nextPage(ConfirmRemoveContainerPage(index, containerIndex), NormalMode, updatedAnswer)
                  .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswer.lrn, index, NormalMode))
            }
          }

          "must go from ConfirmRemoveContainerPage to ContainerNumber page when there are no containers left" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswer = answers
                  .remove(ContainersQuery(index, containerIndex)).success.value
                navigator
                  .nextPage(ConfirmRemoveContainerPage(index, containerIndex), NormalMode, updatedAnswer)
                  .mustBe(containerRoutes.ContainerNumberController.onPageLoad(updatedAnswer.lrn, index, Index(0), NormalMode))
            }
          }

          "must go from AddAnotherContainerPage to AddSpecialMentionController" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(AddAnotherContainerPage(index), NormalMode, answers)
                  .mustBe(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(answers.lrn, index, NormalMode))
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
                  .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(updatedAnswers.lrn, index, referenceIndex, NormalMode))
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
                  .mustBe(controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(updatedAnswers.lrn,itemIndex, NormalMode))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
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
                  .mustBe(controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
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
                  .mustBe(controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
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
                  .mustBe(controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(updatedAnswers.lrn,itemIndex, NormalMode))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
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
                  .mustBe(controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
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
                  .mustBe(controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(updatedAnswers.lrn, itemIndex, NormalMode))
            }
          }
        }
      }
    }

    "in check mode" - {

      "PackageJourney" - {

        "PackageType" - {

          "must go to HowManyPackages when PackageType code isn't bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.lrn, index, index, CheckMode))
            }
          }

          "must go to Add Marks when PackageType code is bulk" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.AddMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(routes.AddMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(routes.HowManyPackagesController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(routes.AddMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(routes.TotalPiecesController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(routes.AddMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(routes.DeclareMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
            }
          }
          "must go to CheckYourAnswers if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(index, index), false).success.value

                navigator
                  .nextPage(AddMarkPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
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
                  .mustBe(routes.PackageTypeController.onPageLoad(answers.lrn, index, nextPackageIndex, CheckMode))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, itemIndex))
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
                  .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, itemIndex))
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
                  .mustBe(containerRoutes.ContainerNumberController.onPageLoad(answers.lrn, itemIndex, containerIndex, CheckMode))
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
                .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswers.lrn, itemIndex, CheckMode))
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
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn, index))
          }
        }

        "must go from 'reference-type page' to 'previous reference' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ReferenceTypePage(index, referenceIndex), CheckMode, answers)
                .mustBe(previousReferenceRoutes.PreviousReferenceController.onPageLoad(answers.lrn, index, referenceIndex, CheckMode))
          }
        }

        "must go from 'previous reference' page to 'add extra information' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(PreviousReferencePage(index, referenceIndex), CheckMode, answers)
                .mustBe(previousReferenceRoutes.AddExtraInformationController.onPageLoad(answers.lrn, index, referenceIndex, CheckMode))
          }
        }

        "must go from 'add extra information' page to 'extra information' page on selecting 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), true).success.value

              navigator
                .nextPage(AddExtraInformationPage(index, referenceIndex), CheckMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.ExtraInformationController.onPageLoad(answers.lrn, index, referenceIndex, CheckMode))
          }
        }

        "must go from 'add extra information' page to 'Add another reference' page on selecting 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(AddExtraInformationPage(index, referenceIndex), false).success.value

              navigator
                .nextPage(AddExtraInformationPage(index, referenceIndex), CheckMode, updatedAnswer)
                .mustBe(previousReferenceRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(answers.lrn, index, CheckMode))
          }
        }

        "must go from 'extra information' page to 'Add another reference' page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers.set(ExtraInformationPage(index, referenceIndex), "text").success.value

              navigator
                .nextPage(ExtraInformationPage(index, referenceIndex), CheckMode, updatedAnswer)
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
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
                .mustBe(previousReferenceRoutes.ReferenceTypeController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                .mustBe(routes.ItemsCheckYourAnswersController.onPageLoad(answers.lrn, index))
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
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.lrn, index, CheckMode))
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
                  .mustBe(routes.AddAnotherPackageController.onPageLoad(answers.lrn, index, CheckMode))
            }
          }

          "must go to PackageType page when 'Yes' is selected and all the packages are removed" in {
            val updatedAnswers = emptyUserAnswers
              .remove(PackagesQuery(index, index)).success.value
              .set(RemovePackagePage(index), true).success.value
            navigator
              .nextPage(RemovePackagePage(index), CheckMode, updatedAnswers)
              .mustBe(routes.PackageTypeController.onPageLoad(updatedAnswers.lrn, index, index, CheckMode))
          }
        }

        "must go from ConfirmRemoveContainerPage to AddAnotherContainer page when there is 1 container left" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers
                .set(ContainerNumberPage(index, containerIndex),"").success.value
              navigator
                .nextPage(ConfirmRemoveContainerPage(index, containerIndex), CheckMode, updatedAnswer)
                .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswer.lrn, index, CheckMode))
          }
        }

        "must go from ConfirmRemoveContainerPage to ContainerNumber page when there are no containers left" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswer = answers
                .remove(ContainersQuery(index, containerIndex)).success.value
              navigator
                .nextPage(ConfirmRemoveContainerPage(index, containerIndex), CheckMode, updatedAnswer)
                .mustBe(containerRoutes.ContainerNumberController.onPageLoad(updatedAnswer.lrn, index, Index(0), CheckMode))
          }
        }
      }
    }
  }
  // format: on
}
