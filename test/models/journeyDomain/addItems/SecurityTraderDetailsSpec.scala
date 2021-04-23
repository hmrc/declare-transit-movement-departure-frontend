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

package models.journeyDomain.addItems

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.EoriNumber
import models.domain.Address
import models.ConsigneeAddress
import models.reference._
import models.journeyDomain.{EitherType, ReaderError}
import pages.addItems.traderSecurityDetails._

class SecurityTraderDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with UserAnswersSpecHelper {

  "Reading from User Answers" - {

    "Consignee" - {

      "when the eori number is known" in {
        val ua = emptyUserAnswers
          .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(true)
          .unsafeSetVal(SecurityConsigneeEoriPage(index))("testEori")

        val expected = SecurityTraderEori(EoriNumber("testEori"))

        val result = SecurityTraderDetails.consigneeDetails2(index).run(ua).right.value

        result mustEqual expected
      }

      "when the eori number is not known" in {
        val consigneeAddress = ConsigneeAddress("1", "2", "3", Country(CountryCode("ZZ"), ""))

        val ua = emptyUserAnswers
          .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(false)
          .unsafeSetVal(SecurityConsigneeNamePage(index))("testName")
          .unsafeSetVal(SecurityConsigneeAddressPage(index))(consigneeAddress)

        val address  = Address("1", "2", "3", Some(Country(CountryCode("ZZ"), "")))
        val expected = SecurityPersonalInformation("testName", address)

        val result = SecurityTraderDetails.consigneeDetails2(index).run(ua).right.value

        result mustEqual expected
      }

    }
  }

}
