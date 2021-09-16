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
import models.{CheckMode, CountryList, CustomsOfficeList, Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.routeDetails._
import play.api.libs.json.Reads
import play.api.mvc.Call
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

import java.time.LocalDateTime

class RouteDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def arrivalTimesAtOffice(index: Index): Option[Row] = getAnswerAndBuildRow[LocalDateTime](
    page = ArrivalTimesAtOfficePage(index),
    format = x => lit"${Format.dateTimeFormattedAMPM(x).toLowerCase}",
    prefix = "arrivalTimesAtOffice",
    id = Some("change-arrival-times-at-office-of-transit"),
    call = routes.ArrivalTimesAtOfficeController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def destinationOffice(customsOfficeList: CustomsOfficeList): Option[Row] = getAnswerAndBuildOfficeRow[CustomsOffice](
    page = DestinationOfficePage,
    f = x => x.id,
    customsOfficeList = customsOfficeList,
    prefix = "destinationOffice",
    id = "change-destination-office",
    call = routes.DestinationOfficeController.onPageLoad(lrn, CheckMode)
  )

  def addAnotherTransitOffice(index: Index, customsOfficeList: CustomsOfficeList): Option[Row] = getAnswerAndBuildOfficeRow[String](
    page = AddAnotherTransitOfficePage(index),
    f = x => x,
    customsOfficeList = customsOfficeList,
    prefix = "addAnotherTransitOffice",
    id = "change-office-of-transit",
    call = routes.OfficeOfTransitCountryController.onPageLoad(lrn = lrn, index = index, mode = CheckMode),
    args = index.display
  )

  def countryOfDispatch(countryList: CountryList): Option[Row] = getAnswerAndBuildSimpleCountryRow[CountryOfDispatch](
    page = CountryOfDispatchPage,
    f = x => x.country,
    countryList = countryList,
    prefix = "countryOfDispatch",
    id = "change-country-of-dispatch",
    call = routes.CountryOfDispatchController.onPageLoad
  )

  def destinationCountry(countryList: CountryList): Option[Row] = getAnswerAndBuildSimpleCountryRow[CountryCode](
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

            buildRemovableRow(
              key = key,
              value = arrivalTime,
              id = s"office-of-transit-${index.display}",
              changeCall = routes.OfficeOfTransitCountryController.onPageLoad(lrn, index, mode),
              removeCall = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, index, mode)
            )
        }
    }

  def movementDestinationCountry(countryList: CountryList): Option[Row] = getAnswerAndBuildSimpleCountryRow[CountryCode](
    page = MovementDestinationCountryPage,
    f = x => x,
    countryList = countryList,
    prefix = "movementDestinationCountry",
    id = "change-movement-destination-country",
    call = routes.MovementDestinationCountryController.onPageLoad
  )

  private def getAnswerAndBuildOfficeRow[T](
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
          buildRow(prefix, lit"${customsOffice.name} (${customsOffice.id})", Some(id), call, args: _*)
      }
  }

}
