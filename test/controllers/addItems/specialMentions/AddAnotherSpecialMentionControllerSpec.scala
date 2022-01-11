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

package controllers.addItems.specialMentions

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.ReferenceDataConnector
import forms.addItems.specialMentions.AddAnotherSpecialMentionFormProvider
import matchers.JsonMatchers
import models.{NormalMode, SpecialMentionList, UserAnswers}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsSpecialMentions
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.specialMentions.AddAnotherSpecialMentionPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AddAnotherSpecialMentionControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  private val formProvider = new AddAnotherSpecialMentionFormProvider()
  private val form         = formProvider()
  private val template     = "addItems/specialMentions/addAnotherSpecialMention.njk"

  private lazy val addAnotherSpecialMentionRoute = routes.AddAnotherSpecialMentionController.onPageLoad(lrn, itemIndex, NormalMode).url

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsSpecialMentions]).toInstance(fakeNavigator))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockRefDataConnector))

  "AddAnotherSpecialMention Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockRefDataConnector.getSpecialMention()(any(), any())).thenReturn(Future.successful(SpecialMentionList(Nil)))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(GET, addAnotherSpecialMentionRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"          -> form,
        "lrn"           -> lrn,
        "mode"          -> NormalMode,
        "itemIndex"     -> itemIndex.display,
        "radios"        -> Radios.yesNo(form("value")),
        "pageTitle"     -> msg"addAnotherSpecialMention.title.plural".withArgs(0, 1),
        "heading"       -> msg"addAnotherSpecialMention.heading.plural".withArgs(0, 1),
        "referenceRows" -> Nil
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(lrn, eoriNumber).set(AddAnotherSpecialMentionPage(itemIndex), true).success.value
      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, addAnotherSpecialMentionRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"          -> form,
        "lrn"           -> lrn,
        "mode"          -> NormalMode,
        "itemIndex"     -> itemIndex.display,
        "pageTitle"     -> msg"addAnotherSpecialMention.title.plural".withArgs(0, 1),
        "heading"       -> msg"addAnotherSpecialMention.heading.plural".withArgs(0, 1),
        "referenceRows" -> Nil
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setUserAnswers(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, addAnotherSpecialMentionRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(POST, addAnotherSpecialMentionRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"      -> boundForm,
        "lrn"       -> lrn,
        "mode"      -> NormalMode,
        "itemIndex" -> itemIndex.display,
        "pageTitle" -> msg"addAnotherSpecialMention.title.plural".withArgs(0, 1),
        "heading"   -> msg"addAnotherSpecialMention.heading.plural".withArgs(0, 1)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, addAnotherSpecialMentionRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, addAnotherSpecialMentionRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
