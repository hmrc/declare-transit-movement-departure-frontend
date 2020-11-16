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

package forms.addItems.traderDetails

import forms.mappings.Mappings
import javax.inject.Inject
import models.Index
import play.api.data.Form
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class TraderDetailsConsigneeEoriNumberFormProvider @Inject() extends Mappings {

  val eoriNumberRegex: String          = "^[a-zA-Z]{2}[0-9]{1,15}"
  val maxLengthEoriNumber: Int         = 17
  val validEoriCharactersRegex: String = "^[a-zA-Z0-9]*$"

  def apply(index: Index): Form[String] =
    Form(
      "value" -> text("traderDetailsConsigneeEoriNumber.error.required", Seq(index.display))
        .verifying(StopOnFirstFail[String](
          maxLength(maxLengthEoriNumber, "traderDetailsConsigneeEoriNumber.error.length"),
          regexp(validEoriCharactersRegex, "traderDetailsConsigneeEoriNumber.error.invalid", index.display),
          regexp(eoriNumberRegex, "traderDetailsConsigneeEoriNumber.error.invalidFormat")
        )))
}
