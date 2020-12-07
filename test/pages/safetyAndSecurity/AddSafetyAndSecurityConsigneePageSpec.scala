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

class AddSafetyAndSecurityConsigneePageSpec extends PageBehaviours {

  "AddSafetyAndSecurityConsigneePage" - {

    beRetrievable[Boolean](AddSafetyAndSecurityConsigneePage)

    beSettable[Boolean](AddSafetyAndSecurityConsigneePage)

    beRemovable[Boolean](AddSafetyAndSecurityConsigneePage)

    "cleanup" - {
      "must clean up the consignee details on selecting option 'No' " in {
        val consigneeAddress = arbitrary[ConsigneeAddress].sample.value

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsigneeEoriPage, false)
              .success
              .value
              .set(SafetyAndSecurityConsigneeEoriPage, "GB000000")
              .success
              .value
              .set(SafetyAndSecurityConsigneeNamePage, "test name")
              .success
              .value
              .set(SafetyAndSecurityConsigneeAddressPage, consigneeAddress)
              .success
              .value
              .set(AddSafetyAndSecurityConsigneePage, false)
              .success
              .value

            updatedAnswers.get(AddSafetyAndSecurityConsigneeEoriPage) must not be defined
            updatedAnswers.get(SafetyAndSecurityConsigneeEoriPage) must not be defined
            updatedAnswers.get(SafetyAndSecurityConsigneeNamePage) must not be defined
            updatedAnswers.get(SafetyAndSecurityConsigneeAddressPage) must not be defined
        }
      }
    }
  }
}
