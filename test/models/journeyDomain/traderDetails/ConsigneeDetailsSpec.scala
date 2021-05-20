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
import models.domain.Address
import models.journeyDomain.UserAnswersReader
import models.{ConsigneeAddress, EoriNumber, UserAnswers}
import org.scalatest.TryValues
import pages._

class ConsigneeDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators with UserAnswersSpecHelper {

  "Parsing ConsigneeDetails from UserAnswers" - {
    "when the eori is known" - {
      "when there is consignee name, address and eori" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
              .unsafeSetVal(WhatIsConsigneeEoriPage)(eoriNumber)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeSetVal(ConsigneeAddressPage)(address)

            val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).right.value

            val expectedAddress: Address = Address.prismAddressToCommonAddress(address)

            result mustEqual ConsigneeDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

        }
      }

      "when name and eori are answered but address is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength)) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
              .unsafeSetVal(WhatIsConsigneeEoriPage)(eoriNumber)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeRemove(ConsigneeAddressPage)

            val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).left.value

            result.page mustEqual ConsigneeAddressPage
        }
      }

      "when address and eori are answered but name is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], arb[ConsigneeAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
              .unsafeSetVal(WhatIsConsigneeEoriPage)(eoriNumber)
              .unsafeSetVal(ConsigneeAddressPage)(address)
              .unsafeRemove(ConsigneeNamePage)

            val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).left.value

            result.page mustEqual ConsigneeNamePage
        }

      }

      "when name and address are answered but eori is missing" in {
        forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
          case (baseUserAnswers, name, address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeSetVal(ConsigneeAddressPage)(address)
              .unsafeRemove(WhatIsConsigneeEoriPage)

            val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).left.value

            result.page mustEqual WhatIsConsigneeEoriPage
        }

      }
    }

    "when the eori is not known" - {
      "when there is consignee nameaddress" in {
        forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
          case (baseUserAnswers, name, address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeSetVal(ConsigneeAddressPage)(address)

            val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).right.value

            val expectedAddress: Address = Address.prismAddressToCommonAddress(address)

            result mustEqual ConsigneeDetails(name, expectedAddress, None)

        }
      }

      "when name is answered but address is missing" in {
        forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength)) {
          case (baseUserAnswers, name) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
              .unsafeSetVal(ConsigneeNamePage)(name)
              .unsafeRemove(ConsigneeAddressPage)

            val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).left.value

            result.page mustEqual ConsigneeAddressPage
        }
      }

      "when address is answered but name is missing" in {
        forAll(arb[UserAnswers], arb[ConsigneeAddress]) {
          case (baseUserAnswers, address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsigneePage)(true)
              .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
              .unsafeSetVal(ConsigneeAddressPage)(address)
              .unsafeRemove(ConsigneeNamePage)

            val result = UserAnswersReader[ConsigneeDetails].run(userAnswers).left.value

            result.page mustEqual ConsigneeNamePage
        }
      }
    }
  }

}
