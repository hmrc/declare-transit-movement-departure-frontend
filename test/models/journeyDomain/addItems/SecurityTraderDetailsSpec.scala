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
import models.{ConsigneeAddress, EoriNumber}
import models.domain.Address
import models.reference._
import pages.AddSecurityDetailsPage
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.AddSafetyAndSecurityConsigneePage

class SecurityTraderDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with UserAnswersSpecHelper {

  "Reading from User Answers" - {

    "Consignee" - {

      "when add security details is 'No' then consignee should be None" in {

        val ua = emptyUserAnswers
          .unsafeSetVal(AddSecurityDetailsPage)(false)

        val result = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

        result mustEqual None
      }

      "when add security details is 'Yes'" - {
        "when the consignee for all items is 'Yes' should be None" in {

          val ua = emptyUserAnswers
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)

          val result = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

          result mustEqual None
        }

        "when there is not a consignee for all items" - {
          "when the eori is known then the security consignee is read" in {
            val ua = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
              .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(true)
              .unsafeSetVal(SecurityConsigneeEoriPage(index))("testEori")

            val expected = SecurityTraderEori(EoriNumber("testEori"))

            val result = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

            result.value mustEqual expected
          }

          "when the eori is not known then security consignee is read" in {
            val consigneeAddress = ConsigneeAddress("1", "2", "3", Country(CountryCode("ZZ"), ""))

            val ua = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
              .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(false)
              .unsafeSetVal(SecurityConsigneeNamePage(index))("testName")
              .unsafeSetVal(SecurityConsigneeAddressPage(index))(consigneeAddress)

            val address  = Address("1", "2", "3", Some(Country(CountryCode("ZZ"), "")))
            val expected = SecurityPersonalInformation("testName", address)

            val result = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

            result.value mustEqual expected
          }

        }
      }

    }
  }
}
