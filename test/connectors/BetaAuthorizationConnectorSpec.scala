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

package connectors

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.github.tomakehurst.wiremock.client.WireMock._
import helper.WireMockServerHandler
import models.BetaEoriNumber
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Future

class BetaAuthorizationConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks {

  private val startUrl = "/transit-movements-trader-authorization"

  lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.transit-movements-trader-authorization.port" -> server.port()
    )
    .build()

  private lazy val connector: BetaAuthorizationConnector =
    app.injector.instanceOf[BetaAuthorizationConnector]

  "BetaAuthorizationConnector" - {

    "getBetaUser" - {
      "must return true if status is NO_CONTENT" in {

        server.stubFor(
          post(urlEqualTo(s"$startUrl/features/private-beta"))
            .withHeader("Content-Type", containing("application/json"))
            .willReturn(
              aResponse()
                .withStatus(NO_CONTENT)
            )
        )

        val result: Future[Boolean] = connector.getBetaUser(BetaEoriNumber("eoriNumber"))
        result.futureValue mustBe true
      }

      "must return false if status is anything else" in {

        server.stubFor(
          post(urlEqualTo(s"$startUrl/features/private-beta"))
            .withHeader("Content-Type", containing("application/json"))
            .willReturn(
              aResponse()
                .withStatus(NOT_FOUND)
            )
        )

        val result: Future[Boolean] = connector.getBetaUser(BetaEoriNumber("eoriNumber"))
        result.futureValue mustBe false
      }
    }
  }

}
