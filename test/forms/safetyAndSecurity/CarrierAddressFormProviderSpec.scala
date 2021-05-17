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

class CarrierAddressFormProviderSpec extends StringFieldBehaviours {

  private val country      = Country(CountryCode("GB"), "United Kingdom")
  private val countries    = CountryList(Seq(country))
  private val formProvider = new CarrierAddressFormProvider()
  private val carrierName  = "carrierName"
  private val form         = formProvider(countries, carrierName)

  ".AddressLine1" - {

    val fieldName   = "AddressLine1"
    val requiredKey = "carrierAddress.error.AddressLine1.required"
    val lengthKey   = "carrierAddress.error.AddressLine1.length"
    val invalidKey  = "carrierAddress.error.AddressLine1.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(carrierName))
    )
    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, carrierName)
  }
  ".AddressLine2" - {

    val fieldName   = "AddressLine2"
    val requiredKey = "carrierAddress.error.AddressLine2.required"
    val lengthKey   = "carrierAddress.error.AddressLine2.length"
    val invalidKey  = "carrierAddress.error.AddressLine2.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(carrierName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, carrierName)
  }

  ".AddressLine3" - {

    val fieldName = "AddressLine3"

    val requiredKey = "carrierAddress.error.postalCode.required"
    val lengthKey   = "carrierAddress.error.postalCode.length"
    val invalidKey  = "carrierAddress.error.postalCode.invalid"

    val maxLength = 9

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(carrierName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, carrierName)
  }
}
