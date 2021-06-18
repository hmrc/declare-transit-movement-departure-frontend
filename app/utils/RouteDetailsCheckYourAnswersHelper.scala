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
import models.{CheckMode, CountryList, CustomsOfficeList, Index, LocalReferenceNumber, Mode, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

class RouteDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def lrn: LocalReferenceNumber = userAnswers.id

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

  def addTransitOffice(): Option[Row] = userAnswers.get(AddTransitOfficePage) map {
    answer =>
      Row(
        key = Key(msg"addTransitOffice.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.AddTransitOfficeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addTransitOffice.checkYourAnswersLabel"))
          )
        )
      )
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

  def countryOfDispatch(codeList: CountryList): Option[Row] = userAnswers.get(CountryOfDispatchPage) map {
    answer =>
      val countryName = codeList.getCountry(answer.country).map(_.description).getOrElse(answer.country.code)

      Row(
        key = Key(msg"countryOfDispatch.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$countryName"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.CountryOfDispatchController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"countryOfDispatch.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-country-of-dispatch")
          )
        )
      )
  }

  def destinationCountry(codeList: CountryList): Option[Row] = userAnswers.get(DestinationCountryPage) map {
    answer =>
      val countryName = codeList.getCountry(answer).map(_.description).getOrElse(answer.code)

      Row(
        key = Key(msg"destinationCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$countryName"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.DestinationCountryController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"destinationCountry.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-destination-country")
          )
        )
      )
  }

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

            Row(
              key = Key(lit"${office.name} (${office.id})"),
              value = Value(lit"$arrivalTime"),
              actions = List(
                Action(
                  content = msg"site.change",
                  href = routes.OfficeOfTransitCountryController.onPageLoad(userAnswers.id, index, mode).url,
                  visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.change.hidden".withArgs(answer)),
                  attributes = Map("id" -> s"""change-office-of-transit-${index.display}""")
                ),
                Action(
                  content = msg"site.delete",
                  href = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(userAnswers.id, index, mode).url,
                  visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.delete.hidden".withArgs(answer)),
                  attributes = Map("id" -> s"""remove-office-of-transit-${index.display}""")
                )
              )
            )
        }
    }

  def movementDestinationCountry(countryList: CountryList): Option[Row] = userAnswers.get(MovementDestinationCountryPage) map {
    answer =>
      val countryName = countryList.getCountry(answer).map(_.description).getOrElse(answer.code)
      Row(
        key = Key(msg"movementDestinationCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$countryName"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.MovementDestinationCountryController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"movementDestinationCountry.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-movement-destination-country")
          )
        )
      )
  }

  def officeOfTransitCountry(index: Index): Option[Row] = userAnswers.get(OfficeOfTransitCountryPage(index)) map {
    answer =>
      Row(
        key = Key(msg"officeOfTransitCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.OfficeOfTransitCountryController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"officeOfTransitCountry.checkYourAnswersLabel"))
          )
        )
      )
  }
}
