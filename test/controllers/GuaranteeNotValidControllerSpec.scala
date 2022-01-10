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
import matchers.JsonMatchers.containJson
import models.InvalidGuaranteeCode.G01
import models.{GuaranteeNotValidMessage, InvalidGuaranteeReasonCode}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.DepartureMessageService

import scala.concurrent.Future

class GuaranteeNotValidControllerSpec extends SpecBase with AppWithDefaultMockFixtures with BeforeAndAfterEach {

  private val mockGuaranteeNotValidService = mock[DepartureMessageService]

  override def beforeEach(): Unit = {
    reset(mockGuaranteeNotValidService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMessageService].toInstance(mockGuaranteeNotValidService))

  "return OK and the correct guarantee not valid view for a GET" in {
    setUserAnswers(None)
    val message = GuaranteeNotValidMessage(lrn.toString, Seq(InvalidGuaranteeReasonCode("ref", G01, None)))
    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    when(mockGuaranteeNotValidService.guaranteeNotValidMessage(any())(any(), any()))
      .thenReturn(Future.successful(Some(message)))

    val request                                = FakeRequest(GET, routes.GuaranteeNotValidController.onPageLoad(departureId).url)
    val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

    val result = route(app, request).value

    status(result) mustEqual OK

    verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
    verify(mockGuaranteeNotValidService, times(1)).guaranteeNotValidMessage(eqTo(departureId))(any(), any())

    val expectedJson = Json.obj(
      "guaranteeNotValidMessage" -> Json.toJson(message),
      "contactUrl"               -> "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/new-computerised-transit-system-enquiries",
      "departureUrl"             -> routes.LocalReferenceNumberController.onPageLoad().url
    )

    templateCaptor.getValue mustEqual "guaranteeNotValid.njk"
    jsonCaptor.getValue must containJson(expectedJson)

  }

  "show 'Technical difficulties' page when arrival rejection message is malformed" in {

    when(mockGuaranteeNotValidService.guaranteeNotValidMessage(any())(any(), any()))
      .thenReturn(Future.successful(None))

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    setUserAnswers(None)

    val request = FakeRequest(GET, routes.GuaranteeNotValidController.onPageLoad(departureId).url)

    val result = route(app, request).value

    status(result) mustEqual INTERNAL_SERVER_ERROR
  }

}
