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

package controllers.addItems.specialMentions

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.addItems.specialMentions.RemoveSpecialMentionFormProvider
import matchers.JsonMatchers
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsSpecialMentions
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, verifyNoInteractions, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.specialMentions.{RemoveSpecialMentionPage, SpecialMentionTypePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class RemoveSpecialMentionControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  private val formProvider = new RemoveSpecialMentionFormProvider()
  private val form         = formProvider()
  private val template     = "addItems/specialMentions/removeSpecialMention.njk"

  lazy val removeSpecialMentionRoute = routes.RemoveSpecialMentionController.onPageLoad(lrn, itemIndex, referenceIndex, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsSpecialMentions]).toInstance(fakeNavigator))

  "RemoveSpecialMention Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(GET, removeSpecialMentionRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"           -> form,
        "mode"           -> NormalMode,
        "lrn"            -> lrn,
        "itemIndex"      -> itemIndex.display,
        "referenceIndex" -> referenceIndex.display,
        "radios"         -> Radios.yesNo(form("value"))
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(lrn, eoriNumber).set(RemoveSpecialMentionPage(itemIndex, referenceIndex), true).success.value
      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, removeSpecialMentionRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "true"))

      val expectedJson = Json.obj(
        "form"           -> filledForm,
        "mode"           -> NormalMode,
        "lrn"            -> lrn,
        "itemIndex"      -> itemIndex.display,
        "referenceIndex" -> referenceIndex.display,
        "radios"         -> Radios.yesNo(filledForm("value"))
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must remove data and redirect to the next page when true is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      val updatedUserAnswers                             = emptyUserAnswers.set(SpecialMentionTypePage(index, referenceIndex), "value").success.value
      setUserAnswers(Some(updatedUserAnswers))

      val request =
        FakeRequest(POST, removeSpecialMentionRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      verify(mockSessionRepository, times(1)).set(userAnswersCaptor.capture())
      userAnswersCaptor.getValue.get(SpecialMentionTypePage(index, referenceIndex)) mustBe None

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must not remove data and redirect to the next page when false is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      val updatedUserAnswers = emptyUserAnswers.set(SpecialMentionTypePage(index, referenceIndex), "value").success.value
      setUserAnswers(Some(updatedUserAnswers))

      val request =
        FakeRequest(POST, removeSpecialMentionRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      verifyNoInteractions(mockSessionRepository)

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(POST, removeSpecialMentionRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"           -> boundForm,
        "mode"           -> NormalMode,
        "lrn"            -> lrn,
        "itemIndex"      -> itemIndex.display,
        "referenceIndex" -> referenceIndex.display,
        "radios"         -> Radios.yesNo(boundForm("value"))
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, removeSpecialMentionRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, removeSpecialMentionRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
