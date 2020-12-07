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

import models.{CarrierAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddCarrierPageSpec extends PageBehaviours {

  "AddCarrierPage" - {

    beRetrievable[Boolean](AddCarrierPage)

    beSettable[Boolean](AddCarrierPage)

    beRemovable[Boolean](AddCarrierPage)
  }

  "cleanup" - {

    "must remove Carrier details when they exist in userAnswers" in {
      val carrierAddress = arbitrary[CarrierAddress].sample.value

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(AddCarrierEoriPage, true)
            .success
            .value
            .set(CarrierEoriPage, "GB123456")
            .success
            .value
            .set(CarrierNamePage, "test name")
            .success
            .value
            .set(CarrierAddressPage, carrierAddress)
            .success
            .value
            .set(AddCarrierPage, false)
            .success
            .value

          result.get(AddCarrierEoriPage) must not be defined
          result.get(CarrierEoriPage) must not be defined
          result.get(CarrierNamePage) must not be defined
          result.get(CarrierAddressPage) must not be defined

      }
    }
  }
}
