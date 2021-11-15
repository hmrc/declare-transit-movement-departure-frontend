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

package forms.generic

import forms.behaviours.OptionFieldBehaviours
import models.RepresentativeCapacity
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError

class EnumerableFormProviderSpec extends OptionFieldBehaviours {

  private val messageKeyPrefix = arbitrary[String].sample.value
  private val form             = new EnumerableFormProvider()[RepresentativeCapacity](messageKeyPrefix)

  ".value" - {

    val fieldName   = "value"
    val requiredKey = s"$messageKeyPrefix.error.required"

    behave like optionsField[RepresentativeCapacity](
      form = form,
      fieldName = fieldName,
      validValues = RepresentativeCapacity.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
