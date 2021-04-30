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

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class SealIdDetailsFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val requiredKey = "sealIdDetails.error.required"
  private val lengthKey   = "sealIdDetails.error.length"
  private val maxLength   = 20
  private val invalidKey  = "sealIdDetails.error.invalidCharacters"
  private val form        = new SealIdDetailsFormProvider()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form(sealIndex),
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form(sealIndex),
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form(sealIndex),
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidCharacters(form(index), fieldName, invalidKey, maxLength)

  }
}
