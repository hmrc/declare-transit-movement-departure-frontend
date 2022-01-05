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

import controllers.addItems.traderDetails.{routes => traderDetailsRoutes}
import controllers.addItems.{routes => addItemsRoutes}
import controllers.routes
import models._
import navigation.Navigator
import pages._
import pages.addItems.{CommodityCodePage, ConfirmStartAddItemsPage}
import pages.traderDetails.{AddConsigneePage, AddConsignorPage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsItemDetailsNavigator @Inject() () extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ConfirmStartAddItemsPage        => ua => confirmStartAddItemsRoute(ua)
    case ItemDescriptionPage(index)      => ua => Some(controllers.addItems.itemDetails.routes.ItemTotalGrossMassController.onPageLoad(ua.lrn, index, NormalMode))
    case ItemTotalGrossMassPage(index)   => ua => Some(controllers.addItems.itemDetails.routes.AddTotalNetMassController.onPageLoad(ua.lrn, index, NormalMode))
    case AddTotalNetMassPage(index)      => ua => addTotalNetMassRouteNormalMode(index, ua)
    case TotalNetMassPage(index)         => ua => Some(controllers.addItems.itemDetails.routes.IsCommodityCodeKnownController.onPageLoad(ua.lrn, index, NormalMode))
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRouteNormalMode(index, ua)
    case CommodityCodePage(index)        => ua => commodityCodeRouteNormalMode(index, ua)
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index)      => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case ItemTotalGrossMassPage(index)   => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case AddTotalNetMassPage(index)      => ua => addTotalNetMassRouteCheckMode(index, ua)
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRouteCheckMode(index, ua)
    case CommodityCodePage(index)        => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case TotalNetMassPage(index)         => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
  }

  private def confirmStartAddItemsRoute(ua: UserAnswers) =
    ua.get(ConfirmStartAddItemsPage) match {
      case Some(true) => Some(controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(ua.lrn, Index(0), NormalMode))
      case _          => Some(routes.DeclarationSummaryController.onPageLoad(ua.lrn))
    }

  private def addTotalNetMassRouteNormalMode(index: Index, ua: UserAnswers) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index))) match {
      case (Some(false), _)   => Some(controllers.addItems.itemDetails.routes.IsCommodityCodeKnownController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(true), None) => Some(controllers.addItems.itemDetails.routes.TotalNetMassController.onPageLoad(ua.lrn, index, NormalMode))
      case _                  => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

  private def addTotalNetMassRouteCheckMode(index: Index, ua: UserAnswers) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index))) match {
      case (Some(true), None) => Some(controllers.addItems.itemDetails.routes.TotalNetMassController.onPageLoad(ua.lrn, index, CheckMode))
      case _                  => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

  private def isCommodityKnownRouteNormalMode(index: Index, ua: UserAnswers) =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(AddConsignorPage), ua.get(AddConsigneePage)) match {
      case (Some(true), _, _)                     => Some(controllers.addItems.itemDetails.routes.CommodityCodeController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(false), Some(false), _)          => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(false), Some(true), Some(false)) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(false), Some(true), Some(true)) =>
        Some(controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(ua.lrn, index, Index(0), NormalMode))
      case _ => None
    }

  private def commodityCodeRouteNormalMode(index: Index, ua: UserAnswers) =
    (ua.get(AddConsignorPage), ua.get(AddConsigneePage)) match {
      case (Some(false), _)          => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(true), Some(false)) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(true), Some(true)) =>
        Some(controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(ua.lrn, index, Index(0), NormalMode))
      case _ => None
    }

  private def isCommodityKnownRouteCheckMode(index: Index, ua: UserAnswers) =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(CommodityCodePage(index))) match {
      case (Some(true), None)    => Some(controllers.addItems.itemDetails.routes.CommodityCodeController.onPageLoad(ua.lrn, index, CheckMode))
      case (Some(true), Some(_)) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case (Some(false), _)      => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case _                     => None
    }
}
