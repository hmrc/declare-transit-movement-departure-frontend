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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.{JourneyModelGenerators, ModelGenerators}
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.addItems.{AddMarkPage, DeclareMarkPage, DeclareNumberOfPackagesPage, HowManyPackagesPage, TotalPiecesPage}
import pages.{PackageTypePage, QuestionPage}
import play.api.libs.json.Json

class PackagesSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with ModelGenerators {

  import PackagesSpec._

  val mandatoryPagesOther: Gen[QuestionPage[_]] = Gen.oneOf(
    PackageTypePage(index, index),
    HowManyPackagesPage(index, index),
    DeclareMarkPage(index, index)
  )

  val mandatoryPagesBulkPackages: Gen[QuestionPage[_]] = PackageTypePage(index, index)

  "PackagesSpec" - {

    "any Packages can be parsed from UserAnswer" in {
      forAll(arbitrary[Packages], arbitrary[UserAnswers]) {
        (packages, userAnswers) =>
          val updatedUserAnswers = setPackageUserAnswers(packages, index, index)(userAnswers)
          val result             = UserAnswersReader[Packages](Packages.packagesReader(index, index)).run(updatedUserAnswers)

          result.value mustEqual packages
      }
    }

    "OtherPackage" - {

      "can be parsed from UserAnswers" in {

        forAll(arbitrary[OtherPackages], arbitrary[UserAnswers]) {
          (otherPackage, userAnswers) =>
            val updatedUserAnswers = setPackageUserAnswers(otherPackage, index, index)(userAnswers)
            val result             = UserAnswersReader[OtherPackages](OtherPackages.otherPackageReader(index, index)).run(updatedUserAnswers)

            result.value mustEqual otherPackage
        }
      }

      "can not be parsed when a mandatory answer is missing" in {

        forAll(arbitrary[OtherPackages], arbitrary[UserAnswers], mandatoryPagesOther) {
          case (otherPackage, userAnswers, mandatoryPage) =>
            val updatedUserAnswers = setPackageUserAnswers(otherPackage, index, index)(userAnswers)

            val userAnswersIncomplete = updatedUserAnswers.unsafeRemoveVal(mandatoryPage)
            val result                = UserAnswersReader[OtherPackages](OtherPackages.otherPackageReader(index, index)).run(userAnswersIncomplete)

            result mustEqual None
        }
      }

    }

    "BulkPackage" - {

      "can be parsed from UserAnswers" in {

        forAll(arbitrary[BulkPackages], arbitrary[UserAnswers]) {
          (bulkPackage, userAnswers) =>
            val updatedUserAnswers = setPackageUserAnswers(bulkPackage, index, index)(userAnswers)
            val result             = UserAnswersReader[BulkPackages](BulkPackages.bulkPackageReader(index, index)).run(updatedUserAnswers)

            result.value mustEqual bulkPackage
        }
      }

      "can not be parsed when" - {
        "an arbitrary mandatory answer is missing" in {

          forAll(arbitrary[BulkPackages], arbitrary[UserAnswers], mandatoryPagesBulkPackages) {
            case (packages, userAnswers, mandatoryPage) =>
              val updatedUserAnswers = setPackageUserAnswers(packages, index, index)(userAnswers)

              val userAnswersIncomplete = updatedUserAnswers.unsafeRemoveVal(mandatoryPage)
              val result                = UserAnswersReader[BulkPackages](BulkPackages.bulkPackageReader(index, index)).run(userAnswersIncomplete)

              result mustEqual None
          }
        }

        "DeclareNumberOfPackagesPage is true but HowManyPackagesPage is not defined" in {
          val genPackages = arbitrary[BulkPackages].map(_.copy(howManyPackagesPage = None))

          forAll(genPackages, arbitrary[UserAnswers]) {
            case (packages, userAnswers) =>
              val updatedUserAnswers =
                setPackageUserAnswers(packages, index, index)(userAnswers)
                  .unsafeSetVal(DeclareNumberOfPackagesPage(index, index))(true)
                  .unsafeRemoveVal(HowManyPackagesPage(index, index))

              val result = UserAnswersReader[BulkPackages](BulkPackages.bulkPackageReader(index, index)).run(updatedUserAnswers)

              result mustEqual None
          }
        }

        "AddMarkPage is true but DeclareMarkPage is not defined" in {
          val genPackages = arbitrary[BulkPackages].map(_.copy(markOrNumber = None))

          forAll(genPackages, arbitrary[UserAnswers]) {
            case (packages, userAnswers) =>
              val updatedUserAnswers =
                setPackageUserAnswers(packages, index, index)(userAnswers)
                  .unsafeSetVal(AddMarkPage(index, index))(true)
                  .unsafeRemoveVal(DeclareMarkPage(index, index))

              val result = UserAnswersReader[BulkPackages](BulkPackages.bulkPackageReader(index, index)).run(updatedUserAnswers)

              result mustEqual None
          }
        }
      }
    }

    "UnpackedPackages" - {

      "can be parsed from UserAnswers" in {

        forAll(arbitrary[UnpackedPackages], arbitrary[UserAnswers]) {
          (bulkPackage, userAnswers) =>
            val updatedUserAnswers = setPackageUserAnswers(bulkPackage, index, index)(userAnswers)
            val result             = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(updatedUserAnswers)

            result.value mustEqual bulkPackage
        }
      }

      "can not be parsed when" - {
        "a mandatory answer is missing" in {
          forAll(arbitrary[UnpackedPackages], arbitrary[UserAnswers], mandatoryPagesBulkPackages) {
            case (packages, userAnswers, mandatoryPage) =>
              val updatedUserAnswers = setPackageUserAnswers(packages, index, index)(userAnswers)

              val userAnswersIncomplete = updatedUserAnswers.unsafeRemoveVal(mandatoryPage)
              val result                = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(userAnswersIncomplete)

              result mustEqual None
          }
        }

        "DeclareNumberOfPackagesPage is true but HowManyPackagesPage is not defined" in {
          val genPackages = arbitrary[UnpackedPackages].map(_.copy(howManyPackagesPage = None))

          forAll(genPackages, arbitrary[UserAnswers]) {
            case (packages, userAnswers) =>
              val updatedUserAnswers =
                setPackageUserAnswers(packages, index, index)(userAnswers)
                  .unsafeSetVal(DeclareNumberOfPackagesPage(index, index))(true)
                  .unsafeRemoveVal(HowManyPackagesPage(index, index))

              val result = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(updatedUserAnswers)

              result mustEqual None
          }
        }

        "AddMarkPage is true but DeclareMarkPage is not defined" in {
          val genPackages = arbitrary[UnpackedPackages].map(_.copy(markOrNumber = None))

          forAll(genPackages, arbitrary[UserAnswers]) {
            case (packages, userAnswers) =>
              val updatedUserAnswers =
                setPackageUserAnswers(packages, index, index)(userAnswers)
                  .unsafeSetVal(AddMarkPage(index, index))(true)
                  .unsafeRemoveVal(DeclareMarkPage(index, index))

              val result = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(updatedUserAnswers)

              result mustEqual None
          }
        }
      }
    }
  }
}

