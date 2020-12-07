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

package pages.safetyAndSecurity

import models.{ConsignorAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddSafetyAndSecurityConsignorEoriPageSpec extends PageBehaviours {

  "AddSafetyAndSecurityConsignorEoriPage" - {

    beRetrievable[Boolean](AddSafetyAndSecurityConsignorEoriPage)

    beSettable[Boolean](AddSafetyAndSecurityConsignorEoriPage)

    beRemovable[Boolean](AddSafetyAndSecurityConsignorEoriPage)
  }

  "cleanup" - {
    "must clean up the consignor eori details on selecting option 'No' " in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(SafetyAndSecurityConsignorEoriPage, "GB000000")
            .success
            .value
            .set(AddSafetyAndSecurityConsignorEoriPage, false)
            .success
            .value

          updatedAnswers.get(SafetyAndSecurityConsignorEoriPage) must not be defined
      }
    }
  }
  "cleanup" - {
    "must clean up the consignor name and address details on selecting option 'No' " in {
      val consignorAddress = arbitrary[ConsignorAddress].sample.value

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(SafetyAndSecurityConsignorNamePage, "TestName")
            .success
            .value
            .set(SafetyAndSecurityConsignorAddressPage, consignorAddress)
            .success
            .value
            .set(AddSafetyAndSecurityConsignorEoriPage, true)
            .success
            .value

          updatedAnswers.get(SafetyAndSecurityConsignorNamePage) must not be defined
          updatedAnswers.get(SafetyAndSecurityConsignorAddressPage) must not be defined
      }
    }
  }
}
