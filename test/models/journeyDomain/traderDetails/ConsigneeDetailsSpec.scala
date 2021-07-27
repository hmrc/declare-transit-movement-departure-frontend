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
import models.journeyDomain.UserAnswersReader
import models.reference.{Country, CountryCode}
import models.{CommonAddress, EoriNumber}
import org.scalatest.TryValues
import pages._
import pages.traderDetails.{AddConsigneePage, ConsigneeAddressPage, ConsigneeNamePage, IsConsigneeEoriKnownPage}

class ConsigneeDetailsSpec extends SpecBase with GeneratorSpec with TryValues with UserAnswersSpecHelper {

  "Parsing ConsigneeDetails from UserAnswers" - {

    "can be parsed from UserAnswers" - {

      "when there is consignee name, address and eori" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsigneePage)(true)
          .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
          .unsafeSetVal(WhatIsConsigneeEoriPage)("eoriNumber")
          .unsafeSetVal(ConsigneeNamePage)("consigneeName")
          .unsafeSetVal(ConsigneeAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))

        val expectedAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123"))

        val expectedResult = ConsigneeDetails("consigneeName", expectedAddress, Some(EoriNumber("eoriNumber")))

        val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).right.value

        result mustBe expectedResult
      }

      "when there is consignee name and address without eori" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsigneePage)(true)
          .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
          .unsafeSetVal(ConsigneeNamePage)("consigneeName")
          .unsafeSetVal(ConsigneeAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))

        val expectedAddress = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123"))

        val expectedResult = ConsigneeDetails("consigneeName", expectedAddress, None)

        val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).right.value

        result mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when name and eori are answered but address is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsigneePage)(true)
          .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
          .unsafeSetVal(WhatIsConsigneeEoriPage)("eoriNumber")
          .unsafeSetVal(ConsigneeNamePage)("consigneeName")
          .unsafeRemove(ConsigneeAddressPage)

        val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).left.value

        result.page mustBe ConsigneeAddressPage
      }

      "when address and eori are answered but name is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsigneePage)(true)
          .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
          .unsafeSetVal(WhatIsConsigneeEoriPage)("eoriNumber")
          .unsafeSetVal(ConsigneeAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))
          .unsafeRemove(ConsigneeNamePage)

        val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).left.value

        result.page mustBe ConsigneeNamePage
      }

      "when name and address are answered and IsConsigneeEoriKnownPage is true but eori is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddConsigneePage)(true)
          .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
          .unsafeSetVal(ConsigneeAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))
          .unsafeSetVal(ConsigneeNamePage)("consigneeName")
          .unsafeRemove(WhatIsConsigneeEoriPage)

        val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).left.value

        result.page mustBe WhatIsConsigneeEoriPage
      }
    }
  }

}
