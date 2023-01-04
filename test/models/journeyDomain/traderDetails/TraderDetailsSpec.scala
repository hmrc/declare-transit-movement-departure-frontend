/*
 * Copyright 2023 HM Revenue & Customs
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
import models.{CommonAddress, EoriNumber, ProcedureType}
import org.scalatest.TryValues
import pages._
import pages.traderDetails._

class TraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with UserAnswersSpecHelper {

  private val traderDetailsUa = emptyUserAnswers
    .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
    .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
    .unsafeSetVal(WhatIsPrincipalEoriPage)("eoriNumber")
    .unsafeSetVal(AddConsignorPage)(false)
    .unsafeSetVal(AddConsigneePage)(false)

  "TraderDetails" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory answers have been defined" in {

        val expectedResult = TraderDetails(
          PrincipalTraderDetails(EoriNumber("eoriNumber"), None),
          None,
          None
        )

        val result = UserAnswersReader[TraderDetails].run(traderDetailsUa).value

        result mustBe expectedResult
      }

      "when all mandatory answers have been defined with Consignor details" in {

        val expectedResult = TraderDetails(
          PrincipalTraderDetails(EoriNumber("eoriNumber"), None),
          Some(ConsignorDetails("consignorName", CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")), None)),
          None
        )

        val userAnswers = traderDetailsUa
          .unsafeSetVal(AddConsignorPage)(true)
          .unsafeSetVal(IsConsignorEoriKnownPage)(false)
          .unsafeSetVal(ConsignorNamePage)("consignorName")
          .unsafeSetVal(ConsignorAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))

        val result = UserAnswersReader[TraderDetails].run(userAnswers).value

        result mustBe expectedResult
      }

      "when all mandatory answers have been defined with Consignee details" in {

        val expectedResult = TraderDetails(
          PrincipalTraderDetails(EoriNumber("eoriNumber"), None),
          None,
          Some(ConsigneeDetails("consigneeName", CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")), None))
        )

        val userAnswers = traderDetailsUa
          .unsafeSetVal(AddConsigneePage)(true)
          .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
          .unsafeSetVal(ConsigneeNamePage)("consigneeName")
          .unsafeSetVal(ConsigneeAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))

        val result = UserAnswersReader[TraderDetails].run(userAnswers).value

        result mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when add consignor page is true but consignor is not defined" in {

        val userAnswers = traderDetailsUa
          .unsafeSetVal(AddConsignorPage)(true)
          .unsafeRemove(IsConsignorEoriKnownPage)
          .unsafeRemove(ConsignorNamePage)
          .unsafeRemove(ConsignorAddressPage)

        val result = UserAnswersReader[TraderDetails].run(userAnswers).left.value

        result.page mustBe IsConsignorEoriKnownPage
      }

      "when add consignee page is true but consignee is not defined" in {
        val userAnswers = traderDetailsUa
          .unsafeSetVal(AddConsigneePage)(true)
          .unsafeRemove(IsConsigneeEoriKnownPage)
          .unsafeRemove(ConsigneeNamePage)
          .unsafeRemove(ConsigneeAddressPage)

        val result = UserAnswersReader[TraderDetails].run(userAnswers).left.value

        result.page mustBe IsConsigneeEoriKnownPage
      }
    }
  }
}
