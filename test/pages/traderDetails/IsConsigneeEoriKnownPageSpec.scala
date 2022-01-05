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

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsConsigneeEoriKnownPageSpec extends PageBehaviours {

  "IsConsigneeEoriKnownPage" - {

    beRetrievable[Boolean](IsConsigneeEoriKnownPage)

    beSettable[Boolean](IsConsigneeEoriKnownPage)

    beRemovable[Boolean](IsConsigneeEoriKnownPage)

    clearDownItems[Boolean](IsConsigneeEoriKnownPage)
  }

  "cleanup" - {

    "must remove ConsigneeEoriPage when there is a change of the answer to 'No'" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(IsConsigneeEoriKnownPage, true)
            .success
            .value
            .set(WhatIsConsigneeEoriPage, "GB123456")
            .success
            .value
            .set(IsConsigneeEoriKnownPage, false)
            .success
            .value

          result.get(WhatIsConsigneeEoriPage) must not be defined
      }
    }
  }
}
