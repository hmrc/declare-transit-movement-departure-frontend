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

package controllers

import base.{MockNunjucksRendererApp, SpecBase}
import config.FrontendAppConfig
import matchers.JsonMatchers
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.DeclarationSubmissionService
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class DeclarationSummaryControllerSpec extends SpecBase with MockNunjucksRendererApp with BeforeAndAfterEach with MockitoSugar with JsonMatchers {

  private val mockDeclarationSubmissionService = mock[DeclarationSubmissionService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DeclarationSubmissionService].toInstance(mockDeclarationSubmissionService))

  override def beforeEach: Unit = {
    reset(mockDeclarationSubmissionService)
    super.beforeEach
  }

  "DeclarationSummary Controller" - {

    "return OK and the correct view for a GET" in {

      dataRetrievalWithData(emptyUserAnswers)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val appConfig = app.injector.instanceOf[FrontendAppConfig]

      val request        = FakeRequest(GET, routes.DeclarationSummaryController.onPageLoad(lrn).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson =
        Json.obj(
          "lrn"                    -> lrn,
          "backToTransitMovements" -> appConfig.manageTransitMovementsViewDeparturesUrl
        )

      templateCaptor.getValue mustEqual "declarationSummary.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to 'Departure declaration sent' page on valid submission" in {
      dataRetrievalWithData(emptyUserAnswers)
      when(mockDeclarationSubmissionService.submit(any())(any())).thenReturn(Future.successful(Right(HttpResponse(ACCEPTED))))

      val request = FakeRequest(POST, routes.DeclarationSummaryController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SubmissionConfirmationController.onPageLoad(lrn).url
    }

    "must show TechnicalDifficulties page when there is a server side error" in {
      dataRetrievalWithData(emptyUserAnswers)
      val genServerError = Gen.chooseNum(500, 599).sample.value

      when(mockDeclarationSubmissionService.submit(any())(any())).thenReturn(Future.successful(Right(HttpResponse(genServerError))))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val request = FakeRequest(POST, routes.DeclarationSummaryController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

    }

  }

}
