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
import models.domain.StringFieldRegex._
import models.messages.guarantee.Guarantee.Constants._
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class LiabilityAmountFormProviderSpec extends StringFieldBehaviours {

  private val form = new LiabilityAmountFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings with invalid characters" in {

      val invalidKey = "liabilityAmount.error.characters"

      val expectedError          = FormError(fieldName, invalidKey, Seq(liabilityAmountCharactersRegex))
      val generator: Gen[String] = RegexpGen.from(s"[!£^*(){}_+=:;|`~,±üçñèé@]{35}")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings with invalid formatting" in {
      val invalidKey             = "liabilityAmount.error.invalidFormat"
      val expectedError          = FormError(fieldName, invalidKey, Seq(liabilityAmountFormatRegex))
      val generator: Gen[String] = RegexpGen.from("^([1-9]\\.[1-9][1-9][1-9])$")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings that do not match greater than zero regex" in {

      val expectedError = List(FormError(fieldName, greaterThanZeroErrorKey, Seq(greaterThanZeroRegex)))
      val invalidString = "0.5"
      val result        = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors mustBe expectedError
    }

  }
}
