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

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.domain.NetMass.Constants._
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class TotalNetMassFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val form = new TotalNetMassFormProvider()(index)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthNetMass)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength   = maxLengthNetMass,
      lengthError = FormError(fieldName, lengthKeyNetMass, Seq(index.display))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKeyNetMass, Seq(index.display))
    )

    "must not bind strings with invalid characters" in {
      val invalidKey             = "totalNetMass.error.invalidCharacters"
      val expectedError          = FormError(fieldName, invalidKey, Seq(totalNetMassInvalidCharactersRegex))
      val generator: Gen[String] = RegexpGen.from(s"[a-zA-Z!£^*(<>){}_+=:;|`~,±üçñèé@]{15}")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings with invalid formatting" in {
      val invalidKey             = "totalNetMass.error.invalidFormat"
      val expectedError          = FormError(fieldName, invalidKey, Seq(totalNetMassInvalidFormatRegex))
      val generator: Gen[String] = RegexpGen.from("^([1-9]\\.[1-9][1-9][1-9][1-9])$")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings that do not match greater than zero regex" in {

      val expectedError = FormError(fieldName, invalidAmountKeyNetMass, Seq(Seq.empty))
      val invalidString = "0.000"
      val result        = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors must contain(expectedError)
    }

  }
}
