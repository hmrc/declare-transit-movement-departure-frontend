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

package forms.addItems.traderDetails

import forms.mappings.Mappings
import models.Index
import models.domain.StringFieldRegex.alphaNumericWithSpaceRegex
import play.api.data.Form
import uk.gov.hmrc.play.mappers.StopOnFirstFail

import javax.inject.Inject

class TraderDetailsConsignorNameFormProvider @Inject() extends Mappings {

  val maxLengthConsignorName = 35

  def apply(index: Index): Form[String] =
    Form(
      "value" -> text("traderDetailsConsignorName.error.required", Seq(index.display))
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLengthConsignorName, "traderDetailsConsignorName.error.length"),
            regexp(alphaNumericWithSpaceRegex, "traderDetailsConsignorName.error.invalid")
          )
        )
    )
}
