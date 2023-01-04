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

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.DocumentTypeList
import models.reference.DocumentType
import play.api.data.FormError

class DocumentTypeFormProviderSpec extends SpecBase with StringFieldBehaviours {

  private val requiredKey = "documentType.error.required"

  private val documentList = DocumentTypeList(
    Seq(
      DocumentType("955", "ATA carnet", true),
      DocumentType("740", "Air waybill", true)
    )
  )

  private val form = new DocumentTypeFormProvider()(documentList)

  ".value" - {

    val fieldName = "value"

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind if previous document type that does not exist in the list" in {

      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a document type code which is in the list" in {

      val boundForm = form.bind(Map("value" -> "955"))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
