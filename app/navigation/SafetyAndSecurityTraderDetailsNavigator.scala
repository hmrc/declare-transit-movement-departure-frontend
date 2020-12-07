/*
 * Copyright 2020 HM Revenue & Customs
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

import controllers.safetyAndSecurity.routes
import javax.inject.{Inject, Singleton}
import models._
import pages.Page
import pages.safetyAndSecurity.{
  AddCarrierEoriPage,
  AddCarrierPage,
  AddSafetyAndSecurityConsigneeEoriPage,
  AddSafetyAndSecurityConsigneePage,
  AddSafetyAndSecurityConsignorEoriPage,
  AddSafetyAndSecurityConsignorPage,
  CarrierAddressPage,
  CarrierEoriPage,
  CarrierNamePage,
  SafetyAndSecurityConsigneeAddressPage,
  SafetyAndSecurityConsigneeEoriPage,
  SafetyAndSecurityConsigneeNamePage,
  SafetyAndSecurityConsignorAddressPage,
  SafetyAndSecurityConsignorEoriPage,
  SafetyAndSecurityConsignorNamePage
}
import play.api.mvc.Call

@Singleton
class SafetyAndSecurityTraderDetailsNavigator @Inject()() extends Navigator {

  // format: off

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSafetyAndSecurityConsignorPage => ua => Some(addSafetyAndSecurityConsignorRoute(ua, NormalMode))
    case AddSafetyAndSecurityConsignorEoriPage => ua => Some(addSafetyAndSecurityConsignorEoriRoute(ua, NormalMode))
    case SafetyAndSecurityConsignorEoriPage => ua => Some(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(ua.id, NormalMode))
    case SafetyAndSecurityConsignorNamePage => ua => Some(routes.SafetyAndSecurityConsignorAddressController.onPageLoad(ua.id, NormalMode))
    case SafetyAndSecurityConsignorAddressPage => ua => Some(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(ua.id, NormalMode))
    case AddSafetyAndSecurityConsigneePage => ua => Some(addSafetyAndSecurityConsigneeRoute(ua, NormalMode))
    case AddSafetyAndSecurityConsigneeEoriPage => ua => Some(addSafetyAndSecurityConsigneeEoriRoute(ua, NormalMode))
    case SafetyAndSecurityConsigneeEoriPage => ua => Some(routes.AddCarrierController.onPageLoad(ua.id, NormalMode))
    case SafetyAndSecurityConsigneeNamePage => ua => Some(routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(ua.id, NormalMode))
    case SafetyAndSecurityConsigneeAddressPage => ua => Some(routes.AddCarrierController.onPageLoad(ua.id, NormalMode))
    case AddCarrierPage => ua => Some(addCarrierRoute(ua, NormalMode))
    case AddCarrierEoriPage => ua => Some(addCarrierEoriRoute(ua, NormalMode))
    case CarrierEoriPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)) // CYA not implemented
    case CarrierNamePage => ua => Some(routes.CarrierAddressController.onPageLoad(ua.id, NormalMode))
    case CarrierAddressPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSafetyAndSecurityConsignorPage => ua => Some(addSafetyAndSecurityConsignorRoute(ua, CheckMode))
    case AddSafetyAndSecurityConsignorEoriPage=> ua => Some(addSafetyAndSecurityConsignorEoriRoute(ua, CheckMode))
    case SafetyAndSecurityConsignorEoriPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case SafetyAndSecurityConsignorNamePage => ua => Some(safetyAndSecurityConsignorNameRoute(ua))
    case SafetyAndSecurityConsignorAddressPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddSafetyAndSecurityConsigneePage => ua => Some(addSafetyAndSecurityConsigneeRoute(ua, CheckMode))
    case AddSafetyAndSecurityConsigneeEoriPage => ua => Some(addSafetyAndSecurityConsigneeEoriRoute(ua, CheckMode))
    case SafetyAndSecurityConsigneeEoriPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case SafetyAndSecurityConsigneeNamePage => ua => Some(safetyAndSecurityConsigneeNameRoute(ua))
    case SafetyAndSecurityConsigneeAddressPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddCarrierPage => ua => Some(addCarrierRoute(ua, CheckMode))
    case AddCarrierEoriPage => ua => Some(addCarrierEoriRoute(ua, CheckMode))
    case CarrierNamePage => ua => Some(carrierNameRoute(ua))
    case CarrierEoriPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case CarrierAddressPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
  }

    private def safetyAndSecurityConsignorNameRoute(ua:UserAnswers) =
    ua.get(SafetyAndSecurityConsignorAddressPage) match {
      case Some(_) =>  routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case None => routes.SafetyAndSecurityConsignorAddressController.onPageLoad(ua.id, CheckMode)
    }

  private def safetyAndSecurityConsigneeNameRoute(ua:UserAnswers) =
    ua.get(SafetyAndSecurityConsigneeAddressPage) match {
      case Some(_) =>  routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case None => routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(ua.id, CheckMode)
    }

    private def addSafetyAndSecurityConsignorRoute(ua: UserAnswers, mode:Mode): Call =
    (ua.get(AddSafetyAndSecurityConsignorPage), ua.get(AddSafetyAndSecurityConsignorEoriPage), mode) match {
      case (Some(true), _,NormalMode)   => routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false),_, NormalMode)  => routes.AddSafetyAndSecurityConsigneeController.onPageLoad(ua.id, NormalMode)
      case (Some(false), _, CheckMode) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true), None, CheckMode) => routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(ua.id, CheckMode)
      case (Some(true), Some(_), CheckMode) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def addSafetyAndSecurityConsignorEoriRoute(ua: UserAnswers, mode:Mode): Call =
    (ua.get(AddSafetyAndSecurityConsignorEoriPage), mode) match {
      case (Some(true),  NormalMode)   => routes.SafetyAndSecurityConsignorEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false),  NormalMode)  => routes.SafetyAndSecurityConsignorNameController.onPageLoad(ua.id, NormalMode)
      case (Some(true),  CheckMode) if ua.get(SafetyAndSecurityConsignorEoriPage).isDefined =>
        routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true),  CheckMode)  => routes.SafetyAndSecurityConsignorEoriController.onPageLoad(ua.id,CheckMode)
      case (Some(false),  CheckMode) if ua.get(SafetyAndSecurityConsignorNamePage).isDefined =>
        routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(false),  CheckMode)  => routes.SafetyAndSecurityConsignorNameController.onPageLoad(ua.id,CheckMode)
    }

  private def addSafetyAndSecurityConsigneeRoute(ua: UserAnswers, mode:Mode): Call =
    (ua.get(AddSafetyAndSecurityConsigneePage), ua.get(AddSafetyAndSecurityConsigneeEoriPage), mode) match {
      case (Some(true),_,NormalMode)   => routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false),_, NormalMode)  => routes.AddCarrierController.onPageLoad(ua.id, NormalMode)
      case (Some(false), _, CheckMode) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true), None, CheckMode) => routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(ua.id, CheckMode)
      case (Some(true), Some(_), CheckMode) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def addSafetyAndSecurityConsigneeEoriRoute(ua: UserAnswers, mode:Mode): Call =
    (ua.get(AddSafetyAndSecurityConsigneeEoriPage), mode) match {
      case (Some(true),  NormalMode)   => routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false),  NormalMode)  => routes.SafetyAndSecurityConsigneeNameController.onPageLoad(ua.id, NormalMode)
      case (Some(true),  CheckMode) if ua.get(SafetyAndSecurityConsigneeEoriPage).isDefined =>
        routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true),  CheckMode)  => routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(ua.id,CheckMode)
      case (Some(false),  CheckMode) if ua.get(SafetyAndSecurityConsigneeNamePage).isDefined =>
        routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(false),  CheckMode)  => routes.SafetyAndSecurityConsigneeNameController.onPageLoad(ua.id,CheckMode)
    }

  private def addCarrierRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddCarrierPage), mode) match {
      case (Some(true), NormalMode)   => routes.AddCarrierEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false), NormalMode)  => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true), CheckMode) if ua.get(AddCarrierEoriPage).isDefined => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true), CheckMode) => routes.AddCarrierEoriController.onPageLoad(ua.id, CheckMode)
      case (Some(false), CheckMode) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def addCarrierEoriRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddCarrierEoriPage), mode) match {
      case (Some(true), NormalMode)   => routes.CarrierEoriController.onPageLoad(ua.id, NormalMode)
      case (Some(false), NormalMode)  => routes.CarrierNameController.onPageLoad(ua.id, NormalMode)
      case (Some(true), CheckMode) if ua.get(CarrierEoriPage).isDefined => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(true), CheckMode) => routes.CarrierEoriController.onPageLoad(ua.id, CheckMode)
      case (Some(false), CheckMode) if ua.get(CarrierNamePage).isDefined => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case (Some(false), CheckMode) => routes.CarrierNameController.onPageLoad(ua.id, CheckMode)
    }

  private def carrierNameRoute(ua: UserAnswers): Call =
    ua.get(CarrierAddressPage) match {
      case Some(_) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case None => routes.CarrierAddressController.onPageLoad(ua.id, CheckMode)
    }
}
