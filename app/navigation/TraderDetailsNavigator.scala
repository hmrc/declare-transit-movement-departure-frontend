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

import controllers.traderDetails.routes
import models.DeclarationType.Option4
import models.ProcedureType.{Normal, Simplified}
import models.{CheckMode, _}
import pages._
import pages.traderDetails._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class TraderDetailsNavigator @Inject() () extends Navigator {

  val normalRoutes: RouteMapping = {
    case IsPrincipalEoriKnownPage => ua => Some(isPrincipalEoriKnownRoute(ua))
    case PrincipalNamePage        => reverseRouteToCall(NormalMode)(routes.PrincipalAddressController.onPageLoad)
    case PrincipalAddressPage     => ua => Some(principalAddressNormalModeRoute(ua))
    case WhatIsPrincipalEoriPage  => ua => Some(whatIsPrincipalEoriRoute(ua, NormalMode))
    case AddConsignorPage         => ua => Some(addConsignorRoute(ua))
    case IsConsignorEoriKnownPage => ua => Some(isConsignorEoriKnownRoute(ua))
    case ConsignorEoriPage        => reverseRouteToCall(NormalMode)(routes.ConsignorNameController.onPageLoad)
    case ConsignorNamePage        => reverseRouteToCall(NormalMode)(routes.ConsignorAddressController.onPageLoad)
    case ConsignorAddressPage     => reverseRouteToCall(NormalMode)(routes.AddConsigneeController.onPageLoad)
    case AddConsigneePage         => ua => Some(addConsigneeRoute(ua, NormalMode))
    case IsConsigneeEoriKnownPage => ua => Some(isConsigneeEoriKnownRoute(ua))
    case ConsigneeNamePage        => reverseRouteToCall(NormalMode)(routes.ConsigneeAddressController.onPageLoad)
    case WhatIsConsigneeEoriPage  => reverseRouteToCall(NormalMode)(routes.ConsigneeNameController.onPageLoad)
    case ConsigneeAddressPage     => ua => Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
    case PrincipalTirHolderIdPage => ua => Some(routes.AddConsignorController.onPageLoad(ua.lrn, NormalMode))
  }

  override def checkModeDefaultPage(userAnswers: UserAnswers): Call =
    routes.TraderDetailsCheckYourAnswersController.onPageLoad(userAnswers.lrn)

  override def checkRoutes: RouteMapping = {

    case IsPrincipalEoriKnownPage => ua => Some(isPrincipalEoriKnownCheckModeRoute(ua))
    case WhatIsPrincipalEoriPage  => ua => Some(whatIsPrincipalEoriRoute(ua, CheckMode))
    case PrincipalAddressPage     => ua => Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
    case PrincipalNamePage        => ua => Some(principalNameCheckModeRoute(ua))
    case AddConsignorPage         => ua => Some(addConsignorRoute(ua, CheckMode))
    case IsConsignorEoriKnownPage => ua => Some(isConsignorEoriKnownRoute(ua, CheckMode))
    case ConsignorEoriPage        => ua => Some(consignorEoriRoute(ua))
    case ConsignorNamePage        => ua => Some(consignorNameCheckModeRoute(ua))
    case AddConsigneePage         => ua => Some(addConsigneeRoute(ua))
    case IsConsigneeEoriKnownPage => ua => Some(isConsigneeEoriKnownCheckModeRoute(ua))
    case WhatIsConsigneeEoriPage  => ua => Some(whatIsConsigneeEoriRoute(ua))
    case ConsigneeNamePage        => ua => Some(consigneeNameRoute(ua))
    case PrincipalTirHolderIdPage => ua => Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
  }

  private def reverseRouteToCall(mode: Mode)(f: (LocalReferenceNumber, Mode) => Call): UserAnswers => Option[Call] =
    ua => Some(f(ua.lrn, mode))

  private def consigneeNameRoute(ua: UserAnswers): Call =
    ua.get(ConsigneeAddressPage) match {
      case Some(_) => checkModeDefaultPage(ua)
      case None    => routes.ConsigneeAddressController.onPageLoad(ua.lrn, CheckMode)
    }

  private def whatIsPrincipalEoriRoute(ua: UserAnswers, mode: Mode): Call = {
    val eoriRegex = "(?i)(gb|xi).*".r
    (ua.get(WhatIsPrincipalEoriPage), ua.get(ProcedureTypePage)) match {
      case (Some(eoriRegex(_)), Some(Normal)) => procedureType(ua, mode, Normal)
      case (Some(_), Some(Simplified))        => procedureType(ua, mode, Simplified)
      case _                                  => routes.PrincipalNameController.onPageLoad(ua.lrn, mode)
    }
  }

  private def addConsignorRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddConsignorPage), ua.get(IsConsignorEoriKnownPage)) match {
      case (Some(true), None) => routes.IsConsignorEoriKnownController.onPageLoad(ua.lrn, mode)
      case (_, _)             => checkModeDefaultPage(ua)
    }

  private def consignorNameCheckModeRoute(ua: UserAnswers): Call =
    ua.get(ConsignorAddressPage) match {
      case Some(_) => checkModeDefaultPage(ua)
      case None    => routes.ConsignorAddressController.onPageLoad(ua.lrn, CheckMode)
    }

  private def addConsigneeRoute(ua: UserAnswers): Call =
    (ua.get(AddConsigneePage), ua.get(IsConsigneeEoriKnownPage)) match {
      case (Some(true), None) => routes.IsConsigneeEoriKnownController.onPageLoad(ua.lrn, CheckMode)
      case (_, _)             => checkModeDefaultPage(ua)
    }

  private def principalAddressNormalModeRoute(ua: UserAnswers) =
    ua.get(DeclarationTypePage) match {
      case Some(Option4) => routes.PrincipalTirHolderIdController.onPageLoad(ua.lrn, NormalMode)
      case _             => routes.AddConsignorController.onPageLoad(ua.lrn, NormalMode)
    }

  private def principalNameCheckModeRoute(ua: UserAnswers): Call =
    ua.get(PrincipalAddressPage) match {
      case Some(_) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
      case None    => routes.PrincipalAddressController.onPageLoad(ua.lrn, CheckMode)
    }

  private def whatIsConsigneeEoriRoute(ua: UserAnswers): Call =
    ua.get(ConsigneeNamePage) match {
      case Some(_) => checkModeDefaultPage(ua)
      case None    => routes.ConsigneeNameController.onPageLoad(ua.lrn, CheckMode)
    }

  private def consignorEoriRoute(ua: UserAnswers): Call =
    ua.get(ConsignorNamePage) match {
      case Some(_) => checkModeDefaultPage(ua)
      case None    => routes.ConsignorNameController.onPageLoad(ua.lrn, CheckMode)
    }

  private def addConsignorRoute(ua: UserAnswers): Call =
    ua.get(AddConsignorPage) match {
      case Some(true)  => routes.IsConsignorEoriKnownController.onPageLoad(ua.lrn, NormalMode)
      case Some(false) => routes.AddConsigneeController.onPageLoad(ua.lrn, NormalMode)
      case None        => routes.AddConsignorController.onPageLoad(ua.lrn, NormalMode)
    }

  private def procedureType(ua: UserAnswers, mode: Mode, procedureType: ProcedureType) =
    (procedureType, mode) match {
      case (Normal, _)     => routes.PrincipalNameController.onPageLoad(ua.lrn, mode)
      case (_, NormalMode) => routes.AddConsignorController.onPageLoad(ua.lrn, mode)
      case (_, CheckMode)  => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def isPrincipalEoriKnownRoute(ua: UserAnswers): Call =
    ua.get(IsPrincipalEoriKnownPage) match {
      case Some(true) => routes.WhatIsPrincipalEoriController.onPageLoad(ua.lrn, NormalMode)
      case _          => routes.PrincipalNameController.onPageLoad(ua.lrn, NormalMode)
    }

  private def isPrincipalEoriKnownCheckModeRoute(ua: UserAnswers): Call =
    (ua.get(IsPrincipalEoriKnownPage), ua.get(WhatIsPrincipalEoriPage), ua.get(PrincipalNamePage)) match {
      case (Some(false), _, None) => routes.PrincipalNameController.onPageLoad(ua.lrn, CheckMode)
      case (Some(true), None, _)  => routes.WhatIsPrincipalEoriController.onPageLoad(ua.lrn, CheckMode)
      case _                      => checkModeDefaultPage(ua)
    }

  private def isConsignorEoriKnownRoute(ua: UserAnswers): Call =
    ua.get(IsConsignorEoriKnownPage) match {
      case Some(true) => routes.ConsignorEoriController.onPageLoad(ua.lrn, NormalMode)
      case _          => routes.ConsignorNameController.onPageLoad(ua.lrn, NormalMode)
    }

  private def isConsignorEoriKnownRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(IsConsignorEoriKnownPage), ua.get(ConsignorEoriPage), ua.get(ConsignorNamePage)) match {
      case (Some(true), None, _)  => routes.ConsignorEoriController.onPageLoad(ua.lrn, mode)
      case (Some(false), _, None) => routes.ConsignorNameController.onPageLoad(ua.lrn, mode)
      case _                      => checkModeDefaultPage(ua)
    }

  private def addConsigneeRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(AddConsigneePage) match {
      case Some(true) => routes.IsConsigneeEoriKnownController.onPageLoad(ua.lrn, mode)
      case _          => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def isConsigneeEoriKnownRoute(ua: UserAnswers): Call =
    ua.get(IsConsigneeEoriKnownPage) match {
      case Some(true)  => routes.WhatIsConsigneeEoriController.onPageLoad(ua.lrn, NormalMode)
      case Some(false) => routes.ConsigneeNameController.onPageLoad(ua.lrn, NormalMode)
      case _           => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def isConsigneeEoriKnownCheckModeRoute(ua: UserAnswers): Call =
    (ua.get(IsConsigneeEoriKnownPage), ua.get(WhatIsConsigneeEoriPage), ua.get(ConsigneeNamePage)) match {
      case (Some(true), None, _)  => routes.WhatIsConsigneeEoriController.onPageLoad(ua.lrn, CheckMode)
      case (Some(false), _, None) => routes.ConsigneeNameController.onPageLoad(ua.lrn, CheckMode)
      case _                      => checkModeDefaultPage(ua)
    }

}
