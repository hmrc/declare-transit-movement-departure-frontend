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

import controllers.goodsSummary.routes
import models.{Mode, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

import java.time.LocalDate

class GoodsSummaryCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode) extends CheckYourAnswersHelper(userAnswers) {

  def agreedLocationOfGoods: Option[Row] = getAnswerAndBuildRow[String](
    page = AgreedLocationOfGoodsPage,
    formatAnswer = formatAsLiteral,
    prefix = "agreedLocationOfGoods",
    id = None,
    call = routes.AgreedLocationOfGoodsController.onPageLoad(lrn, mode)
  )

  def loadingPlace: Option[Row] = getAnswerAndBuildRow[String](
    page = LoadingPlacePage,
    formatAnswer = formatAsLiteral,
    prefix = "loadingPlace",
    id = None,
    call = controllers.routes.LoadingPlaceController.onPageLoad(lrn, mode)
  )

  def addAgreedLocationOfGoods: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddAgreedLocationOfGoodsPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addAgreedLocationOfGoods",
    id = None,
    call = routes.AddAgreedLocationOfGoodsController.onPageLoad(lrn, mode)
  )

  def sealsInformation: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = SealsInformationPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "sealsInformation",
    id = None,
    call = routes.SealsInformationController.onPageLoad(lrn, mode)
  )

  def controlResultDateLimit: Option[Row] = getAnswerAndBuildRow[LocalDate](
    page = ControlResultDateLimitPage,
    formatAnswer = date => lit"${Format.dateFormattedWithMonthName(date)}",
    prefix = "controlResultDateLimit",
    id = Some("change-control-result-date-limit"),
    call = routes.ControlResultDateLimitController.onPageLoad(lrn, mode)
  )

  def addSeals: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSealsPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addSeals",
    id = Some("change-add-seals"),
    call = routes.AddSealsController.onPageLoad(lrn, mode)
  )

  def customsApprovedLocation: Option[Row] = getAnswerAndBuildRow[String](
    page = CustomsApprovedLocationPage,
    formatAnswer = formatAsLiteral,
    prefix = "customsApprovedLocation",
    id = Some("change-customs-approved-location"),
    call = routes.CustomsApprovedLocationController.onPageLoad(lrn, mode)
  )

  def addCustomsApprovedLocation: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCustomsApprovedLocationPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "addCustomsApprovedLocation",
    id = Some("change-add-customs-approved-location"),
    call = routes.AddCustomsApprovedLocationController.onPageLoad(lrn, mode)
  )

  def authorisedLocationCode: Option[Row] = getAnswerAndBuildRow[String](
    page = AuthorisedLocationCodePage,
    formatAnswer = formatAsLiteral,
    prefix = "authorisedLocationCode",
    id = Some("change-authorised-location-code"),
    call = routes.AuthorisedLocationCodeController.onPageLoad(lrn, mode)
  )
}
