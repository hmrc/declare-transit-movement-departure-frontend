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

package services

import base.{MockServiceApp, SpecBase}
import connectors.DepartureMovementConnector
import generators.MessagesModelGenerators
import models.messages.DeclarationRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status.ACCEPTED
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class DeclarationSubmissionServiceSpec extends SpecBase with MockServiceApp with BeforeAndAfterEach with MessagesModelGenerators with ScalaCheckPropertyChecks {

  private val mockDepartureMovementConnector           = mock[DepartureMovementConnector]
  private val mockDeclarationRequestService            = mock[DeclarationRequestService]
  val declarationService: DeclarationSubmissionService = app.injector.instanceOf[DeclarationSubmissionService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMovementConnector].toInstance(mockDepartureMovementConnector))
      .overrides(bind[DeclarationRequestService].toInstance(mockDeclarationRequestService))

  override def beforeEach(): Unit = {
    reset(mockDepartureMovementConnector, mockDeclarationRequestService)
    super.beforeEach()
  }

  "DeclarationSubmissionService" - {

    "must create departure declaration for valid input" in {
      val request: DeclarationRequest = arbitrary[DeclarationRequest].sample.value

      when(mockDepartureMovementConnector.submitDepartureMovement(any())(any())).thenReturn(Future.successful(HttpResponse(ACCEPTED)))
      when(mockDeclarationRequestService.convert(any())).thenReturn(Future.successful(Some(request)))

      declarationService.submit(emptyUserAnswers).futureValue.value.status mustBe ACCEPTED
    }

    "must return failure status on failing to create departure declaration" in {
      val errorCode                   = Gen.chooseNum(400, 599).sample.value
      val request: DeclarationRequest = arbitrary[DeclarationRequest].sample.value

      when(mockDepartureMovementConnector.submitDepartureMovement(any())(any())).thenReturn(Future.successful(HttpResponse(errorCode)))
      when(mockDeclarationRequestService.convert(any())).thenReturn(Future.successful(Some(request)))

      declarationService.submit(emptyUserAnswers).futureValue.value.status mustBe errorCode
    }

    "must return None on failing to create departure declaration" in {
      when(mockDeclarationRequestService.convert(any())).thenReturn(Future.successful(None))

      declarationService.submit(emptyUserAnswers).futureValue mustBe None
    }
  }
}
