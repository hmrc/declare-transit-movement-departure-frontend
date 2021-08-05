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

package pages.addItems.traderSecurityDetails

import base.SpecBase
import models.{CommonAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddSecurityConsigneesEoriPageSpec extends SpecBase with PageBehaviours {

  "AddSecurityConsigneesEoriPage" - {

    beRetrievable[Boolean](AddSecurityConsigneesEoriPage(index))

    beSettable[Boolean](AddSecurityConsigneesEoriPage(index))

    beRemovable[Boolean](AddSecurityConsigneesEoriPage(index))
  }

  "cleanup" - {

    "must remove ConsigneeAddressPage and ConsigneeNamePage when there is a change of the answer to 'Yes'" in {

      val consigneeAddress = arbitrary[CommonAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(AddSecurityConsigneesEoriPage(index), false)
            .success
            .value
            .set(SecurityConsigneeNamePage(index), "answer")
            .success
            .value
            .set(SecurityConsigneeAddressPage(index), consigneeAddress)
            .success
            .value
            .set(AddSecurityConsigneesEoriPage(index), true)
            .success
            .value

          result.get(SecurityConsigneeNamePage(index)) must not be defined
          result.get(SecurityConsigneeAddressPage(index)) must not be defined
      }
    }

    "must remove ConsigneeEoriPage when there is a change of the answer to 'No'" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(AddSecurityConsigneesEoriPage(index), true)
            .success
            .value
            .set(SecurityConsigneeEoriPage(index), "GB123456")
            .success
            .value
            .set(AddSecurityConsigneesEoriPage(index), false)
            .success
            .value

          result.get(SecurityConsigneeEoriPage(index)) must not be defined
      }
    }
  }
}
