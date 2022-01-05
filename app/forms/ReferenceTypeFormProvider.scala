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

package forms

import forms.mappings.Mappings
import models.PreviousReferencesDocumentTypeList
import models.reference.PreviousReferencesDocumentType
import play.api.data.Form

import javax.inject.Inject

class ReferenceTypeFormProvider @Inject() extends Mappings {

  def apply(previousDocumentTypeList: PreviousReferencesDocumentTypeList): Form[PreviousReferencesDocumentType] =
    Form(
      "value" -> text("referenceType.error.required")
        .verifying("referenceType.error.required", value => previousDocumentTypeList.previousReferencesDocumentTypes.exists(_.code == value))
        .transform[PreviousReferencesDocumentType](value => previousDocumentTypeList.getPreviousReferencesDocumentType(value).get, _.code)
    )
}
