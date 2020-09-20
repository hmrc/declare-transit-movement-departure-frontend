/*
 * Copyright 2020 HM Revenue & Customs
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

import forms.mappings.{LocalDateTimeWithAMPM, Mappings}
import javax.inject.Inject
import play.api.data.Form

class ArrivalTimesAtOfficeFormProvider @Inject() extends Mappings {

  def apply(officeOfTransit: String): Form[LocalDateTimeWithAMPM] =
    Form(
      "value" -> localDateTime(
        invalidKey     = "arrivalTimesAtOffice.error.invalid",
        allRequiredKey = "arrivalTimesAtOffice.error.required.all",
        timeRequiredKey = "arrivalTimesAtOffice.error.required.time",
        dateRequiredKey    = "arrivalTimesAtOffice.error.required.date",
        amOrPmRequired = "arrivalTimesAtOffice.amOrPm.error.required",
        args = Seq(officeOfTransit)
      )
    )
}
