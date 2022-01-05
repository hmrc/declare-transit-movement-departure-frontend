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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import pages.DeclarationTypePage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CountriesServiceSpec extends SpecBase with BeforeAndAfterEach with UserAnswersSpecHelper {

  val mockRefDataConnector = mock[ReferenceDataConnector]

  val expectedResult: CountryList = CountryList(
    Seq(
      Country(CountryCode("GB"), "United Kingdom"),
      Country(CountryCode("AD"), "Andorra")
    )
  )

  val service = new CountriesService(mockRefDataConnector)

  override def beforeEach = {
    reset(mockRefDataConnector)
    super.beforeEach
  }

  "CountriesService" - {
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
}
