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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.{Application, Configuration}

import scala.concurrent.Future

class IsDeparturesEnabledServiceSpec extends SpecBase with MockServiceApp with BeforeAndAfterEach with MessagesModelGenerators with ScalaCheckPropertyChecks {

  private val mockBetaAuthorizationConnector = mock[BetaAuthorizationConnector]

  val isDeparturesEnabledService: IsDeparturesEnabledService = app.injector.instanceOf[IsDeparturesEnabledService]

  override def beforeEach(): Unit = {
    reset(mockBetaAuthorizationConnector)
    super.beforeEach()
  }

  "isDeparturesEnabled" - {

    "must return true" - {

      "when departures toggle is true" in {

        val app: Application =
          super
            .guiceApplicationBuilder()
            .configure(Configuration("microservice.services.features.isDeparturesEnabled" -> true))
            .build()

        val isDeparturesEnabledService: IsDeparturesEnabledService = app.injector.instanceOf[IsDeparturesEnabledService]

        isDeparturesEnabledService.isDeparturesEnabled("test").futureValue mustBe true
      }

      "when departures toggle is false and private beta has been enabled and user is beta registered" in {

        val app: Application =
          super
            .guiceApplicationBuilder()
            .configure(Configuration("microservice.services.features.isDeparturesEnabled" -> false))
            .configure(Configuration("microservice.services.features.isPrivateBetaEnabled" -> true))
            .overrides(bind[BetaAuthorizationConnector].toInstance(mockBetaAuthorizationConnector))
            .build()

        val isDeparturesEnabledService: IsDeparturesEnabledService = app.injector.instanceOf[IsDeparturesEnabledService]

        when(mockBetaAuthorizationConnector.getBetaUser(any())(any()))
          .thenReturn(Future.successful(true))

        isDeparturesEnabledService.isDeparturesEnabled("test").futureValue mustBe true
      }
    }

    "must return false" - {

      "when departures toggle is false and private beta has been disabled" in {

        val app: Application =
          super
            .guiceApplicationBuilder()
            .configure(Configuration("microservice.services.features.isDeparturesEnabled" -> false))
            .configure(Configuration("microservice.services.features.isPrivateBetaEnabled" -> false))
            .overrides(bind[BetaAuthorizationConnector].toInstance(mockBetaAuthorizationConnector))
            .build()

        val isDeparturesEnabledService: IsDeparturesEnabledService = app.injector.instanceOf[IsDeparturesEnabledService]

        isDeparturesEnabledService.isDeparturesEnabled("test").futureValue mustBe false
      }

      "when departures toggle is false and private beta has been enabled and user is not beta registered" in {

        val app: Application =
          super
            .guiceApplicationBuilder()
            .configure(Configuration("microservice.services.features.isDeparturesEnabled" -> false))
            .configure(Configuration("microservice.services.features.isPrivateBetaEnabled" -> true))
            .overrides(bind[BetaAuthorizationConnector].toInstance(mockBetaAuthorizationConnector))
            .build()

        val isDeparturesEnabledService: IsDeparturesEnabledService = app.injector.instanceOf[IsDeparturesEnabledService]

        when(mockBetaAuthorizationConnector.getBetaUser(any())(any()))
          .thenReturn(Future.successful(false))

        isDeparturesEnabledService.isDeparturesEnabled("test").futureValue mustBe false
      }
    }
  }
}
