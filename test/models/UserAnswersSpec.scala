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

package models

import base.SpecBase
import play.api.libs.json.Json

import java.time.Instant

class UserAnswersSpec extends SpecBase {

  private val epochMilli = 946684800000L

  private val id = "9091dc9e-62d0-4974-9e5a-6fd2309268f1"

  "must read new date format" in {

    val json = Json.parse(s"""
        |{
        |    "_id" : "$id",
        |    "eoriNumber" : "${eoriNumber.value}",
        |    "lrn" : "${lrn.value}",
        |    "data" : {},
        |    "lastUpdated" : {
        |        "$$date" : {
        |            "$$numberLong" : "$epochMilli"
        |        }
        |    }
        |}""".stripMargin)

    val result = json.as[UserAnswers]

    result mustBe UserAnswers(lrn, eoriNumber, Json.obj(), Instant.ofEpochMilli(epochMilli), Id(id))
  }

}
