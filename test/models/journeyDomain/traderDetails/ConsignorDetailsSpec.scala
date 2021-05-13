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
import models.{ConsignorAddress, EoriNumber, UserAnswers}
import org.scalatest.TryValues
import pages.{ConsignorEoriPage, _}

class ConsignorDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators with UserAnswersSpecHelper {

  "Parsing ConsignorDetails from UserAnswers" - {

    "when the eori is known" - {
      "when name, address and eori are answered" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(true)
              .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeSetVal(ConsignorAddressPage)(address)

            val result = UserAnswersReader[ConsignorDetails].run(userAnswers).right.value

            val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

            result mustEqual ConsignorDetails(name, expectedAddress, Some(EoriNumber(eoriNumber)))

        }
      }

      "when name and eori are answered but address is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength)) {
          case (baseUserAnswers, EoriNumber(eoriNumber), name) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(true)
              .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeRemove(ConsignorAddressPage)

            val result = UserAnswersReader[ConsignorDetails].run(userAnswers).left.value

            result.page mustBe ConsignorAddressPage
        }
      }

      "when address and eori are answered but name is missing" in {
        forAll(arb[UserAnswers], arb[EoriNumber], arb[ConsignorAddress]) {
          case (baseUserAnswers, EoriNumber(eoriNumber), address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(true)
              .unsafeSetVal(ConsignorEoriPage)(eoriNumber)
              .unsafeSetVal(ConsignorAddressPage)(address)
              .unsafeRemove(ConsignorNamePage)

            val result = UserAnswersReader[ConsignorDetails].run(userAnswers).left.value

            result.page mustBe ConsignorNamePage
        }
      }

      "when name and address are answered but eori is missing" in {
        forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
          case (baseUserAnswers, name, address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(true)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeSetVal(ConsignorAddressPage)(address)
              .unsafeRemove(ConsignorEoriPage)

            val result = UserAnswersReader[ConsignorDetails].run(userAnswers).left.value

            result.page mustBe ConsignorEoriPage
        }
      }
    }

    "when the eori is not known" - {
      "when name, address are answered" in {
        forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
          case (baseUserAnswers, name, address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(false)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeSetVal(ConsignorAddressPage)(address)

            val result = UserAnswersReader[ConsignorDetails].run(userAnswers).right.value

            val expectedAddress: Address = Address.prismAddressToConsignorAddress(address)

            result mustEqual ConsignorDetails(name, expectedAddress, None)
        }
      }

      "when name is answered but address is missing" in {
        forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength)) {
          case (baseUserAnswers, name) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(false)
              .unsafeSetVal(ConsignorNamePage)(name)
              .unsafeRemove(ConsignorAddressPage)

            val result = UserAnswersReader[ConsignorDetails].run(userAnswers).left.value

            result.page mustBe ConsignorAddressPage
        }
      }

      "when address is answered but name is missing" in {
        forAll(arb[UserAnswers], arb[ConsignorAddress]) {
          case (baseUserAnswers, address) =>
            val userAnswers = baseUserAnswers
              .unsafeSetVal(AddConsignorPage)(true)
              .unsafeSetVal(IsConsignorEoriKnownPage)(false)
              .unsafeSetVal(ConsignorAddressPage)(address)
              .unsafeRemove(ConsignorNamePage)

            val result = UserAnswersReader[ConsignorDetails].run(userAnswers).left.value

            result.page mustBe ConsignorNamePage
        }
      }
    }
  }

}
