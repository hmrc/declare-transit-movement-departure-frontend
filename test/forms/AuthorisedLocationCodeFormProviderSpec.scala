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
import models.domain.StringFieldRegex.authorisedLocationCodeRegex
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class AuthorisedLocationCodeFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey = "authorisedLocationCode.error.required"
  private val lengthKey   = "authorisedLocationCode.error.length"
  private val maxLength   = 17
  private val form        = new AuthorisedLocationCodeFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must bind special characters" in {

      val specialCharacterString = "&'@/.-?% "

      val result = form.bind(Map(fieldName -> "&'@/.-?% ")).apply(fieldName)

      result.value.value mustBe specialCharacterString
    }

    "must not bind strings with invalid characters" in {

      val invalidKey             = "authorisedLocationCode.error.invalidCharacters"
      val expectedError          = FormError(fieldName, invalidKey, Seq(authorisedLocationCodeRegex))
      val generator: Gen[String] = RegexpGen.from(s"[!£^*(){}_+=:;|`~<>,±üçñèé@]{$maxLength}")

      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
