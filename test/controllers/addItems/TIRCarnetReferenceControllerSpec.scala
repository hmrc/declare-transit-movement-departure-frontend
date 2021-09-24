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

package controllers.addItems

import base.{MockNunjucksRendererApp, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import forms.addItems.TIRCarnetReferenceFormProvider
import matchers.JsonMatchers
import models.DeclarationType.{Option1, Option4}
import models.NormalMode
import navigation.annotations.addItems.AddItemsDocument
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.DeclarationTypePage
import pages.addItems.TIRCarnetReferencePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class TIRCarnetReferenceControllerSpec
    extends SpecBase
    with MockNunjucksRendererApp
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers
    with UserAnswersSpecHelper {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new TIRCarnetReferenceFormProvider()
  private val form         = formProvider()
  private val template     = "tirCarnetReference.njk"

  lazy val tirCarnetReferenceRoute = controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(lrn, index, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsDocument]).toInstance(new FakeNavigator(onwardRoute)))

  "TIRCarnetReference Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(GET, tirCarnetReferenceRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"          -> form,
        "mode"          -> NormalMode,
        "lrn"           -> lrn,
        "itemIndex"     -> itemIndex.display,
        "documentIndex" -> documentIndex.display
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = emptyUserAnswers.set(TIRCarnetReferencePage(index, index), "1234567890").success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, tirCarnetReferenceRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "1234567890"))

      val expectedJson = Json.obj(
        "form"          -> filledForm,
        "lrn"           -> lrn,
        "mode"          -> NormalMode,
        "itemIndex"     -> itemIndex.display,
        "documentIndex" -> documentIndex.display
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted when DeclarationType is TIR" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(DeclarationTypePage)(Option4)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(userAnswers)

      val request =
        FakeRequest(POST, tirCarnetReferenceRoute)
          .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must redirect Session Expired when DeclarationType is not TIR" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(DeclarationTypePage)(Option1)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(userAnswers)

      val request =
        FakeRequest(POST, tirCarnetReferenceRoute)
          .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect Session Expired when DeclarationType is not defined" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, tirCarnetReferenceRoute)
          .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(DeclarationTypePage)(Option4)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(POST, tirCarnetReferenceRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"          -> boundForm,
        "lrn"           -> lrn,
        "mode"          -> NormalMode,
        "itemIndex"     -> itemIndex.display,
        "documentIndex" -> documentIndex.display
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, tirCarnetReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, tirCarnetReferenceRoute)
          .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
