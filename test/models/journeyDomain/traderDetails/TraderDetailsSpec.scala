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
import models.journeyDomain.traderDetails.TraderDetailsSpec.setTraderDetails
import models.{ProcedureType, UserAnswers}
import org.scalatest.TryValues
import pages.{ConsignorEoriPage, _}

class TraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators with UserAnswersSpecHelper {

  "Parsing TraderDetail from UserAnswers" - {
    "for Normal" in {
      forAll(arb[UserAnswers], genTraderDetailsNormal) {
        (baseUserAnswers, traderDetails) =>
          val userAnswers = setTraderDetails(traderDetails)(
            baseUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
          )

          val result = UserAnswersReader[TraderDetails].run(userAnswers).right.value

          result mustEqual traderDetails
      }
    }

    "for Simplified" in {
      forAll(arb[UserAnswers], genTraderDetailsSimplified) {
        (baseUserAnswers, traderDetails) =>
          val userAnswers = setTraderDetails(traderDetails)(
            baseUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
          )

          val result = UserAnswersReader[TraderDetails].run(userAnswers).right.value

          result mustEqual traderDetails
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
        case PrincipalTraderPersonalInfo(_, address)        => Address.prismAddressToPrincipalAddress.getOption(address).get
        case PrincipalTraderEoriPersonalInfo(_, _, address) => Address.prismAddressToPrincipalAddress.getOption(address).get
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
