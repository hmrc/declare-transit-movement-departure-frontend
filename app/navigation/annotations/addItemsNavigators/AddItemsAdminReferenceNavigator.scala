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

import controllers.addItems.previousReferences.{routes => previousReferencesRoutes}
import controllers.addItems.{routes => addItemsRoutes}
import derivable._
import models._
import navigation.Navigator
import pages._
import pages.addItems._
import pages.safetyAndSecurity.{AddCommercialReferenceNumberAllItemsPage, AddTransportChargesPaymentMethodPage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsAdminReferenceNavigator @Inject() () extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, _) => ua => Some(removePreviousAdministrativeReference(itemIndex, NormalMode)(ua))
    case AddAdministrativeReferencePage(itemIndex)                      => ua => addAdministrativeReferencePageNormalMode(itemIndex, ua)
    case ReferenceTypePage(itemIndex, referenceIndex) =>
      ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.lrn, itemIndex, referenceIndex, NormalMode))
    case PreviousReferencePage(itemIndex, referenceIndex) =>
      ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.lrn, itemIndex, referenceIndex, NormalMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, NormalMode)
    case ExtraInformationPage(itemIndex, referenceIndex) =>
      ua => Some(previousReferencesRoutes.ReferenceCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex, referenceIndex, NormalMode))
    case AddAnotherPreviousAdministrativeReferencePage(itemIndex) => ua => addAnotherPreviousAdministrativeReferenceNormalModeRoute(itemIndex, ua)
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddAdministrativeReferencePage(itemIndex) => ua => addAdministrativeReferencePageCheckMode(itemIndex, ua)
    case ReferenceTypePage(itemIndex, referenceIndex) =>
      ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.lrn, itemIndex, referenceIndex, CheckMode))
    case PreviousReferencePage(itemIndex, referenceIndex) =>
      ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.lrn, itemIndex, referenceIndex, CheckMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, CheckMode)
    case ExtraInformationPage(itemIndex, referenceIndex) =>
      ua => Some(previousReferencesRoutes.ReferenceCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex, referenceIndex, CheckMode))
    case ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, _) => ua => Some(removePreviousAdministrativeReference(itemIndex, CheckMode)(ua))
    case AddAnotherPreviousAdministrativeReferencePage(itemIndex)       => ua => addAnotherPreviousAdministrativeReferenceCheckModeRoute(itemIndex, ua)
  }

  private def addAdministrativeReferencePageNormalMode(itemIndex: Index, ua: UserAnswers): Option[Call] = {
    val referenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAdministrativeReferencePage(itemIndex)) match {
      case Some(true)  => Some(previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.lrn, itemIndex, Index(referenceIndex), NormalMode))
      case Some(false) => matchSecurityDetailsAndAddTransportCharges(itemIndex, ua)
      case None        => None
    }
  }

  private def matchSecurityDetailsAndAddTransportCharges(itemIndex: Index, ua: UserAnswers): Option[Call] =
    (ua.get(AddSecurityDetailsPage), ua.get(AddTransportChargesPaymentMethodPage)) match {
      case (Some(true), Some(false)) => Some(controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(ua.lrn, itemIndex, NormalMode))
      case (Some(true), Some(true))  => addCommercialRefNumberAllItems(itemIndex, ua)
      case _                         => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
    }

  private def addCommercialRefNumberAllItems(itemIndex: Index, ua: UserAnswers): Option[Call] =
    ua.get(AddCommercialReferenceNumberAllItemsPage) map {
      case true  => controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(ua.lrn, itemIndex, NormalMode)
      case false => controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(ua.lrn, itemIndex, NormalMode)
    }

  private def addAdministrativeReferencePageCheckMode(itemIndex: Index, ua: UserAnswers): Option[Call] = {
    val referenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAdministrativeReferencePage(itemIndex)) map {
      case true  => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.lrn, itemIndex, Index(referenceIndex), CheckMode)
      case false => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex)
    }
  }

  private def securityDetailsAndTransportCharges(itemIndex: Index, ua: UserAnswers) =
    (ua.get(AddSecurityDetailsPage), ua.get(AddTransportChargesPaymentMethodPage)) match {
      case (Some(true), Some(false)) => controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(ua.lrn, itemIndex, NormalMode)
      case (Some(true), Some(true))  => addReferenceNumberAllItems(itemIndex, ua)
      case _                         => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex)
    }

  private def addAnotherPreviousAdministrativeReferenceNormalModeRoute(itemIndex: Index, ua: UserAnswers): Option[Call] = {
    val newReferenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAnotherPreviousAdministrativeReferencePage(itemIndex)) map {
      case true  => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.lrn, itemIndex, Index(newReferenceIndex), NormalMode)
      case false => securityDetailsAndTransportCharges(itemIndex, ua)
    }
  }

  private def addAnotherPreviousAdministrativeReferenceCheckModeRoute(itemIndex: Index, ua: UserAnswers): Option[Call] = {
    val newReferenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAnotherPreviousAdministrativeReferencePage(itemIndex)) map {
      case true  => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.lrn, itemIndex, Index(newReferenceIndex), CheckMode)
      case false => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex)
    }
  }

  private def addReferenceNumberAllItems(itemIndex: Index, ua: UserAnswers) =
    ua.get(AddCommercialReferenceNumberAllItemsPage) match {
      case Some(true)  => controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(ua.lrn, itemIndex, NormalMode)
      case Some(false) => controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(ua.lrn, itemIndex, NormalMode)
      case _           => controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(ua.lrn, itemIndex, NormalMode)
    }

  private def addExtraInformationPage(ua: UserAnswers, itemIndex: Index, referenceIndex: Index, mode: Mode): Option[Call] =
    ua.get(AddExtraInformationPage(itemIndex, referenceIndex)) map {
      case true =>
        previousReferencesRoutes.ExtraInformationController.onPageLoad(ua.lrn, itemIndex, referenceIndex, mode)
      case false =>
        previousReferencesRoutes.ReferenceCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex, referenceIndex, mode)
    }

  private def removePreviousAdministrativeReference(itemIndex: Index, mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)) match {
      case None | Some(0) => previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(ua.lrn, itemIndex, mode)
      case _              => previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.lrn, itemIndex, mode)
    }

}
