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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.domain.Address
import models.journeyDomain.UserAnswersReader
import models.{EoriNumber, PrincipalAddress, ProcedureType, UserAnswers}
import org.scalatest.TryValues
import pages._

class PrincipalTraderSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators with UserAnswersSpecHelper {

  "Parsing PrincipalTrader from UserAnswers" - {

    "when procedure type is Normal" - {

      "when Eori is known has been answered" - {

        "when Eori is answered" in {
          forAll(arb[UserAnswers], arb[EoriNumber]) {
            (baseUserAnswers, eori) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).right.value

              result mustEqual PrincipalTraderEoriInfo(eori)

          }
        }

        "when Eori is missing" in {
          forAll(arb[UserAnswers]) {
            (baseUserAnswers) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeRemove(WhatIsPrincipalEoriPage)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).isLeft

              result mustEqual true
          }
        }
      }

      "when Eori is not known" - {

        "when principal trader name and address are answered" in {
          forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[PrincipalAddress]) {
            case (baseUserAnswers, name, principalAddress) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                .unsafeSetVal(PrincipalNamePage)(name)
                .unsafeSetVal(PrincipalAddressPage)(principalAddress)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).right.value

              val expectedAddress = Address.prismAddressToPrincipalAddress(principalAddress)

              result mustEqual PrincipalTraderDetails(name, expectedAddress)

          }
        }

        "when address is missing" in {
          forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength)) {
            case (baseUserAnswers, name) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                .unsafeSetVal(PrincipalNamePage)(name)
                .unsafeRemove(PrincipalAddressPage)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).isLeft

              result mustEqual true
          }
        }

        "when name is missing" in {
          forAll(arb[UserAnswers], arb[PrincipalAddress]) {
            case (baseUserAnswers, principalAddress) =>
              val userAnswers = baseUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                .unsafeSetVal(PrincipalAddressPage)(principalAddress)
                .unsafeRemove(PrincipalNamePage)

              val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).isLeft

              result mustEqual true
          }
        }
      }

      "when Principal Eori known page is missing" in {
        forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[EoriNumber], arb[PrincipalAddress]) {
          case (baseUserAnswers, name, eori, principalAddress) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
              .unsafeSetVal(PrincipalNamePage)(name)
              .unsafeSetVal(PrincipalAddressPage)(principalAddress)
              .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)
              .unsafeRemove(IsPrincipalEoriKnownPage)

            val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).isLeft

            result mustEqual true
        }
      }
    }

    "when procedure type is Simplified" - {

      "when Eori is answered" in {
        forAll(arb[UserAnswers], arb[EoriNumber]) {
          (baseUserAnswers, eori) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
              .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)

            val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).right.value

            result mustEqual PrincipalTraderEoriInfo(eori)

        }
      }

      "when Eori is missing" in {
        forAll(arb[UserAnswers]) {
          baseUserAnswers =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
              .unsafeRemove(WhatIsPrincipalEoriPage)

            val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).isLeft

            result mustEqual true
        }
      }
    }

    "when procedure type is missing" in {
      forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[PrincipalAddress]) {
        (baseUserAnswers, eori, name, principalAddress) =>
          val userAnswers = baseUserAnswers
            .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
            .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)
            .unsafeSetVal(PrincipalNamePage)(name)
            .unsafeSetVal(PrincipalAddressPage)(principalAddress)
            .unsafeRemove(ProcedureTypePage)

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).isLeft

          result mustEqual true
      }
    }
  }

}
