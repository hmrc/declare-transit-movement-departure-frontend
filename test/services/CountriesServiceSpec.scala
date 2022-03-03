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
import connectors.ReferenceDataConnector
import models.reference.{Country, CountryCode}
import models.{CountryList, DeclarationType}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import pages.DeclarationTypePage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CountriesServiceSpec extends SpecBase with BeforeAndAfterEach with UserAnswersSpecHelper {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new CountriesService(mockRefDataConnector)

  private val country1: Country = Country(CountryCode("GB"), "United Kingdom")
  private val country2: Country = Country(CountryCode("FR"), "France")
  private val country3: Country = Country(CountryCode("ES"), "Spain")
  private val country4: Country = Country(CountryCode("IT"), "Italy")
  private val country5: Country = Country(CountryCode("DE"), "Germany")
  private val countries         = Seq(country1, country2, country3)
  private val excludedCountries = Seq(country4.code, country5.code)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CountriesService" - {

    "getDestinationCountries" - {

      "must call EU membership list if TIR is selection" in {

        val userAnswers = emptyUserAnswers.unsafeSetVal(DeclarationTypePage)(DeclarationType.Option4)

        when(mockRefDataConnector.getCountries(any())(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getDestinationCountries(userAnswers, excludedCountries).futureValue mustBe
          CountryList(Seq(country2, country3, country1))

        val expectedQueryParameters = Seq(
          "customsOfficeRole" -> "ANY",
          "exclude"           -> "IT",
          "exclude"           -> "DE",
          "membership"        -> "eu"
        )

        verify(mockRefDataConnector).getCountries(eqTo(expectedQueryParameters))(any(), any())
      }

      "must call CTC membership list if TIR is not selection" in {

        val generatedOption = Gen.oneOf(DeclarationType.Option1, DeclarationType.Option2, DeclarationType.Option3).sample.value
        val userAnswers     = emptyUserAnswers.unsafeSetVal(DeclarationTypePage)(generatedOption)

        when(mockRefDataConnector.getCountries(any())(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getDestinationCountries(userAnswers, excludedCountries).futureValue mustBe
          CountryList(Seq(country2, country3, country1))

        val expectedQueryParameters = Seq(
          "customsOfficeRole" -> "ANY",
          "exclude"           -> "IT",
          "exclude"           -> "DE",
          "membership"        -> "ctc"
        )

        verify(mockRefDataConnector).getCountries(eqTo(expectedQueryParameters))(any(), any())
      }
    }

    "getCountriesWithCustomsOffices" - {
      "must return a list of sorted countries with customs offices" in {

        when(mockRefDataConnector.getCountries(any())(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getCountriesWithCustomsOffices(excludedCountries).futureValue mustBe
          CountryList(Seq(country2, country3, country1))

        val expectedQueryParameters = Seq(
          "customsOfficeRole" -> "ANY",
          "exclude"           -> "IT",
          "exclude"           -> "DE"
        )

        verify(mockRefDataConnector).getCountries(eqTo(expectedQueryParameters))(any(), any())
      }
    }

    "getCountries" - {
      "must return a list of sorted countries" in {

        when(mockRefDataConnector.getCountries(any())(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getCountries().futureValue mustBe
          CountryList(Seq(country2, country3, country1))

        verify(mockRefDataConnector).getCountries(eqTo(Nil))(any(), any())
      }
    }

    "getTransitCountries" - {
      "must return a list of sorted transit countries" in {

        when(mockRefDataConnector.getCountries(any())(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getTransitCountries(excludedCountries).futureValue mustBe
          CountryList(Seq(country2, country3, country1))

        val expectedQueryParameters = Seq(
          "exclude"    -> "IT",
          "exclude"    -> "DE",
          "membership" -> "ctc"
        )

        verify(mockRefDataConnector).getCountries(eqTo(expectedQueryParameters))(any(), any())
      }
    }

    "getNonEuTransitCountries" - {
      "must return a list of sorted non-EU transit countries" in {

        when(mockRefDataConnector.getCountries(any())(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getNonEuTransitCountries().futureValue mustBe
          CountryList(Seq(country2, country3, country1))

        val expectedQueryParameters = Seq(
          "membership" -> "non_eu"
        )

        verify(mockRefDataConnector).getCountries(eqTo(expectedQueryParameters))(any(), any())
      }
    }
  }
}
