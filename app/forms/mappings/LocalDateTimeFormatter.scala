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

package forms.mappings

import java.time.{LocalDate, LocalDateTime}

import models.LocalDateTimeWithAMPM
import play.api.data.FormError
import play.api.data.format.Formatter
import utils.Format

import scala.util.{Failure, Success, Try}

private[mappings] class LocalDateTimeFormatter(
  invalidDateKey: String,
  invalidTimeKey: String,
  invalidHourKey: String,
  allRequiredKey: String,
  timeRequiredKey: String,
  dateRequiredKey: String,
  amOrPmRequired: String,
  pastDateErrorKey: String,
  futureDateErrorKey: String,
  args: Seq[String] = Seq.empty
) extends Formatter[LocalDateTimeWithAMPM]
    with Formatters {

  private val fieldAmOrPmKeys = List("amOrPm")
  private val fieldTimeKeys   = List("hour", "minute")
  private val fieldDateKeys   = List("day", "month", "year")

  private def toDateTime(key: String,
                         day: Int,
                         month: Int,
                         year: Int,
                         hour: Int,
                         minute: Int,
                         amOrPm: String): Either[Seq[FormError], LocalDateTimeWithAMPM] = {
    val currentDate                     = LocalDate.now()
    val pastDateTimeArgs: Seq[String]   = args :+ s"${Format.dateFormattedWithMonthName(currentDate.minusDays(1))}"
    val futureDateTimeArgs: Seq[String] = args :+ s"${Format.dateFormattedWithMonthName(currentDate.plusWeeks(2))}"

    Try(LocalDateTime.of(year, month, day, hour, minute)) match {
      case Success(dateTime) =>
        if (dateTime.toLocalDate.isBefore(currentDate)) {
          Left(Seq(FormError(key, pastDateErrorKey, pastDateTimeArgs)))
        } else if (dateTime.toLocalDate.isAfter(currentDate.plusWeeks(2))) {
          Left(Seq(FormError(key, futureDateErrorKey, futureDateTimeArgs)))
        } else if (hour > 12) {
          Left(Seq(FormError(key, invalidHourKey, args)))
        } else if (hour == 0) {
          Left(Seq(FormError(key, invalidTimeKey, args)))
        } else {
          Right(LocalDateTimeWithAMPM(dateTime, amOrPm))
        }
      case Failure(_) =>
        Left(Seq(FormError(key, invalidDateKey, args)))
    }
  }

  private def formatDateTime(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDateTimeWithAMPM] = {

    val int = intFormatter(
      requiredKey    = invalidDateKey,
      wholeNumberKey = invalidDateKey,
      nonNumericKey  = invalidDateKey,
      args
    )
    val string = stringFormatter(
      errorKey = amOrPmRequired,
      args
    )

    for {
      day      <- int.bind(s"$key.day", data).right
      month    <- int.bind(s"$key.month", data).right
      year     <- int.bind(s"$key.year", data).right
      hour     <- int.bind(s"$key.hour", data).right
      minute   <- int.bind(s"$key.minute", data).right
      amOrPm   <- string.bind(s"$key.amOrPm", data).right
      dateTime <- toDateTime(key, day, month, year, hour, minute, amOrPm).right
    } yield dateTime
  }

  //noinspection ScalaStyle
  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDateTimeWithAMPM] = {

    val missingTimeFields = getMissingFields(fieldTimeKeys, key, data)
    val missingDateFields = getMissingFields(fieldDateKeys, key, data)
    val missingAMPMFields = getMissingFields(fieldAmOrPmKeys, key, data)

    (missingDateFields.nonEmpty, missingTimeFields.nonEmpty, missingAMPMFields.nonEmpty) match {
      case (_, _, true) =>
        Left(List(FormError(key, amOrPmRequired, args)))
      case (false, false, _) =>
        formatDateTime(key, data).left.map {
          _.map(_.copy(key = key))
        }
      case (true, _, _) =>
        Left(List(FormError(key, dateRequiredKey, args)))
      case (false, _, _) =>
        Left(List(FormError(key, timeRequiredKey, args)))
      case _ =>
        Left(List(FormError(key, allRequiredKey, args)))
    }
  }

  private def getMissingFields(fields: Seq[String], key: String, data: Map[String, String]): Seq[String] = {
    val fields1: Map[String, Option[String]] = fields.map {
      field =>
        field -> data.get(s"$key.$field").filter(_.nonEmpty)
    }.toMap

    lazy val missingTimeFields = fields1
      .withFilter(_._2.isEmpty)
      .map(_._1)
      .toList
    missingTimeFields
  }

  override def unbind(key: String, value: LocalDateTimeWithAMPM): Map[String, String] =
    Map(
      s"$key.day"    -> value.dateTime.getDayOfMonth.toString,
      s"$key.month"  -> value.dateTime.getMonthValue.toString,
      s"$key.year"   -> value.dateTime.getYear.toString,
      s"$key.hour"   -> value.dateTime.getHour.toString,
      s"$key.minute" -> value.dateTime.getMinute.toString,
      s"$key.amOrPm" -> value.amOrPm
    )
}
