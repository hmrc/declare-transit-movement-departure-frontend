/*
 * Copyright 2020 HM Revenue & Customs
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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase}
import models.journeyDomain.TraderDetails.{PersonalInformation, TraderEori}
import models.{ConsigneeAddress, ConsignorAddress, EoriNumber, PrincipalAddress, UserAnswers}
import org.scalatest.TryValues
import pages._

class TraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues {

  "TraderDetail can be parser from UserAnswers" - {
    "when there is a principal trader eori details only" in {
      forAll(arb[UserAnswers], arb[EoriNumber]) {
        case (baseUserAnswers, eori @ EoriNumber(eoriNumber)) =>
          val userAnswers = baseUserAnswers
            .set(IsPrincipalEoriKnownPage, true)
            .success
            .value
            .set(WhatIsPrincipalEoriPage, eoriNumber)
            .success
            .value
            .set(AddConsigneePage, false)
            .success
            .value
            .set(AddConsignorPage, false)
            .success
            .value

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result mustEqual TraderDetails(TraderEori(eori), None, None)

      }
    }

    "when there is a principal trader name and address only" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[PrincipalAddress]) {
        case (baseUserAnswers, name, principalAddress) =>
          val userAnswers = baseUserAnswers
            .set(IsPrincipalEoriKnownPage, false)
            .success
            .value
            .set(PrincipalNamePage, name)
            .success
            .value
            .set(PrincipalAddressPage, principalAddress)
            .success
            .value
            .set(AddConsigneePage, false)
            .success
            .value
            .set(AddConsignorPage, false)
            .success
            .value

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result mustEqual TraderDetails(PersonalInformation(name, principalAddress), None, None)

      }
    }

    "when there is consignor eori details" in {
      forAll(arb[UserAnswers], arb[EoriNumber], arb[EoriNumber]) {
        case (baseUserAnswers, eori1 @ EoriNumber(eoriNumber1), eori2 @ EoriNumber(eoriNumber2)) =>
          val userAnswers = baseUserAnswers
            .set(IsPrincipalEoriKnownPage, true)
            .success
            .value
            .set(WhatIsPrincipalEoriPage, eoriNumber1)
            .success
            .value
            .set(AddConsigneePage, false)
            .success
            .value
            .set(AddConsignorPage, true)
            .success
            .value
            .set(IsConsignorEoriKnownPage, true)
            .success
            .value
            .set(ConsignorEoriPage, eoriNumber2)
            .success
            .value

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result.consignor.value mustEqual TraderEori(eori2)

      }
    }

    "when there is consignor name and address" in {
      forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsignorAddress]) {
        case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
          val userAnswers = baseUserAnswers
            .set(IsPrincipalEoriKnownPage, true)
            .success
            .value
            .set(WhatIsPrincipalEoriPage, eoriNumber)
            .success
            .value
            .set(AddConsigneePage, false)
            .success
            .value
            .set(AddConsignorPage, true)
            .success
            .value
            .set(IsConsignorEoriKnownPage, false)
            .success
            .value
            .set(ConsignorNamePage, name)
            .success
            .value
            .set(ConsignorAddressPage, address)
            .success
            .value

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result.consignor.value mustEqual PersonalInformation(name, address.principalAddressDoNotMerge)

      }
    }

    "when there is consignee eori details" in {
      forAll(arb[UserAnswers], arb[EoriNumber], arb[EoriNumber]) {
        case (baseUserAnswers, eori1 @ EoriNumber(eoriNumber1), eori2 @ EoriNumber(eoriNumber2)) =>
          val userAnswers = baseUserAnswers
            .set(IsPrincipalEoriKnownPage, true)
            .success
            .value
            .set(WhatIsPrincipalEoriPage, eoriNumber1)
            .success
            .value
            .set(AddConsignorPage, false)
            .success
            .value
            .set(AddConsigneePage, true)
            .success
            .value
            .set(IsConsigneeEoriKnownPage, true)
            .success
            .value
            .set(WhatIsConsigneeEoriPage, eoriNumber2)
            .success
            .value

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result.consignee.value mustEqual TraderEori(eori2)

      }
    }

    "when there is consignee name and address" in {
      forAll(arb[UserAnswers], arb[EoriNumber], stringsWithMaxLength(stringMaxLength), arb[ConsigneeAddress]) {
        case (baseUserAnswers, EoriNumber(eoriNumber), name, address) =>
          val userAnswers = baseUserAnswers
            .set(IsPrincipalEoriKnownPage, true)
            .success
            .value
            .set(WhatIsPrincipalEoriPage, eoriNumber)
            .success
            .value
            .set(AddConsignorPage, false)
            .success
            .value
            .set(AddConsigneePage, true)
            .success
            .value
            .set(IsConsigneeEoriKnownPage, false)
            .success
            .value
            .set(ConsigneeNamePage, name)
            .success
            .value
            .set(ConsigneeAddressPage, address)
            .success
            .value

          val result = UserAnswersParser[Option, TraderDetails].run(userAnswers).value

          result.consignee.value mustEqual PersonalInformation(name, address.principalAddressDoNotMerge)

      }
    }

  }
}
