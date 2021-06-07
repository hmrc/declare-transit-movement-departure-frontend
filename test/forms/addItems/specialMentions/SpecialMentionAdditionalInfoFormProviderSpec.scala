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

package forms.addItems.specialMentions

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class SpecialMentionAdditionalInfoFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val requiredKey = "specialMentionAdditionalInfo.error.required"
  private val lengthKey   = "specialMentionAdditionalInfo.error.length"
  private val invalidKey  = "specialMentionAdditionalInfo.error.invalid"
  private val maxLength   = 70

  val form = new SpecialMentionAdditionalInfoFormProvider()(itemIndex, referenceIndex)

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
      lengthError = FormError(fieldName, lengthKey, Seq(itemIndex.display, referenceIndex.display))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(itemIndex.display, referenceIndex.display))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength, itemIndex.display, referenceIndex.display)
  }
}
