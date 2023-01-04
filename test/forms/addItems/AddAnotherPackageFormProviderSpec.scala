/*
 * Copyright 2023 HM Revenue & Customs
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

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class AddAnotherPackageFormProviderSpec extends BooleanFieldBehaviours {

  private val requiredKey = "addAnotherPackage.error.required"
  private val invalidKey  = "error.boolean"
  private val form        = new AddAnotherPackageFormProvider()

  ".value" - {

    val fieldName = "value"
    "when max limit not hit" - {

      behave like booleanField(
        form(true),
        fieldName,
        invalidError = FormError(fieldName, invalidKey)
      )

      behave like mandatoryField(
        form(true),
        fieldName,
        requiredError = FormError(fieldName, requiredKey)
      )
    }

    "when max limit hit" - {
      "must bind false" in {
        val result = form(false).bind(Map(fieldName -> "true"))
        result.value.value mustBe false
      }

      "must bind true to false" in {
        val result = form(false).bind(Map(fieldName -> "false"))
        result.value.value mustBe false
      }

      "must bind blank to false" in {
        val result = form(false).bind(Map.empty[String, String])
        result.value.value mustBe false
      }
    }
  }

}
