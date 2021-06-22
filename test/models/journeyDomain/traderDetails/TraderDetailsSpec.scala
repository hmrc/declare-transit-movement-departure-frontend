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
import models.ProcedureType.Simplified
import models.domain.Address
import models.journeyDomain.UserAnswersReader
import models.reference.{Country, CountryCode}
import models.{CommonAddress, EoriNumber, ProcedureType, UserAnswers}
import org.scalatest.TryValues
import pages.{ConsignorEoriPage, _}

class TraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators with UserAnswersSpecHelper {

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
          PrincipalTraderDetails(EoriNumber("eoriNumber")),
          None,
          None
        )

        val result = UserAnswersReader[TraderDetails].run(traderDetailsUa).right.value

        result mustBe expectedResult
      }

      "when all mandatory answers have been defined with Consignor details" in {

        val expectedResult = TraderDetails(
          PrincipalTraderDetails(EoriNumber("eoriNumber")),
          Some(ConsignorDetails("consignorName", Address("addressLine1", "addressLine2", "postalCode", Some(Country(CountryCode("GB"), "123"))), None)),
          None
        )

        val userAnswers = traderDetailsUa
          .unsafeSetVal(AddConsignorPage)(true)
          .unsafeSetVal(IsConsignorEoriKnownPage)(false)
          .unsafeSetVal(ConsignorNamePage)("consignorName")
          .unsafeSetVal(ConsignorAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))

        val result = UserAnswersReader[TraderDetails].run(userAnswers).right.value

        result mustBe expectedResult
      }

      "when all mandatory answers have been defined with Consignee details" in {

        val expectedResult = TraderDetails(
          PrincipalTraderDetails(EoriNumber("eoriNumber")),
          None,
          Some(ConsigneeDetails("consigneeName", Address("addressLine1", "addressLine2", "postalCode", Some(Country(CountryCode("GB"), "123"))), None))
        )

        val userAnswers = traderDetailsUa
          .unsafeSetVal(AddConsigneePage)(true)
          .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
          .unsafeSetVal(ConsigneeNamePage)("consigneeName")
          .unsafeSetVal(ConsigneeAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "123")))

        val result = UserAnswersReader[TraderDetails].run(userAnswers).right.value

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

object TraderDetailsSpec extends UserAnswersSpecHelper {

  def setTraderDetails(traderDetails: TraderDetails)(startUserAnswers: UserAnswers): UserAnswers = {
    val isPrincipalEoriKnown: Boolean = traderDetails.principalTraderDetails.isInstanceOf[PrincipalTraderEoriInfo] | traderDetails.principalTraderDetails
      .isInstanceOf[PrincipalTraderEoriPersonalInfo]

    startUserAnswers
      // Set Principal Trader details
      .unsafeSetVal(IsPrincipalEoriKnownPage)(isPrincipalEoriKnown)
      .unsafeSetPFn(WhatIsPrincipalEoriPage)(traderDetails.principalTraderDetails)({
        case PrincipalTraderEoriInfo(eori)               => eori.value
        case PrincipalTraderEoriPersonalInfo(eori, _, _) => eori.value
      })
      .unsafeSetPFn(PrincipalNamePage)(traderDetails.principalTraderDetails)({
        case PrincipalTraderPersonalInfo(name, _)        => name
        case PrincipalTraderEoriPersonalInfo(_, name, _) => name
      })
      .unsafeSetPFn(PrincipalAddressPage)(traderDetails.principalTraderDetails)({
        case PrincipalTraderPersonalInfo(_, address)        => Address.prismAddressToCommonAddress.getOption(address).get
        case PrincipalTraderEoriPersonalInfo(_, _, address) => Address.prismAddressToCommonAddress.getOption(address).get
      })
      .assert("Eori must be provided for Simplified procedure") {
        ua =>
          (ua.get(ProcedureTypePage), ua.get(WhatIsPrincipalEoriPage)) match {
            case (Some(Simplified), None) => false
            case _                        => true
          }
      }
      // Set Consignor details
      .unsafeSetVal(AddConsignorPage)(traderDetails.consignor.isDefined)
      .unsafeSetPFn(IsConsignorEoriKnownPage)(traderDetails.consignor)({
        case Some(ConsignorDetails(_, _, eoriOpt)) => eoriOpt.isDefined
      })
      .unsafeSetPFn(ConsignorEoriPage)(traderDetails.consignor)({
        case Some(ConsignorDetails(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(ConsignorNamePage)(traderDetails.consignor)({
        case Some(ConsignorDetails(name, _, _)) => name
      })
      .unsafeSetPFn(ConsignorAddressPage)(traderDetails.consignor)({
        case Some(ConsignorDetails(_, address, _)) => Address.prismAddressToCommonAddress.getOption(address).get
      })
      // Set Consignee details
      .unsafeSetVal(AddConsigneePage)(traderDetails.consignee.isDefined)
      .unsafeSetPFn(IsConsigneeEoriKnownPage)(traderDetails.consignee)({
        case Some(ConsigneeDetails(_, _, eoriOpt)) => eoriOpt.isDefined
      })
      .unsafeSetPFn(WhatIsConsigneeEoriPage)(traderDetails.consignee)({
        case Some(ConsigneeDetails(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(ConsigneeNamePage)(traderDetails.consignee)({
        case Some(ConsigneeDetails(name, _, _)) => name
      })
      .unsafeSetPFn(ConsigneeAddressPage)(traderDetails.consignee)({
        case Some(ConsigneeDetails(_, address, _)) => Address.prismAddressToCommonAddress.getOption(address).get
      })
  }

}
