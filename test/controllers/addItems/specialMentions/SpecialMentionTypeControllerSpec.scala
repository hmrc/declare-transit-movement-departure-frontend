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
import forms.addItems.specialMentions.SpecialMentionTypeFormProvider
import matchers.JsonMatchers
import models.reference.SpecialMention
import models.{NormalMode, SpecialMentionList}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsSpecialMentions
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.specialMentions.SpecialMentionTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class SpecialMentionTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  private val formProvider = new SpecialMentionTypeFormProvider()

  private val specialMentionList = SpecialMentionList(
    Seq(
      SpecialMention("10600", "Negotiable Bill of lading 'to order blank endorsed'"),
      SpecialMention("30400", "RET-EXP – Copy 3 to be returned")
    )
  )
  private val form     = formProvider(specialMentionList, itemIndex)
  private val template = "addItems/specialMentions/specialMentionType.njk"

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private lazy val specialMentionTypeRoute = routes.SpecialMentionTypeController.onPageLoad(lrn, itemIndex, referenceIndex, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsSpecialMentions]).toInstance(fakeNavigator))
      .overrides(bind[ReferenceDataConnector].toInstance(mockRefDataConnector))

  "SpecialMentionType Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      setUserAnswers(Some(emptyUserAnswers))

      when(mockRefDataConnector.getSpecialMention()(any(), any())).thenReturn(Future.successful(specialMentionList))

      val request                                = FakeRequest(GET, specialMentionTypeRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedSpecialMentionJson = Seq(
        Json.obj("value" -> "", "text"      -> "Select"),
        Json.obj("value" -> "10600", "text" -> "(10600) Negotiable Bill of lading 'to order blank endorsed'", "selected" -> false),
        Json.obj("value" -> "30400", "text" -> "(30400) RET-EXP – Copy 3 to be returned", "selected"                     -> false)
      )

      val expectedJson = Json.obj(
        "form"           -> form,
        "index"          -> itemIndex.display,
        "referenceIndex" -> referenceIndex.display,
        "specialMention" -> expectedSpecialMentionJson,
        "lrn"            -> lrn,
        "mode"           -> NormalMode
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = emptyUserAnswers.set(SpecialMentionTypePage(itemIndex, referenceIndex), "10600").success.value
      setUserAnswers(Some(userAnswers))

      when(mockRefDataConnector.getSpecialMention()(any(), any())).thenReturn(Future.successful(specialMentionList))

      val request                                = FakeRequest(GET, specialMentionTypeRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "10600"))

      val expectedSpecialMentionJson = Seq(
        Json.obj("value" -> "", "text"      -> "Select"),
        Json.obj("value" -> "10600", "text" -> "(10600) Negotiable Bill of lading 'to order blank endorsed'", "selected" -> true),
        Json.obj("value" -> "30400", "text" -> "(30400) RET-EXP – Copy 3 to be returned", "selected"                     -> false)
      )

      val expectedJson = Json.obj(
        "form"           -> filledForm,
        "index"          -> itemIndex.display,
        "referenceIndex" -> referenceIndex.display,
        "specialMention" -> expectedSpecialMentionJson,
        "lrn"            -> lrn,
        "mode"           -> NormalMode
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {

      setUserAnswers(Some(emptyUserAnswers))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRefDataConnector.getSpecialMention()(any(), any())).thenReturn(Future.successful(specialMentionList))

      val request =
        FakeRequest(POST, specialMentionTypeRoute)
          .withFormUrlEncodedBody(("value", "10600"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getSpecialMention()(any(), any())).thenReturn(Future.successful(specialMentionList))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(POST, specialMentionTypeRoute).withFormUrlEncodedBody(("value", ""))
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

      setUserAnswers(None)

      val request = FakeRequest(GET, specialMentionTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, specialMentionTypeRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
