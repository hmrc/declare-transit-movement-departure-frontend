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

package forms.addItems

import forms.behaviours.StringFieldBehaviours
import models.PackageTypeList
import models.reference.PackageType
import play.api.data.FormError

class PackageTypeFormProviderSpec extends StringFieldBehaviours {

  private val packageTypeList: PackageTypeList = PackageTypeList(
    Seq(
      PackageType("AB", "Description 1")
    )
  )
  private val form = new PackageTypeFormProvider()(packageTypeList)

  ".value" - {

    val fieldName   = "value"
    val requiredKey = "packageType.error.required"
    val maxLength   = 2

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

    "not bind if PackageType does not exist in the package type list" in {

      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a PackageType which is in the list" in {

      val boundForm = form.bind(Map("value" -> "AB"))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }

}
