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
import models.reference._
import models.{
  CircumstanceIndicatorList,
  CountryList,
  CustomsOfficeList,
  DangerousGoodsCodeList,
  DocumentTypeList,
  MethodOfPaymentList,
  OfficeOfTransitList,
  PackageTypeList,
  PreviousReferencesDocumentTypeList,
  SpecialMentionList,
  TransportModeList
}
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks {

  private val startUrl = "transit-movements-trader-reference-data"

  lazy val app: Application = new GuiceApplicationBuilder()
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
      |    "id": "1",
      |    "name": "Data1"
      |  },
      |  {
      |    "id": "2",
      |    "name": "Data2"
      |  }
      |]
      |""".stripMargin

  private val officeOfTransitJson: String =
    """
      |  {
      |    "id": "1",
      |    "name": "Data1"
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
      |    "code": "T2F",
      |    "description": "Description T2F"
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

  val errorResponses: Gen[Int] = Gen.chooseNum(400, 599)

  "Reference Data" - {

    "getCustomsOffices" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult =
          CustomsOfficeList(
            Seq(
              CustomsOffice("testId1", "testName1", Seq("role1", "role2"), Some("testPhoneNumber")),
              CustomsOffice("testId2", "testName2", Seq("role1", "role2"), None)
            )
          )

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

        val expectedResult =
          CustomsOfficeList(
            Seq(
              CustomsOffice("testId1", "testName1", Seq("role1", "role2"), Some("testPhoneNumber")),
              CustomsOffice("testId2", "testName2", Seq("role1", "role2"), None)
            )
          )

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

    "getOfficeOfTransit" - {

      "must return Offices of transit when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/office-transit/1"))
            .willReturn(okJson(officeOfTransitJson))
        )

        val expectedResult: OfficeOfTransit = OfficeOfTransit("1", "Data1")

        connector.getOfficeOfTransit("1").futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/office-transit/1", connector.getOfficeOfTransitList())
      }
    }

    "getPackageTypes" - {

      "must return list of package types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/kinds-of-package"))
            .willReturn(okJson(packageTypeJson))
        )

        val expectResult = PackageTypeList(
          Seq(
            PackageType("AB", "description 1"),
            PackageType("CD", "description 2")
          )
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
          get(urlEqualTo(s"/$startUrl/previous-document-type"))
            .willReturn(okJson(previousDocumentJson))
        )

        val expectResult = PreviousReferencesDocumentTypeList(
          Seq(
            PreviousReferencesDocumentType("T1", "Description T1"),
            PreviousReferencesDocumentType("T2F", "Description T2F")
          )
        )

        connector.getPreviousReferencesDocumentTypes().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/previous-document-type", connector.getPreviousReferencesDocumentTypes())
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

    "getSpecialMention" - {

      "must return list of document types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/special-mention"))
            .willReturn(okJson(specialMentionJson))
        )

        val expectResult = SpecialMentionList(
          Seq(
            SpecialMention("10600", "Negotiable Bill of lading 'to order blank endorsed'"),
            SpecialMention("30400", "RET-EXP – Copy 3 to be returned")
          )
        )

        connector.getSpecialMention().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/special-mention", connector.getSpecialMention())
      }

    }

    "getDangerousGoodsCodeList" - {

      "must return Seq of Dangerous goods codes when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/dangerous-goods-code"))
            .willReturn(okJson(dangerousGoodsCodeResponseJson))
        )

        val expectedResult: DangerousGoodsCodeList = DangerousGoodsCodeList(
          Seq(
            DangerousGoodsCode("0004", "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass"),
            DangerousGoodsCode("0005", "CARTRIDGES FOR WEAPONS with bursting charge")
          )
        )

        connector.getDangerousGoodsCodeList().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/dangerous-goods-code", connector.getDangerousGoodsCodeList())
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

        checkErrorResponse(s"/$startUrl/dangerous-goods-code/0004", connector.getDangerousGoodsCodeList())
      }
    }

    "getMethodOfPayment" - {
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
        connector.getMethodOfPaymentList().futureValue mustEqual expectResult
      }
      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/method-of-payment", connector.getMethodOfPaymentList())
      }
    }

    "getCircumstanceIndicatorList" - {

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

        connector.getCircumstanceIndicatorList().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$startUrl/circumstance-indicators", connector.getCircumstanceIndicatorList())
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
