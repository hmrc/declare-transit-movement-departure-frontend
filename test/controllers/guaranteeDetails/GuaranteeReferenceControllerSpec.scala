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

package controllers.guaranteeDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoutes}
import forms.guaranteeDetails.GuaranteeReferenceFormProvider
import matchers.JsonMatchers
import models.GuaranteeType.{FlatRateVoucher, GuaranteeWaiver}
import models.NormalMode
import models.messages.guarantee.GuaranteeReferenceWithGrn
import navigation.annotations.GuaranteeDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class GuaranteeReferenceControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  private val formProvider = new GuaranteeReferenceFormProvider()
  private val form         = formProvider(GuaranteeReferenceWithGrn.Constants.guaranteeReferenceNumberLength)

  private lazy val guaranteeReferenceRoute = routes.GuaranteeReferenceController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[GuaranteeDetails]).toInstance(new FakeNavigator(onwardRoute)))

  "GuaranteeReference Controller" - {

    "must return OK and the correct view for a GET" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, guaranteeReferenceRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"  -> form,
        "mode"  -> NormalMode,
        "index" -> index.display,
        "lrn"   -> lrn
      )

      templateCaptor.getValue mustEqual "guaranteeDetails/guaranteeReference.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.set(GuaranteeReferencePage(index), "123456789012345678901234").success.value

      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, guaranteeReferenceRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "123456789012345678901234"))

      val expectedJson = Json.obj(
        "form"  -> filledForm,
        "index" -> index.display,
        "lrn"   -> lrn,
        "mode"  -> NormalMode
      )

      templateCaptor.getValue mustEqual "guaranteeDetails/guaranteeReference.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted for type that takes 24 chars" in {
      val userAnswers = emptyUserAnswers.set(GuaranteeTypePage(index), FlatRateVoucher).success.value
      setUserAnswers(Some(userAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, guaranteeReferenceRoute)
          .withFormUrlEncodedBody(("value", "123456789012345678901234"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must redirect to the next page when valid data is submitted for type that takes 17 chars" in {
      val userAnswers = emptyUserAnswers.set(GuaranteeTypePage(index), GuaranteeWaiver).success.value
      setUserAnswers(Some(userAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, guaranteeReferenceRoute)
          .withFormUrlEncodedBody(("value", "12345678901234567"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(POST, guaranteeReferenceRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"  -> boundForm,
        "index" -> index.display,
        "lrn"   -> lrn,
        "mode"  -> NormalMode
      )

      templateCaptor.getValue mustEqual "guaranteeDetails/guaranteeReference.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setUserAnswers(None)

      val request = FakeRequest(GET, guaranteeReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setUserAnswers(None)

      val request =
        FakeRequest(POST, guaranteeReferenceRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
