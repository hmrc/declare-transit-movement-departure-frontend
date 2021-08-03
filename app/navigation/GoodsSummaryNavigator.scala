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

package navigation

import controllers.goodsSummary.routes
import derivable.DeriveNumberOfSeals

import javax.inject.{Inject, Singleton}
import models.ProcedureType.{Normal, Simplified}
import models._
import pages._
import play.api.mvc.Call
import pages.movementDetails.PreLodgeDeclarationPage

@Singleton
class GoodsSummaryNavigator @Inject() () extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case AuthorisedLocationCodePage     => ua => Some(routes.ControlResultDateLimitController.onPageLoad(ua.lrn, NormalMode))
    case AddCustomsApprovedLocationPage => ua => Some(addCustomsApprovedLocationRoute(ua, NormalMode))
    case ControlResultDateLimitPage     => ua => Some(routes.AddSealsController.onPageLoad(ua.lrn, NormalMode))
    case CustomsApprovedLocationPage    => ua => Some(routes.AddSealsController.onPageLoad(ua.lrn, NormalMode))
    case AddSealsPage                   => ua => Some(addSealsRoute(ua, NormalMode))
    case SealIdDetailsPage(_)           => ua => Some(routes.SealsInformationController.onPageLoad(ua.lrn, NormalMode))
    case AddSealsLaterPage              => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn))
    case SealsInformationPage           => ua => Some(sealsInformationRoute(ua, NormalMode))
    case ConfirmRemoveSealsPage         => ua => Some(confirmRemoveSealsRoute(ua, CheckMode))
    case ConfirmRemoveSealPage()        => ua => Some(confirmRemoveSeal(ua, NormalMode))
    case LoadingPlacePage               => ua => Some(loadingPlaceRoute(ua))
    case AddAgreedLocationOfGoodsPage   => ua => Some(addAgreedLocationOfGoodsRoute(ua, NormalMode))
    case AgreedLocationOfGoodsPage     => ua => Some(routes.AddSealsController.onPageLoad(ua.lrn, NormalMode))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case AuthorisedLocationCodePage     => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn))
    case ControlResultDateLimitPage     => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn))
    case AddCustomsApprovedLocationPage => ua => Some(addCustomsApprovedLocationRoute(ua, CheckMode))
    case CustomsApprovedLocationPage    => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn))
    case AddSealsPage                   => ua => Some(addSealsRoute(ua, CheckMode))
    case AddSealsLaterPage              => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn))
    case SealIdDetailsPage(_)           => ua => Some(routes.SealsInformationController.onPageLoad(ua.lrn, CheckMode))
    case SealsInformationPage           => ua => Some(sealsInformationRoute(ua, CheckMode))
    case ConfirmRemoveSealsPage         => ua => Some(confirmRemoveSealsRoute(ua, CheckMode))
    case ConfirmRemoveSealPage()        => ua => Some(confirmRemoveSeal(ua, CheckMode))
    case LoadingPlacePage               => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn))
    case AddAgreedLocationOfGoodsPage   => ua => Some(addAgreedLocationOfGoodsRoute(ua, CheckMode))
    case AgreedLocationOfGoodsPage      => ua =>  Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn))

  }

  def confirmRemoveSealsRoute(ua: UserAnswers, mode: Mode) =
    ua.get(ConfirmRemoveSealsPage) match {
      case Some(true) => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn)
      case _          => routes.AddSealsController.onPageLoad(ua.lrn, mode)
    }

  def confirmRemoveSeal(ua: UserAnswers, mode: Mode) = {
    val sealCount = ua.get(DeriveNumberOfSeals).getOrElse(0)
    (ua.get(ConfirmRemoveSealsPage)) match {
      case Some(true) if sealCount > 0 => routes.SealsInformationController.onPageLoad(ua.lrn, mode)
      case Some(true) => routes.AddSealsController.onPageLoad(ua.lrn, mode)
      case _          => routes.SealsInformationController.onPageLoad(ua.lrn, mode)
    }
  }

  def loadingPlaceRoute(ua: UserAnswers): Call =
    (ua.get(ProcedureTypePage), ua.get(PreLodgeDeclarationPage)) match {
      case (Some(Simplified),_) => routes.AuthorisedLocationCodeController.onPageLoad(ua.lrn, NormalMode)
      case (Some(Normal), Some(false))     => routes.AddCustomsApprovedLocationController.onPageLoad(ua.lrn, NormalMode)
      case (Some(Normal), Some(true))     => routes.AddAgreedLocationOfGoodsController.onPageLoad(ua.lrn, NormalMode)
    }

  def addCustomsApprovedLocationRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddCustomsApprovedLocationPage), ua.get(CustomsApprovedLocationPage), mode) match {
      case (Some(true), _, NormalMode)   => routes.CustomsApprovedLocationController.onPageLoad(ua.lrn, NormalMode)
      case (Some(true), None, CheckMode) => routes.CustomsApprovedLocationController.onPageLoad(ua.lrn, CheckMode)
      case (Some(false), _, NormalMode)  => routes.AddAgreedLocationOfGoodsController.onPageLoad(ua.lrn, NormalMode)
      case (Some(false), _, CheckMode) if (ua.get(AgreedLocationOfGoodsPage).isEmpty)  => routes.AddAgreedLocationOfGoodsController.onPageLoad(ua.lrn, CheckMode)
      case _                             => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  def addSealsRoute(ua: UserAnswers, mode: Mode): Call = {
    val sealCount = ua.get(DeriveNumberOfSeals).getOrElse(0)
    val sealIndex = Index(sealCount)

    (ua.get(AddSealsPage), mode) match {
      case (Some(false), _) if sealCount == 0       => routes.AddSealsLaterController.onPageLoad(ua.lrn, mode)
      case (Some(false), _)                         => routes.ConfirmRemoveSealsController.onPageLoad(ua.lrn, mode)
      case (Some(true), CheckMode) if sealCount > 0 => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(true), _) if sealCount >= 10       => routes.SealsInformationController.onPageLoad(ua.lrn, mode)
      case (Some(true), _)                          => routes.SealIdDetailsController.onPageLoad(ua.lrn, sealIndex, mode)
    }
  }

  def sealsInformationRoute(ua: UserAnswers, mode: Mode): Call = {
    val sealCount = ua.get(DeriveNumberOfSeals).getOrElse(0)
    val sealIndex = Index(sealCount)

    (ua.get(SealsInformationPage), mode) match {
      case (Some(true), _)  => routes.SealIdDetailsController.onPageLoad(ua.lrn, sealIndex, mode)
      case (Some(false), _) => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn)
    }
  }

  def addAgreedLocationOfGoodsRoute(ua:UserAnswers, mode: Mode): Call  = 
    (ua.get(AddAgreedLocationOfGoodsPage), mode) match {
      case (Some(true), NormalMode) => routes.AgreedLocationOfGoodsController.onPageLoad(ua.lrn, mode)
      case (Some(false), NormalMode) => routes.AddSealsController.onPageLoad(ua.lrn, mode)
      case (Some(true), CheckMode) if ua.get(AgreedLocationOfGoodsPage).isEmpty  =>
        routes.AgreedLocationOfGoodsController.onPageLoad(ua.lrn, mode)
      case _ => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.lrn)
    }
    // format: on
}
