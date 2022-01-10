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

package controllers.transportDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.NationalityAtDepartureFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode}
import models.{CountryList, NormalMode}
import navigation.annotations.TransportDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.NationalityAtDeparturePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class NationalityAtDepartureControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  val mockReferenceDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val formProvider = new NationalityAtDepartureFormProvider()
  private val country      = Country(CountryCode("GB"), "United Kingdom")
  private val countries    = CountryList(Seq(country))
  private val form         = formProvider(countries)

  private lazy val nationalityAtDepartureRoute = routes.NationalityAtDepartureController.onPageLoad(lrn, NormalMode).url

  def jsonCountryList(preSelected: Boolean): Seq[JsObject] = Seq(
    Json.obj("text" -> "Select", "value"         -> ""),
    Json.obj("text" -> "United Kingdom", "value" -> "GB", "selected" -> preSelected)
  )

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[TransportDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockReferenceDataConnector))

  override def beforeEach: Unit = {
    reset(mockReferenceDataConnector)
    super.beforeEach
  }

  "NationalityAtDeparture Controller" - {

    "must return OK and the correct view for a GET" in {

      setUserAnswers(Some(emptyUserAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val request                                = FakeRequest(GET, nationalityAtDepartureRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"        -> form,
        "lrn"         -> lrn,
        "mode"        -> NormalMode,
        "countries"   -> jsonCountryList(preSelected = false),
        "onSubmitUrl" -> routes.NationalityAtDepartureController.onSubmit(lrn, NormalMode).url
      )

      templateCaptor.getValue mustEqual "nationalityAtDeparture.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(NationalityAtDeparturePage, CountryCode("GB")).success.value

      setUserAnswers(Some(userAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val request                                = FakeRequest(GET, nationalityAtDepartureRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "GB"))

      val expectedJson = Json.obj(
        "form"        -> filledForm,
        "lrn"         -> lrn,
        "mode"        -> NormalMode,
        "countries"   -> jsonCountryList(true),
        "onSubmitUrl" -> routes.NationalityAtDepartureController.onSubmit(lrn, NormalMode).url
      )

      templateCaptor.getValue mustEqual "nationalityAtDeparture.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {

      setUserAnswers(Some(emptyUserAnswers))

      when(mockSessionRepository.set(any()))
        .thenReturn(Future.successful(true))

      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val request =
        FakeRequest(POST, nationalityAtDepartureRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setUserAnswers(Some(emptyUserAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockSessionRepository.set(any()))
        .thenReturn(Future.successful(true))

      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val request                                = FakeRequest(POST, nationalityAtDepartureRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "nationalityAtDeparture.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, nationalityAtDepartureRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, nationalityAtDepartureRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
