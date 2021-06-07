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

package forms.mappings

import java.time.{LocalDate, LocalDateTime}

import models.{Enumerable, LocalReferenceNumber}
import play.api.data.FieldMapping
import play.api.data.Forms.of
import play.api.data.format.Formats.ignoredFormat

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required", args: Seq[Any] = Seq.empty): FieldMapping[String] =
    of(stringFormatter(errorKey, args))

  protected def mandatoryIfBoolean(condition: Boolean, requiredKey: String = "error.required"): FieldMapping[Boolean] =
    if (condition) boolean(requiredKey) else of(ignoredFormat(true))

  protected def int(requiredKey: String = "error.required",
                    wholeNumberKey: String = "error.wholeNumber",
                    nonNumericKey: String = "error.nonNumeric",
                    args: Seq[String] = Seq.empty[String]
  ): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def boolean(requiredKey: String = "error.required", invalidKey: String = "error.boolean", args: Seq[Any] = Seq.empty): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey, args))

  protected def enumerable[A](requiredKey: String = "error.required", invalidKey: String = "error.invalid")(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey))

  protected def localDate(invalidKey: String,
                          allRequiredKey: String,
                          twoRequiredKey: String,
                          requiredKey: String,
                          args: Seq[String] = Seq.empty
  ): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, args))

  //noinspection ScalaStyle
  protected def localDateTime(invalidDateKey: String,
                              invalidTimeKey: String,
                              invalidHourKey: String,
                              allRequiredKey: String,
                              timeRequiredKey: String,
                              dateRequiredKey: String,
                              amOrPmRequired: String,
                              pastDateErrorKey: String,
                              futureDateErrorKey: String,
                              args: Seq[String] = Seq.empty
  ): FieldMapping[LocalDateTime] =
    of(
      new LocalDateTimeFormatter(invalidDateKey,
                                 invalidTimeKey,
                                 invalidHourKey,
                                 allRequiredKey,
                                 timeRequiredKey,
                                 dateRequiredKey,
                                 amOrPmRequired,
                                 pastDateErrorKey,
                                 futureDateErrorKey,
                                 args
      )
    )

  protected def lrn(requiredKey: String, lengthKey: String, invalidKey: String): FieldMapping[LocalReferenceNumber] =
    of(lrnFormatter(requiredKey, lengthKey, invalidKey))
}
