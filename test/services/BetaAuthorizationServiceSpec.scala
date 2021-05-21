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

package services

import base.{MockServiceApp, SpecBase}
import connectors.BetaAuthorizationConnector
import generators.MessagesModelGenerators
import models.BetaEoriNumber
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Configuration}

import scala.concurrent.Future

class BetaAuthorizationServiceSpec extends SpecBase with MockServiceApp with BeforeAndAfterEach with MessagesModelGenerators with ScalaCheckPropertyChecks {

  private val mockBetaAuthorizationConnector = mock[BetaAuthorizationConnector]

  val betaAuthorizationService: BetaAuthorizationService = app.injector.instanceOf[BetaAuthorizationService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(Configuration("privateBetaToggle" -> true))
      .overrides(bind[BetaAuthorizationConnector].toInstance(mockBetaAuthorizationConnector))

  override def beforeEach(): Unit = {
    reset(mockBetaAuthorizationConnector)
    super.beforeEach()
  }

  "BetaAuthorizationService" - {

    "must return true" - {

      "when beta toggle is false" in {

        val app: Application =
          super
            .guiceApplicationBuilder()
            .configure(Configuration("privateBetaToggle" -> false))
            .build()

        val betaAuthorizationService: BetaAuthorizationService = app.injector.instanceOf[BetaAuthorizationService]

        betaAuthorizationService.authorizedUser(BetaEoriNumber("test")).futureValue mustBe true
      }

      "when beta toggle is true and user is beta registered" in {

        when(mockBetaAuthorizationConnector.getBetaUser(any())(any()))
          .thenReturn(Future.successful(true))

        betaAuthorizationService.authorizedUser(BetaEoriNumber("test")).futureValue mustBe true
      }
    }

    "must return false" - {

      "when beta toggle is true and user is not beta registered" in {

        when(mockBetaAuthorizationConnector.getBetaUser(any())(any()))
          .thenReturn(Future.successful(false))

        betaAuthorizationService.authorizedUser(BetaEoriNumber("test")).futureValue mustBe false
      }
    }
  }
}
