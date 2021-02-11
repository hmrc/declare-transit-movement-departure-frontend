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

import forms.Constants.maxLengthEoriNumber
import forms.behaviours.StringFieldBehaviours
import models.domain.StringFieldRegex.eoriNumberRegex
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class WhatIsConsigneeEoriFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey      = "whatIsConsigneeEori.error.required"
  private val lengthKey        = "whatIsConsigneeEori.error.length"
  private val invalidCharsKey  = "whatIsConsigneeEori.error.invalidCharacters"
  private val invalidFormatKey = "whatIsConsigneeEori.error.invalidFormat"
  private val form             = new WhatIsConsigneeEoriFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthEoriNumber)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLengthEoriNumber,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthEoriNumber))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings with invalid characters" in {
      val expectedError          = FormError(fieldName, invalidCharsKey)
      val generator: Gen[String] = RegexpGen.from(s"[!£^*(){}_+=:;|`~,±üçñèé@]{17}")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings that do not match the eoriNumber format regex" in {
      val expectedError          = FormError(fieldName, invalidFormatKey, Seq(eoriNumberRegex))
      val generator: Gen[String] = RegexpGen.from("^[a-zA-Z]{1}[0-9]{1}[0-9a-zA-Z]{1,13}")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
