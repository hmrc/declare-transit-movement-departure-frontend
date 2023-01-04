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

package forms

import forms.mappings.Mappings
import models.Index
import models.domain.SealDomain
import models.domain.StringFieldRegex.stringFieldRegex
import play.api.data.Form
import javax.inject.Inject

class SealIdDetailsFormProvider @Inject() extends Mappings {

  val maxSealsNumberLength = 20

  def apply(index: Index, seals: Seq[SealDomain] = Seq.empty[SealDomain]): Form[String] =
    Form(
      "value" -> trimmedText("sealIdDetails.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxSealsNumberLength, "sealIdDetails.error.length"),
            regexp(stringFieldRegex, "sealIdDetails.error.invalidCharacters"),
            doesNotExistIn(seals, index, "sealIdentity.error.duplicate")
          )
        )
    )
}