object PackagesSpec extends UserAnswersSpecHelper {

  def setPackageUserAnswers(packages: Packages, itemIndex: Index, packageIndex: Index)(userAnswers: UserAnswers): UserAnswers =
    packages match {
      case otherPackage: OtherPackages =>
        userAnswers
          .unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(otherPackage.packageType)
          .unsafeSetVal(HowManyPackagesPage(itemIndex, packageIndex))(otherPackage.howManyPackagesPage)
          .unsafeSetVal(DeclareMarkPage(itemIndex, packageIndex))(otherPackage.markOrNumber)
      case bulkPackage: BulkPackages =>
        userAnswers
          .unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(bulkPackage.packageType)
          .unsafeSetVal(DeclareNumberOfPackagesPage(itemIndex, packageIndex))(bulkPackage.howManyPackagesPage.isDefined)
          .unsafeSetOpt(HowManyPackagesPage(itemIndex, packageIndex))(bulkPackage.howManyPackagesPage)
          .unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(bulkPackage.markOrNumber.isDefined)
          .unsafeSetOpt(DeclareMarkPage(itemIndex, packageIndex))(bulkPackage.markOrNumber)
      case unpackedPackages: UnpackedPackages =>
        userAnswers
          .unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(unpackedPackages.packageType)
          .unsafeSetVal(DeclareNumberOfPackagesPage(itemIndex, packageIndex))(unpackedPackages.howManyPackagesPage.isDefined)
          .unsafeSetOpt(HowManyPackagesPage(itemIndex, packageIndex))(unpackedPackages.howManyPackagesPage)
          .unsafeSetVal(TotalPiecesPage(itemIndex, packageIndex))(unpackedPackages.totalPieces)
          .unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(unpackedPackages.markOrNumber.isDefined)
          .unsafeSetOpt(DeclareMarkPage(itemIndex, packageIndex))(unpackedPackages.markOrNumber)
    }

}
