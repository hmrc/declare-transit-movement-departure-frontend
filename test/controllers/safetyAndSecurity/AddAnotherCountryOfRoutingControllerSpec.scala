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

package controllers.safetyAndSecurity

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoute}
import forms.safetyAndSecurity.AddAnotherCountryOfRoutingFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode}
import models.{CountryList, NormalMode}
import navigation.annotations.SafetyAndSecurity
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AddAnotherCountryOfRoutingControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  private val formProvider = new AddAnotherCountryOfRoutingFormProvider()

  private val mockReferenceDataConnector = mock[ReferenceDataConnector]
  val countries                          = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  private val form                       = formProvider()
  private val template                   = "safetyAndSecurity/addAnotherCountryOfRouting.njk"

  private lazy val addAnotherCountryOfRoutingRoute = routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[SafetyAndSecurity]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockReferenceDataConnector))

  "AddAnotherCountryOfRouting Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(GET, addAnotherCountryOfRoutingRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"      -> form,
        "pageTitle" -> msg"addAnotherCountryOfRouting.title.singular".withArgs(1),
        "heading"   -> msg"addAnotherCountryOfRouting.heading.singular".withArgs(1),
        "lrn"       -> lrn,
        "mode"      -> NormalMode,
        "radios"    -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      setUserAnswers(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, addAnotherCountryOfRoutingRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(POST, addAnotherCountryOfRoutingRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"      -> boundForm,
        "pageTitle" -> msg"addAnotherCountryOfRouting.title.singular".withArgs(1),
        "heading"   -> msg"addAnotherCountryOfRouting.heading.singular".withArgs(1),
        "lrn"       -> lrn,
        "mode"      -> NormalMode,
        "radios"    -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, addAnotherCountryOfRoutingRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, addAnotherCountryOfRoutingRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }
  }
}
