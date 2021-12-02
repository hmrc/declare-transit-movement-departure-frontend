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

import forms.Constants.addressMaxLength
import forms.behaviours.StringFieldBehaviours
import models.CountryList
import models.reference.{Country, CountryCode}
import play.api.data.FormError

class CommonAddressFormProviderSpec extends StringFieldBehaviours {

  private val country   = Country(CountryCode("GB"), "United Kingdom")
  private val countries = CountryList(Seq(country))
  private val name      = "Name"
  private val form      = new CommonAddressFormProvider()(countries, name)

  ".AddressLine1" - {

    val fieldName   = "AddressLine1"
    val requiredKey = "commonAddress.error.AddressLine1.required"
    val lengthKey   = "commonAddress.error.AddressLine1.length"
    val invalidKey  = "commonAddress.error.AddressLine1.invalidCharacters"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(name))
    )

    behave like mandatoryTrimmedField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(name))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, name)
  }

  ".AddressLine2" - {

    val fieldName   = "AddressLine2"
    val requiredKey = "commonAddress.error.AddressLine2.required"
    val lengthKey   = "commonAddress.error.AddressLine2.length"
    val invalidKey  = "commonAddress.error.AddressLine2.invalidCharacters"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(name))
    )

    behave like mandatoryTrimmedField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(name))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, name)
  }

  ".AddressLine3" - {

    val fieldName   = "AddressLine3"
    val requiredKey = "commonAddress.error.postalCode.required"
    val lengthKey   = "commonAddress.error.postalCode.length"
    val invalidKey  = "commonAddress.error.postalCode.invalidCharacters"
    val maxLength   = 9

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(name))
    )

    behave like mandatoryTrimmedField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(name))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, name)
  }
}
