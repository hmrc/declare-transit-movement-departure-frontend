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
import models.domain.StringFieldRegex.principalTirHolderIdFormatRegex
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class PrincipalTirHolderIdFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey      = "principalTirHolderId.error.required"
  private val lengthKey        = "principalTirHolderId.error.length"
  private val invalidCharsKey  = "principalTirHolderId.error.characters"
  private val invalidFormatKey = "principalTirHolderId.error.format"
  private val maxLength        = 35
  val form                     = new PrincipalTirHolderIdFormProvider()()

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

    behave like fieldWithInvalidCharacters(form, fieldName, invalidCharsKey, maxLength)

    "must not bind strings that do not match the principalTirHolderIdRegex format regex" in {
      val expectedError          = FormError(fieldName, invalidFormatKey, Seq(principalTirHolderIdFormatRegex))
      val generator: Gen[String] = RegexpGen.from("^[\\/A-Z0-9]{15}")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
