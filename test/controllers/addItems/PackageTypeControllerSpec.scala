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

package controllers.addItems

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoute}
import forms.addItems.PackageTypeFormProvider
import matchers.JsonMatchers
import models.reference.PackageType
import models.{NormalMode, PackageTypeList}
import navigation.annotations.AddItems
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalatestplus.mockito.MockitoSugar
import pages.PackageTypePage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class PackageTypeControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with JsonMatchers with NunjucksSupport {

  private def onwardRoute = Call("GET", "/foo")

  private val packageType1: PackageType = PackageType("AB", "Description 1")
  private val packageType2: PackageType = PackageType("CD", "Description 2")

  private val packageTypeList: PackageTypeList = PackageTypeList(Seq(packageType1, packageType2))

  private val form = new PackageTypeFormProvider()(packageTypeList)

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  lazy val packageTypeRoute: String = routes.PackageTypeController.onPageLoad(lrn, index, index, NormalMode).url

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mockRefDataConnector)
  }

  "PackageType Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getPackageTypes()(any(), any())).thenReturn(Future.successful(packageTypeList))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockRefDataConnector))
        .build()

      val request        = FakeRequest(GET, packageTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val packageTypesJson = Seq(
        Json.obj("value" -> "", "text"   -> ""),
        Json.obj("value" -> "AB", "text" -> "Description 1 (AB)", "selected" -> false),
        Json.obj("value" -> "CD", "text" -> "Description 2 (CD)", "selected" -> false)
      )

      val expectedJson = Json.obj(
        "form"         -> form,
        "mode"         -> NormalMode,
        "lrn"          -> lrn,
        "packageTypes" -> packageTypesJson
      )

      val jsonCaptorWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual "addItems/packageType.njk"
      jsonCaptorWithoutConfig mustEqual expectedJson

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getPackageTypes()(any(), any())).thenReturn(Future.successful(packageTypeList))

      val userAnswers = emptyUserAnswers.set(PackageTypePage(index, index), packageType1).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockRefDataConnector))
        .build()
      val request        = FakeRequest(GET, packageTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "AB"))

      val packageTypesJson = Seq(
        Json.obj("value" -> "", "text"   -> ""),
        Json.obj("value" -> "AB", "text" -> "Description 1 (AB)", "selected" -> true),
        Json.obj("value" -> "CD", "text" -> "Description 2 (CD)", "selected" -> false)
      )

      val expectedJson = Json.obj(
        "form"         -> filledForm,
        "mode"         -> NormalMode,
        "lrn"          -> lrn,
        "packageTypes" -> packageTypesJson
      )

      val jsonCaptorWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual "addItems/packageType.njk"
      jsonCaptorWithoutConfig mustEqual expectedJson

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRefDataConnector.getPackageTypes()(any(), any())).thenReturn(Future.successful(packageTypeList))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind(classOf[Navigator]).qualifiedWith(classOf[AddItems]).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[ReferenceDataConnector].toInstance(mockRefDataConnector)
          )
          .build()

      val request = FakeRequest(POST, packageTypeRoute).withFormUrlEncodedBody(("value", "AB"))

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getPackageTypes()(any(), any())).thenReturn(Future.successful(packageTypeList))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[ReferenceDataConnector].toInstance(mockRefDataConnector))
        .build()
      val request        = FakeRequest(POST, packageTypeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "addItems/packageType.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, packageTypeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, packageTypeRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
