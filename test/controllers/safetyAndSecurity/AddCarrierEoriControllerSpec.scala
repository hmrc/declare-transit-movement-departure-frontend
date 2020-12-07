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

package controllers.safetyAndSecurity

import base.{MockNunjucksRendererApp, SpecBase}
import controllers.{routes => mainRoute}
import forms.safetyAndSecurity.AddCarrierEoriFormProvider
import matchers.JsonMatchers
import models.{NormalMode, UserAnswers}
import navigation.annotations.SafetyAndSecurityTraderDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.safetyAndSecurity.AddCarrierEoriPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AddCarrierEoriControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new AddCarrierEoriFormProvider()
  private val form         = formProvider()
  private val template     = "safetyAndSecurity/addCarrierEori.njk"

  lazy val addCarrierEoriRoute = routes.AddCarrierEoriController.onPageLoad(lrn, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[SafetyAndSecurityTraderDetails]).toInstance(new FakeNavigator(onwardRoute)))

  "AddCarrierEori Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(GET, addCarrierEoriRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> Radios.yesNo(form("value"))
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(lrn, eoriNumber).set(AddCarrierEoriPage, true).success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, addCarrierEoriRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "true"))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> Radios.yesNo(filledForm("value"))
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, addCarrierEoriRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(POST, addCarrierEoriRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> Radios.yesNo(boundForm("value"))
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, addCarrierEoriRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, addCarrierEoriRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }
  }
}
