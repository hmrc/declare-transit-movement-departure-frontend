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

package forms

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.Gen.{choose, listOfN}
import play.api.data.FormError

class RepresentativeNameFormProviderSpec extends StringFieldBehaviours {

  val requiredKey                     = "representativeName.error.required"
  val lengthKey                       = "representativeName.error.length"
  val invalidKey                      = "representativeName.error.invalid"
  val maxLength                       = 35
  val representativeNameRegex: String = "^[a-zA-Z0-9 ]*$"
  val form                            = new RepresentativeNameFormProvider()()

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

    "must not bind strings longer than max length" in {

      val expectedError = List(FormError(fieldName, lengthKey, Seq(maxLength)))

      forAll(stringsLongerThan(maxLength + 1)) {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }

    "must not bind strings that do not match regex" in {

      val expectedError = List(FormError(fieldName, invalidKey, Seq(representativeNameRegex)))
      val genInvalidString: Gen[String] = {
        stringsWithMaxLength(maxLength) suchThat (!_.matches(representativeNameRegex))
      }

      forAll(genInvalidString) {
        invalidString =>
          val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors mustBe expectedError
      }
    }
  }
}
