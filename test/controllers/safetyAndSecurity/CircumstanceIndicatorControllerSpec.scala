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

package controllers.safetyAndSecurity

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoute}
import forms.safetyAndSecurity.CircumstanceIndicatorFormProvider
import matchers.JsonMatchers
import models.reference.CircumstanceIndicator
import models.userAnswerScenarios.Scenario1.UserAnswersSpecHelperOps
import models.{CheckMode, CircumstanceIndicatorList, NormalMode}
import navigation.annotations.SafetyAndSecurity
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.safetyAndSecurity.{CircumstanceIndicatorPage, PlaceOfUnloadingCodePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.CircumstanceIndicatorsService
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.getCircumstanceIndicatorsAsJson

import scala.concurrent.Future

class CircumstanceIndicatorControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers
    with BeforeAndAfterEach {

  private val formProvider                      = new CircumstanceIndicatorFormProvider()
  private val template                          = "safetyAndSecurity/circumstanceIndicator.njk"
  private val mockCircumstanceIndicatorsService = mock[CircumstanceIndicatorsService]

  private val circumstanceIndicatorList: CircumstanceIndicatorList = CircumstanceIndicatorList(
    Seq(
      CircumstanceIndicator("A", "Data1"),
      CircumstanceIndicator("B", "Data2"),
      CircumstanceIndicator("E", "Data2")
    )
  )
  private val form = formProvider(circumstanceIndicatorList)

  private lazy val circumstanceIndicatorRoute          = routes.CircumstanceIndicatorController.onPageLoad(lrn, NormalMode).url
  private lazy val circumstanceIndicatorCheckModeRoute = routes.CircumstanceIndicatorController.onPageLoad(lrn, CheckMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[SafetyAndSecurity]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind[CircumstanceIndicatorsService].toInstance(mockCircumstanceIndicatorsService))

  override def beforeEach(): Unit = {
    reset(mockCircumstanceIndicatorsService)
    super.beforeEach()
  }

  "CircumstanceIndicator Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockCircumstanceIndicatorsService.getCircumstanceIndicators()(any())).thenReturn(Future.successful(circumstanceIndicatorList))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(GET, circumstanceIndicatorRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"                   -> form,
        "mode"                   -> NormalMode,
        "lrn"                    -> lrn,
        "circumstanceIndicators" -> getCircumstanceIndicatorsAsJson(form.value, circumstanceIndicatorList.circumstanceIndicators)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockCircumstanceIndicatorsService.getCircumstanceIndicators()(any())).thenReturn(Future.successful(circumstanceIndicatorList))

      val userAnswers = emptyUserAnswers.set(CircumstanceIndicatorPage, "A").success.value
      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, circumstanceIndicatorRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "A"))

      val expectedJson = Json.obj(
        "form"                   -> filledForm,
        "lrn"                    -> lrn,
        "mode"                   -> NormalMode,
        "circumstanceIndicators" -> getCircumstanceIndicatorsAsJson(filledForm.value, circumstanceIndicatorList.circumstanceIndicators)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCircumstanceIndicatorsService.getCircumstanceIndicators()(any())).thenReturn(Future.successful(circumstanceIndicatorList))

      setUserAnswers(Some(emptyUserAnswers))
      circumstanceIndicatorList.circumstanceIndicators.foreach {
        indicator =>
          val request =
            FakeRequest(POST, circumstanceIndicatorRoute)
              .withFormUrlEncodedBody(("value", indicator.code))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url

      }

    }

    "must redirect to the next page when valid data is submitted in CheckMode for Indicator E with Unloading Code" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCircumstanceIndicatorsService.getCircumstanceIndicators()(any())).thenReturn(Future.successful(circumstanceIndicatorList))

      val updatedAnswers = emptyUserAnswers
        .unsafeSetVal(PlaceOfUnloadingCodePage)("unloadingcode")
      setUserAnswers(Some(updatedAnswers))
      val request =
        FakeRequest(POST, circumstanceIndicatorCheckModeRoute)
          .withFormUrlEncodedBody(("value", "E"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must redirect to the next page when valid data is submitted in CheckMode for Indicator E with  Unloading Code not set" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCircumstanceIndicatorsService.getCircumstanceIndicators()(any())).thenReturn(Future.successful(circumstanceIndicatorList))

      setUserAnswers(Some(emptyUserAnswers))
      val request =
        FakeRequest(POST, circumstanceIndicatorCheckModeRoute)
          .withFormUrlEncodedBody(("value", "E"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockCircumstanceIndicatorsService.getCircumstanceIndicators()(any())).thenReturn(Future.successful(circumstanceIndicatorList))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(POST, circumstanceIndicatorRoute).withFormUrlEncodedBody(("value", ""))
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

      val request = FakeRequest(GET, circumstanceIndicatorRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, circumstanceIndicatorRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }
  }

}
