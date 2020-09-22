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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, okJson, urlEqualTo}
import helper.WireMockServerHandler
import models.reference.{Country, CountryCode, CustomsOffice, OfficeOfTransit, TransportMode}
import models.{CountryList, CustomsOfficeList, OfficeOfTransitList, TransportModeList}
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks {

  private val startUrl = "transit-movements-trader-reference-data"

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.referenceData.port" -> server.port()
    )
    .build()

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val customsOfficeResponseJson: String =
    """
      |[
      | {
      |   "id" : "testId1",
      |   "name" : "testName1",
      |   "roles" : ["role1", "role2"],
      |   "phoneNumber" : "testPhoneNumber"
      | },
      | {
      |   "id" : "testId2",
      |   "name" : "testName2",
      |   "roles" : ["role1", "role2"]
      | }
      |]
      |""".stripMargin

  private val countryListResponseJson: String =
    """
      |[
      | {
      |   "code":"GB",
      |   "state":"valid",
      |   "description":"United Kingdom"
      | },
      | {
      |   "code":"AD",
      |   "state":"valid",
      |   "description":"Andorra"
      | }
      |]
      |""".stripMargin

  private val transportModeListResponseJson: String =
    """
      |[
      |  {
      |    "state": "valid",
      |    "activeFrom": "2020-05-30",
      |    "code": "1",
      |    "description": "Sea transport"
      |  },
      |  {
      |    "state": "valid",
      |    "activeFrom": "2015-07-01",
      |    "code": "10",
      |    "description": "Sea transport"
      |  }
      |]
      |""".stripMargin

  private val officeOfTransitResponseJson: String =
    """
      |[
      |  {
      |    "ID": "1",
      |    "NAME": "Data1"
      |  },
      |  {
      |    "ID": "2",
      |    "NAME": "Data2"
      |  }
      |]
      |""".stripMargin


  val errorResponses: Gen[Int] = Gen.chooseNum(400, 599)

  "Reference Data" - {

    "getCustomsOffices" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult = {
          CustomsOfficeList(Seq(
            CustomsOffice("testId1", "testName1", Seq("role1", "role2"), Some("testPhoneNumber")),
            CustomsOffice("testId2", "testName2", Seq("role1", "role2"), None)
          ))
        }

        connector.getCustomsOffices.futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/customs-offices", connector.getCustomsOffices)
      }
    }

    "getCustomsOfficesOfTheCountry" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices/GB"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult = {
          CustomsOfficeList(Seq(
            CustomsOffice("testId1", "testName1", Seq("role1", "role2"), Some("testPhoneNumber")),
            CustomsOffice("testId2", "testName2", Seq("role1", "role2"), None)
          ))
        }

        connector.getCustomsOfficesOfTheCountry(CountryCode("GB")).futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/customs-offices/GB", connector.getCustomsOfficesOfTheCountry(CountryCode("GB")))
      }
    }

    "getCountryList" - {

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/countries-full-list"))
            .willReturn(okJson(countryListResponseJson))
        )

        val expectedResult: CountryList = CountryList(
          Seq(
            Country(CountryCode("GB"), "United Kingdom"),
            Country(CountryCode("AD"), "Andorra")
          )
        )

        connector.getCountryList.futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/countries-full-list", connector.getCountryList)
      }
    }

    "getTransitCountryList" - {

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/transit-countries"))
            .willReturn(okJson(countryListResponseJson))
        )

        val expectedResult: CountryList = CountryList(
          Seq(
            Country(CountryCode("GB"), "United Kingdom"),
            Country(CountryCode("AD"), "Andorra")
          )
        )

        connector.getTransitCountryList().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/transit-countries", connector.getTransitCountryList())
      }
    }

    "getTransportModes" - {

      "must return Seq of Transport modes when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/transport-modes"))
            .willReturn(okJson(transportModeListResponseJson))
        )

        val expectedResult: TransportModeList = TransportModeList(
          Seq(
            TransportMode("1", "Sea transport"),
            TransportMode("10", "Sea transport")
          )
        )

        connector.getTransportModes().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/transport-modes", connector.getTransportModes())
      }
    }

    "getOfficeOfTransitList" - {

      "must return Seq of Offices of transit when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/office-transit"))
            .willReturn(okJson(officeOfTransitResponseJson))
        )

        val expectedResult: OfficeOfTransitList = OfficeOfTransitList(
          Seq(
            OfficeOfTransit("1", "Data1"),
            OfficeOfTransit("2", "Data2")
          )
        )

        connector.getOfficeOfTransitList().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/office-transit", connector.getOfficeOfTransitList())
      }
    }
  }

  private def checkErrorResponse(url: String, result: Future[_]): Assertion =
    forAll(errorResponses) {
      errorResponse =>
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(errorResponse)
            )
        )

        whenReady(result.failed) {
          _ mustBe an[Exception]
        }
    }
}
