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

package controllers.addItems.documents

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.addItems.DocumentTypeFormProvider
import matchers.JsonMatchers
import models.reference.DocumentType
import models.{DocumentTypeList, NormalMode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsDocument
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class DocumentTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  private val formProvider = new DocumentTypeFormProvider()

  private val documentTypeList = DocumentTypeList(
    Seq(
      DocumentType("955", "ATA carnet", transportDocument = true),
      DocumentType("740", "Air waybill", transportDocument = true)
    )
  )
  private val form     = formProvider(documentTypeList)
  private val template = "addItems/documentType.njk"

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private lazy val documentTypeRoute = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(lrn, index, documentIndex, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsDocument]).toInstance(fakeNavigator))
      .overrides(bind[ReferenceDataConnector].toInstance(mockRefDataConnector))

  override def beforeEach: Unit = {
    super.beforeEach()
    Mockito.reset(mockRefDataConnector)
  }

  "DocumentType Controller" - {

    "must return OK and the correct view for a GET" in {
      setUserAnswers(Some(emptyUserAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val request                                = FakeRequest(GET, documentTypeRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedDocumentTypeJson = Seq(
        Json.obj("value" -> "", "text"    -> "Select"),
        Json.obj("value" -> "955", "text" -> "(955) ATA carnet", "selected"  -> false),
        Json.obj("value" -> "740", "text" -> "(740) Air waybill", "selected" -> false)
      )
      val expectedJson = Json.obj(
        "form"          -> form,
        "index"         -> index.display,
        "documentIndex" -> documentIndex.display,
        "documents"     -> expectedDocumentTypeJson,
        "mode"          -> NormalMode,
        "lrn"           -> lrn
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val userAnswers = emptyUserAnswers.set(addItems.DocumentTypePage(itemIndex, documentIndex), "955").success.value
      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, documentTypeRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "955"))

      val expectedDocumentTypeJson = Seq(
        Json.obj("value" -> "", "text"    -> "Select"),
        Json.obj("value" -> "955", "text" -> "(955) ATA carnet", "selected"  -> true),
        Json.obj("value" -> "740", "text" -> "(740) Air waybill", "selected" -> false)
      )
      val expectedJson = Json.obj(
        "form"          -> filledForm,
        "index"         -> index.display,
        "documentIndex" -> documentIndex.display,
        "documents"     -> expectedDocumentTypeJson,
        "lrn"           -> lrn,
        "mode"          -> NormalMode
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRefDataConnector.getDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val request =
        FakeRequest(POST, documentTypeRoute)
          .withFormUrlEncodedBody(("value", "955"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setUserAnswers(Some(emptyUserAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val request                                = FakeRequest(POST, documentTypeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"          -> boundForm,
        "index"         -> index.display,
        "documentIndex" -> documentIndex.display,
        "lrn"           -> lrn,
        "mode"          -> NormalMode
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, documentTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, documentTypeRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
