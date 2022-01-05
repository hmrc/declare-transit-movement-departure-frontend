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
import controllers.{routes => mainRoutes}
import models._
import navigation.Navigator
import pages._
import pages.addItems.traderDetails._
import pages.traderDetails.AddConsigneePage
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsTraderDetailsNavigator @Inject() () extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TraderDetailsConsignorEoriKnownPage(index) => ua => consignorEoriKnownNormalMode(ua, index)
    case TraderDetailsConsignorEoriNumberPage(index) =>
      ua => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.lrn, index, NormalMode))
    case TraderDetailsConsignorNamePage(index)      => ua => Some(traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(ua.lrn, index, NormalMode))
    case TraderDetailsConsignorAddressPage(index)   => ua => consignorAddressNormalMode(ua, index)
    case TraderDetailsConsigneeEoriKnownPage(index) => ua => consigneeEoriKnownNormalMode(ua, index)
    case TraderDetailsConsigneeEoriNumberPage(index) =>
      ua => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.lrn, index, NormalMode))
    case TraderDetailsConsigneeNamePage(index) => ua => Some(traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(ua.lrn, index, NormalMode))
    case TraderDetailsConsigneeAddressPage(index) =>
      ua => Some(controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(ua.lrn, index, Index(0), NormalMode))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TraderDetailsConsignorEoriKnownPage(index)  => ua => consignorEoriKnownCheckMode(ua, index)
    case TraderDetailsConsignorEoriNumberPage(index) => ua => consignorEoriNumberCheckMode(ua, index)
    case TraderDetailsConsignorNamePage(index)       => ua => consignorName(ua, index)
    case TraderDetailsConsignorAddressPage(index)    => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case TraderDetailsConsigneeEoriKnownPage(index)  => ua => consigneeEoriKnownCheckMode(ua, index)
    case TraderDetailsConsigneeEoriNumberPage(index) => ua => consigneeEoriNumberCheckMode(ua, index)
    case TraderDetailsConsigneeNamePage(index)       => ua => consigneeName(ua, index)
    case TraderDetailsConsigneeAddressPage(index)    => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
  }

  private def consigneeName(ua: UserAnswers, index: Index) =
    ua.get(TraderDetailsConsigneeAddressPage(index)) match {
      case Some(_) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case _       => Some(traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(ua.lrn, index, CheckMode))
    }

  private def consigneeEoriNumberCheckMode(ua: UserAnswers, index: Index) =
    ua.get(TraderDetailsConsigneeNamePage(index)) match {
      case Some(_) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case _       => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.lrn, index, CheckMode))
    }

  private def consigneeEoriKnownNormalMode(ua: UserAnswers, index: Index) =
    (ua.get(TraderDetailsConsigneeEoriKnownPage(index)),
     ua.get(TraderDetailsConsigneeEoriNumberPage(index)),
     ua.get(TraderDetailsConsigneeNamePage(index))
    ) match {
      case (Some(true), _, _)  => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(false), _, _) => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.lrn, index, NormalMode))
      case _                   => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

  private def consigneeEoriKnownCheckMode(ua: UserAnswers, index: Index) =
    (ua.get(TraderDetailsConsigneeEoriKnownPage(index)),
     ua.get(TraderDetailsConsigneeEoriNumberPage(index)),
     ua.get(TraderDetailsConsigneeNamePage(index))
    ) match {
      case (Some(true), None, _)  => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(ua.lrn, index, CheckMode))
      case (Some(false), _, None) => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.lrn, index, CheckMode))
      case _                      => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

  private def consignorAddressNormalMode(ua: UserAnswers, index: Index) =
    ua.get(AddConsigneePage).map {
      case false => traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.lrn, index, NormalMode)
      case true  => controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(ua.lrn, index, Index(0), NormalMode)
    }

  private def consignorName(ua: UserAnswers, index: Index) =
    ua.get(TraderDetailsConsignorAddressPage(index)) match {
      case Some(_) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case _       => Some(traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(ua.lrn, index, CheckMode))
    }

  private def consignorEoriNumberCheckMode(ua: UserAnswers, index: Index) =
    ua.get(TraderDetailsConsignorNamePage(index)) match {
      case Some(_) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case _       => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.lrn, index, CheckMode))
    }

  private def consignorEoriKnownNormalMode(ua: UserAnswers, index: Index) =
    ua.get(TraderDetailsConsignorEoriKnownPage(index)) match {
      case Some(true)  => Some(traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(ua.lrn, index, NormalMode))
      case Some(false) => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.lrn, index, NormalMode))
      case _           => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def consignorEoriKnownCheckMode(ua: UserAnswers, index: Index) =
    (ua.get(TraderDetailsConsignorEoriKnownPage(index)),
     ua.get(TraderDetailsConsignorEoriNumberPage(index)),
     ua.get(TraderDetailsConsignorNamePage(index))
    ) match {
      case (Some(true), None, _)  => Some(traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(ua.lrn, index, CheckMode))
      case (Some(false), _, None) => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.lrn, index, CheckMode))
      case _                      => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

}
