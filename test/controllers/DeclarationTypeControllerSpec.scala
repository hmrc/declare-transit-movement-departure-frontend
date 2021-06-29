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

package controllers

import base.{MockNunjucksRendererApp, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import controllers.{routes => mainRoutes}
import forms.DeclarationTypeFormProvider
import matchers.JsonMatchers
import models.ProcedureType.Normal
import models.reference.{CountryCode, CustomsOffice}
import models.{DeclarationType, NormalMode}
import navigation.annotations.PreTaskListDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.{DeclarationTypePage, OfficeOfDeparturePage, ProcedureTypePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class DeclarationTypeControllerSpec
    extends SpecBase
    with MockNunjucksRendererApp
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers
    with UserAnswersSpecHelper {

  def onwardRoute = Call("GET", "/foo")

  lazy val declarationTypeRoute = routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url

  val formProvider    = new DeclarationTypeFormProvider()
  val form            = formProvider()
  val gbCustomsOffice = CustomsOffice("Id", "Name", CountryCode("GB"), None)

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[PreTaskListDetails]).toInstance(new FakeNavigator(onwardRoute)))

  "DeclarationType Controller" - {

    "must return OK and the correct view for a GET" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(GET, declarationTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val updatedAnswers = emptyUserAnswers
        .unsafeSetVal(ProcedureTypePage)(Normal)
        .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> DeclarationType.radios(form, updatedAnswers)()
      )

      templateCaptor.getValue mustEqual "declarationType.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = emptyUserAnswers.set(DeclarationTypePage, DeclarationType.values.head).success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, declarationTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> DeclarationType.values.head.toString))

      val updatedAnswers = emptyUserAnswers
        .unsafeSetVal(ProcedureTypePage)(Normal)
        .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> DeclarationType.radios(filledForm, updatedAnswers)()
      )

      templateCaptor.getValue mustEqual "declarationType.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to the next page when valid data is submitted" in {

      dataRetrievalWithData(emptyUserAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, declarationTypeRoute)
          .withFormUrlEncodedBody(("value", DeclarationType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(POST, declarationTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val updatedAnswers = emptyUserAnswers
        .unsafeSetVal(ProcedureTypePage)(Normal)
        .unsafeSetVal(OfficeOfDeparturePage)(gbCustomsOffice)

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> DeclarationType.radios(boundForm, updatedAnswers)()
      )

      templateCaptor.getValue mustEqual "declarationType.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, declarationTypeRoute)
          .withFormUrlEncodedBody(("value", DeclarationType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
