/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.movementDetails

import base.{MockNunjucksRendererApp, SpecBase}
import controllers.{routes => mainRoutes}
import matchers.JsonMatchers
import models.DeclarationType
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.DeclarationTypePage
import pages.movementDetails.PreLodgeDeclarationPage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

// format: off
class MovementDetailsCheckYourAnswersControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with JsonMatchers {
  "MovementDetailsCheckYourAnswers Controller" - {

    "return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application    = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request        = FakeRequest(GET, routes.MovementDetailsCheckYourAnswersController.onPageLoad(lrn).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"         -> lrn,
        "nextPageUrl" -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
      )

      val jsonCaptorWithoutConfig: JsObject = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "movementDetailsCheckYourAnswers.njk"
      jsonCaptorWithoutConfig mustBe expectedJson

      application.stop()
    }

    "must contain correct number of rows and keys when passed answers" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val ua = emptyUserAnswers
        .set(DeclarationTypePage, DeclarationType.Option1).success.value
        .set(PreLodgeDeclarationPage, true).success.value

      val application    = applicationBuilder(userAnswers = Some(ua)).build()
      val request        = FakeRequest(GET, routes.MovementDetailsCheckYourAnswersController.onPageLoad(lrn).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value
      status(result)

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val json: JsObject = jsonCaptor.getValue

      val sections = json("sections")
      val rows     = sections \\ "key"


      rows.size mustBe 2
      rows(0)("text").as[String] mustBe "declarationType.checkYourAnswersLabel"
      rows(1)("text").as[String] mustBe "preLodgeDeclaration.checkYourAnswersLabel"

      application.stop()
    }
  }
  // format: on
}
