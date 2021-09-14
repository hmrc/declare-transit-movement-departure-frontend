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
import models.reference.{CountryCode, CountryOfDispatch}
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

  def destinationOffice(customsOfficeList: CustomsOfficeList): Option[Row] = userAnswers.get(DestinationOfficePage) flatMap {
    answer =>
      customsOfficeList.getCustomsOffice(answer.id) map {
        customsOffice =>
          Row(
            key = Key(msg"destinationOffice.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
            value = Value(lit"${customsOffice.name} (${customsOffice.id})"),
            actions = List(
              Action(
                content = msg"site.edit",
                href = routes.DestinationOfficeController.onPageLoad(lrn, CheckMode).url,
                visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"destinationOffice.checkYourAnswersLabel")),
                attributes = Map("id" -> "change-destination-office")
              )
            )
          )
      }
  }

  def addAnotherTransitOffice(index: Index, customsOfficeList: CustomsOfficeList): Option[Row] =
    userAnswers.get(AddAnotherTransitOfficePage(index)) flatMap {
      answer =>
        customsOfficeList.getCustomsOffice(answer) map {
          officeOfTransit =>
            Row(
              key = Key(msg"addAnotherTransitOffice.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${officeOfTransit.name} (${officeOfTransit.id})"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.OfficeOfTransitCountryController.onPageLoad(lrn = lrn, index = index, mode = CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addAnotherTransitOffice.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-office-of-transit")
                )
              )
            )
        }
    }

  def countryOfDispatch(countryList: CountryList): Option[Row] = countryRow[CountryOfDispatch](
    CountryOfDispatchPage,
    x => x.country,
    countryList,
    "countryOfDispatch",
    "change-country-of-dispatch",
    routes.CountryOfDispatchController.onPageLoad
  )

  def destinationCountry(countryList: CountryList): Option[Row] = countryRow[CountryCode](
    DestinationCountryPage,
    x => x,
    countryList,
    "destinationCountry",
    "change-destination-country",
    routes.DestinationCountryController.onPageLoad
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
    MovementDestinationCountryPage,
    x => x,
    countryList,
    "movementDestinationCountry",
    "change-movement-destination-country",
    routes.MovementDestinationCountryController.onPageLoad
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
      Row(
        key = Key(msg"$prefix.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$countryName"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = call(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"$prefix.checkYourAnswersLabel")),
            attributes = Map("id" -> id)
          )
        )
      )
  }
}
