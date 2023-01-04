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

package forms

import forms.mappings.Mappings
import play.api.data.Form
import utils.Format

import java.time.LocalDate
import javax.inject.Inject

class ArrivalDatesAtOfficeFormProvider @Inject() extends Mappings {

  private val localDate  = LocalDate.now()
  private val pastDate   = localDate.minusDays(1)
  private val futureDate = localDate.plusWeeks(2)

  def apply(officeOfTransit: String): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "arrivalDatesAtOffice.error.invalid.date",
        allRequiredKey = "arrivalDatesAtOffice.error.required.date",
        twoRequiredKey = "arrivalDatesAtOffice.error.required.two",
        requiredKey = "arrivalDatesAtOffice.error.required.date",
        args = Seq(officeOfTransit)
      ).verifying(
        maxDate(futureDate, "arrivalDatesAtOffice.error.future.date", officeOfTransit, Format.dateFormatterDDMMYYYY.format(futureDate.plusDays(1))),
        minDate(pastDate, "arrivalDatesAtOffice.error.past.date", officeOfTransit, Format.dateFormatterDDMMYYYY.format(pastDate))
      )
    )
}
