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

package pages.addItems.traderDetails

import base.SpecBase
import models.{CommonAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.CommonAddItemsAddressPage
import pages.behaviours.PageBehaviours

class TraderDetailsConsigneeEoriKnownPageSpec extends PageBehaviours with SpecBase {

  "TraderDetailsConsigneeEoriKnownPage" - {

    beRetrievable[Boolean](TraderDetailsConsigneeEoriKnownPage(index))

    beSettable[Boolean](TraderDetailsConsigneeEoriKnownPage(index))

    beRemovable[Boolean](TraderDetailsConsigneeEoriKnownPage(index))
  }

  "cleanup" - {

    "must remove TraderDetailsConsigneeEoriNumberPage when EORI not known in in userAnswers" in {

      val consigneeAddress = arbitrary[CommonAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(TraderDetailsConsigneeEoriNumberPage(index), "GB0010")
            .success
            .value
            .set(TraderDetailsConsigneeNamePage(index), "answer")
            .success
            .value
            .set(CommonAddItemsAddressPage(index, "traderDetailsConsigneeAddress"), consigneeAddress)
            .success
            .value
            .set(TraderDetailsConsigneeEoriKnownPage(index), false)
            .success
            .value

          result.get(TraderDetailsConsigneeEoriNumberPage(index)) must not be defined
      }
    }
  }
}
