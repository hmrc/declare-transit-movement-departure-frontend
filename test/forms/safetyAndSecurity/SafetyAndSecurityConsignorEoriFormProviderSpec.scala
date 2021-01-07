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

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class SafetyAndSecurityConsignorEoriFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey = "safetyAndSecurityConsignorEori.error.required"
  private val lengthKey   = "safetyAndSecurityConsignorEori.error.length"
  private val invalidKey  = "safetyAndSecurityConsignorEori.error.invalid"
  private val maxLength   = 17
  private val eoriRegex   = s"^[a-zA-Z0-9]{1,$maxLength}$$"

  val form = new SafetyAndSecurityConsignorEoriFormProvider()()

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
      maxLength   = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings that do not match the invalid characters regex" in {

      val expectedError =
        List(FormError(fieldName, invalidKey, Seq(eoriRegex)))
      val genInvalidString: Gen[String] = {
        stringsWithLength(maxLength) suchThat (!_.matches(eoriRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
