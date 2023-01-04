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

package services

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.routeDetails.{alwaysExcludedTransitCountries, gbExcludedCountries}
import models.DeclarationType._
import models.reference.{CountryCode, CustomsOffice}
import org.scalacheck.Gen
import org.scalatest.matchers.must.Matchers
import pages.{DeclarationTypePage, OfficeOfDeparturePage}

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

    "movementDestinationCountryExcludedCountries" - {

      "must return NI excluded countries and also San Marino when OfficeOfDeparturePage is 'XI' and DeclarationTypePage is Option 1 or Option 4" in {

        val declarationType = Gen.oneOf(Option1, Option4).sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))
          .unsafeSetVal(DeclarationTypePage)(declarationType)

        val expectedResult = alwaysExcludedTransitCountries :+ CountryCode("SM")

        val result = ExcludedCountriesService.movementDestinationCountryExcludedCountries(userAnswers)

        result.value mustBe expectedResult
      }

      "must only return NI excluded countries when OfficeOfDeparturePage is 'XI' and DeclarationTypePage is Option 2 or Option 3" in {

        val declarationType = Gen.oneOf(Option2, Option3).sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))
          .unsafeSetVal(DeclarationTypePage)(declarationType)

        val expectedResult = alwaysExcludedTransitCountries

        val result = ExcludedCountriesService.movementDestinationCountryExcludedCountries(userAnswers)

        result.value mustBe expectedResult
      }

      "must only return GB excluded countries when OfficeOfDeparturePage anything else" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))

        val expectedResult = alwaysExcludedTransitCountries ++ gbExcludedCountries

        val result = ExcludedCountriesService.movementDestinationCountryExcludedCountries(userAnswers)

        result.value mustBe expectedResult
      }

      "must return none when OfficeOfDeparturePage and Declaration type are not defined" in {

        val result = ExcludedCountriesService.movementDestinationCountryExcludedCountries(emptyUserAnswers)

        result mustBe None
      }
    }
  }
}
