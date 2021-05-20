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

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class PrincipalAddressFormProviderSpec extends StringFieldBehaviours {

  private val principalName: String = "principal Name"
  private val form                  = new PrincipalAddressFormProvider()(principalName)

  ".numberAndStreet" - {

    val fieldName   = "numberAndStreet"
    val requiredKey = "principalAddress.error.numberAndStreet.required"
    val lengthKey   = "principalAddress.error.numberAndStreet.length"
    val invalidKey  = "principalAddress.error.numberAndStreet.invalidCharacters"
    val maxLength   = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(principalName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(principalName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, principalName)
  }

  ".town" - {

    val fieldName   = "town"
    val requiredKey = "principalAddress.error.town.required"
    val lengthKey   = "principalAddress.error.town.length"
    val invalidKey  = "principalAddress.error.town.invalidCharacters"
    val maxLength   = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(principalName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(principalName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, principalName)
  }

  ".postcode" - {

    val fieldName            = "postcode"
    val requiredKey          = "principalAddress.error.postcode.required"
    val lengthKey            = "principalAddress.error.postcode.length"
    val invalidCharactersKey = "principalAddress.error.postcode.invalidCharacters"
    val invalidFormat        = "principalAddress.error.postcode.invalidFormat"
    val maxLength            = 9

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(principalName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidCharactersKey, maxLength, principalName)

    behave like postcodeWithInvalidFormat(form, fieldName, invalidFormat, maxLength, principalName)

  }
}
