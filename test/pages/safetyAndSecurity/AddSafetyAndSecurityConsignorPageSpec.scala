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

import base.SpecBase
import generators.Generators
import models.{ConsignorAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.behaviours.PageBehaviours

class AddSafetyAndSecurityConsignorPageSpec extends SpecBase with PageBehaviours with ScalaCheckPropertyChecks with Generators {

  "AddSafetyAndSecurityConsignorPage" - {

    beRetrievable[Boolean](AddSafetyAndSecurityConsignorPage)

    beSettable[Boolean](AddSafetyAndSecurityConsignorPage)

    beRemovable[Boolean](AddSafetyAndSecurityConsignorPage)

    "cleanup" - {
      "must clean up the consignor details on selecting option 'No' " in {
        val consignorAddress = arbitrary[ConsignorAddress].sample.value

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddSafetyAndSecurityConsignorEoriPage, false)
              .success
              .value
              .set(SafetyAndSecurityConsignorEoriPage, "GB000000")
              .success
              .value
              .set(SafetyAndSecurityConsignorNamePage, "test name")
              .success
              .value
              .set(SafetyAndSecurityConsignorAddressPage, consignorAddress)
              .success
              .value
              .set(AddSafetyAndSecurityConsignorPage, false)
              .success
              .value

            updatedAnswers.get(AddSafetyAndSecurityConsignorEoriPage) must not be defined
            updatedAnswers.get(SafetyAndSecurityConsignorEoriPage) must not be defined
            updatedAnswers.get(SafetyAndSecurityConsignorNamePage) must not be defined
            updatedAnswers.get(SafetyAndSecurityConsignorAddressPage) must not be defined
        }
      }
    }
  }
}
