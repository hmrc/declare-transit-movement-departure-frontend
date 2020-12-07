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

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddPlaceOfUnloadingCodePageSpec extends PageBehaviours {

  "AddPlaceOfUnloadingCodePage" - {

    beRetrievable[Boolean](AddPlaceOfUnloadingCodePage)

    beSettable[Boolean](AddPlaceOfUnloadingCodePage)

    beRemovable[Boolean](AddPlaceOfUnloadingCodePage)
  }

  "cleanup" - {

    "must remove PlaceOfUnloadingCodePage when AddPlaceOfUnloadingCodePage is false" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(PlaceOfUnloadingCodePage, "value")
            .success
            .value
            .set(AddPlaceOfUnloadingCodePage, false)
            .success
            .value

          result.get(PlaceOfUnloadingCodePage) must not be defined
      }
    }
  }
}
