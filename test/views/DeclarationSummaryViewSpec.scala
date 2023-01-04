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

package views

import models.LocalReferenceNumber
import play.api.libs.json.Json
import viewModels.DeclarationSummaryViewModel

class DeclarationSummaryViewSpec extends SingleViewSpec("declarationSummary.njk") {

  "does not have the a submit button when isDeclarationComplete is false" in {
    val json = Json.obj(
      "lrn"                    -> "",
      "sections"               -> Json.arr(),
      "backToTransitMovements" -> "",
      "isDeclarationComplete"  -> false
    )

    val doc = renderDocument(json).futureValue

    assertNotRenderedById(doc, "submit")
  }

  "has a submit button when isDeclarationComplete is true" in {
    val json = Json.obj(
      "lrn"                    -> "lrn",
      "sections"               -> Json.arr(),
      "backToTransitMovements" -> "",
      "isDeclarationComplete"  -> true,
      "onSubmitUrl"            -> DeclarationSummaryViewModel.nextPage(LocalReferenceNumber("lrn").get).url
    )

    val doc = renderDocument(json).futureValue

    assertRenderedById(doc, "submit")
  }

}
