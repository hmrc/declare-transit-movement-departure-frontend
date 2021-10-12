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
import controllers.addItems.routes
import generators.Generators
import models.reference.PackageType
import models.{CheckMode, Index, UserAnswers}
import navigation.annotations.addItemsNavigators.AddItemsPackagesInfoNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import pages.addItems.containers._
import pages.generalInformation.ContainersUsedPage
import queries.ContainersQuery

class AddItemsPackagesInfoCheckModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
  val navigator = new AddItemsPackagesInfoNavigator

  "Add Items section" - {

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
                  .mustBe(controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(answers.lrn, index, CheckMode))
            }
          }

          "must go to Add Marks when PackageType code is bulk" in {

            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
            }
          }

          "must go to TotalPieces when PackageType code is unpacked" in {

            forAll(arbitrary[UserAnswers], arbitraryUnPackedPackageType.arbitrary) {
              (answers, packageType) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value

                navigator
                  .nextPage(PackageTypePage(index, index), CheckMode, updatedAnswers)
                  .mustBe(controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(answers.lrn, index, index, CheckMode))
            }
          }
        }
        "HowManyPackages must go to AddMark page" in {
            forAll(arbitrary[UserAnswers], arbitraryBulkPackageType.arbitrary, arbitrary[Int]) {
              (answers, packageType, howManyPackages) =>
                val updatedAnswers = answers
                  .set(PackageTypePage(index, index), packageType).success.value
                  .set(HowManyPackagesPage(index, index), howManyPackages).success.value

                navigator
                  .nextPage(HowManyPackagesPage(index, index), CheckMode, updatedAnswers)
                  .mustBe(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(controllers.addItems.packagesInformation.routes.DeclareMarkController.onPageLoad(answers.lrn, index, index, CheckMode))
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
                  .mustBe(controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(answers.lrn, index, nextPackageIndex, CheckMode))
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


    }
  }
  // format: on
}
