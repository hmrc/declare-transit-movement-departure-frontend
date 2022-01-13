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
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import pages.DeclarationTypePage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CountriesServiceSpec extends SpecBase with BeforeAndAfterEach with UserAnswersSpecHelper {

  val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  val service                                      = new CountriesService(mockRefDataConnector)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CountriesService" - {

    "getDestinationCountryList" - {

      val expectedResult: CountryList = CountryList(
        Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )
      )

      "Call EU Membership list if TIR is selection" in {

        val userAnswers = emptyUserAnswers.unsafeSetVal(DeclarationTypePage)(DeclarationType.Option4)

        when(mockRefDataConnector.getCountriesWithCustomsOfficesAndEuMembership(any())(any(), any())).thenReturn(Future.successful(expectedResult))

        service.getDestinationCountryList(userAnswers, Seq.empty).futureValue mustBe expectedResult

        verify(mockRefDataConnector, times(0)).getCountriesWithCustomsOfficesAndCTCMembership(any())(any(), any())
        verify(mockRefDataConnector, times(1)).getCountriesWithCustomsOfficesAndEuMembership(any())(any(), any())
      }

      "Call CTC Membership list if TIR is not selection" in {
        val generatedOption = Gen.oneOf(DeclarationType.Option1, DeclarationType.Option2, DeclarationType.Option3).sample.value
        val userAnswers     = emptyUserAnswers.unsafeSetVal(DeclarationTypePage)(generatedOption)

        when(mockRefDataConnector.getCountriesWithCustomsOfficesAndCTCMembership(any())(any(), any())).thenReturn(Future.successful(expectedResult))

        service.getDestinationCountryList(userAnswers, Seq.empty).futureValue mustBe expectedResult

        verify(mockRefDataConnector, times(1)).getCountriesWithCustomsOfficesAndCTCMembership(any())(any(), any())
        verify(mockRefDataConnector, times(0)).getCountriesWithCustomsOfficesAndEuMembership(any())(any(), any())
      }
    }

    val country1          = Country(CountryCode("GB"), "United Kingdom")
    val country2          = Country(CountryCode("FR"), "France")
    val country3          = Country(CountryCode("ES"), "Spain")
    val country4          = Country(CountryCode("IT"), "Italy")
    val country5          = Country(CountryCode("DE"), "Germany")
    val countries         = Seq(country1, country2, country3)
    val excludedCountries = Seq(country4.code, country5.code)

    "getCountriesWithCustomsOfficesAndCtcMembership" - {
      "must return a list of sorted countries with customs offices and CTC membership" in {

        when(mockRefDataConnector.getCountries(any())(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getCountriesWithCustomsOfficesAndCtcMembership(excludedCountries).futureValue mustBe
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

    "getCountriesWithCustomsOfficesAndEuMembership" - {
      "must return a list of sorted countries with customs offices and EU membership" in {

        when(mockRefDataConnector.getCountries(any())(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getCountriesWithCustomsOfficesAndEuMembership(excludedCountries).futureValue mustBe
          CountryList(Seq(country2, country3, country1))

        val expectedQueryParameters = Seq(
          "customsOfficeRole" -> "ANY",
          "exclude"           -> "IT",
          "exclude"           -> "DE",
          "membership"        -> "eu"
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
  }
}
