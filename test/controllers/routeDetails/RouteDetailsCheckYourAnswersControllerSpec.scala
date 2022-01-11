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

package controllers.routeDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import matchers.JsonMatchers
import models.DeclarationType.{Option1, Option2, Option3, Option4}
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{CountryList, CustomsOfficeList, Index, NormalMode}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{times, verify, when}
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.routeDetails.{MovementDestinationCountryPage, OfficeOfTransitCountryPage}
import pages.{DeclarationTypePage, OfficeOfDeparturePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsBoolean, JsObject, JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class RouteDetailsCheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with JsonMatchers {

  private val countries                            = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  private val customsOfficeGB: CustomsOffice       = CustomsOffice("id", "name", CountryCode("GB"), None)
  private val customsOfficeXI: CustomsOffice       = CustomsOffice("id", "name", CountryCode("XI"), None)
  private val customsOfficeList: CustomsOfficeList = CustomsOfficeList(Seq(customsOfficeGB))
  lazy val routeDetailsCheckYourAnswersRoute       = mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
  val mockReferenceDataConnector                   = mock[ReferenceDataConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockReferenceDataConnector))

  "RouteDetailsCheckYourAnswers Controller" - {

    "return OK and the correct view for a GET on a GB movement" in {
      val generatedDeclarationType = Gen.oneOf(Option1, Option2).sample.value

      val userAnswers = emptyUserAnswers
        .set(OfficeOfDeparturePage, customsOfficeGB)
        .toOption
        .value
        .set(DeclarationTypePage, generatedDeclarationType)
        .toOption
        .value
        .set(MovementDestinationCountryPage, CountryCode("GB"))
        .toOption
        .value
        .set(OfficeOfTransitCountryPage(Index(0)), CountryCode("GB"))
        .toOption
        .value

      setUserAnswers(Some(userAnswers))
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getTransitCountryList(eqTo(Seq(CountryCode("JE"))))(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any(), any())(any(), any())).thenReturn(Future.successful(customsOfficeList))
      when(mockReferenceDataConnector.getCustomsOffices(any())(any(), any())).thenReturn(Future.successful(customsOfficeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"                    -> lrn,
        "nextPageUrl"            -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url,
        "addOfficesOfTransitUrl" -> routes.AddTransitOfficeController.onPageLoad(lrn, NormalMode).url,
        "showOfficesOfTransit"   -> true
      )

      val jsonCaptorWithoutConfig: JsObject = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "routeDetailsCheckYourAnswers.njk"
      jsonCaptorWithoutConfig mustBe expectedJson
      (jsonCaptorWithoutConfig \ "addOfficesOfTransitUrl").get mustBe JsString(routes.AddTransitOfficeController.onPageLoad(lrn, NormalMode).url)
      (jsonCaptorWithoutConfig \ "showOfficesOfTransit").get mustBe JsBoolean(true)
    }

    "return OK and the correct view for a GET on a XI movement without offices of transit and a non TIR (Option4) declaration type" in {
      val generatedDeclarationType = Gen.oneOf(Option1, Option2, Option3).sample.value

      val userAnswers = emptyUserAnswers
        .set(OfficeOfDeparturePage, customsOfficeXI)
        .toOption
        .value
        .set(DeclarationTypePage, generatedDeclarationType)
        .toOption
        .value
        .set(MovementDestinationCountryPage, CountryCode("XI"))
        .toOption
        .value

      setUserAnswers(Some(userAnswers))
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getTransitCountryList(eqTo(Seq(CountryCode("JE"))))(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any(), any())(any(), any())).thenReturn(Future.successful(customsOfficeList))
      when(mockReferenceDataConnector.getCustomsOffices(any())(any(), any())).thenReturn(Future.successful(customsOfficeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"                    -> lrn,
        "nextPageUrl"            -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url,
        "addOfficesOfTransitUrl" -> routes.AddOfficeOfTransitController.onPageLoad(lrn, NormalMode).url,
        "showOfficesOfTransit"   -> true
      )

      val jsonCaptorWithoutConfig: JsObject = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "routeDetailsCheckYourAnswers.njk"
      jsonCaptorWithoutConfig mustBe expectedJson
      (jsonCaptorWithoutConfig \ "addOfficesOfTransitUrl").get mustBe JsString(routes.AddOfficeOfTransitController.onPageLoad(lrn, NormalMode).url)
      (jsonCaptorWithoutConfig \ "showOfficesOfTransit").get mustBe JsBoolean(true)
    }

    "return OK and the correct view for a GET on a XI movement with offices of transit and a non TIR (Option4) declaration type" in {
      val generatedDeclarationType = Gen.oneOf(Option1, Option2, Option3).sample.value

      val userAnswers = emptyUserAnswers
        .set(OfficeOfDeparturePage, customsOfficeXI)
        .toOption
        .value
        .set(DeclarationTypePage, generatedDeclarationType)
        .toOption
        .value
        .set(MovementDestinationCountryPage, CountryCode("XI"))
        .toOption
        .value
        .set(OfficeOfTransitCountryPage(Index(0)), CountryCode("XI"))
        .toOption
        .value

      setUserAnswers(Some(userAnswers))
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getTransitCountryList(eqTo(Seq(CountryCode("JE"))))(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any(), any())(any(), any())).thenReturn(Future.successful(customsOfficeList))
      when(mockReferenceDataConnector.getCustomsOffices(any())(any(), any())).thenReturn(Future.successful(customsOfficeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"                    -> lrn,
        "nextPageUrl"            -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url,
        "addOfficesOfTransitUrl" -> routes.AddTransitOfficeController.onPageLoad(lrn, NormalMode).url,
        "showOfficesOfTransit"   -> true
      )

      val jsonCaptorWithoutConfig: JsObject = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "routeDetailsCheckYourAnswers.njk"
      jsonCaptorWithoutConfig mustBe expectedJson
      (jsonCaptorWithoutConfig \ "addOfficesOfTransitUrl").get mustBe JsString(routes.AddTransitOfficeController.onPageLoad(lrn, NormalMode).url)
      (jsonCaptorWithoutConfig \ "showOfficesOfTransit").get mustBe JsBoolean(true)
    }

    "return OK and the correct view for a GET on a XI movement and a TIR (Option4) declaration type" in {
      val userAnswers = emptyUserAnswers
        .set(OfficeOfDeparturePage, customsOfficeXI)
        .toOption
        .value
        .set(DeclarationTypePage, Option4)
        .toOption
        .value
        .set(MovementDestinationCountryPage, CountryCode("XI"))
        .toOption
        .value

      setUserAnswers(Some(userAnswers))
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getTransitCountryList(eqTo(Seq(CountryCode("JE"))))(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getCustomsOfficesOfTheCountry(any(), any())(any(), any())).thenReturn(Future.successful(customsOfficeList))
      when(mockReferenceDataConnector.getCustomsOffices(any())(any(), any())).thenReturn(Future.successful(customsOfficeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"                  -> lrn,
        "nextPageUrl"          -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url,
        "showOfficesOfTransit" -> false
      )

      val jsonCaptorWithoutConfig: JsObject = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "routeDetailsCheckYourAnswers.njk"
      jsonCaptorWithoutConfig mustBe expectedJson
      (jsonCaptorWithoutConfig \ "showOfficesOfTransit").get mustBe JsBoolean(false)
    }

    "must redirect to session reset page if DestinationCountry data is empty" in {
      setUserAnswers(Some(emptyUserAnswers))

      val request = FakeRequest(GET, routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
