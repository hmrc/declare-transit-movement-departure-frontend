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

import controllers.routeDetails.routes
import models.reference.{CountryCode, CountryOfDispatch, CustomsOffice}
import models.{CountryList, CustomsOfficeList, Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.routeDetails._
import play.api.libs.json.Reads
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

import java.time.LocalDateTime

class RouteDetailsCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode) extends CheckYourAnswersHelper(userAnswers) {

  def arrivalDatesAtOffice(index: Index): Option[Row] = getAnswerAndBuildRow[LocalDateTime](
    page = ArrivalDatesAtOfficePage(index),
    formatAnswer = dateTime => formatAsLiteral(formatAsDate(dateTime)),
    prefix = "arrivalDatesAtOffice",
    id = Some(s"change-arrival-dates-at-office-of-transit-${index.display}"),
    call = routes.ArrivalDatesAtOfficeController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def destinationOffice(customsOfficeList: CustomsOfficeList): Option[Row] = getAnswerAndBuildOfficeRow[CustomsOffice](
    page = DestinationOfficePage,
    formatAnswer = customsOffice => customsOffice.id,
    customsOfficeList = customsOfficeList,
    buildRow = answer =>
      buildRow(
        prefix = "destinationOffice",
        answer = answer,
        id = Some("change-destination-office"),
        call = routes.DestinationOfficeController.onPageLoad(lrn, mode)
      )
  )

  def addAnotherTransitOffice(index: Index, customsOfficeList: CustomsOfficeList): Option[Row] = getAnswerAndBuildOfficeRow[String](
    page = AddAnotherTransitOfficePage(index),
    formatAnswer = formatAsSelf,
    customsOfficeList = customsOfficeList,
    buildRow = answer =>
      buildRow(
        prefix = "addAnotherTransitOffice",
        answer = answer,
        id = Some(s"change-office-of-transit-${index.display}"),
        call = routes.OfficeOfTransitCountryController.onPageLoad(lrn, index, mode),
        args = index.display
      )
  )

  def countryOfDispatch(countryList: CountryList): Option[Row] = getAnswerAndBuildSimpleCountryRow[CountryOfDispatch](
    page = CountryOfDispatchPage,
    getCountryCode = countryCode => countryCode.country,
    countryList = countryList,
    prefix = "countryOfDispatch",
    id = Some("change-country-of-dispatch"),
    call = routes.CountryOfDispatchController.onPageLoad(lrn, mode)
  )

  def destinationCountry(countryList: CountryList): Option[Row] = getAnswerAndBuildSimpleCountryRow[CountryCode](
    page = DestinationCountryPage,
    getCountryCode = countryCode => countryCode,
    countryList = countryList,
    prefix = "destinationCountry",
    id = Some("change-destination-country"),
    call = routes.DestinationCountryController.onPageLoad(lrn, mode)
  )

  def officeOfTransitRow(index: Index, customsOfficeList: CustomsOfficeList): Option[Row] =
    getAnswerAndBuildOfficeRow[String](
      page = AddAnotherTransitOfficePage(index),
      formatAnswer = formatAsSelf,
      customsOfficeList = customsOfficeList,
      buildRow = label =>
        buildRemovableRow(
          label = label,
          value = userAnswers
            .get(ArrivalDatesAtOfficePage(index))
            .fold("")(formatAsDate),
          id = s"office-of-transit-${index.display}",
          changeCall = routes.OfficeOfTransitCountryController.onPageLoad(lrn, index, mode),
          removeCall = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, index, mode)
        )
    )

  def movementDestinationCountry(countryList: CountryList): Option[Row] = getAnswerAndBuildSimpleCountryRow[CountryCode](
    page = MovementDestinationCountryPage,
    getCountryCode = countryCode => countryCode,
    countryList = countryList,
    prefix = "movementDestinationCountry",
    id = Some("change-movement-destination-country"),
    call = routes.MovementDestinationCountryController.onPageLoad(lrn, mode)
  )

  private def getAnswerAndBuildOfficeRow[T](
    page: QuestionPage[T],
    formatAnswer: T => String,
    customsOfficeList: CustomsOfficeList,
    buildRow: Text => Row
  )(implicit rds: Reads[T]): Option[Row] = userAnswers.get(page) flatMap {
    answer =>
      customsOfficeList.getCustomsOffice(formatAnswer(answer)) map {
        customsOffice =>
          buildRow(lit"${customsOffice.toString}")
      }
  }

}
