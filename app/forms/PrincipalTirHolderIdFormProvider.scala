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

import forms.mappings.Mappings
import models.domain.StringFieldRegex.{principalTirHolderIdCharRegex, principalTirHolderIdFormatRegex, stringFieldRegex}
import play.api.data.Form
import uk.gov.hmrc.play.mappers.StopOnFirstFail

import javax.inject.Inject

class PrincipalTirHolderIdFormProvider @Inject() extends Mappings {

  val maxLengthPrincipalTirHolderId = 35

  def apply(): Form[String] =
    Form(
      "value" -> text("principalTirHolderId.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLengthPrincipalTirHolderId, "principalTirHolderId.error.length"),
            regexp(principalTirHolderIdCharRegex, "principalTirHolderId.error.characters"),
            regexp(principalTirHolderIdFormatRegex, "principalTirHolderId.error.format")
          )
        )
    )
}
