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
import forms.addItems.AddAnotherPreviousAdministrativeReferenceFormProvider
import matchers.JsonMatchers
import models.reference.PreviousReferencesDocumentType
import models.{NormalMode, PreviousReferencesDocumentTypeList, UserAnswers}
import navigation.annotations.addItems.AddItemsAdminReference
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.AddAnotherPreviousAdministrativeReferencePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class AddAnotherPreviousAdministrativeReferenceControllerSpec
    extends SpecBase
    with MockNunjucksRendererApp
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider         = new AddAnotherPreviousAdministrativeReferenceFormProvider()
  private val form                 = formProvider()
  private val template             = "addItems/addAnotherPreviousAdministrativeReference.njk"
  private val mockRefDataConnector = mock[ReferenceDataConnector]

  private val documentTypeList = PreviousReferencesDocumentTypeList(
    Seq(
      PreviousReferencesDocumentType("T1", Some("Description T1")),
      PreviousReferencesDocumentType("T2F", None)
    )
  )

  lazy val addAnotherPreviousAdministrativeReferenceRoute =
    routes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsAdminReference]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind[ReferenceDataConnector].toInstance(mockRefDataConnector))

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "AddAnotherPreviousAdministrativeReference Controller" - {

    "must return OK and the correct view for a GET" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      val request        = FakeRequest(GET, addAnotherPreviousAdministrativeReferenceRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"      -> form,
        "index"     -> index.display,
        "lrn"       -> lrn,
        "pageTitle" -> msg"addAnotherPreviousAdministrativeReference.title.plural".withArgs(1),
        "heading"   -> msg"addAnotherPreviousAdministrativeReference.heading.plural".withArgs(1)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(lrn, eoriNumber).set(AddAnotherPreviousAdministrativeReferencePage(index), true).success.value
      dataRetrievalWithData(userAnswers)
      val request        = FakeRequest(GET, addAnotherPreviousAdministrativeReferenceRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"      -> form,
        "index"     -> index.display,
        "lrn"       -> lrn,
        "pageTitle" -> msg"addAnotherPreviousAdministrativeReference.title.plural".withArgs(1),
        "heading"   -> msg"addAnotherPreviousAdministrativeReference.heading.plural".withArgs(1)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, addAnotherPreviousAdministrativeReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any())).thenReturn(Future.successful(documentTypeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request        = FakeRequest(POST, addAnotherPreviousAdministrativeReferenceRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"      -> boundForm,
        "index"     -> index.display,
        "lrn"       -> lrn,
        "pageTitle" -> msg"addAnotherPreviousAdministrativeReference.title.plural".withArgs(1),
        "heading"   -> msg"addAnotherPreviousAdministrativeReference.heading.plural".withArgs(1)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, addAnotherPreviousAdministrativeReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, addAnotherPreviousAdministrativeReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
