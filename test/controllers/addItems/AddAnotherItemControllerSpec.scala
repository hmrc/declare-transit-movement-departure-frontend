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

package controllers.addItems

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoutes}
import forms.addItems.AddAnotherItemFormProvider
import matchers.JsonMatchers
import models.Index
import navigation.Navigator
import navigation.annotations.addItems.AddItems
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.ItemDescriptionPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AddAnotherItemControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  val formProvider = new AddAnotherItemFormProvider()
  val form         = formProvider(true)

  lazy val addAnotherItemRoute = routes.AddAnotherItemController.onPageLoad(lrn).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItems]).toInstance(fakeNavigator))

  "AddAnotherItem Controller" - {

    "must return OK and the correct view for a GET when we can still add more items" in {
      val userAnswers = emptyUserAnswers.set(ItemDescriptionPage(index), "test").success.value

      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      val request                                = FakeRequest(GET, addAnotherItemRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"           -> form,
        "lrn"            -> lrn,
        "pageTitle"      -> msg"addAnotherItem.title.singular".withArgs(1),
        "heading"        -> msg"addAnotherItem.heading.singular".withArgs(1),
        "allowMoreItems" -> true,
        "radios"         -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual "addItems/addAnotherItem.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must return OK and the correct view for a GET when we have multiple items but can still add more items" in {
      val userAnswers = emptyUserAnswers
        .set(ItemDescriptionPage(index), "test")
        .success
        .value
        .set(ItemDescriptionPage(Index(1)), "test")
        .success
        .value

      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      val request                                = FakeRequest(GET, addAnotherItemRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"           -> form,
        "lrn"            -> lrn,
        "pageTitle"      -> msg"addAnotherItem.title.plural".withArgs(1),
        "heading"        -> msg"addAnotherItem.heading.plural".withArgs(1),
        "allowMoreItems" -> true,
        "radios"         -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual "addItems/addAnotherItem.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must return OK and the correct view for a GET when max items has been reached" in {
      val userAnswers = emptyUserAnswers
        .set(ItemDescriptionPage(index), "test")
        .success
        .value
        .set(ItemDescriptionPage(Index(1)), "test")
        .success
        .value
        .set(ItemDescriptionPage(Index(2)), "test")
        .success
        .value

      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      val request                                = FakeRequest(GET, addAnotherItemRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"           -> form,
        "lrn"            -> lrn,
        "pageTitle"      -> msg"addAnotherItem.title.plural".withArgs(1),
        "heading"        -> msg"addAnotherItem.heading.plural".withArgs(1),
        "allowMoreItems" -> false,
        "radios"         -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual "addItems/addAnotherItem.njk"
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to the next page when valid data is submitted" in {

      setUserAnswers(Some(emptyUserAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, addAnotherItemRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must redirect to the next page when valid data is not submitted but we've hit the max allowed items" in {
      val userAnswers = emptyUserAnswers
        .set(ItemDescriptionPage(index), "test")
        .success
        .value
        .set(ItemDescriptionPage(Index(1)), "test")
        .success
        .value
        .set(ItemDescriptionPage(Index(2)), "test")
        .success
        .value

      setUserAnswers(Some(userAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, addAnotherItemRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers.set(ItemDescriptionPage(index), "test").success.value

      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(POST, addAnotherItemRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"           -> boundForm,
        "pageTitle"      -> msg"addAnotherItem.title.singular".withArgs(1),
        "heading"        -> msg"addAnotherItem.heading.singular".withArgs(1),
        "lrn"            -> lrn,
        "allowMoreItems" -> true,
        "radios"         -> Radios.yesNo(boundForm("value"))
      )

      templateCaptor.getValue mustEqual "addItems/addAnotherItem.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setUserAnswers(None)
      val request = FakeRequest(GET, addAnotherItemRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setUserAnswers(None)
      val request =
        FakeRequest(POST, addAnotherItemRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
