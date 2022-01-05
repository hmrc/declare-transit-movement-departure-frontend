/*
 * Copyright 2022 HM Revenue & Customs
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

package navigation.annotations.addItemsNavigators

import controllers.addItems.traderSecurityDetails.routes
import models._
import navigation.Navigator
import pages.Page
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.{AddSafetyAndSecurityConsigneePage, CircumstanceIndicatorPage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsTradersSecurityDetailsNavigator @Inject() () extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSecurityConsignorsEoriPage(index) => ua => addSecurityConsignorsEoriNormalModeRoute(ua, index)
    case SecurityConsignorNamePage(index)     => ua => Some(routes.SecurityConsignorAddressController.onPageLoad(ua.lrn, index, NormalMode))
    case SecurityConsignorEoriPage(index)     => ua => securityConsignorEoriRoute(ua, index, NormalMode)
    case SecurityConsignorAddressPage(index)  => ua => securityConsignorEoriRoute(ua, index, NormalMode)
    case AddSecurityConsigneesEoriPage(index) => ua => addSecurityConsigneesEoriNormalModeRoute(ua, index)
    case SecurityConsigneeNamePage(index)     => ua => Some(routes.SecurityConsigneeAddressController.onPageLoad(ua.lrn, index, NormalMode))
    case SecurityConsigneeAddressPage(index)  => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case SecurityConsigneeEoriPage(index)     => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSecurityConsignorsEoriPage(index) => ua => addSecurityConsignorsEoriCheckModeRoute(ua, index)
    case SecurityConsignorEoriPage(index)     => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case SecurityConsignorNamePage(index)     => ua => securityConsignorNameRoute(ua, index)
    case SecurityConsignorAddressPage(index)  => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case AddSecurityConsigneesEoriPage(index) => ua => addSecurityConsigneesEoriCheckModeRoute(ua, index)
    case SecurityConsigneeEoriPage(index)     => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case SecurityConsigneeAddressPage(index)  => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
  }

  private def securityConsignorNameRoute(ua: UserAnswers, index: Index) =
    ua.get(SecurityConsignorAddressPage(index)) match {
      case Some(_) => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case None    => Some(routes.SecurityConsignorAddressController.onPageLoad(ua.lrn, index, CheckMode))
    }

  private def securityConsignorEoriRoute(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(AddSafetyAndSecurityConsigneePage) map {
      case true =>
        controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index)
      case false => circumstanceIndicatorCheck(ua, index, mode)
    }

  private def addSecurityConsignorsEoriNormalModeRoute(ua: UserAnswers, index: Index) =
    ua.get(AddSecurityConsignorsEoriPage(index)) map {
      case true =>
        routes.SecurityConsignorEoriController.onPageLoad(ua.lrn, index, NormalMode)
      case false =>
        routes.SecurityConsignorNameController.onPageLoad(ua.lrn, index, NormalMode)
    }

  private def addSecurityConsignorsEoriCheckModeRoute(ua: UserAnswers, index: Index) =
    ua.get(AddSecurityConsignorsEoriPage(index)) match {

      case Some(true) if ua.get(SecurityConsignorEoriPage(index)).isDefined =>
        Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case Some(true) => Some(routes.SecurityConsignorEoriController.onPageLoad(ua.lrn, index, CheckMode))
      case Some(false) if ua.get(SecurityConsignorNamePage(index)).isDefined =>
        Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case Some(false) => Some(routes.SecurityConsignorNameController.onPageLoad(ua.lrn, index, CheckMode))
    }

  private def addSecurityConsigneesEoriNormalModeRoute(ua: UserAnswers, index: Index) =
    ua.get(AddSecurityConsigneesEoriPage(index)) map {
      case true =>
        routes.SecurityConsigneeEoriController.onPageLoad(ua.lrn, index, NormalMode)
      case false =>
        routes.SecurityConsigneeNameController.onPageLoad(ua.lrn, index, NormalMode)
    }

  private def addSecurityConsigneesEoriCheckModeRoute(ua: UserAnswers, index: Index) =
    ua.get(AddSecurityConsigneesEoriPage(index)) match {
      case Some(true) if ua.get(SecurityConsigneeEoriPage(index)).isDefined =>
        Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case Some(true) => Some(routes.SecurityConsigneeEoriController.onPageLoad(ua.lrn, index, CheckMode))
      case Some(false) if ua.get(SecurityConsigneeNamePage(index)).isDefined =>
        Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case Some(false) => Some(routes.SecurityConsigneeNameController.onPageLoad(ua.lrn, index, CheckMode))
    }

  private def circumstanceIndicatorCheck(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(CircumstanceIndicatorPage) match {
      case Some("E") => controllers.addItems.traderSecurityDetails.routes.SecurityConsigneeEoriController.onPageLoad(ua.lrn, index, mode)
      case _         => controllers.addItems.traderSecurityDetails.routes.AddSecurityConsigneesEoriController.onPageLoad(ua.lrn, index, mode)
    }
}
