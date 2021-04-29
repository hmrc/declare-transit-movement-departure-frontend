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

import controllers.addItems.traderSecurityDetails.routes
import models._
import pages.Page
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.{AddSafetyAndSecurityConsigneePage, CircumstanceIndicatorPage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class TradersSecurityDetailsNavigator @Inject()() extends Navigator {

  // format: off
  //todo -update when Security Trader Details section done
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSecurityConsignorsEoriPage(index) => ua => addSecurityConsignorsEoriRoute(ua, index, NormalMode)
    case SecurityConsignorNamePage(index) => ua => Some(routes.SecurityConsignorAddressController.onPageLoad(ua.id, index, NormalMode))
    case SecurityConsignorEoriPage(index) => ua => securityConsignorEoriRoute(ua, index, NormalMode)
    case SecurityConsignorAddressPage(index) => ua => securityConsignorEoriRoute(ua, index, NormalMode)
    case AddSecurityConsigneesEoriPage(index) => ua => addSecurityConsigneesEoriRoute(ua, index, NormalMode)
    case SecurityConsigneeNamePage(index) => ua => Some(routes.SecurityConsigneeAddressController.onPageLoad(ua.id, index, NormalMode))
    case SecurityConsigneeAddressPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case SecurityConsigneeEoriPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSecurityConsignorsEoriPage(index) => ua => addSecurityConsignorsEoriRoute(ua, index, CheckMode)
    case SecurityConsignorEoriPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case SecurityConsignorNamePage(index) => ua => securityConsignorNameRoute(ua, index)
    case SecurityConsignorAddressPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddSecurityConsigneesEoriPage(index) => ua => addSecurityConsigneesEoriRoute(ua, index, CheckMode)
    case SecurityConsigneeEoriPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case SecurityConsigneeAddressPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
  }

  private def securityConsignorNameRoute(ua: UserAnswers, index: Index) = 
    ua.get(SecurityConsignorAddressPage(index)) match {
      case Some(_) => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case None => Some(routes.SecurityConsignorAddressController.onPageLoad(ua.id, index, CheckMode))
    }
  
  private def securityConsignorEoriRoute(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(AddSafetyAndSecurityConsigneePage) match {
      case Some(true) =>
        Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case Some(false) => Some(circumstanceIndicatorCheck(ua, index, mode))
    }

  private def addSecurityConsignorsEoriRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddSecurityConsignorsEoriPage(index)), mode) match {
      case (Some(true), NormalMode) =>
        Some(routes.SecurityConsignorEoriController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), NormalMode) =>
        Some(routes.SecurityConsignorNameController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), CheckMode) if (ua.get(SecurityConsignorEoriPage(index)).isDefined)
      => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(true), CheckMode) => Some(routes.SecurityConsignorEoriController.onPageLoad(ua.id, index, CheckMode))
      case (Some(false), CheckMode) if (ua.get(SecurityConsignorNamePage(index)).isDefined)
      => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false), CheckMode) => Some(routes.SecurityConsignorNameController.onPageLoad(ua.id, index, CheckMode))
    }

  private def addSecurityConsigneesEoriRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddSecurityConsigneesEoriPage(index)), mode) match {
      case (Some(true), NormalMode) =>
        Some(routes.SecurityConsigneeEoriController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), NormalMode) =>
        Some(routes.SecurityConsigneeNameController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), CheckMode) if (ua.get(SecurityConsigneeEoriPage(index)).isDefined)
      => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(true), CheckMode) => Some(routes.SecurityConsigneeEoriController.onPageLoad(ua.id, index, CheckMode))
      case (Some(false), CheckMode) if (ua.get(SecurityConsigneeNamePage(index)).isDefined)
      => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false), CheckMode) => Some(routes.SecurityConsigneeNameController.onPageLoad(ua.id, index, CheckMode))
    }

  def circumstanceIndicatorCheck(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(CircumstanceIndicatorPage) match {
      case Some("E") => controllers.addItems.traderSecurityDetails.routes.SecurityConsigneeEoriController.onPageLoad(ua.id, index, mode)
      case _         => controllers.addItems.traderSecurityDetails.routes.AddSecurityConsigneesEoriController.onPageLoad(ua.id, index, mode)
    }
  // format: on
}
