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

package forms

import forms.Constants._
import forms.mappings.Mappings
import models.domain.StringFieldRegex.{alphaNumericRegex, eoriNumberRegex}
import models.reference.CountryCode
import play.api.data.Form
import javax.inject.Inject

class WhatIsPrincipalEoriFormProvider @Inject() extends Mappings {

  def apply(simplified: Boolean, countryCode: CountryCode): Form[String] =
    Form(
      "value" -> text("whatIsPrincipalEori.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLengthEoriNumber, "whatIsPrincipalEori.error.length"),
            regexp(alphaNumericRegex, "whatIsPrincipalEori.error.invalidCharacters"),
            regexp(eoriNumberRegex, "whatIsPrincipalEori.error.invalidFormat"),
            isSimplified(simplified, countryCode, "whatIsPrincipalEori.error.prefix")
          )
        )
    )
}
