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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.reference.PackageType
import org.scalacheck.Gen
import pages.addItems._
import pages.{PackageTypePage, QuestionPage}

class PackagesSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  private val unpackedPackageCode = Gen.oneOf(PackageType.unpackedCodes).sample.value
  private val bulkPackageCode     = Gen.oneOf(PackageType.bulkCodes).sample.value
  private val otherPackageCode    = arb[String].retryUntil(!PackageType.bulkAndUnpackedCodes.contains(_)).sample.value

  private val unpackedPackageUa = emptyUserAnswers
    .unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(PackageType(unpackedPackageCode, "description"))
    .unsafeSetVal(TotalPiecesPage(itemIndex, packageIndex))(123)
    .unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(false)

  private val bulkPackageUa = emptyUserAnswers
    .unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(PackageType(bulkPackageCode, "description"))
    .unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(false)

  private val otherPackageUa = emptyUserAnswers
    .unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(PackageType(otherPackageCode, "description"))
    .unsafeSetVal(HowManyPackagesPage(itemIndex, packageIndex))(10)
    .unsafeSetVal(DeclareMarkPage(itemIndex, packageIndex))("mark")

  "PackagesSpec" - {

    "can be parsed from user answers" - {

      "when package type is unpacked" in {

        val expectedResult = UnpackedPackages(PackageType(unpackedPackageCode, "description"), 123, None)

        val result = UserAnswersReader[Packages](Packages.packagesReader(index, index)).run(unpackedPackageUa)

        result.value mustBe expectedResult
      }

      "when package type is bulk" in {

        val expectedResult = BulkPackages(PackageType(bulkPackageCode, "description"), None)

        val result = UserAnswersReader[Packages](Packages.packagesReader(index, index)).run(bulkPackageUa)

        result.value mustBe expectedResult
      }

      "when package type is anything else" in {
        val expectedResult = OtherPackages(PackageType(otherPackageCode, "description"), 10, "mark")

        val result = UserAnswersReader[Packages](Packages.packagesReader(index, index)).run(otherPackageUa)

        result.value mustBe expectedResult
      }
    }

    "OtherPackage" - {

      "can be parsed from UserAnswers" - {

        "when all mandatory answers are defined" in {

          val expectedResult = OtherPackages(PackageType(otherPackageCode, "description"), 10, "mark")

          val result = UserAnswersReader[OtherPackages](OtherPackages.otherPackageReader(index, index)).run(otherPackageUa)

          result.value mustBe expectedResult
        }

      }

      "cannot be parsed from UserAnswers" - {

        "when a mandatory answer is missing" in {

          val mandatoryPagesOther: Gen[QuestionPage[_]] = Gen.oneOf(
            PackageTypePage(index, index),
            HowManyPackagesPage(index, index),
            DeclareMarkPage(index, index)
          )

          forAll(mandatoryPagesOther) {
            mandatoryPage =>
              val userAnswers = otherPackageUa.unsafeRemove(mandatoryPage)

              val result = UserAnswersReader[OtherPackages](OtherPackages.otherPackageReader(index, index)).run(userAnswers)

              result.left.value.page mustBe mandatoryPage
          }
        }
      }
    }

    "BulkPackage" - {

      "can be parsed from UserAnswers" - {

        "when all mandatory answers are defined and mark or number is not defined" in {

          val expectedResult = BulkPackages(PackageType(bulkPackageCode, "description"), None)

          val result = UserAnswersReader[BulkPackages](BulkPackages.bulkPackageReader(index, index)).run(bulkPackageUa)

          result.value mustBe expectedResult
        }

        "when all mandatory answers are defined and mark or number is defined" in {

          val expectedResult = BulkPackages(PackageType(bulkPackageCode, "description"), Some("markOrNumber"))

          val userAnswers = bulkPackageUa
            .unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(true)
            .unsafeSetVal(DeclareMarkPage(itemIndex, packageIndex))("markOrNumber")

          val result = UserAnswersReader[BulkPackages](BulkPackages.bulkPackageReader(index, index)).run(userAnswers)

          result.value mustBe expectedResult
        }

      }

      "cannot be parsed from UserAnswers" - {

        "when a mandatory page is missing" in {

          val mandatoryPagesOther: Gen[QuestionPage[_]] = Gen.oneOf(
            PackageTypePage(index, index),
            AddMarkPage(index, index)
          )

          forAll(mandatoryPagesOther) {
            mandatoryPage =>
              val userAnswers = bulkPackageUa.unsafeRemove(mandatoryPage)

              val result = UserAnswersReader[Packages](Packages.packagesReader(index, index)).run(userAnswers)

              result.left.value.page mustBe mandatoryPage
          }
        }

        "AddMarkPage is true but DeclareMarkPage is not defined" in {

          val userAnswers = bulkPackageUa
            .unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(true)
            .unsafeRemove(DeclareMarkPage(itemIndex, packageIndex))

          val result = UserAnswersReader[BulkPackages](BulkPackages.bulkPackageReader(index, index)).run(userAnswers)

          result.left.value.page mustBe DeclareMarkPage(itemIndex, packageIndex)
        }
      }
    }

    "UnpackedPackages" - {

      "can be parsed from UserAnswers" - {

        "when mark or number is not defined" in {

          val expectedResult = UnpackedPackages(PackageType(unpackedPackageCode, "description"), 123, None)

          val result = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(unpackedPackageUa)

          result.value mustBe expectedResult
        }

        "when mark or number is defined" in {

          val expectedResult = UnpackedPackages(PackageType(unpackedPackageCode, "description"), 123, Some("markOrNumber"))

          val userAnswers = unpackedPackageUa
            .unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(true)
            .unsafeSetVal(DeclareMarkPage(itemIndex, packageIndex))("markOrNumber")

          val result = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "cannot be parsed from UserAnswers " - {
        "when a mandatory answer is missing" in {

          val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
            PackageTypePage(itemIndex, packageIndex),
            TotalPiecesPage(itemIndex, packageIndex),
            AddMarkPage(itemIndex, packageIndex)
          )

          forAll(mandatoryPages) {
            mandatoryPage =>
              val userAnswers = unpackedPackageUa.unsafeRemove(mandatoryPage)

              val result = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(userAnswers)

              result.left.value.page mustBe mandatoryPage
          }
        }

        "AddMarkPage is true but DeclareMarkPage is not defined" in {

          val userAnswers = unpackedPackageUa
            .unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(true)
            .unsafeRemove(DeclareMarkPage(itemIndex, packageIndex))

          val result = UserAnswersReader[UnpackedPackages](UnpackedPackages.unpackedPackagesReader(index, index)).run(userAnswers)

          result.left.value.page mustBe DeclareMarkPage(itemIndex, packageIndex)
        }
      }
    }
  }
}
