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

import forms.behaviours.BooleanFieldBehaviours
import models.Index
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError

class YesNoFormProviderSpec extends BooleanFieldBehaviours {

  private val messageKeyPrefix = arbitrary[String].sample.value
  private val requiredKey      = s"$messageKeyPrefix.error.required"
  private val invalidKey       = "error.boolean"

  ".value" - {

    val fieldName = "value"

    "when has an index" - {

      val index = Index(0)
      val form  = new YesNoFormProvider()(messageKeyPrefix, index)

      behave like booleanField(
        form = form,
        fieldName = fieldName,
        invalidError = FormError(fieldName, invalidKey, Seq(index.display))
      )

      behave like mandatoryField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(index.display))
      )
    }

    "when doesn't have an index" - {

      val form = new YesNoFormProvider()(messageKeyPrefix)

      behave like booleanField(
        form = form,
        fieldName = fieldName,
        invalidError = FormError(fieldName, invalidKey)
      )

      behave like mandatoryField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey)
      )
    }
  }
}
