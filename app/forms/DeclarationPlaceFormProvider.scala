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

package forms

import forms.mappings.Mappings
import play.api.data.Form
import javax.inject.Inject

class DeclarationPlaceFormProvider @Inject() extends Mappings {

  val postCodeRegex: String = "^[a-zA-Z]{1,2}[0-9][0-9a-zA-Z]?\\s?[0-9][a-zA-Z]{2}$"
  val maxLengthPostCode     = 9

  def apply(): Form[String] =
    Form(
      "value" -> trimmedText("declarationPlace.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLengthPostCode, "declarationPlace.error.length"),
            regexp(postCodeRegex, "declarationPlace.error.invalid", Seq.empty)
          )
        )
    )
}
