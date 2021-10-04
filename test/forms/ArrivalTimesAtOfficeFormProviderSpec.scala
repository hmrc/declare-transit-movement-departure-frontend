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

import forms.behaviours.DateBehaviours
import play.api.data.FormError
import utils.Format

import java.time.LocalDate

class ArrivalTimesAtOfficeFormProviderSpec extends DateBehaviours {

  private val officeOfTransit             = "office"
  private val form                        = new ArrivalTimesAtOfficeFormProvider()(officeOfTransit)
  private val localDate                   = LocalDate.now()
  private val pastDate                    = localDate.minusDays(1)
  private val futureDate                  = localDate.plusWeeks(2)
  private val formattedPastDate: String   = s"${Format.dateFormatterDDMMYYYY(pastDate)}"
  private val formattedFutureDate: String = s"${Format.dateFormatterDDMMYYYY(futureDate)}"
  private val formPastError               = FormError("value", "arrivalTimesAtOffice.error.past.date", Seq(officeOfTransit, formattedPastDate))
  private val formFutureError             = FormError("value", "arrivalTimesAtOffice.error.future.date", Seq(officeOfTransit, formattedFutureDate))

  ".value" - {

    behave like dateField(form, "value", localDate)

    behave like dateFieldWithMin(form, "value", localDate, formPastError)

    behave like dateFieldWithMax(form, "value", localDate, formFutureError)

    behave like mandatoryDateField(form, "value", "arrivalTimesAtOffice.error.required.date", Seq(officeOfTransit))

  }
}
