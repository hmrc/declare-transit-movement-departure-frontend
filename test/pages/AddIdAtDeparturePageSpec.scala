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

package pages

import base.SpecBase
import models.reference.CountryCode
import pages.behaviours.PageBehaviours

class AddIdAtDeparturePageSpec extends PageBehaviours with SpecBase {

  "AddIdAtDeparturePage" - {

    beRetrievable[Boolean](AddIdAtDeparturePage)

    beSettable[Boolean](AddIdAtDeparturePage)

    beRemovable[Boolean](AddIdAtDeparturePage)

    "cleanup" - {

      "must remove IdAtDeparturePage, AddNationalityAtDeparturePage,NationalityAtDeparturePage when AddIdAtDeparture changes to 'No'" in {

        val result = emptyUserAnswers
          .set(IdAtDeparturePage, "id")
          .success
          .value
          .set(AddNationalityAtDeparturePage, true)
          .success
          .value
          .set(NationalityAtDeparturePage, CountryCode("GB"))
          .success
          .value
          .set(AddIdAtDeparturePage, false)
          .success
          .value
        result.get(IdAtDeparturePage) must not be defined
        result.get(AddNationalityAtDeparturePage) must not be defined
        result.get(NationalityAtDeparturePage) must not be defined
      }
    }
  }
}
