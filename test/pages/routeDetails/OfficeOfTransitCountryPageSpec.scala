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

package pages.routeDetails

import models.Index
import models.reference.CountryCode
import pages.behaviours.PageBehaviours

class OfficeOfTransitCountryPageSpec extends PageBehaviours {

  val index = Index(0)

  "OfficeOfTransitCountryPage" - {

    beRetrievable[CountryCode](OfficeOfTransitCountryPage(index))

    beSettable[CountryCode](OfficeOfTransitCountryPage(index))

    beRemovable[CountryCode](OfficeOfTransitCountryPage(index))

    clearDownItems[CountryCode](OfficeOfTransitCountryPage(index))
  }
}
