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

package pages

import generators.Generators
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours
import org.scalacheck.Arbitrary.arbitrary

class LiabilityAmountPageSpec extends PageBehaviours with Generators {

  implicit lazy val arbitraryNonEmptyString: Arbitrary[String] = Arbitrary(nonEmptyString)

  "LiabilityAmountPage" - {

    beRetrievable[String](LiabilityAmountPage)

    beSettable[String](LiabilityAmountPage)

    beRemovable[String](LiabilityAmountPage)
  }
}
