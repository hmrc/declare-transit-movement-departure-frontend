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

package controllers.addItems.previousReferences

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.ReferenceTypeFormProvider
import matchers.JsonMatchers
import models.reference.PreviousReferencesDocumentType
import models.{NormalMode, PreviousReferencesDocumentTypeList}
import navigation.annotations.addItems.AddItems
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.ReferenceTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class ReferenceTypeControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new ReferenceTypeFormProvider()

  private val documentTypeList = PreviousReferencesDocumentTypeList(
    Seq(
      PreviousReferencesDocumentType("T1", Some("Description T1")),
      PreviousReferencesDocumentType("T2F", None),
      PreviousReferencesDocumentType("CO", Some(""))
    )
  )

  private val form     = formProvider(documentTypeList)
  private val template = "addItems/referenceType.njk"

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  lazy val referenceTypeRoute = routes.ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItems]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind[ReferenceDataConnector].toInstance(mockRefDataConnector))

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mockRefDataConnector)
  }

  "ReferenceType Controller" - {

    "must return OK and the correct view for a GET" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val request        = FakeRequest(GET, referenceTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedPreviousDocumentTypeJson = Seq(
        Json.obj("value" -> "", "text"    -> ""),
        Json.obj("value" -> "T1", "text"  -> "(T1) Description T1", "selected" -> false),
        Json.obj("value" -> "T2F", "text" -> "T2F", "selected"                 -> false),
        Json.obj("value" -> "CO", "text"  -> "CO", "selected"                  -> false)
      )

      val expectedJson = Json.obj(
        "form"              -> form,
        "index"             -> index.display,
        "referenceIndex"    -> referenceIndex.display,
        "previousDocuments" -> expectedPreviousDocumentTypeJson,
        "mode"              -> NormalMode,
        "lrn"               -> lrn
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val userAnswers = emptyUserAnswers.set(ReferenceTypePage(index, referenceIndex), "T1").success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, referenceTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "T1"))

      val expectedPreviousDocumentTypeJson = Seq(
        Json.obj("value" -> "", "text"    -> ""),
        Json.obj("value" -> "T1", "text"  -> "(T1) Description T1", "selected" -> true),
        Json.obj("value" -> "T2F", "text" -> "T2F", "selected"                 -> false),
        Json.obj("value" -> "CO", "text"  -> "CO", "selected"                  -> false)
      )

      val expectedJson = Json.obj(
        "form"              -> filledForm,
        "index"             -> index.display,
        "referenceIndex"    -> referenceIndex.display,
        "previousDocuments" -> expectedPreviousDocumentTypeJson,
        "lrn"               -> lrn,
        "mode"              -> NormalMode
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val request =
        FakeRequest(POST, referenceTypeRoute)
          .withFormUrlEncodedBody(("value", "T1"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val request        = FakeRequest(POST, referenceTypeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"           -> boundForm,
        "index"          -> index.display,
        "referenceIndex" -> referenceIndex.display,
        "lrn"            -> lrn,
        "mode"           -> NormalMode
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, referenceTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, referenceTypeRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
