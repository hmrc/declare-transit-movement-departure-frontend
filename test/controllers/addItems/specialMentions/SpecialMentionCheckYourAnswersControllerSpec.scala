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

package controllers.addItems.specialMentions

import base.{MockNunjucksRendererApp, SpecBase}
import matchers.JsonMatchers
import models.NormalMode
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class SpecialMentionCheckYourAnswersControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val specialMentionCyaRoute: String =
    routes.SpecialMentionCheckYourAnswersController.onPageLoad(lrn, itemIndex, referenceIndex, NormalMode).url

  "SpecialMentionCheckYourAnswersController" - {

    "must return OK and the correct view for a GET" in {

      dataRetrievalWithData(emptyUserAnswers)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request                                = FakeRequest(GET, specialMentionCyaRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "nextPageUrl" -> routes.AddAnotherSpecialMentionController.onPageLoad(lrn, itemIndex, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey - "section"

      templateCaptor.getValue mustEqual "addItems/specialMentions/specialMentionCheckYourAnswers.njk"
      jsonWithoutConfig mustBe expectedJson
    }
  }
}
