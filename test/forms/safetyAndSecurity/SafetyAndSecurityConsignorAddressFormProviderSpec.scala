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
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class SafetyAndSecurityConsignorAddressFormProviderSpec extends StringFieldBehaviours {

  private val countries     = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  private val consignorName = "ConsignorName"
  private val form          = new SafetyAndSecurityConsignorAddressFormProvider()(countries, consignorName)

  private val validAddressStringGenOverLength: Gen[String] = for {
    num  <- Gen.chooseNum[Int](addressMaxLength + 1, addressMaxLength + 5)
    list <- Gen.listOfN(num, Gen.alphaNumChar)
  } yield list.mkString("")

  ".AddressLine1" - {

    val fieldName   = "AddressLine1"
    val requiredKey = "safetyAndSecurityConsignorAddress.error.AddressLine1.required"
    val lengthKey   = "safetyAndSecurityConsignorAddress.error.AddressLine1.length"
    val invalidKey  = "safetyAndSecurityConsignorAddress.error.AddressLine1.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consignorName)),
      validAddressStringGenOverLength
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
    val requiredKey = "safetyAndSecurityConsignorAddress.error.AddressLine2.required"
    val lengthKey   = "safetyAndSecurityConsignorAddress.error.AddressLine2.length"
    val invalidKey  = "safetyAndSecurityConsignorAddress.error.AddressLine2.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consignorName)),
      validAddressStringGenOverLength
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
    val requiredKey = "safetyAndSecurityConsignorAddress.error.AddressLine3.required"
    val lengthKey   = "safetyAndSecurityConsignorAddress.error.AddressLine3.length"
    val invalidKey  = "safetyAndSecurityConsignorAddress.error.AddressLine3.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = addressMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(consignorName)),
      validAddressStringGenOverLength
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(consignorName))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, addressMaxLength, consignorName)
  }

}
