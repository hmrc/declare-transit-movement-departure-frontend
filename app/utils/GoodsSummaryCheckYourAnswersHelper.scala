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
import models.{CheckMode, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

import java.time.LocalDate

class GoodsSummaryCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def agreedLocationOfGoods: Option[Row] = getAnswerAndBuildRow[String](
    page = AgreedLocationOfGoodsPage,
    format = x => lit"$x",
    prefix = "agreedLocationOfGoods",
    id = None,
    call = routes.AgreedLocationOfGoodsController.onPageLoad(lrn, CheckMode)
  )

  def loadingPlace: Option[Row] = getAnswerAndBuildRow[String](
    page = LoadingPlacePage,
    format = x => lit"$x",
    prefix = "loadingPlace",
    id = None,
    call = controllers.routes.LoadingPlaceController.onPageLoad(lrn, CheckMode)
  )

  def addAgreedLocationOfGoods: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddAgreedLocationOfGoodsPage,
    format = yesOrNo,
    prefix = "addAgreedLocationOfGoods",
    id = None,
    call = routes.AddAgreedLocationOfGoodsController.onPageLoad(lrn, CheckMode)
  )

  def sealsInformation: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = SealsInformationPage,
    format = yesOrNo,
    prefix = "sealsInformation",
    id = None,
    call = routes.SealsInformationController.onPageLoad(lrn, CheckMode)
  )

  def controlResultDateLimit: Option[Row] = getAnswerAndBuildRow[LocalDate](
    page = ControlResultDateLimitPage,
    format = x => lit"${Format.dateFormattedWithMonthName(x)}",
    prefix = "controlResultDateLimit",
    id = Some("change-control-result-date-limit"),
    call = routes.ControlResultDateLimitController.onPageLoad(lrn, CheckMode)
  )

  def addSeals: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSealsPage,
    format = yesOrNo,
    prefix = "addSeals",
    id = Some("change-add-seals"),
    call = routes.AddSealsController.onPageLoad(lrn, CheckMode)
  )

  def customsApprovedLocation: Option[Row] = getAnswerAndBuildRow[String](
    page = CustomsApprovedLocationPage,
    format = x => lit"$x",
    prefix = "customsApprovedLocation",
    id = Some("change-customs-approved-location"),
    call = routes.CustomsApprovedLocationController.onPageLoad(lrn, CheckMode)
  )

  def addCustomsApprovedLocation: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddCustomsApprovedLocationPage,
    format = yesOrNo,
    prefix = "addCustomsApprovedLocation",
    id = Some("change-add-customs-approved-location"),
    call = routes.AddCustomsApprovedLocationController.onPageLoad(lrn, CheckMode)
  )

  def authorisedLocationCode: Option[Row] = getAnswerAndBuildRow[String](
    page = AuthorisedLocationCodePage,
    format = x => lit"$x",
    prefix = "authorisedLocationCode",
    id = Some("change-authorised-location-code"),
    call = routes.AuthorisedLocationCodeController.onPageLoad(lrn, CheckMode)
  )
}
