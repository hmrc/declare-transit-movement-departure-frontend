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
import models.{ConsigneeAddress, ConsignorAddress, EoriNumber}
import models.domain.Address
import models.journeyDomain.EitherType
import models.reference._
import org.scalactic.source
import org.scalatest.Resources
import org.scalatest.exceptions.{StackDepthException, TestFailedException}
import pages.AddSecurityDetailsPage
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddSafetyAndSecurityConsigneePage, AddSafetyAndSecurityConsignorPage, CircumstanceIndicatorPage}

class SecurityTraderDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with UserAnswersSpecHelper {

  "Reading from User Answers" - {

    "Consignee" - {

      "when add security details is 'No' then consignee should be None" in {

        val ua = emptyUserAnswers
          .unsafeSetVal(AddSecurityDetailsPage)(false)

        val result = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

        result mustBe None
      }

      "when add security details is 'Yes'" - {
        "when the consignee for all items is 'Yes' should be None" in {

          val ua = emptyUserAnswers
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)

          val result = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

          result mustBe None
        }

        "when there is not a consignee for all items" - {
          "when the eori is known" - {
            "and user has select 'Yes' for add circumstanceIndicator and did not select E for circumstanceIndicatorPage then the security consignee is read" in {
              val ua = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
                .unsafeSetVal(CircumstanceIndicatorPage)("A")
                .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
                .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(true)
                .unsafeSetVal(SecurityConsigneeEoriPage(index))("testEori")

              val expected = SecurityTraderEori(EoriNumber("testEori"))
              val result   = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

              result.value mustEqual expected
            }

            "and user has select 'Yes' for add circumstanceIndicator and did select E for circumstanceIndicatorPage then the security consignee is read" in {
              val ua = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
                .unsafeSetVal(CircumstanceIndicatorPage)("E")
                .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
                .unsafeSetVal(SecurityConsigneeEoriPage(index))("testEori")

              val expected = SecurityTraderEori(EoriNumber("testEori"))
              val result   = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

              result.value mustEqual expected
            }

            "and user has select 'No' for add circumstanceIndicator then the security consignee is read" in {
              val ua = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
                .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
                .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(true)
                .unsafeSetVal(SecurityConsigneeEoriPage(index))("testEori")

              val expected = SecurityTraderEori(EoriNumber("testEori"))
              val result   = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

              result.value mustEqual expected
            }
          }

          "when the eori is not known" - {

            "when the user selects 'No' for add circumstance indicator page then the consignee is read" in {
              val consigneeAddress = ConsigneeAddress("1", "2", "3", Country(CountryCode("ZZ"), ""))

              val ua = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
                .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
                .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(false)
                .unsafeSetVal(SecurityConsigneeNamePage(index))("testName")
                .unsafeSetVal(SecurityConsigneeAddressPage(index))(consigneeAddress)

              val address  = Address("1", "2", "3", Some(Country(CountryCode("ZZ"), "")))
              val expected = SecurityPersonalInformation("testName", address)
              val result   = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

              result.value mustEqual expected
            }

            "when the user selects 'Yes' for add circumstance indicator page but does  not select 'E' for circumstance indicator then the consignee is read" in {

              val consigneeAddress = ConsigneeAddress("1", "2", "3", Country(CountryCode("ZZ"), ""))

              val ua = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
                .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
                .unsafeSetVal(CircumstanceIndicatorPage)("A")
                .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(false)
                .unsafeSetVal(SecurityConsigneeNamePage(index))("testName")
                .unsafeSetVal(SecurityConsigneeAddressPage(index))(consigneeAddress)

              val address  = Address("1", "2", "3", Some(Country(CountryCode("ZZ"), "")))
              val expected = SecurityPersonalInformation("testName", address)

              val result = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

              result.value mustEqual expected
            }

            "when the user selects 'Yes' for add circumstance indicator page and does select 'E' for circumstance indicator then the consignee is read" in {

              val ua = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
                .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
                .unsafeSetVal(CircumstanceIndicatorPage)("E")
                .unsafeSetVal(AddSecurityConsigneesEoriPage(index))(true)
                .unsafeSetVal(SecurityConsigneeEoriPage(index))("testEori")

              val expected = SecurityTraderEori(EoriNumber("testEori"))
              val result   = SecurityTraderDetails.consigneeDetails(index).run(ua).right.value

              result.value mustEqual expected
            }

          }
        }

      }
    }

    "Consignor" - {

      "when add security details is 'No' then consignor should be None" in {

        val ua = emptyUserAnswers
          .unsafeSetVal(AddSecurityDetailsPage)(false)

        val result = SecurityTraderDetails.consignorDetails(index).run(ua).right.value

        result mustBe None
      }

      "when add security details is 'Yes'" - {
        "when the consignor for all items is 'Yes' should be None" in {

          val ua = emptyUserAnswers
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)

          val result = SecurityTraderDetails.consignorDetails(index).run(ua).right.value

          result mustBe None
        }

        "when there is not a consignor for all items" - {
          "when the eori is known" in {

            val ua = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddSecurityConsignorsEoriPage(index))(true)
              .unsafeSetVal(SecurityConsignorEoriPage(index))("testEori")
              .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)

            val expected = SecurityTraderEori(EoriNumber("testEori"))
            val result   = SecurityTraderDetails.consignorDetails(index).run(ua).isSuccessful

            result.value mustEqual expected
          }

        }

        "when the eori is not known" - {

          "when the user selects 'No' for add circumstance indicator page then the consignee is read" in {
            val consigneeAddress = ConsignorAddress("1", "2", "3", Country(CountryCode("ZZ"), ""))

            val ua = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)
              .unsafeSetVal(AddSecurityConsignorsEoriPage(index))(false)
              .unsafeSetVal(SecurityConsignorNamePage(index))("testName")
              .unsafeSetVal(SecurityConsignorAddressPage(index))(consigneeAddress)

            val address  = Address("1", "2", "3", Some(Country(CountryCode("ZZ"), "")))
            val expected = SecurityPersonalInformation("testName", address)
            val result   = SecurityTraderDetails.consignorDetails(index).run(ua).right.value

            result.value mustEqual expected
          }

        }
      }

    }
  }

}
