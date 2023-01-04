/*
 * Copyright 2023 HM Revenue & Customs
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

package pages

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class InlandModePageSpec extends PageBehaviours {

  "InlandModePage" - {

    beRetrievable[String](InlandModePage)

    beSettable[String](InlandModePage)

    beRemovable[String](InlandModePage)

    "cleanup" - {

      "must remove AddIdAtDeparturePage,IdAtDeparturePage, AddNationalityAtDeparturePage, NationalityAtDeparturePage whenInland Mode is changes to 5" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result = userAnswers
              .set(InlandModePage, "5")
              .success
              .value
            result.get(AddIdAtDeparturePage) must not be defined
            result.get(IdAtDeparturePage) must not be defined
            result.get(AddNationalityAtDeparturePage) must not be defined
            result.get(NationalityAtDeparturePage) must not be defined
        }
      }
    }
  }
}
