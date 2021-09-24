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

package controllers.guaranteeDetails

import base.{MockNunjucksRendererApp, SpecBase}
import forms.guaranteeDetails.GuaranteeTypeFormProvider
import matchers.JsonMatchers
import models.{GuaranteeType, NormalMode}
import navigation.annotations.GuaranteeDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.guaranteeDetails.GuaranteeTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class GuaranteeTypeControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  lazy val guaranteeTypeRoute = routes.GuaranteeTypeController.onPageLoad(lrn, index, NormalMode).url

  val formProvider = new GuaranteeTypeFormProvider()
  val form         = formProvider()

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[GuaranteeDetails]).toInstance(new FakeNavigator(onwardRoute)))

  "GuaranteeType Controller" - {

    "must return OK and the correct view for a GET" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(GET, guaranteeTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "index"  -> index.display,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> GuaranteeType.radios(form)
      )

      templateCaptor.getValue mustEqual "guaranteeDetails/guaranteeType.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.set(GuaranteeTypePage(index), GuaranteeType.values.head).success.value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(GET, guaranteeTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> GuaranteeType.values.head.toString))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "index"  -> index.display,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> GuaranteeType.radios(filledForm)
      )

      templateCaptor.getValue mustEqual "guaranteeDetails/guaranteeType.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, guaranteeTypeRoute)
          .withFormUrlEncodedBody(("value", GuaranteeType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(POST, guaranteeTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "index"  -> index.display,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> GuaranteeType.radios(boundForm)
      )

      templateCaptor.getValue mustEqual "guaranteeDetails/guaranteeType.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      dataRetrievalNoData()

      val request = FakeRequest(GET, guaranteeTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      dataRetrievalNoData()

      val request =
        FakeRequest(POST, guaranteeTypeRoute)
          .withFormUrlEncodedBody(("value", GuaranteeType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
