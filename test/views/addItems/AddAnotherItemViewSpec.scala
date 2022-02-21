/*
 * Copyright 2022 HM Revenue & Customs
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

package views.addItems

import play.api.libs.json.Json
import views.SingleViewSpec

class AddAnotherItemViewSpec extends SingleViewSpec("addItems/addAnotherItem.njk") {

  "must display the maxLimitReached text when reached maximum item limit" in {
    val baseJson =
      Json.obj(
        "maxLimitReached" -> true
      )
    val doc = renderDocument(baseJson).futureValue
    getByElementTestIdSelector(doc, "maxLimit") must not be empty
  }

  "must not display the maxLimitReached text when below maximum item limit" in {
    val baseJson =
      Json.obj(
        "maxLimitReached" -> false
      )
    val doc = renderDocument(baseJson).futureValue
    getByElementTestIdSelector(doc, "maxLimit") must be(empty)
  }

  "must display the add another item Yes/No radio when below maximum item limit" in {
    val baseJson =
      Json.obj(
        "maxLimitReached" -> false
      )
    val doc = renderDocument(baseJson).futureValue
    assertContainsClass(doc, "govuk-radios")
  }
  "must not display the add another item Yes/No radio when reached maximum item limit" in {
    val baseJson =
      Json.obj(
        "maxLimitReached" -> true
      )
    val doc = renderDocument(baseJson).futureValue
    assertContainsNoClass(doc, "govuk-radios")

  }
}
