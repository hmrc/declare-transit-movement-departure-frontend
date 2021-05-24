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

class AddSecurityConsignorsEoriPageSpec extends SpecBase with PageBehaviours {

  "AddSecurityConsignorsEoriPage" - {

    beRetrievable[Boolean](AddSecurityConsignorsEoriPage(index))

    beSettable[Boolean](AddSecurityConsignorsEoriPage(index))

    beRemovable[Boolean](AddSecurityConsignorsEoriPage(index))
  }

  "cleanup" - {

    "must remove ConsignorAddressPage and ConsignorNamePage when there is a change of the answer to 'Yes'" in {

      val consignorAddress = arbitrary[CommonAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(SecurityConsignorNamePage(index), "answer")
            .success
            .value
            .set(SecurityConsignorAddressPage(index), consignorAddress)
            .success
            .value
            .set(AddSecurityConsignorsEoriPage(index), true)
            .success
            .value

          result.get(SecurityConsignorNamePage(index)) must not be defined
          result.get(SecurityConsignorAddressPage(index)) must not be defined
      }
    }

    "must remove ConsignorEoriPage when there is a change of the answer to 'No'" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(SecurityConsignorEoriPage(index), "GB123456")
            .success
            .value
            .set(AddSecurityConsignorsEoriPage(index), false)
            .success
            .value

          result.get(SecurityConsignorEoriPage(index)) must not be defined
      }
    }
  }
}
