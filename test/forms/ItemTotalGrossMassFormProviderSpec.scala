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
import models.domain.GrossMass.Constants._
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class ItemTotalGrossMassFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val form = new ItemTotalGrossMassFormProvider()(index)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthGrossMass)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLengthGrossMass,
      lengthError = FormError(fieldName, lengthKeyGrossMass, Seq(index.display))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKeyGrossMass, Seq(index.display))
    )

    "must not bind strings with invalid characters" in {
      val invalidKey             = invalidCharactersKeyGrossMass
      val expectedError          = FormError(fieldName, invalidKey, Seq(index.display))
      val generator: Gen[String] = RegexpGen.from(s"[!£^*(){}_+=:;|`~,±üçñèé@]{15}")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings that do not match the total gross mass invalid format regex" in {

      val invalidKey    = invalidFormatKeyGrossMass
      val expectedError = FormError(fieldName, invalidKey, Seq(index.display))

      val invalidString = "0.100001"
      val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors must contain(expectedError)
    }

    "must not bind string of '0'" in {

      val invalidString = "0"
      val expectedError = List(FormError(fieldName, invalidAmountKeyGrossMass, Seq(index.display)))
      val result        = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors mustBe expectedError
    }

  }
}
