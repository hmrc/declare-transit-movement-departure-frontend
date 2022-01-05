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

package pages.safetyAndSecurity

import models.Index
import models.reference.CountryCode
import pages.behaviours.PageBehaviours

class CountryOfRoutingPageSpec extends PageBehaviours {

  "CountryOfRoutingPage" - {

    beRetrievable[CountryCode](CountryOfRoutingPage(Index(0)))

    beSettable[CountryCode](CountryOfRoutingPage(Index(0)))

    beRemovable[CountryCode](CountryOfRoutingPage(Index(0)))

    clearDownItems[CountryCode](CountryOfRoutingPage(Index(0)))
  }
}
