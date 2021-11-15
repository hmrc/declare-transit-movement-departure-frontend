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

package controllers.addItems.containers

import base.{MockNunjucksRendererApp, SpecBase}
import forms.generic.YesNoFormProvider
import matchers.JsonMatchers
import models.NormalMode
import navigation.annotations.addItems.AddItemsContainer
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AddAnotherContainerControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = routes.ContainerNumberController.onPageLoad(lrn, itemIndex, containerIndex, NormalMode)

  private val formProvider = new YesNoFormProvider()
  private val form         = formProvider("addAnotherContainer")
  private val template     = "addItems/containers/addAnotherContainer.njk"

  private def expectedJson(form: Form[_]) = Json.obj(
    "form"           -> form,
    "index"          -> itemIndex.display,
    "mode"           -> NormalMode,
    "lrn"            -> lrn,
    "pageTitle"      -> "addAnotherContainer.title.plural",
    "containerCount" -> 0,
    "radios"         -> Radios.yesNo(form("value"))
  )

  private lazy val addAnotherContainerRoute = routes.AddAnotherContainerController.onPageLoad(lrn, itemIndex, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsContainer]).toInstance(new FakeNavigator(onwardRoute)))

  "AddAnotherContainer Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(GET, addAnotherContainerRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val jsonWithoutConfig = jsonCaptor.getValue - configKey - "containerRows"

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson(form)

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, addAnotherContainerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(POST, addAnotherContainerRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val jsonWithoutConfig = jsonCaptor.getValue - configKey - "containerRows"

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson(boundForm)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, addAnotherContainerRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, addAnotherContainerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
