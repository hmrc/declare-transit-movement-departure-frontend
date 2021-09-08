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
import cats.data.NonEmptyList
import commonTestUtils.UserAnswersSpecHelper
import generators.UserAnswersGenerator
import models.Index
import models.journeyDomain.Packages.{BulkPackages, OtherPackages, UnpackedPackages}
import models.reference.PackageType
import models.userAnswerScenarios.Scenario1
import pages.{AddSecurityDetailsPage, ItemTotalGrossMassPage}

class JourneyDomainSpec extends SpecBase with GeneratorSpec with UserAnswersGenerator with UserAnswersSpecHelper {

  "JourneyDomain" - {
    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {

        forAll(genUserAnswerScenario) {
          userAnswerScenario =>
            val result = UserAnswersReader[JourneyDomain].run(userAnswerScenario.userAnswers).value

            result mustBe userAnswerScenario.toModel
        }
      }

      "cannot be parsed from UserAnswers" - {

        "when a safety and security is missing" in {

          forAll(genUserAnswerScenario) {
            userAnswerScenario =>
              val userAnswers = userAnswerScenario.userAnswers
                .unsafeRemove(AddSecurityDetailsPage)

              val result = UserAnswersReader[JourneyDomain].run(userAnswers).left.value

              result.page mustBe AddSecurityDetailsPage
          }
        }
      }

      "ItemSections" - {
        "Must submit the correct amount for total gross mass" in {

          val updatedUserAnswer = Scenario1.userAnswers
            .unsafeSetVal(ItemTotalGrossMassPage(Index(0)))(100.123)
            .unsafeSetVal(ItemTotalGrossMassPage(Index(1)))(200.123)

          val itemSectionList = UserAnswersReader[NonEmptyList[ItemSection]].run(updatedUserAnswer).value

          val result = ItemSections(itemSectionList)

          result.totalGrossMassFormatted mustBe "300.246"
        }
      }

      "Must submit the correct number for total packages" - {

        "must submit 1 for each bulk package" in {
          val item = ItemSection(
            itemDetails = ItemDetails("ItemTwosDescription", "12345.000", None, None),
            consignor = None,
            consignee = None,
            packages = NonEmptyList(
              BulkPackages(PackageType("VQ", "GD2PKG1"), None),
              List.empty
            ),
            containers = None,
            specialMentions = None,
            producedDocuments = None,
            itemSecurityTraderDetails = None,
            previousReferences = None
          )

          val result = ItemSections(NonEmptyList(item, List.empty))

          result.totalPackages mustBe 1
        }

        "must submit exact number for pieces of packages when unpacked" in {
          val item = ItemSection(
            itemDetails = ItemDetails("ItemTwosDescription", "12345.000", None, None),
            consignor = None,
            consignee = None,
            packages = NonEmptyList(
              UnpackedPackages(PackageType("NE", "GD2PKG2"), 10, Some("GD2PK2MK")),
              List.empty
            ),
            containers = None,
            specialMentions = None,
            producedDocuments = None,
            itemSecurityTraderDetails = None,
            previousReferences = None
          )

          val result = ItemSections(NonEmptyList(item, List.empty))

          result.totalPackages mustBe 10
        }

        "must submit exact number for other packages types" in {
          val item = ItemSection(
            itemDetails = ItemDetails("ItemTwosDescription", "12345.000", None, None),
            consignor = None,
            consignee = None,
            packages = NonEmptyList(
              OtherPackages(PackageType("BAG", "GD2PKG3"), 2, "GD2PK3MK"),
              List.empty
            ),
            containers = None,
            specialMentions = None,
            producedDocuments = None,
            itemSecurityTraderDetails = None,
            previousReferences = None
          )

          val result = ItemSections(NonEmptyList(item, List.empty))

          result.totalPackages mustBe 2
        }

        "must submit the total of all packages types for one item" in {
          val item = ItemSection(
            itemDetails = ItemDetails("ItemTwosDescription", "12345.000", None, None),
            consignor = None,
            consignee = None,
            packages = NonEmptyList(
              BulkPackages(PackageType("VQ", "GD2PKG1"), None),
              List(UnpackedPackages(PackageType("NE", "GD2PKG2"), 10, Some("GD2PK2MK")), OtherPackages(PackageType("BAG", "GD2PKG3"), 10, "GD2PK3MK"))
            ),
            containers = None,
            specialMentions = None,
            producedDocuments = None,
            itemSecurityTraderDetails = None,
            previousReferences = None
          )

          val result = ItemSections(NonEmptyList(item, List.empty))

          result.totalPackages mustBe 21
        }

        "must submit the total of all packages types for two items" in {
          val item1 = ItemSection(
            itemDetails = ItemDetails("ItemTwosDescription", "12345.000", None, None),
            consignor = None,
            consignee = None,
            packages = NonEmptyList(
              BulkPackages(PackageType("VQ", "GD2PKG1"), None),
              List(UnpackedPackages(PackageType("NE", "GD2PKG2"), 10, Some("GD2PK2MK")), OtherPackages(PackageType("BAG", "GD2PKG3"), 10, "GD2PK3MK"))
            ),
            containers = None,
            specialMentions = None,
            producedDocuments = None,
            itemSecurityTraderDetails = None,
            previousReferences = None
          )

          val item2 = ItemSection(
            itemDetails = ItemDetails("ItemTwosDescription", "12345.000", None, None),
            consignor = None,
            consignee = None,
            packages = NonEmptyList(
              BulkPackages(PackageType("VQ", "GD2PKG1"), None),
              List(UnpackedPackages(PackageType("NE", "GD2PKG2"), 10, Some("GD2PK2MK")), OtherPackages(PackageType("BAG", "GD2PKG3"), 10, "GD2PK3MK"))
            ),
            containers = None,
            specialMentions = None,
            producedDocuments = None,
            itemSecurityTraderDetails = None,
            previousReferences = None
          )

          val result = ItemSections(NonEmptyList(item1, List(item2)))

          result.totalPackages mustBe 42
        }

      }
    }
  }
}
