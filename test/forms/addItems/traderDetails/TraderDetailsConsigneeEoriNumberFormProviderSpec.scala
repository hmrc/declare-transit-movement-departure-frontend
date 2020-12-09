/*
 * Copyright 2020 HM Revenue & Customs
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

import forms.behaviours.StringFieldBehaviours
import forms.Constants._
import models.Index
import org.scalacheck.Gen
import play.api.data.FormError

class TraderDetailsConsigneeEoriNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey      = "traderDetailsConsigneeEoriNumber.error.required"
  val lengthKey        = "traderDetailsConsigneeEoriNumber.error.length"
  val invalidCharsKey  = "traderDetailsConsigneeEoriNumber.error.invalid"
  val invalidFormatKey = "traderDetailsConsigneeEoriNumber.error.invalidFormat"
  val index            = Index(0)

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

    "must not bind strings that do not match the EORI number regex" ignore { //Need to address this random failure case

      val expectedError =
        List(FormError(fieldName, invalidCharsKey, Seq(index.display)))

      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLengthEoriNumber) suchThat (!_.matches(validEoriCharactersRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match the EORI number format regex" ignore { //Need to address this random failure case

      val expectedError =
        List(FormError(fieldName, invalidFormatKey, Seq(eoriNumberRegex)))

      val genInvalidString: Gen[String] = {
        alphaNumericWithMaxLength(maxLengthEoriNumber).map(_.toUpperCase) suchThat (!_.matches(eoriNumberRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
