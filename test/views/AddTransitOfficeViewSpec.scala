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

package views

import play.api.libs.json.Json

class AddTransitOfficeViewSpec extends SingleViewSpec("addTransitOffice.njk") {

  "must display the maxLimitReached text when reached maximum transit office limit" in {
    val baseJson =
      Json.obj(
        "maxLimitReached" -> true
      )
    val doc = renderDocument(baseJson).futureValue
    getByElementTestIdSelector(doc, "maxLimit") must not be empty
  }

  "must not display the maxLimitReached text when below maximum office limit" in {
    val baseJson =
      Json.obj(
        "maxLimitReached" -> false
      )
    val doc = renderDocument(baseJson).futureValue
    getByElementTestIdSelector(doc, "maxLimit") must be(empty)
  }

  "must display the add another office Yes/No radio when below maximum office limit" in {
    val baseJson =
      Json.obj(
        "maxLimitReached" -> false
      )
    val doc = renderDocument(baseJson).futureValue
    getByElementTestIdSelector(doc, "addOfficeRadio") must not be empty
  }
  "must not display the add another office Yes/No radio when reached maximum office limit" in {
    val baseJson =
      Json.obj(
        "maxLimitReached" -> true
      )
    val doc = renderDocument(baseJson).futureValue
    getByElementTestIdSelector(doc, "addOfficeRadio") must be(empty)
  }
}
