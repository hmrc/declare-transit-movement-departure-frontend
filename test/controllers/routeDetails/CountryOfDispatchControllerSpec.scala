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

package controllers.routeDetails

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoute}
import forms.CountryOfDispatchFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode, CountryOfDispatch}
import models.{CountryList, NormalMode}
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.CountryOfDispatchPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class CountryOfDispatchControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  val mockReferenceDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new CountryOfDispatchFormProvider()
  val countries    = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  val form         = formProvider(countries)

  lazy val countryOfDispatchRoute = routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url

  def jsonCountryList(preSelected: Boolean): Seq[JsObject] = Seq(
    Json.obj("text" -> "", "value"               -> ""),
    Json.obj("text" -> "United Kingdom", "value" -> "GB", "selected" -> preSelected)
  )

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockReferenceDataConnector))

  "CountryOfDispatch Controller" - {

    "must return OK and the correct view for a GET" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val request        = FakeRequest(GET, countryOfDispatchRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"        -> form,
        "lrn"         -> lrn,
        "mode"        -> NormalMode,
        "countries"   -> jsonCountryList(preSelected = false),
        "onSubmitUrl" -> routes.CountryOfDispatchController.onSubmit(lrn, NormalMode).url
      )

      templateCaptor.getValue mustEqual "countryOfDispatch.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.set(CountryOfDispatchPage, CountryOfDispatch(CountryCode("GB"), true)).success.value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val request        = FakeRequest(GET, countryOfDispatchRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "value" -> "GB"
        )
      )

      val expectedJson = Json.obj(
        "form"        -> filledForm,
        "lrn"         -> lrn,
        "countries"   -> jsonCountryList(true),
        "mode"        -> NormalMode,
        "onSubmitUrl" -> routes.CountryOfDispatchController.onSubmit(lrn, NormalMode).url
      )

      templateCaptor.getValue mustEqual "countryOfDispatch.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockReferenceDataConnector.getNonEUTransitCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val request =
        FakeRequest(POST, countryOfDispatchRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      val request        = FakeRequest(POST, countryOfDispatchRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "countryOfDispatch.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      dataRetrievalNoData()

      val request = FakeRequest(GET, countryOfDispatchRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      dataRetrievalNoData()

      val request =
        FakeRequest(POST, countryOfDispatchRoute)
          .withFormUrlEncodedBody(("value", "value 1"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url
    }
  }
}
