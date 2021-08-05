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

    case ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, _) => ua => Some(removePreviousAdministrativeReference(itemIndex, NormalMode)(ua))
    case AddAdministrativeReferencePage(itemIndex) => ua => addAdministrativeReferencePage(itemIndex, ua, NormalMode)
    case ReferenceTypePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.lrn, itemIndex, referenceIndex, NormalMode))
    case PreviousReferencePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.lrn, itemIndex, referenceIndex, NormalMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, NormalMode)
    case ExtraInformationPage(itemIndex, _) => ua => Some(previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.lrn, itemIndex, NormalMode))
    case AddAnotherPreviousAdministrativeReferencePage(itemIndex) => ua => addAnotherPreviousAdministrativeReferenceRoute(itemIndex, ua, NormalMode)
    case ContainerNumberPage(itemIndex, _) => ua => Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.lrn, itemIndex, NormalMode))
    case AddAnotherContainerPage(itemIndex) => ua => Some(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(ua.lrn, itemIndex, NormalMode))
    case ConfirmRemoveContainerPage(index, _) => ua => Some(confirmRemoveContainerRoute(ua, index, NormalMode))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {


    case AddAdministrativeReferencePage(itemIndex) => ua => addAdministrativeReferencePage(itemIndex, ua, CheckMode)
    case ReferenceTypePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.lrn, itemIndex, referenceIndex, CheckMode))
    case PreviousReferencePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.lrn, itemIndex, referenceIndex, CheckMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, CheckMode)
    case ExtraInformationPage(itemIndex, _) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
    case ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, _) => ua => Some(removePreviousAdministrativeReference(itemIndex, CheckMode)(ua))
    case AddAnotherPreviousAdministrativeReferencePage(itemIndex) => ua => addAnotherPreviousAdministrativeReferenceRoute(itemIndex, ua, CheckMode)
    case ContainerNumberPage(itemIndex, _) => ua => Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.lrn, itemIndex, CheckMode))
    case AddAnotherContainerPage(itemIndex) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
    case ConfirmRemoveContainerPage(index, _) => ua => Some(confirmRemoveContainerRoute(ua, index, CheckMode))
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

  def packageType(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    ua.get(PackageTypePage(itemIndex, packageIndex)) match {
      case Some(packageType) if bulkCodes.contains(packageType.code) =>
        Some(addItemsRoutes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case Some(packageType) if unpackedCodes.contains(packageType.code) =>
        Some(addItemsRoutes.TotalPiecesController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case Some(_) => Some(addItemsRoutes.HowManyPackagesController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def howManyPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(HowManyPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(_), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(addItemsRoutes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case (Some(_), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(addItemsRoutes.TotalPiecesController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case (Some(_), Some(_)) => Some(addItemsRoutes.DeclareMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def declareNumberOfPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(DeclareNumberOfPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(true), _) => Some(addItemsRoutes.HowManyPackagesController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case (Some(false), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(addItemsRoutes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case (Some(false), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(addItemsRoutes.TotalPiecesController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addMark(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddMarkPage(itemIndex, packageIndex)), mode) match {
      case (Some(true), _) => Some(addItemsRoutes.DeclareMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, mode))
      case (Some(false), NormalMode) => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.lrn, itemIndex, mode))
      case (Some(false), CheckMode) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addAnotherPackage(itemIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddAnotherPackagePage(itemIndex)), ua.get(ContainersUsedPage), ua.get(DeriveNumberOfContainers(itemIndex)).getOrElse(0)) match {
      case (Some(true), _, _) =>
        val nextPackageIndex: Int = ua.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
        Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.lrn, itemIndex, Index(nextPackageIndex), mode))
      case (Some(false), Some(false), _) if mode == NormalMode =>
        Some(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(ua.lrn, itemIndex, mode))
      case (Some(false), _, 0) =>
        Some(containerRoutes.ContainerNumberController.onPageLoad(ua.lrn, itemIndex, Index(0), mode))
      case (Some(false), _, _) if mode == CheckMode =>
        Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
      case (Some(false), _, _) =>
        Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.lrn, itemIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def removePackage(itemIndex: Index, mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfPackages(itemIndex)) match {
      case None | Some(0) => addItemsRoutes.PackageTypeController.onPageLoad(ua.lrn, itemIndex, Index(0), mode)
      case _ => addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.lrn, itemIndex, mode)
    }

  private def addAdministrativeReferencePage(itemIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val referenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAdministrativeReferencePage(itemIndex)) map {
      case true => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.lrn, itemIndex, Index(referenceIndex), mode)
      case _ if mode == CheckMode => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex)
      case _ => securityDetailsAndTransportCharges(itemIndex, ua)
    }
  }

  private def securityDetailsAndTransportCharges(itemIndex: Index, ua: UserAnswers) = {
    (ua.get(AddSecurityDetailsPage), ua.get(AddTransportChargesPaymentMethodPage)) match {
      case (Some(true), Some(false)) => controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(ua.lrn, itemIndex, NormalMode)
      case (Some(true), Some(true)) => addReferenceNumberAllItems(itemIndex, ua)
      case _ => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex)
    }
  }

  private def addAnotherPreviousAdministrativeReferenceRoute(itemIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val newReferenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAnotherPreviousAdministrativeReferencePage(itemIndex)) map {
      case true => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.lrn, itemIndex, Index(newReferenceIndex), mode)
      case _ if mode == CheckMode => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex)
      case _ => securityDetailsAndTransportCharges(itemIndex, ua)
    }
  }

  private def addReferenceNumberAllItems(itemIndex: Index, ua: UserAnswers) = {
    ua.get(AddCommercialReferenceNumberAllItemsPage) match {
      case Some(true) => controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(ua.lrn, itemIndex, NormalMode)
      case Some(false) => controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(ua.lrn, itemIndex, NormalMode)
      case _ => controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(ua.lrn, itemIndex, NormalMode)
    }
  }

  private def addExtraInformationPage(ua: UserAnswers, itemIndex: Index, referenceIndex: Index, mode: Mode): Option[Call] =
    ua.get(AddExtraInformationPage(itemIndex, referenceIndex)) map {
      case true =>
        previousReferencesRoutes.ExtraInformationController.onPageLoad(ua.lrn, itemIndex, referenceIndex, mode)
      case false =>
        previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.lrn, itemIndex, mode)
    }

  private def removePreviousAdministrativeReference(itemIndex: Index, mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)) match {
      case None | Some(0) => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.lrn, itemIndex, Index(0), mode)
      case _ => previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.lrn, itemIndex, mode)
    }

  private def confirmRemoveContainerRoute(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(DeriveNumberOfContainers(index)).getOrElse(0) match {
      case 0 => containerRoutes.ContainerNumberController.onPageLoad(ua.lrn, index, Index(0), mode)
      case _ => containerRoutes.AddAnotherContainerController.onPageLoad(ua.lrn, index, mode)
    }

  // format: on
}
