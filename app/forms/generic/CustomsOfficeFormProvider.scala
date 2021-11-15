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

import forms.mappings.Mappings
import models.CustomsOfficeList
import models.reference.CustomsOffice
import play.api.data.Form

import javax.inject.Inject

class CustomsOfficeFormProvider @Inject() extends Mappings {

  def apply(messageKeyPrefix: String, customsOffices: CustomsOfficeList): Form[CustomsOffice] =
    apply(messageKeyPrefix, customsOffices, Nil)

  def apply(messageKeyPrefix: String, customsOffices: CustomsOfficeList, countryName: String): Form[CustomsOffice] =
    apply(messageKeyPrefix, customsOffices, Seq(countryName))

  private def apply(messageKeyPrefix: String, customsOffices: CustomsOfficeList, args: Seq[Any]): Form[CustomsOffice] =
    Form(
      "value" -> text(s"$messageKeyPrefix.error.required", args)
        .verifying(s"$messageKeyPrefix.error.required", value => customsOffices.getAll.exists(_.id == value))
        .transform[CustomsOffice](value => customsOffices.getCustomsOffice(value).get, _.id)
    )
}
