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
import matchers.JsonMatchers
import models.{DocumentTypeList, PreviousReferencesDocumentTypeList, SpecialMentionList}
import navigation.Navigator
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
import services.{DocumentTypesService, PreviousDocumentTypesService, SpecialMentionTypesService}
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class ItemsCheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  lazy val itemRoute = routes.ItemsCheckYourAnswersController.onPageLoad(lrn, index).url

  private val mockDocumentTypesService: DocumentTypesService                 = mock[DocumentTypesService]
  private val mockPreviousDocumentTypesService: PreviousDocumentTypesService = mock[PreviousDocumentTypesService]
  private val mockSpecialMentionTypesService: SpecialMentionTypesService     = mock[SpecialMentionTypesService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).toInstance(fakeNavigator))
      .overrides(bind(classOf[DocumentTypesService]).toInstance(mockDocumentTypesService))
      .overrides(bind(classOf[PreviousDocumentTypesService]).toInstance(mockPreviousDocumentTypesService))
      .overrides(bind(classOf[SpecialMentionTypesService]).toInstance(mockSpecialMentionTypesService))

  "ItemsCheckYourAnswersController" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockDocumentTypesService.getDocumentTypes()(any())).thenReturn(Future.successful(DocumentTypeList(Nil)))
      when(mockSpecialMentionTypesService.getSpecialMentionTypes()(any())).thenReturn(Future.successful(SpecialMentionList(Nil)))
      when(mockPreviousDocumentTypesService.getPreviousDocumentTypes()(any())).thenReturn(Future.successful(PreviousReferencesDocumentTypeList(Nil)))

      setUserAnswers(Some(emptyUserAnswers))

      val request                                = FakeRequest(GET, itemRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"         -> lrn,
        "nextPageUrl" -> routes.AddAnotherItemController.onPageLoad(lrn).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "addItems/itemsCheckYourAnswers.njk"
      jsonWithoutConfig mustBe expectedJson
    }
  }
}
