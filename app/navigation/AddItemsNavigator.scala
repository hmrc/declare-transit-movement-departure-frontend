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

import cats.implicits._
import controllers.addItems.containers.{routes => containerRoutes}
import controllers.addItems.previousReferences.{routes => previousReferencesRoutes}
import controllers.addItems.specialMentions.{routes => specialMentionsRoutes}
import controllers.addItems.traderDetails.{routes => traderDetailsRoutes}
import controllers.addItems.{routes => addItemsRoutes}
import controllers.{routes => mainRoutes}
import derivable._
import models._
import models.reference.PackageType.{bulkCodes, unpackedCodes}
import pages._
import pages.addItems.containers._
import pages.addItems.traderDetails._
import pages.addItems._
import pages.safetyAndSecurity.{AddCommercialReferenceNumberAllItemsPage, AddTransportChargesPaymentMethodPage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsNavigator @Inject() () extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case AddAnotherItemPage => ua => Some(addAnotherItemRoute(ua))

  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case AddAnotherItemPage => ua => Some(addAnotherItemRoute(ua))

  }

  private def addAnotherItemRoute(userAnswers: UserAnswers): Call = {
    val count = userAnswers.get(DeriveNumberOfItems).getOrElse(0)
    userAnswers.get(AddAnotherItemPage) match {
      case Some(true) => addItemsRoutes.ItemDescriptionController.onPageLoad(userAnswers.lrn, Index(count), NormalMode)
      case _ => mainRoutes.DeclarationSummaryController.onPageLoad(userAnswers.lrn)
    }
  }

  private def removeItem(mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfItems) match {
      case None | Some(0) => addItemsRoutes.ItemDescriptionController.onPageLoad(ua.lrn, Index(0), mode)
      case _ => addItemsRoutes.AddAnotherItemController.onPageLoad(ua.lrn)
    }



  // format: on
}
