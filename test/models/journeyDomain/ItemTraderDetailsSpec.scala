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
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.reference.{Country, CountryCode}
import models.{CommonAddress, EoriNumber}
import org.scalacheck.Gen
import org.scalatest.TryValues
import pages._
import pages.addItems.traderDetails._
import pages.traderDetails.{AddConsigneePage, AddConsignorPage}

class ItemTraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with UserAnswersSpecHelper {

  "ItemTraderDetail" - {
    "Consignor" - {
      "can be parsed" - {

        "when there is no eoriNumber but only name and address" in {

          val commonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddConsignorPage)(false)
            .unsafeSetVal(AddConsigneePage)(true)
            .unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(false)
            .unsafeSetVal(TraderDetailsConsignorNamePage(index))("name")
            .unsafeSetVal(TraderDetailsConsignorAddressPage(index))(commonAddress)

          val expectedResult =
            RequiredDetails("name", CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description")), None)

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          result.consignor.value mustEqual expectedResult
        }

        "when there is a name, address and eori" in {

          val commonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddConsignorPage)(false)
            .unsafeSetVal(AddConsigneePage)(true)
            .unsafeSetVal(TraderDetailsConsignorNamePage(index))("name")
            .unsafeSetVal(TraderDetailsConsignorAddressPage(index))(commonAddress)
            .unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(true)
            .unsafeSetVal(TraderDetailsConsignorEoriNumberPage(index))("eoriNumber1")

          val expectedResult = RequiredDetails(
            "name",
            CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description")),
            Some(EoriNumber("eoriNumber1"))
          )

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          result.consignor.value mustEqual expectedResult
        }

        "when header level consignor has already been answered" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddConsignorPage)(true)
            .unsafeSetVal(AddConsigneePage)(true)
            .unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(true)
            .unsafeSetVal(TraderDetailsConsignorEoriNumberPage(index))("eoriNumber1")

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          result.consignor must be(None)
        }
      }

      "cannot be parsed" - {

        "when a mandatory page is missing" in {

          val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
            AddConsignorPage,
            TraderDetailsConsignorNamePage(index),
            TraderDetailsConsignorAddressPage(index),
            TraderDetailsConsignorEoriKnownPage(index)
          )

          forAll(mandatoryPages) {
            mandatoryPage =>
              val commonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

              val userAnswers = emptyUserAnswers
                .unsafeSetVal(AddConsignorPage)(false)
                .unsafeSetVal(AddConsigneePage)(true)
                .unsafeSetVal(TraderDetailsConsignorNamePage(index))("name")
                .unsafeSetVal(TraderDetailsConsignorAddressPage(index))(commonAddress)
                .unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(true)
                .unsafeSetVal(TraderDetailsConsignorEoriNumberPage(index))("eoriNumber1")
                .unsafeRemove(mandatoryPage)

              val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).left.value

              result.page mustEqual mandatoryPage
          }
        }

        "when eori known page is true and eori number page is not defined" in {

          val commonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddConsignorPage)(false)
            .unsafeSetVal(AddConsigneePage)(true)
            .unsafeSetVal(TraderDetailsConsignorNamePage(index))("name")
            .unsafeSetVal(TraderDetailsConsignorAddressPage(index))(commonAddress)
            .unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(true)

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).left.value

          result.page mustEqual TraderDetailsConsignorEoriNumberPage(index)
        }
      }
    }

    "Consignee" - {

      "can be parsed" - {

        "when there is no eori only name and address" in {

          val commonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddConsignorPage)(true)
            .unsafeSetVal(AddConsigneePage)(false)
            .unsafeSetVal(TraderDetailsConsigneeNamePage(index))("name")
            .unsafeSetVal(TraderDetailsConsigneeAddressPage(index))(commonAddress)
            .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(false)

          val expectedResult =
            RequiredDetails("name", CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description")), None)

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          result.consignee.value mustEqual expectedResult
        }

        "when there is name, address and eori" in {

          val commonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddConsignorPage)(true)
            .unsafeSetVal(AddConsigneePage)(false)
            .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(true)
            .unsafeSetVal(TraderDetailsConsigneeEoriNumberPage(index))("eoriNumber1")
            .unsafeSetVal(TraderDetailsConsigneeNamePage(index))("name")
            .unsafeSetVal(TraderDetailsConsigneeAddressPage(index))(commonAddress)

          val expectedResult = RequiredDetails(
            "name",
            CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description")),
            Some(EoriNumber("eoriNumber1"))
          )

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          result.consignee.value mustEqual expectedResult
        }

        "when header level consignee has already been answered" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddConsignorPage)(true)
            .unsafeSetVal(AddConsigneePage)(true)
            .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(true)
            .unsafeSetVal(TraderDetailsConsigneeEoriNumberPage(index))("eoriNumber1")

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          result.consignee must be(None)
        }
      }

      "cannot be parsed" - {

        "when a mandatory page is missing" in {

          val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
            AddConsigneePage,
            TraderDetailsConsigneeNamePage(index),
            TraderDetailsConsigneeAddressPage(index),
            TraderDetailsConsigneeEoriKnownPage(index)
          )

          forAll(mandatoryPages) {
            mandatoryPage =>
              val commonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

              val userAnswers = emptyUserAnswers
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(AddConsigneePage)(false)
                .unsafeSetVal(TraderDetailsConsigneeNamePage(index))("name")
                .unsafeSetVal(TraderDetailsConsigneeAddressPage(index))(commonAddress)
                .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(false)
                .unsafeRemove(mandatoryPage)

              val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).left.value

              result.page mustEqual mandatoryPage
          }
        }

        "when eori known page is true and eori number page is not defined" in {

          val commonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddConsignorPage)(true)
            .unsafeSetVal(AddConsigneePage)(false)
            .unsafeSetVal(TraderDetailsConsigneeNamePage(index))("name")
            .unsafeSetVal(TraderDetailsConsigneeAddressPage(index))(commonAddress)
            .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(true)

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).left.value

          result.page mustEqual TraderDetailsConsigneeEoriNumberPage(index)
        }
      }
    }
  }
}
