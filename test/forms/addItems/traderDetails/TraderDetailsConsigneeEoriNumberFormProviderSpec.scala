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
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class TraderDetailsConsigneeEoriNumberFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey      = "traderDetailsConsigneeEoriNumber.error.required"
  private val lengthKey        = "traderDetailsConsigneeEoriNumber.error.length"
  private val invalidKey       = "traderDetailsConsigneeEoriNumber.error.invalid"
  private val invalidFormatKey = "traderDetailsConsigneeEoriNumber.error.invalidFormat"
  private val index            = Index(0)

  val form = new TraderDetailsConsigneeEoriNumberFormProvider()(index)

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
      requiredError = FormError(fieldName, requiredKey, Seq(index.display))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLengthEoriNumber)

    "must not bind strings that do not match the EORI number format regex" in {

      val expectedError =
        List(FormError(fieldName, invalidFormatKey, Seq(eoriNumberRegex)))

      val generator: Gen[String] = RegexpGen.from(s"^[0-9]{2}[a-zA-Z0-9]{15}")
      forAll(generator) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
