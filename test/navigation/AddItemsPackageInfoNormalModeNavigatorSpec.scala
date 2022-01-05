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
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.containers.{routes => containerRoutes}
import controllers.addItems.packagesInformation.{routes => packageRoutes}
import controllers.addItems.specialMentions.{routes => specialMentionsRoutes}
import generators.Generators
import models.reference.PackageType
import models.{Index, NormalMode, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsPackagesInfoNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import pages.addItems.containers._
import pages.generalInformation.ContainersUsedPage
import queries._

class AddItemsPackageInfoNormalModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
  val navigator = new AddItemsPackagesInfoNavigator

  "Add Items section" - {

    "in normal mode" - {

      "PackageJourney" - {

        "PackageType" - {

          "must go to HowManyPackages when PackageType code isn't bulk or unpacked" in {

            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(itemIndex, packageIndex), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(itemIndex, packageIndex), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.HowManyPackagesController.onPageLoad(answers.lrn, itemIndex, packageIndex, NormalMode))
            }
          }

          "must go to AddMark when PackageType code is bulk" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(itemIndex, packageIndex), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(itemIndex, packageIndex), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.AddMarkController.onPageLoad(answers.lrn, itemIndex, packageIndex, NormalMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(itemIndex, packageIndex), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(itemIndex, packageIndex), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.TotalPiecesController.onPageLoad(answers.lrn, itemIndex, packageIndex, NormalMode))
            }
          }
        }

        "HowManyPackages must go to DeclareMark " in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType], arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(itemIndex, packageIndex), packageType).success.value
                  .set(HowManyPackagesPage(itemIndex, packageIndex), howManyPackages).success.value

                navigator
                  .nextPage(HowManyPackagesPage(itemIndex, packageIndex), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.DeclareMarkController.onPageLoad(answers.lrn, itemIndex, packageIndex, NormalMode))
            }
          }

        "TotalPieces" - {
          "must go to AddMark" in {
            forAll(arbitrary[UserAnswers], arbitrary[Int]) {
              (answers, totalPieces) =>
                val updatedAnswers = answers
                  .set(TotalPackagesPage, totalPieces).success.value

                navigator
                  .nextPage(TotalPiecesPage(itemIndex, packageIndex), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.AddMarkController.onPageLoad(answers.lrn, itemIndex, packageIndex, NormalMode))
            }
          }
        }

        "AddMark" - {
          "must go to DeclareMark if answers is 'Yes'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(itemIndex, packageIndex), true).success.value

                navigator
                  .nextPage(AddMarkPage(itemIndex, packageIndex), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.DeclareMarkController.onPageLoad(answers.lrn, itemIndex, packageIndex, NormalMode))
            }
          }
          "must go to CYA if answers if 'No'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .set(AddMarkPage(itemIndex, packageIndex), false).success.value

                navigator
                  .nextPage(AddMarkPage(itemIndex, packageIndex), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.PackageCheckYourAnswersController.onPageLoad(answers.lrn, itemIndex, packageIndex, NormalMode))
            }
          }
        }

        "DeclareMark" - {
          "must go to CYA" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, declareMark) =>
                val updatedAnswers = answers
                  .set(DeclareMarkPage(itemIndex, packageIndex), declareMark).success.value

                navigator
                  .nextPage(DeclareMarkPage(itemIndex, packageIndex), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.PackageCheckYourAnswersController.onPageLoad(answers.lrn, itemIndex, packageIndex, NormalMode))
            }
          }
        }

        "AddAnotherPackage" - {
          "must go to PackageType if the answer is 'Yes' and increment package index" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(itemIndex, packageIndex), packageType).success.value
                  .set(AddAnotherPackagePage(index), true).success.value

                val nextPackageIndex = Index(index.position + 1)

                navigator
                  .nextPage(AddAnotherPackagePage(index), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.PackageTypeController.onPageLoad(answers.lrn, index, nextPackageIndex, NormalMode))
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


        "RemovePackage" - {

          "must go to AddAnotherPackage page when 'No' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(itemIndex, packageIndex), packageType).success.value
                  .set(PackageTypePage(itemIndex, packageIndex), packageType).success.value
                  .set(AddAnotherPackagePage(index), true).success.value
                  .set(RemovePackagePage(index), false).success.value
                navigator
                  .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.AddAnotherPackageController.onPageLoad(answers.lrn, index, NormalMode))
            }
          }

          "must go to AddAnotherPackage page when 'Yes' is selected and there are more than one package" in {
            forAll(arbitrary[UserAnswers], arbitrary[PackageType]) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(itemIndex, packageIndex), packageType).success.value
                  .set(PackageTypePage(itemIndex, packageIndex), packageType).success.value
                  .set(AddAnotherPackagePage(index), true).success.value
                  .set(RemovePackagePage(index), true).success.value
                navigator
                  .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
                  .mustBe(packageRoutes.AddAnotherPackageController.onPageLoad(answers.lrn, index, NormalMode))
            }
          }

          "must go to PackageType page when 'Yes' is selected and all the packages are removed" in {
            val updatedAnswers = emptyUserAnswers
              .remove(PackagesQuery(itemIndex, packageIndex)).success.value
              .set(RemovePackagePage(index), true).success.value
            navigator
              .nextPage(RemovePackagePage(index), NormalMode, updatedAnswers)
              .mustBe(packageRoutes.PackageTypeController.onPageLoad(updatedAnswers.lrn, itemIndex, packageIndex, NormalMode))
          }
        }

      }


    }
    // format: on
  }
}
