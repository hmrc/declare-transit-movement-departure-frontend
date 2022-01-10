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

package controllers.movementDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoute}
import forms.RepresentativeCapacityFormProvider
import matchers.JsonMatchers
import models.{NormalMode, RepresentativeCapacity}
import navigation.annotations.MovementDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.generalInformation.RepresentativeCapacityPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class RepresentativeCapacityControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  lazy val representativeCapacityRoute =
    routes.RepresentativeCapacityController.onPageLoad(lrn, NormalMode).url

  val formProvider = new RepresentativeCapacityFormProvider()
  val form         = formProvider()

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[MovementDetails]).toInstance(new FakeNavigator(onwardRoute)))

  "RepresentativeCapacity Controller" - {

    "must return OK and the correct view for a GET" in {
      setUserAnswers(Some(emptyUserAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, representativeCapacityRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1))
        .render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> RepresentativeCapacity.radios(form)
      )

      templateCaptor.getValue mustEqual "representativeCapacity.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = emptyUserAnswers
        .set(RepresentativeCapacityPage, RepresentativeCapacity.values.head)
        .success
        .value
      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, representativeCapacityRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1))
        .render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm =
        form.bind(Map("value" -> RepresentativeCapacity.values.head.toString))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> RepresentativeCapacity.radios(filledForm)
      )

      templateCaptor.getValue mustEqual "representativeCapacity.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to the next page when valid data is submitted" in {

      setUserAnswers(Some(emptyUserAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, representativeCapacityRoute)
          .withFormUrlEncodedBody(
            ("value", RepresentativeCapacity.values.head.toString)
          )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request = FakeRequest(POST, representativeCapacityRoute)
        .withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm                              = form.bind(Map("value" -> "invalid value"))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1))
        .render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "lrn"    -> lrn,
        "radios" -> RepresentativeCapacity.radios(boundForm)
      )

      templateCaptor.getValue mustEqual "representativeCapacity.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, representativeCapacityRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController
        .onPageLoad()
        .url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, representativeCapacityRoute)
          .withFormUrlEncodedBody(
            ("value", RepresentativeCapacity.values.head.toString)
          )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController
        .onPageLoad()
        .url

    }
  }
}
