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

import javax.inject.{Inject, Singleton}
import models._
import pages._
import pages.traderDetails.{
  AddConsigneePage,
  AddConsignorPage,
  ConsigneeAddressPage,
  ConsigneeNamePage,
  ConsignorAddressPage,
  ConsignorEoriPage,
  ConsignorNamePage,
  IsConsigneeEoriKnownPage,
  IsConsignorEoriKnownPage,
  IsPrincipalEoriKnownPage,
  PrincipalAddressPage,
  PrincipalNamePage,
  WhatIsPrincipalEoriPage
}
import play.api.mvc.Call

@Singleton
class TraderDetailsNavigator @Inject() () extends Navigator {

  val normalRoutes: RouteMapping = {
    case IsPrincipalEoriKnownPage =>
      ua => Some(isPrincipalEoriKnownRoute(ua, NormalMode))
    case PrincipalNamePage       => reverseRouteToCall(NormalMode)(routes.PrincipalAddressController.onPageLoad(_, _))
    case PrincipalAddressPage    => ua => Some(principalAddressRoute(ua))
    case WhatIsPrincipalEoriPage => ua => Some(whatIsPrincipalEoriRoute(ua, NormalMode))
    case AddConsignorPage =>
      ua =>
        ua.get(AddConsignorPage) match {
          case Some(true)  => Some(routes.IsConsignorEoriKnownController.onPageLoad(ua.lrn, NormalMode))
          case Some(false) => Some(routes.AddConsigneeController.onPageLoad(ua.lrn, NormalMode))
          case None        => Some(routes.AddConsignorController.onPageLoad(ua.lrn, NormalMode))
        }
    case IsConsignorEoriKnownPage =>
      ua => Some(isConsignorEoriKnownRoute(ua, NormalMode))
    case ConsignorEoriPage    => reverseRouteToCall(NormalMode)(routes.ConsignorNameController.onPageLoad(_, _))
    case ConsignorNamePage    => reverseRouteToCall(NormalMode)(routes.ConsignorAddressController.onPageLoad(_, _))
    case ConsignorAddressPage => reverseRouteToCall(NormalMode)(routes.AddConsigneeController.onPageLoad(_, _))
    case AddConsigneePage =>
      ua => Some(addConsigneeRoute(ua, NormalMode))
    case IsConsigneeEoriKnownPage =>
      ua => Some(isConsigneeEoriKnownRoute(ua, NormalMode))
    case ConsigneeNamePage       => reverseRouteToCall(NormalMode)(routes.ConsigneeAddressController.onPageLoad(_, _))
    case WhatIsConsigneeEoriPage => reverseRouteToCall(NormalMode)(routes.ConsigneeNameController.onPageLoad(_, _))
    case ConsigneeAddressPage =>
      ua => Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
    case PrincipalTirHolderIdPage => ua => Some(routes.AddConsignorController.onPageLoad(ua.lrn, NormalMode))
  }

  override def checkModeDefaultPage(userAnswers: UserAnswers): Call =
    routes.TraderDetailsCheckYourAnswersController.onPageLoad(userAnswers.lrn)

