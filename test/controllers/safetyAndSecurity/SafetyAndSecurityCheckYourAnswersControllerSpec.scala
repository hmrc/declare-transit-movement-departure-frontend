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

package controllers.safetyAndSecurity

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import matchers.JsonMatchers
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
import controllers.{routes => mainRoutes}
import models.{CircumstanceIndicatorList, CountryList, NormalMode}
import models.reference.{CircumstanceIndicator, Country, CountryCode}

import scala.concurrent.Future

class SafetyAndSecurityCheckYourAnswersControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val mockReferenceDataConnector = mock[ReferenceDataConnector]
  val countries                          = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  val circumstanceIndicatorsList         = CircumstanceIndicatorList(Seq(CircumstanceIndicator("C", "Road mode of transport")))

  lazy val safetyAndSecurityRoute = routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(lrn).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockReferenceDataConnector))

  "SafetyAndSecurityCheckYourAnswersController" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any())).thenReturn(Future.successful(countries))
      when(mockReferenceDataConnector.getCircumstanceIndicatorList()(any(), any())).thenReturn(Future.successful(circumstanceIndicatorsList))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(GET, safetyAndSecurityRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"                           -> lrn,
        "addAnotherCountryOfRoutingUrl" -> routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, NormalMode).url,
        "nextPageUrl"                   -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "safetyAndSecurity/SafetyAndSecurityCheckYourAnswers.njk"
      jsonWithoutConfig mustBe expectedJson
    }
  }
}
