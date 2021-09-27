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

package controllers.addItems.securityDetails

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.addItems.securityDetails.DangerousGoodsCodeFormProvider
import matchers.JsonMatchers
import models.reference.DangerousGoodsCode
import models.{DangerousGoodsCodeList, NormalMode}
import navigation.annotations.SecurityDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.securityDetails.DangerousGoodsCodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class DangerousGoodsCodeControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  val dangerousGoodsCode1 = DangerousGoodsCode("0004", "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass")
  val dangerousGoodsCode2 = DangerousGoodsCode("0005", "CARTRIDGES FOR WEAPONS with bursting charge")
  val dangerousGoodsCodes = DangerousGoodsCodeList(Seq(dangerousGoodsCode1, dangerousGoodsCode2))

  private val form     = new DangerousGoodsCodeFormProvider()(dangerousGoodsCodes)
  private val template = "addItems/securityDetails/dangerousGoodsCode.njk"

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private lazy val dangerousGoodsCodeRoute = routes.DangerousGoodsCodeController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[SecurityDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockRefDataConnector))

  "DangerousGoodsCode Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getDangerousGoodsCodeList()(any(), any())).thenReturn(Future.successful(dangerousGoodsCodes))

      dataRetrievalWithData(emptyUserAnswers)

      val request                                = FakeRequest(GET, dangerousGoodsCodeRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedDangerousGoodsCodeJson = Seq(
        Json.obj("value" -> "", "text"     -> "Select"),
        Json.obj("value" -> "0004", "text" -> "(0004) AMMONIUM PICRATE dry or wetted with less than 10% water, by mass", "selected" -> false),
        Json.obj("value" -> "0005", "text" -> "(0005) CARTRIDGES FOR WEAPONS with bursting charge", "selected"                      -> false)
      )

      val expectedJson = Json.obj(
        "form"                -> form,
        "index"               -> index.display,
        "dangerousGoodsCodes" -> expectedDangerousGoodsCodeJson,
        "lrn"                 -> lrn,
        "mode"                -> NormalMode
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getDangerousGoodsCodeList()(any(), any())).thenReturn(Future.successful(dangerousGoodsCodes))

      val userAnswers = emptyUserAnswers.set(DangerousGoodsCodePage(index), dangerousGoodsCode1.code).success.value
      dataRetrievalWithData(userAnswers)

      val request                                = FakeRequest(GET, dangerousGoodsCodeRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "0004"))

      val expectedDangerousGoodsCodeJson = Seq(
        Json.obj("value" -> "", "text"     -> "Select"),
        Json.obj("value" -> "0004", "text" -> "(0004) AMMONIUM PICRATE dry or wetted with less than 10% water, by mass", "selected" -> true),
        Json.obj("value" -> "0005", "text" -> "(0005) CARTRIDGES FOR WEAPONS with bursting charge", "selected"                      -> false)
      )

      val expectedJson = Json.obj(
        "form"                -> filledForm,
        "index"               -> index.display,
        "dangerousGoodsCodes" -> expectedDangerousGoodsCodeJson,
        "lrn"                 -> lrn,
        "mode"                -> NormalMode
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)

    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRefDataConnector.getDangerousGoodsCodeList()(any(), any())).thenReturn(Future.successful(dangerousGoodsCodes))
      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, dangerousGoodsCodeRoute)
          .withFormUrlEncodedBody(("value", "0004"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getDangerousGoodsCodeList()(any(), any())).thenReturn(Future.successful(dangerousGoodsCodes))
      dataRetrievalWithData(emptyUserAnswers)

      val request                                = FakeRequest(POST, dangerousGoodsCodeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, dangerousGoodsCodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, dangerousGoodsCodeRoute)
          .withFormUrlEncodedBody(("value", "test"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
