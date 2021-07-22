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

import controllers.addItems.securityDetails.routes

import javax.inject.{Inject, Singleton}
import models._
import models.reference.{CountryCode, CustomsOffice}
import pages.{OfficeOfDeparturePage, Page}
import pages.addItems.securityDetails._
import pages.safetyAndSecurity.{
  AddCommercialReferenceNumberAllItemsPage,
  AddSafetyAndSecurityConsigneePage,
  AddSafetyAndSecurityConsignorPage,
  CircumstanceIndicatorPage
}
import play.api.mvc.Call

@Singleton
class SecurityDetailsNavigator @Inject() () extends Navigator {

  // format: off
  //todo -update when Safety and Security section built
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TransportChargesPage(index) => ua => transportChargesRoute(ua, index, NormalMode)
    case CommercialReferenceNumberPage(index) => ua => Some(routes.AddDangerousGoodsCodeController.onPageLoad(ua.lrn, index, NormalMode))
    case AddDangerousGoodsCodePage(index) => ua => addDangerousGoodsCodeRoute(ua, index, NormalMode)
    case DangerousGoodsCodePage(index) => ua => dangerousGoodsCodeRoute(ua, index, NormalMode)
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TransportChargesPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case CommercialReferenceNumberPage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case AddDangerousGoodsCodePage(index) => ua => addDangerousGoodsCodeRoute(ua, index, CheckMode)
    case DangerousGoodsCodePage(index) => ua => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
  }

   private def addDangerousGoodsCodeRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddDangerousGoodsCodePage(index)), ua.get(DangerousGoodsCodePage(index)), mode) match {
      case (Some(true), _, NormalMode) => Some(routes.DangerousGoodsCodeController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(false), _, NormalMode) => dangerousGoodsCodeRoute(ua, index, mode)
      case (Some(true), None, CheckMode)    => Some(routes.DangerousGoodsCodeController.onPageLoad(ua.lrn, index, CheckMode))
      case (Some(_), _, CheckMode) => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

  private def transportChargesRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddCommercialReferenceNumberAllItemsPage), mode) match {
      case (Some(true), NormalMode) => Some(routes.AddDangerousGoodsCodeController.onPageLoad(ua.lrn, index, NormalMode))
      case (_, NormalMode) => Some(routes.CommercialReferenceNumberController.onPageLoad(ua.lrn, index, NormalMode))
    }

  private def dangerousGoodsCodeRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddSafetyAndSecurityConsignorPage), ua.get(AddSafetyAndSecurityConsigneePage)) match {
      case (Some(true), Some(true)) => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case (Some(true), Some(false)) => Some(circumstanceIndicatorCheck(ua, index, mode))
      case (Some(false), _) => consignorCircumstanceIndicatorCheck(ua, index, mode)
    }

  def consignorCircumstanceIndicatorCheck(userAnswers: UserAnswers, index:Index, mode: Mode) = {
    userAnswers.get(OfficeOfDeparturePage).map {
      case CustomsOffice(_, _, CountryCode("XI"), _)=> userAnswers.get(CircumstanceIndicatorPage) match {
        case Some("E") => controllers.addItems.traderSecurityDetails.routes.SecurityConsignorEoriController.onPageLoad(userAnswers.lrn,index, mode)
        case _ => controllers.addItems.traderSecurityDetails.routes.AddSecurityConsignorsEoriController.onPageLoad(userAnswers.lrn,index, mode)
      }
      case _ => controllers.addItems.traderSecurityDetails.routes.AddSecurityConsignorsEoriController.onPageLoad(userAnswers.lrn,index, mode)
    }
  }

  def circumstanceIndicatorCheck(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(CircumstanceIndicatorPage) match {
      case Some("E") => controllers.addItems.traderSecurityDetails.routes.SecurityConsigneeEoriController.onPageLoad(ua.lrn, index, mode)
      case _         => controllers.addItems.traderSecurityDetails.routes.AddSecurityConsigneesEoriController.onPageLoad(ua.lrn, index, mode)
    }
  // format: on
}
