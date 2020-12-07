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

import models.{ConsigneeAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddSafetyAndSecurityConsigneeEoriPageSpec extends PageBehaviours {

  "AddSafetyAndSecurityConsigneeEoriPage" - {

    beRetrievable[Boolean](AddSafetyAndSecurityConsigneeEoriPage)

    beSettable[Boolean](AddSafetyAndSecurityConsigneeEoriPage)

    beRemovable[Boolean](AddSafetyAndSecurityConsigneeEoriPage)

    "cleanup" - {
      "must clean up the consignee eori details on selecting option 'No' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(SafetyAndSecurityConsigneeEoriPage, "GB000000")
              .success
              .value
              .set(AddSafetyAndSecurityConsigneeEoriPage, false)
              .success
              .value

            updatedAnswers.get(SafetyAndSecurityConsigneeEoriPage) must not be defined
        }
      }
    }
    "cleanup" - {
      "must clean up the consignee name and address details on selecting option 'No' " in {
        val consigneeAddress = arbitrary[ConsigneeAddress].sample.value

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(SafetyAndSecurityConsigneeNamePage, "TestName")
              .success
              .value
              .set(SafetyAndSecurityConsigneeAddressPage, consigneeAddress)
              .success
              .value
              .set(AddSafetyAndSecurityConsigneeEoriPage, true)
              .success
              .value

            updatedAnswers.get(SafetyAndSecurityConsigneeNamePage) must not be defined
            updatedAnswers.get(SafetyAndSecurityConsigneeAddressPage) must not be defined
        }
      }
    }
  }
}
