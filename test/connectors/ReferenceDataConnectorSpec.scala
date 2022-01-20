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

package connectors

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, okJson, urlEqualTo}
import helper.WireMockServerHandler
import models.reference._
import models.{
  CircumstanceIndicatorList,
  CustomsOfficeList,
  DangerousGoodsCodeList,
  DocumentTypeList,
  MethodOfPaymentList,
  PreviousReferencesDocumentTypeList,
  SpecialMentionList
}
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.mvc.Http.HeaderNames.CONTENT_TYPE
import play.mvc.Http.MimeTypes.JSON
import play.mvc.Http.Status._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler with ScalaCheckPropertyChecks {

  private val startUrl = "transit-movements-trader-reference-data"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.referenceData.port" -> server.port()
    )

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val customsOfficeResponseJson: String =
    """
      |[
      | {
      |   "id" : "testId1",
      |   "name" : "testName1",
      |   "roles" : ["role1", "role2"],
      |   "countryId" : "GB",
      |   "phoneNumber" : "testPhoneNumber"
      | },
      | {
      |   "id" : "testId2",
      |   "name" : "testName2",
      |   "countryId" : "GB",
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

  private val countriesResponseJson: String =
    """
      |[
      | {
      |   "code":"GB",
      |   "description":"United Kingdom"
      | },
      | {
      |   "code":"AD",
      |   "description":"Andorra"
      | }
      |]
      |""".stripMargin

  private val nonEUCountryListResponseJson: String =
    """
      |[
      | {
      |   "code":"GB",
      |   "state":"valid",
      |   "description":"United Kingdom"
      | },
      | {
      |   "code":"NO",
      |   "state":"valid",
      |   "description":"Norway"
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

  private val customsOfficeJson: String =
    """
      |  {
      |    "id": "1",
      |    "name": "Data1",
      |    "roles" : ["role1", "role2"],
      |    "countryId" : "GB",
      |    "phoneNumber" : "testPhoneNumber"
      |  }
      |""".stripMargin

  private val packageTypeJson: String =
    """
      |[
      |  {
      |    "state": "valid",
      |    "activeFrom": "2015-10-01",
      |    "code": "AB",
      |    "description": "description 1"
      |  },
      |  {
      |    "state": "valid",
      |    "activeFrom": "2015-07-01",
      |    "code": "CD",
      |    "description": "description 2"
      |  }
      |]
      |""".stripMargin

  private val previousDocumentJson: String =
    """
      |[
      |  {
      |    "code": "T1",
      |    "description": "Description T1"
      |  },
      |  {
      |    "code": "T2F"
      |  }
      |]
      |""".stripMargin

  private val documentJson: String =
    """
      |[
      | {
      |    "code": "18",
      |    "transportDocument": false,
      |    "description": "Movement certificate A.TR.1"
      |  },
      |  {
      |    "code": "2",
      |    "transportDocument": false,
      |    "description": "Certificate of conformity"
      |  }
      |]
      |""".stripMargin

  private val specialMentionJson: String =
    """
      |[
      | {
      |    "code": "10600",
      |    "description": "Negotiable Bill of lading 'to order blank endorsed'"
      |  },
      |  {
      |    "code": "30400",
      |    "description": "RET-EXP – Copy 3 to be returned"
      |  }
      |]
      |""".stripMargin

  private val dangerousGoodsCodeJson: String =
    """
      |  {
      |    "code": "0004",
      |    "description": "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass"
      |  }
      |""".stripMargin

  private val dangerousGoodsCodeResponseJson: String =
    """
      |[
      |  {
      |    "code": "0004",
      |    "description": "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass"
      |  },
      |  {
      |    "code": "0005",
      |    "description": "CARTRIDGES FOR WEAPONS with bursting charge"
      |  }
      |]
      |""".stripMargin

  private val methodOfPaymentJson: String =
    """
      |[
      | {
      |    "code": "A",
      |    "description": "Payment in cash"
      |  },
      |  {
      |    "code": "B",
      |    "description": "Payment by credit card"
      |  }
      |]
      |""".stripMargin

  private val circumstanceIndicatorJson: String =
    """
      |[
      |  {
      |    "code": "A",
      |    "description": "Data1"
      |  },
      |  {
      |    "code": "B",
      |    "description": "Data2"
      |  }
      |]
      |""".stripMargin

  val errorResponses: Gen[Int] = Gen
    .chooseNum(400, 599)
    .map(
      x => if (x == 404) x + 1 else x
    )

  "Reference Data" - {

    "getCustomsOffices" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult = Seq(
          CustomsOffice("testId1", "testName1", CountryCode("GB"), Some("testPhoneNumber")),
          CustomsOffice("testId2", "testName2", CountryCode("GB"), None)
        )

        connector.getCustomsOffices().futureValue mustBe expectedResult
      }

      "must return a successful future response with roles with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices?role=NPM"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult = Seq(
          CustomsOffice("testId1", "testName1", CountryCode("GB"), Some("testPhoneNumber")),
          CustomsOffice("testId2", "testName2", CountryCode("GB"), None)
        )

        connector.getCustomsOffices(Seq("NPM")).futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/customs-offices", connector.getCustomsOffices())
      }
    }

    "getCustomsOfficesOfTheCountry" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices/GB"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult =
          CustomsOfficeList(
            Seq(
              CustomsOffice("testId1", "testName1", CountryCode("GB"), Some("testPhoneNumber")),
              CustomsOffice("testId2", "testName2", CountryCode("GB"), None)
            )
          )

        connector.getCustomsOfficesForCountry(CountryCode("GB")).futureValue mustBe expectedResult
      }

      "must return a successful future response when roles are defined with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices/GB?role=TRA"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult =
          CustomsOfficeList(
            Seq(
              CustomsOffice("testId1", "testName1", CountryCode("GB"), Some("testPhoneNumber")),
              CustomsOffice("testId2", "testName2", CountryCode("GB"), None)
            )
          )

        connector.getCustomsOfficesForCountry(CountryCode("GB"), Seq("TRA")).futureValue mustBe expectedResult
      }

      "must return a successful future response when CustomsOffice is not found" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices/AR?role=TRA")).willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withHeader(CONTENT_TYPE, JSON)
          )
        )

        val expectedResult =
          CustomsOfficeList(Nil)

        connector.getCustomsOfficesForCountry(CountryCode("AR"), Seq("TRA")).futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/customs-offices/GB", connector.getCustomsOfficesForCountry(CountryCode("GB")))
      }
    }

    "getCountryList" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/countries-full-list"))
            .willReturn(okJson(countryListResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )
        connector.getCountries.futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/countries-full-list", connector.getCountries)
      }
    }

    "getCountries" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/countries?customsOfficeRole=ANY&exclude=IT&exclude=DE&membership=ctc"))
            .willReturn(okJson(countriesResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        val queryParameters = Seq(
          "customsOfficeRole" -> "ANY",
          "exclude"           -> "IT",
          "exclude"           -> "DE",
          "membership"        -> "ctc"
        )

        connector.getCountries(queryParameters).futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/countries?customsOfficeRole=ANY", connector.getCountries(Nil))
      }
    }

    "getTransitCountryList" - {

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/transit-countries"))
            .willReturn(okJson(countryListResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getTransitCountries().futureValue mustEqual expectedResult
      }

      "must return Seq of Country when passed with query parameters and is successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/transit-countries?excludeCountries=JE&excludeCountries=AB"))
            .willReturn(okJson(countryListResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        val queryParameters = Seq(
          "excludeCountries" -> "JE",
          "excludeCountries" -> "AB"
        )

        connector.getTransitCountries(queryParameters).futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/transit-countries", connector.getTransitCountries())
      }
    }

    "getNonEUTransitCountryList" - {

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/non-eu-transit-countries"))
            .willReturn(okJson(nonEUCountryListResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("NO"), "Norway")
        )

        connector.getNonEuTransitCountries().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/non-eu-transit-countries", connector.getNonEuTransitCountries())
      }
    }

    "getTransportModes" - {

      "must return Seq of Transport modes when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/transport-modes"))
            .willReturn(okJson(transportModeListResponseJson))
        )

        val expectedResult: Seq[TransportMode] = Seq(
          TransportMode("1", "Sea transport"),
          TransportMode("10", "Sea transport")
        )

        connector.getTransportModes().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/transport-modes", connector.getTransportModes())
      }
    }

    "getCustomsOffice" - {

      "must return a Customs Office when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-office/1"))
            .willReturn(okJson(customsOfficeJson))
        )

        val expectedResult: CustomsOffice = CustomsOffice("1", "Data1", CountryCode("GB"), Some("testPhoneNumber"))

        connector.getCustomsOffice("1").futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/customs-office/1", connector.getCustomsOffice("1"))
      }
    }

    "getPackageTypes" - {

      "must return list of package types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/kinds-of-package"))
            .willReturn(okJson(packageTypeJson))
        )

        val expectResult = Seq(
          PackageType("AB", "description 1"),
          PackageType("CD", "description 2")
        )

        connector.getPackageTypes().futureValue mustEqual expectResult

      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/kinds-of-package", connector.getPackageTypes())
      }

    }

    "getPreviousDocumentType" - {

      "must return list of previous document types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/previous-document-types"))
            .willReturn(okJson(previousDocumentJson))
        )

        val expectResult = PreviousReferencesDocumentTypeList(
          Seq(
            PreviousReferencesDocumentType("T1", Some("Description T1")),
            PreviousReferencesDocumentType("T2F", None)
          )
        )

        connector.getPreviousReferencesDocumentTypes().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/previous-document-types", connector.getPreviousReferencesDocumentTypes())
      }

    }

    "getDocumentType" - {

      "must return list of document types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/document-types"))
            .willReturn(okJson(documentJson))
        )

        val expectResult = DocumentTypeList(
          Seq(
            DocumentType("18", "Movement certificate A.TR.1", false),
            DocumentType("2", "Certificate of conformity", false)
          )
        )

        connector.getDocumentTypes().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/document-type", connector.getDocumentTypes())
      }

    }

    "getSpecialMentionTypes" - {

      "must return list of document types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/additional-information"))
            .willReturn(okJson(specialMentionJson))
        )

        val expectResult = SpecialMentionList(
          Seq(
            SpecialMention("10600", "Negotiable Bill of lading 'to order blank endorsed'"),
            SpecialMention("30400", "RET-EXP – Copy 3 to be returned")
          )
        )

        connector.getSpecialMentionTypes().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/additional-information", connector.getSpecialMentionTypes())
      }

    }

    "getDangerousGoodsCodes" - {

      "must return Seq of Dangerous goods codes when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/dangerous-goods-codes"))
            .willReturn(okJson(dangerousGoodsCodeResponseJson))
        )

        val expectedResult: DangerousGoodsCodeList = DangerousGoodsCodeList(
          Seq(
            DangerousGoodsCode("0004", "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass"),
            DangerousGoodsCode("0005", "CARTRIDGES FOR WEAPONS with bursting charge")
          )
        )

        connector.getDangerousGoodsCodes().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/dangerous-goods-code", connector.getDangerousGoodsCodes())
      }
    }

    "getDangerousGoodsCode" - {

      "must return Dangerous goods code when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/dangerous-goods-code/0004"))
            .willReturn(okJson(dangerousGoodsCodeJson))
        )

        val expectedResult: DangerousGoodsCode = DangerousGoodsCode("0004", "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass")

        connector.getDangerousGoodsCode("0004").futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/dangerous-goods-code/0004", connector.getDangerousGoodsCode("0004"))
      }
    }

    "getMethodsOfPayment" - {
      "must return list of methods of payment when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/method-of-payment"))
            .willReturn(okJson(methodOfPaymentJson))
        )
        val expectResult = MethodOfPaymentList(
          Seq(
            MethodOfPayment("A", "Payment in cash"),
            MethodOfPayment("B", "Payment by credit card")
          )
        )
        connector.getMethodsOfPayment().futureValue mustEqual expectResult
      }
      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/method-of-payment", connector.getMethodsOfPayment())
      }
    }

    "getCircumstanceIndicators" - {

      "must return Seq of circumstance indicators when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/circumstance-indicators"))
            .willReturn(okJson(circumstanceIndicatorJson))
        )

        val expectedResult: CircumstanceIndicatorList = CircumstanceIndicatorList(
          Seq(
            CircumstanceIndicator("A", "Data1"),
            CircumstanceIndicator("B", "Data2")
          )
        )

        connector.getCircumstanceIndicators().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/circumstance-indicators", connector.getCircumstanceIndicators())
      }
    }

  }

  private def checkErrorResponse(url: String, result: => Future[_]): Assertion =
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
