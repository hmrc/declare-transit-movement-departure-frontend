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

package pages.addItems.traderDetails

import base.SpecBase
import models.{CommonAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class TraderDetailsConsignorEoriKnownPageSpec extends PageBehaviours with SpecBase {

  "TraderDetailsConsignorEoriKnownPage" - {

    beRetrievable[Boolean](TraderDetailsConsignorEoriKnownPage(index))

    beSettable[Boolean](TraderDetailsConsignorEoriKnownPage(index))

    beRemovable[Boolean](TraderDetailsConsignorEoriKnownPage(index))
  }

  "cleanup" - {

    "must remove TraderDetailsConsignorEoriNumberPage when EORI not known in in userAnswers" in {

      val consignorAddress = arbitrary[CommonAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(TraderDetailsConsignorEoriKnownPage(index), true)
            .success
            .value
            .set(TraderDetailsConsignorEoriNumberPage(index), "GB0010")
            .success
            .value
            .set(TraderDetailsConsignorNamePage(index), "answer")
            .success
            .value
            .set(TraderDetailsConsignorAddressPage(index), consignorAddress)
            .success
            .value
            .set(TraderDetailsConsignorEoriKnownPage(index), false)
            .success
            .value

          result.get(TraderDetailsConsignorEoriNumberPage(index)) must not be defined
      }
    }
  }

}
