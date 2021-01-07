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

import forms.Constants.{addressMaxLength, addressRegex}
import forms.behaviours.StringFieldBehaviours
import models.CountryList
import models.reference.{Country, CountryCode}
import org.scalacheck.Gen
import play.api.data.FormError

class SecurityConsigneeAddressFormProviderSpec extends StringFieldBehaviours {

  val requiredKey   = "securityConsigneeAddress.error.required"
  val lengthKey     = "securityConsigneeAddress.error.length"
  val country       = Country(CountryCode("GB"), "United Kingdom")
  val countries     = CountryList(Seq(country))
  val consigneeName = "Test"
  val form          = new SecurityConsigneeAddressFormProvider()(countries, consigneeName)

  ".AddressLine1" - {

    val fieldName   = "AddressLine1"
    val requiredKey = "securityConsigneeAddress.error.AddressLine1.required"
    val lengthKey   = "securityConsigneeAddress.error.AddressLine1.length"
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

    "must not bind strings that do not match the address line regex" in {
      val invalidKey = "securityConsigneeAddress.error.line1.invalid"

      val expectedError =
        List(FormError(fieldName, invalidKey, Seq(consigneeName)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(addressRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }

  ".AddressLine2" - {

    val fieldName   = "AddressLine2"
    val requiredKey = "securityConsigneeAddress.error.AddressLine2.required"
    val lengthKey   = "securityConsigneeAddress.error.AddressLine2.length"
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

    "must not bind strings that do not match the address line regex" in {
      val invalidKey = "securityConsigneeAddress.error.line2.invalid"

      val expectedError =
        List(FormError(fieldName, invalidKey, Seq(consigneeName)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(addressRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }

  ".AddressLine3" - {

    val fieldName   = "AddressLine3"
    val requiredKey = "securityConsigneeAddress.error.AddressLine3.required"
    val lengthKey   = "securityConsigneeAddress.error.AddressLine3.length"
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

    "must not bind strings that do not match the address line regex" in {
      val invalidKey = "securityConsigneeAddress.error.line3.invalid"

      val expectedError =
        List(FormError(fieldName, invalidKey, Seq(consigneeName)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(addressRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }

}
