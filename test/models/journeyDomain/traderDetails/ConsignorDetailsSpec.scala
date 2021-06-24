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

package models.journeyDomain.traderDetails

import base.{GeneratorSpec, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import generators.JourneyModelGenerators
import models.journeyDomain.UserAnswersReader
import models.reference.{Country, CountryCode}
import models.{CommonAddress, EoriNumber}
import org.scalatest.TryValues
import pages.{ConsignorEoriPage, _}

class ConsignorDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators with UserAnswersSpecHelper {

  "Parsing ConsignorDetails from UserAnswers" - {

    "can be parsed from UserAnswers" - {

      "when there is consignor name, address and eori" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsignorPage)(true)
          .unsafeSetVal(IsConsignorEoriKnownPage)(true)
          .unsafeSetVal(ConsignorEoriPage)("eoriNumber")
          .unsafeSetVal(ConsignorNamePage)("consignorName")
          .unsafeSetVal(ConsignorAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))

        val expectedAddress: CommonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123"))

        val expectedResult = ConsignorDetails("consignorName", expectedAddress, Some(EoriNumber("eoriNumber")))

        val result = UserAnswersReader[ConsignorDetails].run(userAnswers).right.value

        result mustBe expectedResult
      }

      "when there is consignor name and address without eori" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsignorPage)(true)
          .unsafeSetVal(IsConsignorEoriKnownPage)(false)
          .unsafeSetVal(ConsignorNamePage)("consignorName")
          .unsafeSetVal(ConsignorAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))

        val expectedAddress: CommonAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123"))

        val expectedResult = ConsignorDetails("consignorName", expectedAddress, None)

        val result = UserAnswersReader[ConsignorDetails].run(userAnswers).right.value

        result mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when name and eori are answered but address is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsignorPage)(true)
          .unsafeSetVal(IsConsignorEoriKnownPage)(true)
          .unsafeSetVal(ConsignorEoriPage)("eoriNumber")
          .unsafeSetVal(ConsignorNamePage)("consignorName")
          .unsafeRemove(ConsignorAddressPage)

        val result = UserAnswersReader[ConsignorDetails].run(userAnswers).left.value

        result.page mustBe ConsignorAddressPage
      }

      "when address and eori are answered but name is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsignorPage)(true)
          .unsafeSetVal(IsConsignorEoriKnownPage)(true)
          .unsafeSetVal(ConsignorEoriPage)("eoriNumber")
          .unsafeSetVal(ConsignorAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))
          .unsafeRemove(ConsignorNamePage)

        val result = UserAnswersReader[ConsignorDetails].run(userAnswers).left.value

        result.page mustBe ConsignorNamePage
      }

      "when name and address are answered and IsConsignorEoriKnownPage is true but eori is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsignorPage)(true)
          .unsafeSetVal(IsConsignorEoriKnownPage)(true)
          .unsafeSetVal(ConsignorAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))
          .unsafeSetVal(ConsignorNamePage)("consignorName")
          .unsafeRemove(ConsignorEoriPage)

        val result = UserAnswersReader[ConsignorDetails].run(userAnswers).left.value

        result.page mustBe ConsignorEoriPage
      }
    }
  }

}
