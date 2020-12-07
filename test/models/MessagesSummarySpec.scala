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

package models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

class MessagesSummarySpec extends AnyFreeSpec with Matchers {
  "MessagesSummarySpec" - {
    "De-serialise to Model" in {

      val messagesSummary = MessagesSummary(
        DepartureId(1),
        MessagesLocation(
          "/movements/arrivals/1/messages/1",
          Some("/movements/arrivals/1/messages/2"),
          Some("/movements/arrivals/1/messages/3"),
          Some("/movements/arrivals/1/messages/4"),
          Some("/movements/arrivals/1/messages/5")
        )
      )

      val json = Json.obj(
        "departureId" -> 1,
        "messages" -> Json.obj(
          "IE015" -> "/movements/arrivals/1/messages/1",
          "IE055" -> "/movements/arrivals/1/messages/2",
          "IE016" -> "/movements/arrivals/1/messages/3",
          "IE009" -> "/movements/arrivals/1/messages/4",
          "IE014" -> "/movements/arrivals/1/messages/5"
        )
      )

      json.as[MessagesSummary] mustBe messagesSummary
    }
  }
}
