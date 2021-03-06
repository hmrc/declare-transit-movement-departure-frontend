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

package controllers.guaranteeDetails

import base.{MockNunjucksRendererApp, SpecBase}
import forms.AddAnotherGuaranteeFormProvider
import matchers.JsonMatchers
import models.{GuaranteeType, UserAnswers}
import navigation.annotations.GuaranteeDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.AddAnotherGuaranteePage
import pages.guaranteeDetails.GuaranteeTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AddAnotherGuaranteeControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new AddAnotherGuaranteeFormProvider()
  private val form         = formProvider(true)
  private val template     = "guaranteeDetails/addAnotherGuarantee.njk"

  lazy val addAnotherGuaranteeRoute = routes.AddAnotherGuaranteeController.onPageLoad(lrn).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[GuaranteeDetails]).toInstance(new FakeNavigator(onwardRoute)))

  val guarantee: GuaranteeType = GuaranteeType.FlatRateVoucher

  "AddAnotherGuarantee Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(
        emptyUserAnswers
          .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher)
          .success
          .value
      )

      val request        = FakeRequest(GET, addAnotherGuaranteeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"                -> form,
        "lrn"                 -> lrn,
        "pageTitle"           -> msg"addAnotherGuarantee.title.singular".withArgs(1),
        "heading"             -> msg"addAnotherGuarantee.heading.singular".withArgs(1),
        "allowMoreGuarantees" -> true,
        "radios"              -> Radios.yesNo(form("value"))
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(
        emptyUserAnswers
          .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher)
          .success
          .value
      )

      val request        = FakeRequest(GET, addAnotherGuaranteeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"                -> form,
        "lrn"                 -> lrn,
        "pageTitle"           -> msg"addAnotherGuarantee.title.singular".withArgs(1),
        "heading"             -> msg"addAnotherGuarantee.heading.singular".withArgs(1),
        "allowMoreGuarantees" -> true,
        "radios"              -> Radios.yesNo(form("value"))
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, addAnotherGuaranteeRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(
        emptyUserAnswers
          .set(GuaranteeTypePage(index), GuaranteeType.FlatRateVoucher)
          .success
          .value
      )

      val request        = FakeRequest(POST, addAnotherGuaranteeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"                -> boundForm,
        "lrn"                 -> lrn,
        "pageTitle"           -> msg"addAnotherGuarantee.title.singular".withArgs(1),
        "heading"             -> msg"addAnotherGuarantee.heading.singular".withArgs(1),
        "allowMoreGuarantees" -> true,
        "radios"              -> Radios.yesNo(form("value"))
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, addAnotherGuaranteeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, addAnotherGuaranteeRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
