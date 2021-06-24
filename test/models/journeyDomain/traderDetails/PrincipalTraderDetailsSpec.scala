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
import models.{CommonAddress, EoriNumber, ProcedureType}
import org.scalatest.TryValues
import pages._

class PrincipalTraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators with UserAnswersSpecHelper {

  "PrincipleTraderDetails" - {

    "can be parsed from UserAnswers" - {

      "when Procedure type in Normal" - {

        "and Eori is answered in prefix is GB" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
            .unsafeSetVal(WhatIsPrincipalEoriPage)("GB123456")

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).right.value

          val expectedResult = PrincipalTraderEoriInfo(EoriNumber("GB123456"))

          result mustEqual expectedResult

        }

        "and Eori is answered in prefix is not GB" in {

          val address = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
            .unsafeSetVal(WhatIsPrincipalEoriPage)(eoriNumber.value)
            .unsafeSetVal(PrincipalNamePage)("principleName")
            .unsafeSetVal(PrincipalAddressPage)(address)

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).right.value

          val expectedResult = PrincipalTraderEoriPersonalInfo(eoriNumber, "principleName", address)

          result mustBe expectedResult
        }

        "and principal trader name and address are answered without eori" in {

          val address = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
            .unsafeSetVal(PrincipalNamePage)("principleName")
            .unsafeSetVal(PrincipalAddressPage)(address)

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).right.value

          val expectedResult = PrincipalTraderDetails("principleName", address)

          result mustBe expectedResult
        }
      }

      "when procedure type is Simplified" - {

        "and Eori is answered" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
            .unsafeSetVal(WhatIsPrincipalEoriPage)("GB123456")

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).right.value

          val expectedResult = PrincipalTraderEoriInfo(EoriNumber("GB123456"))

          result mustBe expectedResult
        }
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when procedure type is missing" in {
        val result = UserAnswersReader[PrincipalTraderDetails].run(emptyUserAnswers).left.value

        result.page mustBe ProcedureTypePage
      }

      "when procedure type is Normal" - {

        "and principle eori is know but eori is missing" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
            .unsafeRemove(WhatIsPrincipalEoriPage)

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).left.value

          result.page mustEqual WhatIsPrincipalEoriPage
        }

        "and principle address is missing" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
            .unsafeSetVal(PrincipalNamePage)("principleName")
            .unsafeRemove(PrincipalAddressPage)

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).left.value

          result.page mustBe PrincipalAddressPage
        }

        "and principle name is missing" in {

          val address = CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description"))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
            .unsafeSetVal(PrincipalAddressPage)(address)
            .unsafeRemove(PrincipalNamePage)

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).left.value

          result.page mustBe PrincipalNamePage
        }

        "and Principal Eori known page is missing" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(PrincipalNamePage)("principleName")
            .unsafeSetVal(PrincipalAddressPage)(CommonAddress("addressLine1", "addressLine2", "postalCode", Country(CountryCode("GB"), "description")))
            .unsafeSetVal(WhatIsPrincipalEoriPage)("principleEori")
            .unsafeRemove(IsPrincipalEoriKnownPage)

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).left.value

          result.page mustBe IsPrincipalEoriKnownPage
        }
      }

      "when procedure type is Simplified" - {

        "and Eori is missing" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
            .unsafeRemove(WhatIsPrincipalEoriPage)

          val result = UserAnswersReader[PrincipalTraderDetails].run(userAnswers).left.value

          result.page mustEqual WhatIsPrincipalEoriPage
        }
      }
    }
  }
}
