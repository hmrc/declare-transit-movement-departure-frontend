/*
 * Copyright 2022 HM Revenue & Customs
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

import forms.mappings.Mappings
import models.Index
import models.domain.StringFieldRegex.alphaNumericWithSpaceRegex
import play.api.data.Form
import javax.inject.Inject

class SpecialMentionAdditionalInfoFormProvider @Inject() extends Mappings {

  def apply(itemIndex: Index, referenceIndex: Index): Form[String] =
    Form(
      "value" -> trimmedText("specialMentionAdditionalInfo.error.required", Seq(itemIndex.display, referenceIndex.display))
        .verifying(
          forms.StopOnFirstFail[String](
            maxLength(70, "specialMentionAdditionalInfo.error.length", itemIndex.display, referenceIndex.display),
            regexp(alphaNumericWithSpaceRegex, "specialMentionAdditionalInfo.error.invalid", Seq(itemIndex.display, referenceIndex.display))
          )
        )
    )
}
