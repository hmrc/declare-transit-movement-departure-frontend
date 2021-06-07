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

import forms.Constants._
import forms.behaviours.StringFieldBehaviours
import models.Index
import models.domain.StringFieldRegex.eoriNumberRegex
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class TraderDetailsConsignorEoriNumberFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey      = "traderDetailsConsignorEoriNumber.error.required"
  private val lengthKey        = "traderDetailsConsignorEoriNumber.error.length"
  private val invalidKey       = "traderDetailsConsignorEoriNumber.error.invalid"
  private val invalidFormatKey = "traderDetailsConsignorEoriNumber.error.invalidFormat"
  private val index            = Index(0)
  private val form             = new TraderDetailsConsignorEoriNumberFormProvider()(index)

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
      maxLength = maxLengthEoriNumber,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthEoriNumber))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(index.display))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLengthEoriNumber)

    "must not bind strings that do not match the eori number format regex" in {

      val expectedError          = FormError(fieldName, invalidFormatKey, Seq(eoriNumberRegex))
      val generator: Gen[String] = RegexpGen.from(s"^[0-9]{2}[a-zA-Z0-9]{15}")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
