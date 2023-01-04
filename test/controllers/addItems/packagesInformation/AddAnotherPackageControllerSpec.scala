/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.addItems.packagesInformation

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoutes}
import forms.addItems.AddAnotherPackageFormProvider
import matchers.JsonMatchers
import models.reference.PackageType
import models.{Index, NormalMode, UserAnswers}
import navigation.annotations.addItems.AddItemsPackagesInfo
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.PackageTypePage
import pages.addItems.AddAnotherPackagePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AddAnotherPackageControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  val formProvider = new AddAnotherPackageFormProvider()
  val form         = formProvider(true)

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsPackagesInfo]).toInstance(new FakeNavigator(onwardRoute)))

  lazy val addAnotherPackageRoute = controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(lrn, index, NormalMode).url

  "AddAnotherPackage Controller" - {

    "must return OK and the correct view for a GET" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, addAnotherPackageRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"              -> form,
        "mode"              -> NormalMode,
        "lrn"               -> lrn,
        "itemIndex"         -> itemIndex.display,
        "allowMorePackages" -> true,
        "radios"            -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual "addItems/addAnotherPackage.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(lrn, eoriNumber).set(AddAnotherPackagePage(index), true).success.value
      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, addAnotherPackageRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "true"))

      val expectedJson = Json.obj(
        "form"      -> filledForm,
        "mode"      -> NormalMode,
        "lrn"       -> lrn,
        "itemIndex" -> itemIndex.display,
        "radios"    -> Radios.yesNo(filledForm("value"))
      )

      templateCaptor.getValue mustEqual "addItems/addAnotherPackage.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to the next page when valid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, addAnotherPackageRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must redirect to the CYA page when reached maximum number of packages" in {
      val userAnswers = emptyUserAnswers
        .set(PackageTypePage(Index(0), Index(0)), PackageType("AB", "Description 1"))
        .success
        .value
        .set(PackageTypePage(Index(0), Index(1)), PackageType("AB", "Description 1"))
        .success
        .value
        .set(PackageTypePage(Index(0), Index(2)), PackageType("AB", "Description 1"))
        .success
        .value

      setUserAnswers(Some(userAnswers))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, addAnotherPackageRoute)
          .withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(POST, addAnotherPackageRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"              -> boundForm,
        "mode"              -> NormalMode,
        "lrn"               -> lrn,
        "itemIndex"         -> itemIndex.display,
        "allowMorePackages" -> true,
        "radios"            -> Radios.yesNo(formProvider(true)("value"))
      )

      templateCaptor.getValue mustEqual "addItems/addAnotherPackage.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)
      val request = FakeRequest(GET, addAnotherPackageRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, addAnotherPackageRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
