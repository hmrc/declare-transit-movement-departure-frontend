/*
 * Copyright 2022 HM Revenue & Customs
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

package pages.traderDetails

import models.{CommonAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddConsignorPageSpec extends PageBehaviours {

  "AddConsignorPage" - {

    beRetrievable[Boolean](AddConsignorPage)

    beSettable[Boolean](AddConsignorPage)

    beRemovable[Boolean](AddConsignorPage)

    clearDownItems[Boolean](AddConsignorPage)
  }

  "cleanup" - {

    "must remove ConsignorAddressPage, ConsignorNamePage and ConsignorEoriPage when they exist in userAnswers" in {

      val consignorAddress = arbitrary[CommonAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(AddConsignorPage, false)
            .success
            .value
            .set(ConsignorNamePage, "answer")
            .success
            .value
            .set(ConsignorAddressPage, consignorAddress)
            .success
            .value
            .set(ConsignorEoriPage, "GB123456")
            .success
            .value
            .set(AddConsignorPage, true)
            .success
            .value

          result.get(ConsignorNamePage) must not be defined
          result.get(ConsignorAddressPage) must not be defined
          result.get(ConsignorEoriPage) must not be defined

      }
    }

  }

}
