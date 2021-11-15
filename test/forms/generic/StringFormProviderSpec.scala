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

package forms.generic

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Arbitrary._
import play.api.data.FormError

class StringFormProviderSpec extends StringFieldBehaviours {

  private val messageKeyPrefix = arbitrary[String].sample.value
  private val maxLength        = arbitrary[Int].sample.value
  private val requiredKey      = s"$messageKeyPrefix.error.required"
  private val lengthKey        = s"$messageKeyPrefix.error.length"
  private val invalidKey       = s"$messageKeyPrefix.error.invalidCharacters"
  private val form             = new StringFormProvider()(messageKeyPrefix, maxLength)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      invalidKey = invalidKey,
      length = maxLength
    )
  }
}
