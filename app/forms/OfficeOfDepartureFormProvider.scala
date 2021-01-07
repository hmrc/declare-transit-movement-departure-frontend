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
import javax.inject.Inject
import models.CustomsOfficeList
import models.reference.CustomsOffice
import play.api.data.Form

class OfficeOfDepartureFormProvider @Inject() extends Mappings {

  def apply(customsOffices: CustomsOfficeList): Form[CustomsOffice] =
    Form(
      "value" -> text("officeOfDeparture.error.required")
        .verifying("officeOfDeparture.error.required", value => customsOffices.customsOffices.exists(_.id == value))
        .transform[CustomsOffice](value => customsOffices.getCustomsOffice(value).get, _.id)
    )
}
