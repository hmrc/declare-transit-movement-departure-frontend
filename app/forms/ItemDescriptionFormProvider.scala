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

package forms

import forms.mappings.Mappings
import models.Index
import models.domain.StringFieldRegex.stringFieldRegex
import play.api.data.Form
import javax.inject.Inject

class ItemDescriptionFormProvider @Inject() extends Mappings {

  def apply(index: Index): Form[String] =
    Form(
      "value" -> trimmedText("itemDescription.error.required", Seq(index.display))
        .verifying(
          StopOnFirstFail[String](
            maxLength(280, "itemDescription.error.length", index.display),
            regexp(stringFieldRegex, "itemDescription.error.invalid", Seq(index.display))
          )
        )
    )
}
