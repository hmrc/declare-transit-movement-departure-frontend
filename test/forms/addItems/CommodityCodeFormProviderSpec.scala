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

package forms.addItems

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.domain.StringFieldRegex.commodityCodeCharactersRegex
import org.scalacheck.Gen
import play.api.data.FormError

class CommodityCodeFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val requiredKey                      = "commodityCode.error.required"
  private val lengthKey                        = "commodityCode.error.length"
  private val maxLength                        = 10
  private val commodityCodeInvalidCharacterKey = "commodityCode.errors.invalidCharacters"
  private val commodityCodeInvalidFormatKey    = "commodityCode.errors.invalidFormat"
  private val form                             = new CommodityCodeFormProvider()(index)

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
      requiredError = FormError(fieldName, requiredKey, Seq(index.display))
    )

    "must not bind strings that do not match the commodity code invalid characters regex" in {

      val expectedError =
        List(FormError(fieldName, commodityCodeInvalidCharacterKey, Seq(index.display)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(commodityCodeCharactersRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match the commodity code invalid format regex" in {

      val expectedError =
        List(FormError(fieldName, commodityCodeInvalidFormatKey, Seq(index.display)))

      val result = form.bind(Map(fieldName -> "12345")).apply(fieldName)
      result.errors mustBe expectedError

    }
  }

}
