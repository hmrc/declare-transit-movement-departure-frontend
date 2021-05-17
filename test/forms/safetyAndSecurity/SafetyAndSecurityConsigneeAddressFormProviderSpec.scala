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

package forms.safetyAndSecurity

import forms.Constants.addressMaxLength
import forms.behaviours.StringFieldBehaviours
import models.CountryList
import models.reference.{Country, CountryCode}
import play.api.data.FormError

class SafetyAndSecurityConsigneeAddressFormProviderSpec extends StringFieldBehaviours {

  private val countries     = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  private val consigneeName = "consigneeName"
  private val form          = new SafetyAndSecurityConsigneeAddressFormProvider()(countries, consigneeName)

  ".AddressLine1" - {

    val fieldName   = "AddressLine1"
    val requiredKey = "safetyAndSecurityConsigneeAddress.error.required"
    val lengthKey   = "safetyAndSecurityConsigneeAddress.error.length"
    val invalidKey  = "safetyAndSecurityConsigneeAddress.error.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(1))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, 1)
  }
  ".AddressLine2" - {

    val fieldName   = "AddressLine2"
    val requiredKey = "safetyAndSecurityConsigneeAddress.error.required"
    val lengthKey   = "safetyAndSecurityConsigneeAddress.error.length"
    val invalidKey  = "safetyAndSecurityConsigneeAddress.error.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(2))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, 2)
  }

  ".AddressLine3" - {

    val fieldName   = "AddressLine3"
    val requiredKey = "safetyAndSecurityConsigneeAddress.postalCode.error.required"
    val lengthKey   = "safetyAndSecurityConsigneeAddress.postalCode.error.length"
    val invalidKey  = "safetyAndSecurityConsigneeAddress.postalCode.error.invalid"

    val maxLength = 9

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consigneeName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, consigneeName)
  }
}
