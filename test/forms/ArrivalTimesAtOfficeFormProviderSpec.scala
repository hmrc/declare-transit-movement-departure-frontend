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

import forms.behaviours.DateTimeWithAMPMBehaviours
import play.api.data.FormError
import utils.Format

class ArrivalTimesAtOfficeFormProviderSpec extends DateTimeWithAMPMBehaviours {

  private val officeOfTransit                 = "office"
  private val form                            = new ArrivalTimesAtOfficeFormProvider()(officeOfTransit)
  private val localDateTime                   = dateTime.withHour(1)
  private val pastDateTime                    = localDateTime.minusDays(1)
  private val futureDateTime                  = localDateTime.plusWeeks(2)
  private val formattedPastDateTime: String   = s"${Format.dateFormattedWithMonthName(pastDateTime)}"
  private val formattedFutureDateTime: String = s"${Format.dateFormattedWithMonthName(futureDateTime)}"
  private val formPastError                   = FormError("value", "arrivalTimesAtOffice.error.past.date", Seq(officeOfTransit, formattedPastDateTime))
  private val formFutureError                 = FormError("value", "arrivalTimesAtOffice.error.future.date", Seq(officeOfTransit, formattedFutureDateTime))

  ".value" - {

    behave like dateTimeField(form, "value", localDateTime)

    behave like dateTimeFieldWithMin(form, "value", localDateTime, formPastError)

    behave like dateFieldWithMax(form, "value", futureDateTime, formFutureError)

    behave like invalidDateField(form, "value", "arrivalTimesAtOffice.error.invalid.date", Seq(officeOfTransit))

    behave like invalidHourField(form, "value", "arrivalTimesAtOffice.error.invalid.hour", Seq(officeOfTransit))

    behave like invalidTimeField(form, "value", "arrivalTimesAtOffice.error.invalid.time", Seq(officeOfTransit))

    behave like mandatoryDateField(form, "value", "arrivalTimesAtOffice.error.required.date", Seq(officeOfTransit))

    behave like mandatoryTimeField(form, "value", "arrivalTimesAtOffice.error.required.time", Seq(officeOfTransit))

    behave like mandatoryAMPMField(form, "value", "arrivalTimesAtOffice.amOrPm.error.required", Seq(officeOfTransit))

  }
}
