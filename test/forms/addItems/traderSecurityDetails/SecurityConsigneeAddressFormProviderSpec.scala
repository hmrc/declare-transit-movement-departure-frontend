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

package forms.addItems.traderSecurityDetails

import forms.Constants.addressMaxLength
import forms.behaviours.StringFieldBehaviours
import models.CountryList
import models.reference.{Country, CountryCode}
import play.api.data.FormError

class SecurityConsigneeAddressFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey   = "securityConsigneeAddress.error.required"
  private val lengthKey     = "securityConsigneeAddress.error.length"
  private val country       = Country(CountryCode("GB"), "United Kingdom")
  private val countries     = CountryList(Seq(country))
  private val consigneeName = "Test"
  private val form          = new SecurityConsigneeAddressFormProvider()(countries, consigneeName)

  ".AddressLine1" - {

    val fieldName   = "AddressLine1"
    val requiredKey = "securityConsigneeAddress.error.AddressLine1.required"
    val lengthKey   = "securityConsigneeAddress.error.AddressLine1.length"
    val invalidKey  = "securityConsigneeAddress.error.AddressLine1.invalid"
    val maxLength   = addressMaxLength

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consigneeName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, consigneeName)
  }

  ".AddressLine2" - {

    val fieldName   = "AddressLine2"
    val requiredKey = "securityConsigneeAddress.error.AddressLine2.required"
    val lengthKey   = "securityConsigneeAddress.error.AddressLine2.length"
    val invalidKey  = "securityConsigneeAddress.error.AddressLine2.invalid"
    val maxLength   = addressMaxLength

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consigneeName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, consigneeName)
  }

  ".AddressLine3" - {

    val fieldName   = "AddressLine3"
    val requiredKey = "securityConsigneeAddress.error.AddressLine3.required"
    val lengthKey   = "securityConsigneeAddress.error.AddressLine3.length"
    val invalidKey  = "securityConsigneeAddress.error.AddressLine3.invalid"
    val maxLength   = addressMaxLength

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consigneeName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, consigneeName)
  }

}
