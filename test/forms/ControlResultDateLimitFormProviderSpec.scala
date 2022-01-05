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

import forms.behaviours.DateBehaviours
import play.api.data.FormError

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ControlResultDateLimitFormProviderSpec extends DateBehaviours {

  private val form                             = new ControlResultDateLimitFormProvider()()
  private def dateIn15Days: LocalDate          = LocalDate.now.plusDays(15)
  private def dateIn14Days: LocalDate          = LocalDate.now.plusDays(14)
  private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  ".value" - {

    val validData = datesBetween(
      min = LocalDate.now,
      max = LocalDate.now.plusDays(14)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "controlResultDateLimit.error.required.all")

    behave like dateFieldWithMax(form,
                                 "value",
                                 max = dateIn15Days,
                                 FormError("value", "controlResultDateLimit.error.max.date", Seq(dateFormatter.format(dateIn14Days)))
    )

    behave like dateFieldWithMin(form, "value", min = LocalDate.now.minusDays(1), FormError("value", "controlResultDateLimit.error.min.date"))

  }
}
