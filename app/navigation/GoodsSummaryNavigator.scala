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

@Singleton
class GoodsSummaryNavigator @Inject()() extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case DeclarePackagesPage            => ua => Some(declarePackageRoute(ua, NormalMode))
    case TotalPackagesPage              => ua => Some(routes.TotalGrossMassController.onPageLoad(ua.id, NormalMode))
    case TotalGrossMassPage             => ua => Some(totalGrossMassRoute(ua))
    case AuthorisedLocationCodePage     => ua => Some(routes.ControlResultDateLimitController.onPageLoad(ua.id, NormalMode))
    case AddCustomsApprovedLocationPage => ua => Some(addCustomsApprovedLocationRoute(ua, NormalMode))
    case ControlResultDateLimitPage     => ua => Some(routes.AddSealsController.onPageLoad(ua.id, NormalMode))
    case CustomsApprovedLocationPage    => ua => Some(routes.AddSealsController.onPageLoad(ua.id, NormalMode))
    case AddSealsPage                   => ua => Some(addSealsRoute(ua, NormalMode))
    case SealIdDetailsPage(_)           => ua => Some(routes.SealsInformationController.onPageLoad(ua.id, NormalMode))
    case AddSealsLaterPage              => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id))
    case SealsInformationPage           => ua => Some(sealsInformationRoute(ua, NormalMode))
    case ConfirmRemoveSealsPage         => ua => Some(confirmRemoveSealsRoute(ua, CheckMode))
    case ConfirmRemoveSealPage()        => ua => Some(confirmRemoveSeal(ua, NormalMode))
    case LoadingPlacePage               => ua => Some(loadingPlaceRoute(ua))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case DeclarePackagesPage            => ua => Some(declarePackageRoute(ua, CheckMode))
    case TotalPackagesPage              => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id))
    case TotalGrossMassPage             => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id))
    case AuthorisedLocationCodePage     => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id))
    case ControlResultDateLimitPage     => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id))
    case AddCustomsApprovedLocationPage => ua => Some(addCustomsApprovedLocationRoute(ua, CheckMode))
    case CustomsApprovedLocationPage    => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id))
    case AddSealsPage                   => ua => Some(addSealsRoute(ua, CheckMode))
    case AddSealsLaterPage              => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id))
    case SealIdDetailsPage(_)           => ua => Some(routes.SealsInformationController.onPageLoad(ua.id, CheckMode))
    case SealsInformationPage           => ua => Some(sealsInformationRoute(ua, CheckMode))
    case ConfirmRemoveSealsPage         => ua => Some(confirmRemoveSealsRoute(ua, CheckMode))
    case ConfirmRemoveSealPage()        => ua => Some(confirmRemoveSeal(ua, CheckMode))
    case LoadingPlacePage               => ua => Some(routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id))

  }

  def confirmRemoveSealsRoute(ua: UserAnswers, mode: Mode) =
    ua.get(ConfirmRemoveSealsPage) match {
      case Some(true) => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id)
      case _          => routes.AddSealsController.onPageLoad(ua.id, mode)
    }

  def confirmRemoveSeal(ua: UserAnswers, mode: Mode) = {
    val sealCount = ua.get(DeriveNumberOfSeals).getOrElse(0)
    (ua.get(ConfirmRemoveSealsPage)) match {
      case Some(true) if sealCount > 0 => routes.SealsInformationController.onPageLoad(ua.id, mode)
      case Some(true) => routes.AddSealsController.onPageLoad(ua.id, mode)
      case _          => routes.SealsInformationController.onPageLoad(ua.id, mode)
    }
  }

  def declarePackageRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(DeclarePackagesPage), ua.get(TotalPackagesPage), mode) match {
      case (Some(true), Some(_), CheckMode) => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true), _, _)               => routes.TotalPackagesController.onPageLoad(ua.id, mode)
      case (Some(false), _, NormalMode)     => routes.TotalGrossMassController.onPageLoad(ua.id, mode)
      case (Some(false), _, CheckMode)      => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id)
    }

  def totalGrossMassRoute(ua: UserAnswers): Call =
    (ua.get(ProcedureTypePage), ua.get(AddSecurityDetailsPage)) match {
      case (_, Some(true) )     => controllers.routes.LoadingPlaceController.onPageLoad(ua.id, NormalMode)
      case (Some(Normal), _) =>  routes.AddCustomsApprovedLocationController.onPageLoad(ua.id, NormalMode)
      case (Some(Simplified), _) => routes.AuthorisedLocationCodeController.onPageLoad(ua.id, NormalMode)
    }

  def loadingPlaceRoute(ua: UserAnswers): Call =
    ua.get(ProcedureTypePage) match {
      case Some(Normal)     => routes.AddCustomsApprovedLocationController.onPageLoad(ua.id, NormalMode)
      case Some(Simplified) => routes.AuthorisedLocationCodeController.onPageLoad(ua.id, NormalMode)
    }

  def addCustomsApprovedLocationRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddCustomsApprovedLocationPage), ua.get(CustomsApprovedLocationPage), mode) match {
      case (Some(true), _, NormalMode)   => routes.CustomsApprovedLocationController.onPageLoad(ua.id, NormalMode)
      case (Some(true), None, CheckMode) => routes.CustomsApprovedLocationController.onPageLoad(ua.id, CheckMode)
      case (Some(false), _, NormalMode)  => routes.AddSealsController.onPageLoad(ua.id, NormalMode)
      case _                             => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id)
    }

  def addSealsRoute(ua: UserAnswers, mode: Mode): Call = {
    val sealCount = ua.get(DeriveNumberOfSeals).getOrElse(0)
    val sealIndex = Index(sealCount)

    (ua.get(AddSealsPage), mode) match {
      case (Some(false), _) if sealCount == 0       => routes.AddSealsLaterController.onPageLoad(ua.id, mode)
      case (Some(false), _)                         => routes.ConfirmRemoveSealsController.onPageLoad(ua.id, mode)
      case (Some(true), CheckMode) if sealCount > 0 => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true), _) if sealCount >= 10       => routes.SealsInformationController.onPageLoad(ua.id, mode)
      case (Some(true), _)                          => routes.SealIdDetailsController.onPageLoad(ua.id, sealIndex, mode)
    }
  }

  def sealsInformationRoute(ua: UserAnswers, mode: Mode): Call = {
    val sealCount = ua.get(DeriveNumberOfSeals).getOrElse(0)
    val sealIndex = Index(sealCount)

    (ua.get(SealsInformationPage), mode) match {
      case (Some(true), _)  => routes.SealIdDetailsController.onPageLoad(ua.id, sealIndex, mode)
      case (Some(false), _) => routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id)
    }
  }
  // format: on
}
