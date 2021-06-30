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

package services

import base.SpecBase
import config.FrontendAppConfig
import connectors.ReferenceDataConnector
import models.CustomsOfficeList
import models.reference.{CountryCode, CustomsOffice}
import org.mockito.Mockito.{reset, times, verify, when}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomsOfficesServiceSpec extends SpecBase with BeforeAndAfterEach {

  val mockRefDataConnector                = mock[ReferenceDataConnector]
  val mockFrontendAppConfig               = mock[FrontendAppConfig]
  val gbCustomsOffice1: CustomsOffice     = CustomsOffice("officeId", "someName", CountryCode("GB"), None)
  val gbCustomsOffice2: CustomsOffice     = CustomsOffice("id", "name", CountryCode("GB"), None)
  val xiCustomsOffice1: CustomsOffice     = CustomsOffice("xi", "ni", CountryCode("XI"), None)
  val gbCustomsOffices: CustomsOfficeList = CustomsOfficeList(Seq(gbCustomsOffice1, gbCustomsOffice2))
  val xiCustomsOffices: CustomsOfficeList = CustomsOfficeList(Seq(xiCustomsOffice1))
  val customsOffices: CustomsOfficeList   = CustomsOfficeList(gbCustomsOffices.getAll ++ xiCustomsOffices.getAll)

  val service = new CustomsOfficesService(mockFrontendAppConfig, mockRefDataConnector)

  override def beforeEach = {
    reset(mockFrontendAppConfig, mockRefDataConnector)
    super.beforeEach
  }

  "CustomsOfficesService" - {

    "must return a list of GB and NI customs offices" in {

      when(mockRefDataConnector.getCustomsOfficesOfTheCountry(eqTo(CountryCode("XI")))(any(), any())).thenReturn(Future.successful(xiCustomsOffices))
      when(mockRefDataConnector.getCustomsOfficesOfTheCountry(eqTo(CountryCode("GB")))(any(), any())).thenReturn(Future.successful(gbCustomsOffices))
      when(mockFrontendAppConfig.isNIJourneyEnabled).thenReturn(true)

      service.getCustomsOfficesOfDeparture.futureValue.getAll mustBe customsOffices.getAll

      verify(mockRefDataConnector, times(1)).getCustomsOfficesOfTheCountry(eqTo(CountryCode("XI")))(any(), any())
      verify(mockRefDataConnector, times(1)).getCustomsOfficesOfTheCountry(eqTo(CountryCode("GB")))(any(), any())

    }

    "must return a list of GB customs offices" in {

      when(mockRefDataConnector.getCustomsOfficesOfTheCountry(eqTo(CountryCode("XI")))(any(), any())).thenReturn(Future.successful(xiCustomsOffices))
      when(mockRefDataConnector.getCustomsOfficesOfTheCountry(eqTo(CountryCode("GB")))(any(), any())).thenReturn(Future.successful(gbCustomsOffices))
      when(mockFrontendAppConfig.isNIJourneyEnabled).thenReturn(false)

      service.getCustomsOfficesOfDeparture.futureValue.getAll mustBe gbCustomsOffices.getAll

      verify(mockRefDataConnector, times(1)).getCustomsOfficesOfTheCountry(eqTo(CountryCode("GB")))(any(), any())
      verify(mockRefDataConnector, times(0)).getCustomsOfficesOfTheCountry(eqTo(CountryCode("XI")))(any(), any())

    }

  }
}
