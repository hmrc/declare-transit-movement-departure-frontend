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
import org.scalacheck.Gen
import org.scalatest.MustMatchers
import play.api.data.{Field, FormError}

class LocalReferenceNumberFormProviderSpec extends StringFieldBehaviours with MustMatchers {

  val requiredKey = "localReferenceNumber.error.required"
  val lengthKey = "localReferenceNumber.error.length"
  val maxLength = 22
  val fieldName = "value"

  val form = new LocalReferenceNumberFormProvider()()

  ".value" - {

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
  }

  "must not bind strings that do not match regex" in {

    val invalidKey = "localReferenceNumber.error.invalidCharacters"
    val validRegex    = "[a-zA-Z0-9-_]+"
    val expectedError = FormError(fieldName, invalidKey, Seq(validRegex))

    val genInvalidString: Gen[String] = {
      stringsWithMaxLength(maxLength) suchThat (!_.matches("[a-zA-Z0-9-_]+"))
    }

    forAll(genInvalidString) {
      invalidString =>
        val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors must contain(expectedError)
    }
  }

  "must not bind strings longer than 22 characters" in {

    val expectedError = FormError(fieldName, lengthKey, Seq(maxLength))
    forAll(stringsLongerThan(maxLength + 1)) {
      string =>
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors must contain(expectedError)
    }
  }
}
