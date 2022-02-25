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

package controllers.addItems.previousReferences

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoutes}
import forms.addItems.AddAnotherPreviousAdministrativeReferenceFormProvider
import matchers.JsonMatchers
import models.reference.PreviousReferencesDocumentType
import models.{Index, NormalMode, PreviousReferencesDocumentTypeList, UserAnswers}
import navigation.annotations.addItems.AddItemsAdminReference
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.{AddAnotherPreviousAdministrativeReferencePage, PreviousReferencePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.PreviousDocumentTypesService
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class AddAnotherPreviousAdministrativeReferenceControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers {

  private val formProvider                     = new AddAnotherPreviousAdministrativeReferenceFormProvider()
  private val form                             = formProvider(true)
  private val template                         = "addItems/addAnotherPreviousAdministrativeReference.njk"
  private val mockPreviousDocumentTypesService = mock[PreviousDocumentTypesService]

  private val documentTypeList = PreviousReferencesDocumentTypeList(
    Seq(
      PreviousReferencesDocumentType("T1", Some("Description T1")),
      PreviousReferencesDocumentType("T2F", None)
    )
  )

  private lazy val addAnotherPreviousAdministrativeReferenceRoute =
    routes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsAdminReference]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind[PreviousDocumentTypesService].toInstance(mockPreviousDocumentTypesService))

  override def beforeEach(): Unit = {
    reset(mockPreviousDocumentTypesService)
    super.beforeEach()
  }

  "AddAnotherPreviousAdministrativeReference Controller" - {

    "must return OK and the correct view for a GET" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockPreviousDocumentTypesService.getPreviousDocumentTypes()(any())).thenReturn(Future.successful(documentTypeList))

      val request                                = FakeRequest(GET, addAnotherPreviousAdministrativeReferenceRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"                        -> form,
        "index"                       -> index.display,
        "lrn"                         -> lrn,
        "mode"                        -> NormalMode,
        "pageTitle"                   -> msg"addAnotherPreviousAdministrativeReference.title.plural".withArgs(1),
        "heading"                     -> msg"addAnotherPreviousAdministrativeReference.heading.plural".withArgs(1),
        "allowMorePreviousReferences" -> true
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      when(mockPreviousDocumentTypesService.getPreviousDocumentTypes()(any())).thenReturn(Future.successful(documentTypeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(lrn, eoriNumber).set(AddAnotherPreviousAdministrativeReferencePage(index), true).success.value
      setUserAnswers(Some(userAnswers))
      val request                                = FakeRequest(GET, addAnotherPreviousAdministrativeReferenceRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"                        -> form,
        "index"                       -> index.display,
        "lrn"                         -> lrn,
        "mode"                        -> NormalMode,
        "pageTitle"                   -> msg"addAnotherPreviousAdministrativeReference.title.plural".withArgs(1),
        "heading"                     -> msg"addAnotherPreviousAdministrativeReference.heading.plural".withArgs(1),
        "allowMorePreviousReferences" -> true
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockPreviousDocumentTypesService.getPreviousDocumentTypes()(any())).thenReturn(Future.successful(documentTypeList))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, addAnotherPreviousAdministrativeReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must redirect to the next page when valid data is submitted when max number reached" in {
      val userAnswers = emptyUserAnswers
        .set(PreviousReferencePage(Index(0), Index(0)), "document1")
        .success
        .value
        .set(PreviousReferencePage(Index(0), Index(1)), "document2")
        .success
        .value
        .set(PreviousReferencePage(Index(0), Index(2)), "document3")
        .success
        .value

      setUserAnswers(Some(userAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, addAnotherPreviousAdministrativeReferenceRoute)
          .withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockPreviousDocumentTypesService.getPreviousDocumentTypes()(any())).thenReturn(Future.successful(documentTypeList))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(POST, addAnotherPreviousAdministrativeReferenceRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"                        -> boundForm,
        "index"                       -> index.display,
        "lrn"                         -> lrn,
        "mode"                        -> NormalMode,
        "pageTitle"                   -> msg"addAnotherPreviousAdministrativeReference.title.plural".withArgs(1),
        "heading"                     -> msg"addAnotherPreviousAdministrativeReference.heading.plural".withArgs(1),
        "allowMorePreviousReferences" -> true
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, addAnotherPreviousAdministrativeReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, addAnotherPreviousAdministrativeReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
