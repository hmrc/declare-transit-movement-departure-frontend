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

package forms.addItems.securityDetails

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.DangerousGoodsCodeList
import models.reference.DangerousGoodsCode
import play.api.data.FormError

class DangerousGoodsCodeFormProviderSpec extends SpecBase with StringFieldBehaviours {

  private val requiredKey         = "dangerousGoodsCode.error.required"
  private val maxLength           = 4
  private val dangerousGoodsCode1 = DangerousGoodsCode("0004", "AMMONIUM PICRATE dry or wetted with less than 10% water, by mass")
  private val dangerousGoodsCode2 = DangerousGoodsCode("0005", "CARTRIDGES FOR WEAPONS with bursting charge")
  private val dangerousGoodsCodes = DangerousGoodsCodeList(Seq(dangerousGoodsCode1, dangerousGoodsCode2))
  private val form                = new DangerousGoodsCodeFormProvider()(dangerousGoodsCodes)

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

    "not bind if dangerous goods code does not exist in the dangerous goods code list" in {

      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a dangerous goods code which is in the list" in {

      val boundForm = form.bind(Map("value" -> "0004"))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
