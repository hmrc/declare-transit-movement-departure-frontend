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

package utils

import controllers.routeDetails.routes
import models.reference.{CountryCode, CountryOfDispatch, CustomsOffice}
import models.{CheckMode, CountryList, CustomsOfficeList, Index, LocalReferenceNumber, Mode, UserAnswers}
import pages.QuestionPage
import pages.routeDetails._
import play.api.libs.json.Reads
import play.api.mvc.Call
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

class RouteDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  private def lrn: LocalReferenceNumber = userAnswers.lrn

  def arrivalTimesAtOffice(index: Index): Option[Row] = userAnswers.get(ArrivalTimesAtOfficePage(index)) map {
    answer =>
      val dateTime: String = s"${Format.dateTimeFormattedAMPM(answer).toLowerCase}"
      Row(
        key = Key(msg"arrivalTimesAtOffice.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(Literal(dateTime)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.ArrivalTimesAtOfficeController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"arrivalTimesAtOffice.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-arrival-times-at-office-of-transit")
          )
        )
      )
  }

  def destinationOffice(customsOfficeList: CustomsOfficeList): Option[Row] = officeRow[CustomsOffice](
    page = DestinationOfficePage,
    f = x => x.id,
    customsOfficeList = customsOfficeList,
    prefix = "destinationOffice",
    id = "change-destination-office",
    call = routes.DestinationOfficeController.onPageLoad(lrn, CheckMode)
  )

  def addAnotherTransitOffice(index: Index, customsOfficeList: CustomsOfficeList): Option[Row] = officeRow[String](
    page = AddAnotherTransitOfficePage(index),
    f = x => x,
    customsOfficeList = customsOfficeList,
    prefix = "addAnotherTransitOffice",
    id = "change-office-of-transit",
    call = routes.OfficeOfTransitCountryController.onPageLoad(lrn = lrn, index = index, mode = CheckMode),
    args = index.display
  )

  def countryOfDispatch(countryList: CountryList): Option[Row] = countryRow[CountryOfDispatch](
    page = CountryOfDispatchPage,
    f = x => x.country,
    countryList = countryList,
    prefix = "countryOfDispatch",
    id = "change-country-of-dispatch",
    call = routes.CountryOfDispatchController.onPageLoad
  )

  def destinationCountry(countryList: CountryList): Option[Row] = countryRow[CountryCode](
    page = DestinationCountryPage,
    f = x => x,
    countryList = countryList,
    prefix = "destinationCountry",
    id = "change-destination-country",
    call = routes.DestinationCountryController.onPageLoad
  )

  def officeOfTransitRow(index: Index, customsOfficeList: CustomsOfficeList, mode: Mode): Option[Row] =
    userAnswers.get(AddAnotherTransitOfficePage(index)).flatMap {
      answer =>
        customsOfficeList.getCustomsOffice(answer).map {
          office =>
            val arrivalTime =
              userAnswers
                .get(ArrivalTimesAtOfficePage(index))
                .map(
                  time => s"${Format.dateTimeFormattedAMPM(time).toLowerCase}"
                )
                .getOrElse("")

            val key = s"${office.name} (${office.id})"

            Row(
              key = Key(lit"$key"),
              value = Value(lit"$arrivalTime"),
              actions = List(
                Action(
                  content = msg"site.change",
                  href = routes.OfficeOfTransitCountryController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                  attributes = Map("id" -> s"change-office-of-transit-${index.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(key)),
                  attributes = Map("id" -> s"remove-office-of-transit-${index.display}")
                )
              )
            )
        }
    }

  def movementDestinationCountry(countryList: CountryList): Option[Row] = countryRow[CountryCode](
    page = MovementDestinationCountryPage,
    f = x => x,
    countryList = countryList,
    prefix = "movementDestinationCountry",
    id = "change-movement-destination-country",
    call = routes.MovementDestinationCountryController.onPageLoad
  )

  private def countryRow[T](
    page: QuestionPage[T],
    f: T => CountryCode,
    countryList: CountryList,
    prefix: String,
    id: String,
    call: (LocalReferenceNumber, Mode) => Call
  )(implicit rds: Reads[T]): Option[Row] = userAnswers.get(page) map {
    answer =>
      val countryName = countryList.getCountry(f(answer)).map(_.description).getOrElse(f(answer).code)
      row(prefix, countryName, id, call(lrn, CheckMode))
  }

  private def officeRow[T](
    page: QuestionPage[T],
    f: T => String,
    customsOfficeList: CustomsOfficeList,
    prefix: String,
    id: String,
    call: Call,
    args: Any*
  )(implicit rds: Reads[T]): Option[Row] = userAnswers.get(page) flatMap {
    answer =>
      customsOfficeList.getCustomsOffice(f(answer)) map {
        customsOffice =>
          row(prefix, s"${customsOffice.name} (${customsOffice.id})", id, call, args: _*)
      }
  }

  private def row(prefix: String, value: String, id: String, call: Call, args: Any*): Row =
    Row(
      key = Key(msg"$prefix.checkYourAnswersLabel".withArgs(args: _*), classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content = msg"site.edit",
          href = call.url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"$prefix.checkYourAnswersLabel")),
          attributes = Map("id" -> id)
        )
      )
    )

}
