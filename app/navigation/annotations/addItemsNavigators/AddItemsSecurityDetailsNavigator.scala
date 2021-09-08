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

package navigation.annotations.addItemsNavigators

import controllers.addItems.securityDetails.routes
import models._
import models.reference.{CountryCode, CustomsOffice}
import navigation.Navigator
import pages.addItems.securityDetails._
import pages.safetyAndSecurity.{
  AddCommercialReferenceNumberAllItemsPage,
  AddSafetyAndSecurityConsigneePage,
  AddSafetyAndSecurityConsignorPage,
  CircumstanceIndicatorPage
}
import pages.{OfficeOfDeparturePage, Page}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsSecurityDetailsNavigator @Inject() () extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TransportChargesPage(index)          => ua => transportChargesRoute(ua, index)
    case CommercialReferenceNumberPage(index) => ua => Some(routes.AddDangerousGoodsCodeController.onPageLoad(ua.lrn, index, NormalMode))
    case AddDangerousGoodsCodePage(index)     => ua => addDangerousGoodsCodeNormalModeRoute(ua, index)
    case DangerousGoodsCodePage(index)        => ua => dangerousGoodsCodeRoute(ua, index, NormalMode)
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TransportChargesPage(index)          => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case CommercialReferenceNumberPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case AddDangerousGoodsCodePage(index)     => ua => addDangerousGoodsCodeCheckModeRoute(ua, index)
    case DangerousGoodsCodePage(index)        => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
  }

  private def addDangerousGoodsCodeNormalModeRoute(ua: UserAnswers, index: Index) =
    ua.get(AddDangerousGoodsCodePage(index)) match {
      case Some(true)  => Some(routes.DangerousGoodsCodeController.onPageLoad(ua.lrn, index, NormalMode))
      case Some(false) => dangerousGoodsCodeRoute(ua, index, NormalMode)
      case _           => None
    }

  private def addDangerousGoodsCodeCheckModeRoute(ua: UserAnswers, index: Index) =
    (ua.get(AddDangerousGoodsCodePage(index)), ua.get(DangerousGoodsCodePage(index))) match {
      case (Some(true), None) => Some(routes.DangerousGoodsCodeController.onPageLoad(ua.lrn, index, CheckMode))
      case (Some(_), _)       => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case _                  => None
    }

  private def transportChargesRoute(ua: UserAnswers, index: Index) =
    ua.get(AddCommercialReferenceNumberAllItemsPage) match {
      case Some(true) => Some(routes.AddDangerousGoodsCodeController.onPageLoad(ua.lrn, index, NormalMode))
      case _          => Some(routes.CommercialReferenceNumberController.onPageLoad(ua.lrn, index, NormalMode))
    }

  private def dangerousGoodsCodeRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddSafetyAndSecurityConsignorPage), ua.get(AddSafetyAndSecurityConsigneePage)) match {
      case (Some(true), Some(true))  => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case (Some(true), Some(false)) => Some(circumstanceIndicatorCheck(ua, index, mode))
      case (Some(false), _)          => consignorCircumstanceIndicatorCheck(ua, index, mode)
      case _                         => None
    }

  private def consignorCircumstanceIndicatorCheck(userAnswers: UserAnswers, index: Index, mode: Mode) =
    userAnswers.get(OfficeOfDeparturePage).map {
      case CustomsOffice(_, _, CountryCode("XI"), _) =>
        userAnswers.get(CircumstanceIndicatorPage) match {
          case Some("E") => controllers.addItems.traderSecurityDetails.routes.SecurityConsignorEoriController.onPageLoad(userAnswers.lrn, index, mode)
          case _         => controllers.addItems.traderSecurityDetails.routes.AddSecurityConsignorsEoriController.onPageLoad(userAnswers.lrn, index, mode)
        }
      case _ => controllers.addItems.traderSecurityDetails.routes.AddSecurityConsignorsEoriController.onPageLoad(userAnswers.lrn, index, mode)
    }

  private def circumstanceIndicatorCheck(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(CircumstanceIndicatorPage) match {
      case Some("E") => controllers.addItems.traderSecurityDetails.routes.SecurityConsigneeEoriController.onPageLoad(ua.lrn, index, mode)
      case _         => controllers.addItems.traderSecurityDetails.routes.AddSecurityConsigneesEoriController.onPageLoad(ua.lrn, index, mode)
    }

}
