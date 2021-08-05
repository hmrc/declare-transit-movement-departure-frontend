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

package controllers.addItems

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import matchers.JsonMatchers
import models.{CountryList, DocumentTypeList, MethodOfPaymentList, PreviousReferencesDocumentTypeList, SpecialMentionList}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class ItemsCheckYourAnswersControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  lazy val itemRoute = routes.ItemsCheckYourAnswersController.onPageLoad(lrn, index).url

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockRefDataConnector))

  "ItemsCheckYourAnswersController" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getDocumentTypes()(any(), any())).thenReturn(Future.successful(DocumentTypeList(Nil)))
      when(mockRefDataConnector.getSpecialMention()(any(), any())).thenReturn(Future.successful(SpecialMentionList(Nil)))
      when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any())).thenReturn(Future.successful(PreviousReferencesDocumentTypeList(Nil)))
      when(mockRefDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(CountryList(Nil)))
      when(mockRefDataConnector.getMethodOfPaymentList()(any(), any())).thenReturn(Future.successful(MethodOfPaymentList(Nil)))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(GET, itemRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"         -> lrn,
        "nextPageUrl" -> routes.AddAnotherItemController.onPageLoad(lrn).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "addItemsAnnotations/itemsCheckYourAnswers.njk"
      jsonWithoutConfig mustBe expectedJson
    }
  }
}
