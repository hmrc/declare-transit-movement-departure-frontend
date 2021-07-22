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

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AddAnotherGuaranteeFormProviderSpec extends BooleanFieldBehaviours with FormSpec {

  private val requiredKey    = "addAnotherGuarantee.error.required"
  private val tirRequiredKey = "addAnotherGuarantee.tir.error.required"
  private val invalidKey     = "error.boolean"
  private val form           = new AddAnotherGuaranteeFormProvider()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form(allowMoreGuarantees = true, isTir = false),
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form(allowMoreGuarantees = true, isTir = false),
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind when key is not present at all for a TIR declaration" in {

      val result = form(allowMoreGuarantees = true, isTir = true).bind(emptyForm).apply(fieldName)

      val expectedError = List(FormError(fieldName, tirRequiredKey))

      result.errors mustEqual expectedError
    }

    "must not bind blank values for a TIR declaration" in {

      val result = form(allowMoreGuarantees = true, isTir = true).bind(Map(fieldName -> "")).apply(fieldName)

      val expectedError = List(FormError(fieldName, tirRequiredKey))

      result.errors mustEqual expectedError
    }
  }
}
