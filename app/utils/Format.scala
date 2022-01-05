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

package utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime, OffsetDateTime}

object Format {

  val dateFormatter: DateTimeFormatter               = DateTimeFormatter.ofPattern("yyyyMMdd")
  def dateFormatted(date: LocalDate): String         = date.format(dateFormatter)
  def dateFormatted(dateTime: LocalDateTime): String = dateTime.format(dateFormatter)

  val dateFormatterDDMMYYYY: DateTimeFormatter           = DateTimeFormatter.ofPattern("dd MM yyyy")
  def dateFormattedDDMMYYYY(dateTime: LocalDate): String = dateTime.format(dateFormatterDDMMYYYY)

  val dateFormatterMonthName: DateTimeFormatter                   = DateTimeFormatter.ofPattern("d MMMM yyyy")
  def dateFormattedWithMonthName(date: LocalDate): String         = date.format(dateFormatterMonthName)
  def dateFormattedWithMonthName(dateTime: LocalDateTime): String = dateTime.format(dateFormatterMonthName)

  val timeFormatter: DateTimeFormatter               = DateTimeFormatter.ofPattern("HHmm")
  def timeFormatted(time: LocalTime): String         = time.format(timeFormatter)
  def timeFormatted(dateTime: LocalDateTime): String = dateTime.format(timeFormatter)

  val timeFormatterFromAMPM: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mma")

  val dateTimeFormatter: DateTimeFormatter               = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm")
  def dateTimeFormatted(dateTime: LocalDateTime): String = dateTime.format(dateTimeFormatter)

  val dateTimeFormatterIE015: DateTimeFormatter               = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
  def dateTimeFormattedIE015(dateTime: LocalDateTime): String = dateTime.format(dateTimeFormatterIE015)

  def dateFormattedForHeader(dateTime: OffsetDateTime): String =
    dateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME)
}
