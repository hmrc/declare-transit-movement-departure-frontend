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

package derivable

import base.{SpecBase, UserAnswersSpecHelper}
import models.Index
import models.reference.CountryCode
import pages.{AddAnotherTransitOfficePage, OfficeOfTransitCountryPage}

class DeriveOfficesOfTransitIdsSpec extends SpecBase with UserAnswersSpecHelper {

  "when there are no answer for any question in the routeDetailsOfficesOfTransit loop" - {
    "returns None" in {
      val userAnswers = emptyUserAnswers
      val result      = userAnswers.get(DeriveOfficesOfTransitIds)
      result mustBe None
    }
  }

  "when there are answer for questions in the routeDetailsOfficesOfTransit loop" - {
    "when all office ids are answered and there are no incomplete loops from user answers" in {
      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("officeId0")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(1)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(1)))("officeId1")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(2)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(2)))("officeId2")
      val result = userAnswers.get(DeriveOfficesOfTransitIds).value
      result must contain theSameElementsInOrderAs Seq("officeId0", "officeId1", "officeId2")
    }

    "when there is a single incomplete loop and Offices Of Transit hasn't been answered, returns None" in {
      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("TT"))
      val result = userAnswers.get(DeriveOfficesOfTransitIds).value
      result mustBe Seq.empty
    }

    "when there complete loops and an incomplete loop, returns a list with an office id" in {
      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(0)))(CountryCode("GB"))
        .unsafeSetVal(AddAnotherTransitOfficePage(Index(0)))("officeId0")
        .unsafeSetVal(OfficeOfTransitCountryPage(Index(1)))(CountryCode("GB"))
      val result = userAnswers.get(DeriveOfficesOfTransitIds).value
      result mustBe List("officeId0")
    }
  }

}
