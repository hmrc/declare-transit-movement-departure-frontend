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

package forms.safetyAndSecurity

import forms.behaviours.StringFieldBehaviours
import models.CircumstanceIndicatorList
import models.reference.CircumstanceIndicator
import play.api.data.FormError

class CircumstanceIndicatorFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey = "circumstanceIndicator.error.required"
  private val lengthKey   = "circumstanceIndicator.error.length"
  private val maxLength   = 2
  private val circumstanceIndicatorList: CircumstanceIndicatorList = CircumstanceIndicatorList(
    Seq(
      CircumstanceIndicator("A", "Data1"),
      CircumstanceIndicator("B", "Data2")
    )
  )

  val form = new CircumstanceIndicatorFormProvider()(circumstanceIndicatorList)

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

    "not bind if CircumstanceIndicator does not exist in the CircumstanceIndicatorList" in {

      val boundForm = form.bind(Map("value" -> "X"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind Circumstance Indicator if it exist in the list" in {

      val boundForm = form.bind(Map("value" -> "A"))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
