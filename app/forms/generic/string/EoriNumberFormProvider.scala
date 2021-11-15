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

package forms.generic.string

import forms.Constants.maxLengthEoriNumber
import forms.StopOnFirstFail
import forms.mappings.Mappings
import models.Index
import models.domain.StringFieldRegex.{alphaNumericRegex, eoriNumberRegex, stringFieldRegex}
import models.reference.CountryCode
import play.api.data.Form

import javax.inject.Inject

class EoriNumberFormProvider @Inject() extends Mappings {

  def apply(messageKeyPrefix: String): Form[String] =
    apply(messageKeyPrefix, Nil)

  def apply(messageKeyPrefix: String, index: Index): Form[String] =
    apply(messageKeyPrefix, Seq(index.display))

  private def apply(messageKeyPrefix: String, args: Seq[Any]): Form[String] =
    Form(
      "value" -> text(s"$messageKeyPrefix.error.required", args)
        .verifying(
          forms.StopOnFirstFail[String](
            maxLength(maxLengthEoriNumber, s"$messageKeyPrefix.error.length"),
            regexp(stringFieldRegex, s"$messageKeyPrefix.error.invalidCharacters"),
            regexp(eoriNumberRegex, s"$messageKeyPrefix.error.invalidFormat")
          )
        )
    )

  def apply(messageKeyPrefix: String, simplified: Boolean, countryCode: CountryCode): Form[String] =
    Form(
      "value" -> text(s"$messageKeyPrefix.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLengthEoriNumber, s"$messageKeyPrefix.error.length"),
            regexp(alphaNumericRegex, s"$messageKeyPrefix.error.invalidCharacters"),
            regexp(eoriNumberRegex, s"$messageKeyPrefix.error.invalidFormat"),
            isSimplified(simplified, countryCode, s"$messageKeyPrefix.error.prefix")
          )
        )
    )
}
