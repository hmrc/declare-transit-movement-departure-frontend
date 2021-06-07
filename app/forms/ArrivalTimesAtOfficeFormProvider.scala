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

import java.time.LocalDateTime

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class ArrivalTimesAtOfficeFormProvider @Inject() extends Mappings {

  def apply(officeOfTransit: String): Form[LocalDateTime] =
    Form(
      "value" -> localDateTime(
        invalidDateKey = "arrivalTimesAtOffice.error.invalid.date",
        invalidTimeKey = "arrivalTimesAtOffice.error.invalid.time",
        invalidHourKey = "arrivalTimesAtOffice.error.invalid.hour",
        allRequiredKey = "arrivalTimesAtOffice.error.required.all",
        timeRequiredKey = "arrivalTimesAtOffice.error.required.time",
        dateRequiredKey = "arrivalTimesAtOffice.error.required.date",
        amOrPmRequired = "arrivalTimesAtOffice.amOrPm.error.required",
        pastDateErrorKey = "arrivalTimesAtOffice.error.past.date",
        futureDateErrorKey = "arrivalTimesAtOffice.error.future.date",
        args = Seq(officeOfTransit)
      )
    )
}
