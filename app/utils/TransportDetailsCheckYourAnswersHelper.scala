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

import controllers.transportDetails.routes
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, Rail}
import models.{CheckMode, CountryList, LocalReferenceNumber, TransportModeList, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class TransportDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def modeAtBorder(transportModeList: TransportModeList): Option[Row] = userAnswers.get(ModeAtBorderPage) map {
    answer =>
      val modeList = transportModeList.getTransportMode(answer).map(_.description).getOrElse(answer)

      Row(
        key   = Key(msg"modeAtBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$modeList"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ModeAtBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"modeAtBorder.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-mode-at-border")
          )
        )
      )
  }

  def modeCrossingBorder(transportModeList: TransportModeList): Option[Row] = userAnswers.get(ModeCrossingBorderPage) map {
    answer =>
      val modeList = transportModeList.getTransportMode(answer).map(_.description).getOrElse(answer)

      Row(
        key   = Key(msg"modeCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$modeList"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ModeCrossingBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"modeCrossingBorder.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-mode-crossing-border")
          )
        )
      )
  }

  def inlandMode(transportModeList: TransportModeList): Option[Row] = userAnswers.get(InlandModePage) map {
    answer =>
      val modeList = transportModeList
        .getTransportMode(answer)
        .map(
          transport => "(" + transport.code + ") " + transport.description
        )
        .getOrElse(answer)
      Row(
        key   = Key(msg"inlandMode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$modeList"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.InlandModeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"inlandMode.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-inland-mode")
          )
        )
      )
  }

  def idCrossingBorder: Option[Row] =
    userAnswers.get(IdCrossingBorderPage) map {

      answer =>
        Row(
          key   = Key(msg"idCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(lit"$answer"),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = routes.IdCrossingBorderController.onPageLoad(lrn, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"idCrossingBorder.checkYourAnswersLabel")),
              attributes         = Map("id" -> "change-id-crossing-border")
            )
          )
        )
    }

  def nationalityAtDeparture(codeList: CountryList, inlandModeCode: String): Option[Row] =
    if (!Mode5or7.Constants.codes.map(_.toString).contains(inlandModeCode)
        && !Rail.Constants.codes.map(_.toString).contains(inlandModeCode)) {

      userAnswers.get(NationalityAtDeparturePage) map {
        answer =>
          val countryName = codeList.getCountry(answer).map(_.description).getOrElse(answer.code)

          Row(
            key   = Key(msg"nationalityAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
            value = Value(lit"$countryName"),
            actions = List(
              Action(
                content            = msg"site.edit",
                href               = routes.NationalityAtDepartureController.onPageLoad(lrn, CheckMode).url,
                visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"nationalityAtDeparture.checkYourAnswersLabel")),
                attributes         = Map("id" -> "change-nationality-at-departure")
              )
            )
          )
      }
    } else None

  def nationalityCrossingBorder(codeList: CountryList): Option[Row] = userAnswers.get(NationalityCrossingBorderPage) map {
    answer =>
      val countryName = codeList.getCountry(answer).map(_.description).getOrElse(answer.code)

      Row(
        key   = Key(msg"nationalityCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$countryName"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.NationalityCrossingBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"nationalityCrossingBorder.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-nationality-crossing-border")
          )
        )
      )
  }

  def idAtDeparture(inlandModeCode: String): Option[Row] =
    if (!Mode5or7.Constants.codes.map(_.toString).contains(inlandModeCode)) {
      userAnswers.get(IdAtDeparturePage) map {
        answer =>
          Row(
            key   = Key(msg"idAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
            value = Value(lit"$answer"),
            actions = List(
              Action(
                content            = msg"site.edit",
                href               = routes.IdAtDepartureController.onPageLoad(lrn, CheckMode).url,
                visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"idAtDeparture.checkYourAnswersLabel")),
              )
            )
          )
      }
    } else
      None

  def changeAtBorder: Option[Row] = userAnswers.get(ChangeAtBorderPage) map {
    answer =>
      Row(
        key   = Key(msg"changeAtBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ChangeAtBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeAtBorder.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-change-at-border")
          )
        )
      )
  }

  def addIdAtDeparture(inlandModeCode: String): Option[Row] =
    if (!Mode5or7.Constants.codes.map(_.toString).contains(inlandModeCode)) {
      userAnswers.get(AddIdAtDeparturePage) map {
        answer =>
          Row(
            key   = Key(msg"addIdAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
            value = Value(yesOrNo(answer)),
            actions = List(
              Action(
                content            = msg"site.edit",
                href               = routes.AddIdAtDepartureController.onPageLoad(lrn, CheckMode).url,
                visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addIdAtDeparture.checkYourAnswersLabel")),
                attributes         = Map("id" -> "change-add-id-at-departure")
              )
            )
          )
      }
    } else None

  def addNationalityAtDeparture(inlandModeCode: String): Option[Row] =
    if (!Mode5or7.Constants.codes.map(_.toString).contains(inlandModeCode)
        && !Rail.Constants.codes.map(_.toString).contains(inlandModeCode)) {
      userAnswers.get(AddNationalityAtDeparturePage) map {
        answer =>
          Row(
            key   = Key(msg"addNationalityAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
            value = Value(yesOrNo(answer)),
            actions = List(
              Action(
                content            = msg"site.edit",
                href               = routes.AddNationalityAtDepartureController.onPageLoad(lrn, CheckMode).url,
                visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addNationalityAtDeparture.checkYourAnswersLabel")),
                attributes         = Map("id" -> "change-add-nationality-at-departure")
              )
            )
          )
      }
    } else None

  def lrn: LocalReferenceNumber = userAnswers.id
}
