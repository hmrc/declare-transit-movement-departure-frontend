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

package controllers.addItems.securityDetails

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.addItems.securityDetails.TransportChargesFormProvider
import matchers.JsonMatchers
import models.reference.MethodOfPayment
import models.{MethodOfPaymentList, NormalMode}
import navigation.annotations.SecurityDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.mockito.{ArgumentCaptor, Mockito}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.securityDetails.TransportChargesPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class TransportChargesControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new TransportChargesFormProvider()
  private val methodOfPaymentList = MethodOfPaymentList(
    Seq(
      MethodOfPayment("A", "Payment in cash"),
      MethodOfPayment("B", "Payment by credit card")
    )
  )
  private val form     = formProvider(methodOfPaymentList)
  private val template = "addItems/securityDetails/transportCharges.njk"

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  lazy val transportChargesRoute = routes.TransportChargesController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[SecurityDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind[ReferenceDataConnector].toInstance(mockRefDataConnector))

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mockRefDataConnector)
  }

  "TransportCharges Controller" - {

    "must return OK and the correct view for a GET" in {
      dataRetrievalWithData(emptyUserAnswers)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getMethodOfPaymentList()(any(), any())).thenReturn(Future.successful(methodOfPaymentList))

      val request        = FakeRequest(GET, transportChargesRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedMethodOfPaymentJson = Seq(
        Json.obj("value" -> "", "text"  -> ""),
        Json.obj("value" -> "A", "text" -> "(A) Payment in cash", "selected" -> false),
        Json.obj("value" -> "B", "text" -> "(B) Payment by credit card", "selected" -> false)
      )

      val expectedJson = Json.obj(
        "form"     -> form,
        "index"    -> itemIndex.display,
        "payments" -> expectedMethodOfPaymentJson,
        "lrn"      -> lrn,
        "mode"     -> NormalMode
      )
      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getMethodOfPaymentList()(any(), any())).thenReturn(Future.successful(methodOfPaymentList))

      val userAnswers = emptyUserAnswers.set(TransportChargesPage(index), "A").success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, transportChargesRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "A"))

      val expectedMethodOfPaymentJson = Seq(
        Json.obj("value" -> "", "text"  -> ""),
        Json.obj("value" -> "A", "text" -> "(A) Payment in cash", "selected" -> true),
        Json.obj("value" -> "B", "text" -> "(B) Payment by credit card", "selected" -> false)
      )

      val expectedJson = Json.obj(
        "form"     -> filledForm,
        "index"    -> itemIndex.display,
        "payments" -> expectedMethodOfPaymentJson,
        "lrn"      -> lrn,
        "mode"     -> NormalMode
      )
      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      when(mockRefDataConnector.getMethodOfPaymentList()(any(), any())).thenReturn(Future.successful(methodOfPaymentList))

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, transportChargesRoute)
          .withFormUrlEncodedBody(("value", "A"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getMethodOfPaymentList()(any(), any())).thenReturn(Future.successful(methodOfPaymentList))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(POST, transportChargesRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"  -> boundForm,
        "index" -> itemIndex.display,
        "lrn"   -> lrn,
        "mode"  -> NormalMode
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, transportChargesRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, transportChargesRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