  override def checkRoutes: RouteMapping = {

    case IsPrincipalEoriKnownPage =>
      ua =>
        (ua.get(IsPrincipalEoriKnownPage), ua.get(WhatIsPrincipalEoriPage), ua.get(PrincipalNamePage)) match {
          case (Some(true), None, _)  => Some(routes.WhatIsPrincipalEoriController.onPageLoad(ua.lrn, CheckMode))
          case (Some(false), _, None) => Some(routes.PrincipalNameController.onPageLoad(ua.lrn, CheckMode))
          case (None, None, _)        => Some(routes.IsPrincipalEoriKnownController.onPageLoad(ua.lrn, NormalMode))
          case _                      => Some(checkModeDefaultPage(ua))
        }
    case WhatIsPrincipalEoriPage => ua => Some(whatIsPrincipalEoriRoute(ua, CheckMode))
    case PrincipalAddressPage    => ua => Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn))

    case PrincipalNamePage =>
      ua =>
        ua.get(PrincipalAddressPage) match {
          case Some(_) => Some(checkModeDefaultPage(ua))
          case None    => Some(routes.PrincipalAddressController.onPageLoad(ua.lrn, CheckMode))
        }

    case AddConsignorPage =>
      ua =>
        (ua.get(AddConsignorPage), ua.get(IsConsignorEoriKnownPage)) match {
          case (Some(true), None) => Some(routes.IsConsignorEoriKnownController.onPageLoad(ua.lrn, CheckMode))
          case (_, _)             => Some(checkModeDefaultPage(ua))
        }

    case IsConsignorEoriKnownPage =>
      ua =>
        (ua.get(IsConsignorEoriKnownPage), ua.get(ConsignorEoriPage), ua.get(ConsignorNamePage)) match {
          case (Some(true), None, _)  => Some(routes.ConsignorEoriController.onPageLoad(ua.lrn, CheckMode))
          case (Some(false), _, None) => Some(routes.ConsignorNameController.onPageLoad(ua.lrn, CheckMode))
          case _                      => Some(checkModeDefaultPage(ua))
        }

    case ConsignorEoriPage =>
      ua =>
        ua.get(ConsignorNamePage) match {
          case Some(value) => Some(checkModeDefaultPage(ua))
          case None        => Some(routes.ConsignorNameController.onPageLoad(ua.lrn, CheckMode))
        }

    case ConsignorNamePage =>
      ua =>
        ua.get(ConsignorAddressPage) match {
          case Some(value) => Some(checkModeDefaultPage(ua))
          case None        => Some(routes.ConsignorAddressController.onPageLoad(ua.lrn, CheckMode))
        }

    case AddConsigneePage =>
      ua =>
        (ua.get(AddConsigneePage), ua.get(IsConsigneeEoriKnownPage)) match {
          case (Some(true), None) => Some(routes.IsConsigneeEoriKnownController.onPageLoad(ua.lrn, CheckMode))
          case (_, _)             => Some(checkModeDefaultPage(ua))
        }

    case IsConsigneeEoriKnownPage =>
      ua =>
        (ua.get(IsConsigneeEoriKnownPage), ua.get(WhatIsConsigneeEoriPage), ua.get(ConsigneeNamePage)) match {
          case (Some(true), None, _)  => Some(routes.WhatIsConsigneeEoriController.onPageLoad(ua.lrn, CheckMode))
          case (Some(false), _, None) => Some(routes.ConsigneeNameController.onPageLoad(ua.lrn, CheckMode))
          case _                      => Some(checkModeDefaultPage(ua))
        }

    case WhatIsConsigneeEoriPage =>
      ua =>
        ua.get(ConsigneeNamePage) match {
          case Some(value) => Some(checkModeDefaultPage(ua))
          case None        => Some(routes.ConsigneeNameController.onPageLoad(ua.lrn, CheckMode))
        }

    case ConsigneeNamePage =>
      ua =>
        ua.get(ConsigneeAddressPage) match {
          case Some(value) => Some(checkModeDefaultPage(ua))
          case None        => Some(routes.ConsigneeAddressController.onPageLoad(ua.lrn, CheckMode))
        }
    case PrincipalTirHolderIdPage => ua => Some(routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
  }

  private def reverseRouteToCall(mode: Mode)(f: (LocalReferenceNumber, Mode) => Call): UserAnswers => Option[Call] =
    ua => Some(f(ua.lrn, mode))

  private def whatIsPrincipalEoriRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(WhatIsPrincipalEoriPage), ua.get(ProcedureTypePage)) match {
      case (Some(_), Some(Simplified))                                                                 => declarationTypeTIR(ua, mode)
      case (Some(x), Some(Normal)) if x.toUpperCase.startsWith("GB") || x.toUpperCase.startsWith("XI") => declarationTypeTIR(ua, mode)
      case _                                                                                           => routes.PrincipalNameController.onPageLoad(ua.lrn, mode)
    }

  private def declarationTypeTIR(ua: UserAnswers, mode: Mode) =
    (ua.get(DeclarationTypePage), mode) match {
      case (Some(Option4), _) => routes.PrincipalNameController.onPageLoad(ua.lrn, mode)
      case (_, NormalMode)    => routes.AddConsignorController.onPageLoad(ua.lrn, mode)
      case (_, CheckMode)     => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def principalAddressRoute(ua: UserAnswers) =
    ua.get(DeclarationTypePage) match {
      case Some(Option4) => routes.PrincipalTirHolderIdController.onPageLoad(ua.lrn, NormalMode)
      case _             => routes.AddConsignorController.onPageLoad(ua.lrn, NormalMode)
    }

  private def isPrincipalEoriKnownRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(IsPrincipalEoriKnownPage) match {
      case Some(true)  => routes.WhatIsPrincipalEoriController.onPageLoad(ua.lrn, mode)
      case Some(false) => routes.PrincipalNameController.onPageLoad(ua.lrn, mode)
      case _           => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def isConsignorEoriKnownRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(IsConsignorEoriKnownPage), mode) match {
      case (Some(true), NormalMode) => routes.ConsignorEoriController.onPageLoad(ua.lrn, NormalMode)
      case (Some(true), CheckMode)  => routes.ConsignorEoriController.onPageLoad(ua.lrn, CheckMode)
      case (Some(false), _)         => routes.ConsignorNameController.onPageLoad(ua.lrn, mode)
      case _                        => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def addConsigneeRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddConsigneePage), mode) match {
      case (Some(true), NormalMode)  => routes.IsConsigneeEoriKnownController.onPageLoad(ua.lrn, NormalMode)
      case (Some(true), CheckMode)   => routes.IsConsigneeEoriKnownController.onPageLoad(ua.lrn, CheckMode)
      case (Some(false), NormalMode) => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
      case _                         => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def isConsigneeEoriKnownRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(IsConsigneeEoriKnownPage), mode) match {
      case (Some(true), NormalMode) => routes.WhatIsConsigneeEoriController.onPageLoad(ua.lrn, NormalMode)
      case (Some(true), CheckMode)  => routes.WhatIsConsigneeEoriController.onPageLoad(ua.lrn, CheckMode)
      case (Some(false), _)         => routes.ConsigneeNameController.onPageLoad(ua.lrn, mode)
      case _                        => routes.TraderDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

}
