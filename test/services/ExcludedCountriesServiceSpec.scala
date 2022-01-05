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

package services

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.routeDetails.{alwaysExcludedTransitCountries, gbExcludedCountries}
import models.reference.{CountryCode, CustomsOffice}
import org.scalatest.matchers.must.Matchers
import pages.OfficeOfDeparturePage

class ExcludedCountriesServiceSpec extends SpecBase with Matchers with UserAnswersSpecHelper {

  "ExcludedCountriesService" - {

    "routeDetailsExcludedCountries" - {

      "must only return NI excluded countries when OfficeOfDeparturePage is 'XI'" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))

        val expectedResult = alwaysExcludedTransitCountries

        val result = ExcludedCountriesService.routeDetailsExcludedCountries(userAnswers)

        result.value mustBe expectedResult
      }

      "must only return GB excluded countries when OfficeOfDeparturePage anything else" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))

        val expectedResult = alwaysExcludedTransitCountries ++ gbExcludedCountries

        val result = ExcludedCountriesService.routeDetailsExcludedCountries(userAnswers)

        result.value mustBe expectedResult
      }

      "must return none when OfficeOfDeparturePage is not defined" in {

        val result = ExcludedCountriesService.routeDetailsExcludedCountries(emptyUserAnswers)

        result mustBe None
      }
    }
  }
}
