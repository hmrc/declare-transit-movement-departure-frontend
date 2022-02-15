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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import matchers.JsonMatchers
import models.CancellationDecisionUpdateMessage
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.DepartureMessageService

import java.time.LocalDate
import scala.concurrent.Future

class CancellationDecisionUpdateControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with JsonMatchers {

  private val mockDepartureMessageService = mock[DepartureMessageService]

  override def beforeEach(): Unit = {
    reset(mockDepartureMessageService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMessageService].toInstance(mockDepartureMessageService))

  "CancellationDecsionUpdate Controller" - {

    "return OK and the correct view for a GET when cancellation is not initiated by Customs and cancellation decision is Yes" in {
      val message = CancellationDecisionUpdateMessage("", Some(LocalDate.parse("2019-09-12")), 0, Some(1), LocalDate.parse("2019-09-12"), Some(""))

      when(mockDepartureMessageService.cancellationDecisionUpdateMessage(any())(any(), any()))
        .thenReturn(Future.successful(Some(message)))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(GET, routes.CancellationDecisionUpdateController.onPageLoad(departureId).url)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val cancellationOutcome = "cancellationDecision.declarationCancelled"
      val expectedJson = Json.obj(
        "cancellationOutcome" -> cancellationOutcome
      )

      templateCaptor.getValue mustEqual "cancellationDecisionUpdate.njk"
      jsonCaptor.getValue must containJson(expectedJson)
      jsonCaptor.getValue.values must contain(JsString(cancellationOutcome))
    }

    "return OK and the correct view for a GET when cancellation is not initiated by Customs and Cancellation decision  is No" in {
      val message = CancellationDecisionUpdateMessage("", Some(LocalDate.parse("2019-09-12")), 0, Some(0), LocalDate.parse("2019-09-12"), Some(""))

      when(mockDepartureMessageService.cancellationDecisionUpdateMessage(any())(any(), any()))
        .thenReturn(Future.successful(Some(message)))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(GET, routes.CancellationDecisionUpdateController.onPageLoad(departureId).url)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val cancellationOutcome = "cancellationDecision.cancellationRejected"
      val expectedJson = Json.obj(
        "cancellationOutcome" -> cancellationOutcome
      )

      templateCaptor.getValue mustEqual "cancellationDecisionUpdate.njk"
      jsonCaptor.getValue must containJson(expectedJson)
      jsonCaptor.getValue.values must contain(JsString(cancellationOutcome))
    }

    "return OK and the correct view for a GET when cancellation is initiated by Customs" in {
      val message = CancellationDecisionUpdateMessage("", Some(LocalDate.parse("2019-09-12")), 1, Some(0), LocalDate.parse("2019-09-12"), Some(""))

      when(mockDepartureMessageService.cancellationDecisionUpdateMessage(any())(any(), any()))
        .thenReturn(Future.successful(Some(message)))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(GET, routes.CancellationDecisionUpdateController.onPageLoad(departureId).url)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val cancellationOutcome = "cancellationDecision.declarationCancelled"
      val expectedJson = Json.obj(
        "cancellationOutcome" -> cancellationOutcome
      )

      templateCaptor.getValue mustEqual "cancellationDecisionUpdate.njk"
      jsonCaptor.getValue must containJson(expectedJson)
      jsonCaptor.getValue.values must contain(JsString(cancellationOutcome))
    }
  }

  "show technical difficulties page when no response from service" in {

    when(mockDepartureMessageService.cancellationDecisionUpdateMessage(any())(any(), any()))
      .thenReturn(Future.successful(None))

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    setUserAnswers(Some(emptyUserAnswers))

    val request = FakeRequest(GET, routes.CancellationDecisionUpdateController.onPageLoad(departureId).url)

    val result = route(app, request).value

    status(result) mustEqual INTERNAL_SERVER_ERROR
  }
}
