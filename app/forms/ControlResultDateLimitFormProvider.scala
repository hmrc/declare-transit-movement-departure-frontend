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

package forms

import forms.mappings.Mappings
import play.api.data.Form

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ControlResultDateLimitFormProvider @Inject() extends Mappings {

  def dateIn14Days: LocalDate          = LocalDate.now.plusDays(14)
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "controlResultDateLimit.error.invalid",
        allRequiredKey = "controlResultDateLimit.error.required.all",
        twoRequiredKey = "controlResultDateLimit.error.required.two",
        requiredKey = "controlResultDateLimit.error.required"
      ).verifying(
        maxDate(dateIn14Days, "controlResultDateLimit.error.max.date", dateFormatter.format(dateIn14Days)),
        minDate(LocalDate.now, "controlResultDateLimit.error.min.date")
      )
    )
}
