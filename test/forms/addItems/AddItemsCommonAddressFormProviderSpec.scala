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

package forms.addItems

import forms.Constants.addressMaxLength
import forms.behaviours.StringFieldBehaviours
import models.reference.{Country, CountryCode}
import models.{CountryList, Index}
import play.api.data.FormError

class AddItemsCommonAddressFormProviderSpec extends StringFieldBehaviours {

  private val country       = Country(CountryCode("GB"), "United Kingdom")
  private val countries     = CountryList(Seq(country))
  private val consigneeName = "consigneeName"
  private val formProvider  = new AddItemsCommonAddressFormProvider()
  private val form          = formProvider(countries, consigneeName, Index(0))

  ".AddressLine1" - {

    val fieldName   = "AddressLine1"
    val requiredKey = "traderDetailsConsigneeAddress.error.AddressLine1.required"
    val lengthKey   = "traderDetailsConsigneeAddress.error.AddressLine1.length"
    val invalidKey  = "traderDetailsConsigneeAddress.error.AddressLine1.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consigneeName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consigneeName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, consigneeName)
  }

  ".AddressLine2" - {

    val fieldName   = "AddressLine2"
    val requiredKey = "traderDetailsConsigneeAddress.error.AddressLine2.required"
    val lengthKey   = "traderDetailsConsigneeAddress.error.AddressLine2.length"
    val invalidKey  = "traderDetailsConsigneeAddress.error.AddressLine2.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consigneeName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consigneeName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, consigneeName)
  }

  ".AddressLine3" - {

    val fieldName   = "AddressLine3"
    val requiredKey = "traderDetailsConsigneeAddress.error.postalCode.required"
    val lengthKey   = "traderDetailsConsigneeAddress.error.postalCode.length"
    val invalidKey  = "traderDetailsConsigneeAddress.error.postalCode.invalid"
    val maxLength   = 9

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consigneeName, 1))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consigneeName, 1))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, consigneeName, 1)
  }
}
