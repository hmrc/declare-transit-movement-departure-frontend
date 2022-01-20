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

package controllers.addItems.previousReferences

import base.{AppWithDefaultMockFixtures, SpecBase}
import matchers.JsonMatchers
import models.{NormalMode, PreviousReferencesDocumentTypeList}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.PreviousDocumentTypesService
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class ReferenceCheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  lazy val referenceCyaRoute: String =
    routes.ReferenceCheckYourAnswersController.onPageLoad(lrn, itemIndex, referenceIndex, NormalMode).url

  private val mockPreviousDocumentTypesService: PreviousDocumentTypesService = mock[PreviousDocumentTypesService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreviousDocumentTypesService]).toInstance(mockPreviousDocumentTypesService))

  "ReferenceCheckYourAnswersController" - {

    "must return OK and the correct view for a GET" in {

      setUserAnswers(Some(emptyUserAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockPreviousDocumentTypesService.getPreviousDocumentTypes()(any()))
        .thenReturn(Future.successful(PreviousReferencesDocumentTypeList(Nil)))

      val request                                = FakeRequest(GET, referenceCyaRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "nextPageUrl" -> routes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(lrn, itemIndex, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey - "section"

      templateCaptor.getValue mustEqual "addItems/referenceCheckYourAnswers.njk"
      jsonWithoutConfig mustBe expectedJson
    }
  }
}
