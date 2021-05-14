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

package forms.addItems.traderDetails

import forms.Constants.addressMaxLength
import forms.behaviours.StringFieldBehaviours
import models.CountryList
import models.reference.{Country, CountryCode}
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class TraderDetailsConsignorAddressFormProviderSpec extends StringFieldBehaviours {

  private val country       = Country(CountryCode("GB"), "United Kingdom")
  private val countries     = CountryList(Seq(country))
  private val consignorName = "consignorName"
  private val formProvider  = new TraderDetailsConsignorAddressFormProvider()
  private val form          = formProvider(countries, consignorName)

  ".AddressLine1" - {

    val fieldName   = "AddressLine1"
    val requiredKey = "traderDetailsConsignorAddress.error.AddressLine1.required"
    val lengthKey   = "traderDetailsConsignorAddress.error.AddressLine1.length"
    val invalidKey  = "traderDetailsConsignorAddress.error.AddressLine1.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consignorName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consignorName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, consignorName)
  }

  ".AddressLine2" - {

    val fieldName   = "AddressLine2"
    val requiredKey = "traderDetailsConsignorAddress.error.AddressLine2.required"
    val lengthKey   = "traderDetailsConsignorAddress.error.AddressLine2.length"
    val invalidKey  = "traderDetailsConsignorAddress.error.AddressLine2.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consignorName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consignorName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, consignorName)
  }

  ".AddressLine3" - {

    val fieldName   = "AddressLine3"
    val requiredKey = "traderDetailsConsignorAddress.error.AddressLine3.required"
    val lengthKey   = "traderDetailsConsignorAddress.error.AddressLine3.length"
    val invalidKey  = "traderDetailsConsignorAddress.error.AddressLine3.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consignorName))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consignorName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, consignorName)
  }
}
