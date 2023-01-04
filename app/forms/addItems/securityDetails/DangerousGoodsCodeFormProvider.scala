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

package forms.addItems.securityDetails

import forms.mappings.Mappings
import models.DangerousGoodsCodeList
import models.reference.DangerousGoodsCode
import play.api.data.Form

import javax.inject.Inject

class DangerousGoodsCodeFormProvider @Inject() extends Mappings {

  def apply(dangerousGoodsCodeList: DangerousGoodsCodeList): Form[DangerousGoodsCode] =
    Form(
      "value" -> text("dangerousGoodsCode.error.required")
        .verifying("dangerousGoodsCode.error.required", value => dangerousGoodsCodeList.dangerousGoodsCodes.exists(_.code == value))
        .transform[DangerousGoodsCode](value => dangerousGoodsCodeList.getDangerousGoodsCode(value).get, _.code)
    )
}
