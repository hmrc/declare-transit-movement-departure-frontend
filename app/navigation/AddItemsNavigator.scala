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

import controllers.addItems.previousReferences.{routes => previousReferencesRoutes}
import controllers.addItems.traderDetails.{routes => traderDetailsRoutes}
import controllers.addItems.specialMentions.{routes => specialMentionsRoutes}
import controllers.addItems.{routes => addItemsRoutes}
import controllers.addItems.{routes => addAnotherPackageRoutes}
import controllers.addItems.containers.{routes => containerRoutes}
import controllers.{routes => mainRoutes}
import derivable._
import javax.inject.{Inject, Singleton}
import models._
import models.reference.CountryCode
import models.reference.PackageType.{bulkAndUnpackedCodes, bulkCodes, unpackedCodes}
import pages._
import pages.addItems.containers._
import pages.addItems.traderDetails._
import pages.addItems.{AddAnotherPreviousAdministrativeReferencePage, _}
import pages.safetyAndSecurity.{AddCommercialReferenceNumberAllItemsPage, AddTransportChargesPaymentMethodPage, CommercialReferenceNumberAllItemsPage}
import play.api.mvc.Call

@Singleton
class AddItemsNavigator @Inject()() extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index) => ua => Some(addItemsRoutes.ItemTotalGrossMassController.onPageLoad(ua.id, index, NormalMode))
    case ItemTotalGrossMassPage(index) => ua => Some(addItemsRoutes.AddTotalNetMassController.onPageLoad(ua.id, index, NormalMode))
    case AddTotalNetMassPage(index) => ua => addTotalNetMassRoute(index, ua, NormalMode)
    case TotalNetMassPage(index) => ua => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRouteNormalMode(index, ua)
    case AddAnotherItemPage => ua => Some(addAnotherItemRoute(ua))
    case ConfirmRemoveItemPage => ua => Some(removeItem(NormalMode)(ua))
    case CommodityCodePage(index) => ua => commodityCodeRouteNormalMode(index, ua)
    case PackageTypePage(itemIndex, packageIndex) => ua => packageType(itemIndex, packageIndex, ua, NormalMode)
    case HowManyPackagesPage(itemIndex, packageIndex) => ua => howManyPackages(itemIndex, packageIndex, ua, NormalMode)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua, NormalMode)
    case TotalPiecesPage(itemIndex, packageIndex) => ua => Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
    case AddMarkPage(itemIndex, packageIndex) => ua => addMark(itemIndex, packageIndex, ua, NormalMode)
    case DeclareMarkPage(itemIndex, packageIndex) => ua => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherPackagePage(itemIndex) => ua => addAnotherPackage(itemIndex, ua, NormalMode)
    case TraderDetailsConsignorEoriKnownPage(index) => ua => consignorEoriKnown(ua, index, NormalMode)
    case TraderDetailsConsignorEoriNumberPage(index) => ua => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.id, index, NormalMode))
    case TraderDetailsConsignorNamePage(index) => ua => Some(traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(ua.id, index, NormalMode))
    case TraderDetailsConsignorAddressPage(index) => ua => consignorAddressNormalMode(ua, index)
    case TraderDetailsConsigneeEoriKnownPage(index) => ua => consigneeEoriKnown(ua, index, NormalMode)
    case TraderDetailsConsigneeEoriNumberPage(index) => ua => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.id, index, NormalMode))
    case TraderDetailsConsigneeNamePage(index) => ua => consigneeName(ua, index, NormalMode)
    case TraderDetailsConsigneeAddressPage(index) => ua => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), NormalMode))
    case DeclareMarkPage(itemIndex, _) => ua => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherPackagePage(itemIndex) => ua => addAnotherPackage(itemIndex, ua, NormalMode)
    case RemovePackagePage(itemIndex) => ua => Some(removePackage(itemIndex, NormalMode)(ua))
    case ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex) => ua => Some(removePreviousAdministrativeReference(itemIndex, NormalMode)(ua))
    case AddAdministrativeReferencePage(itemIndex) => ua => addAdministrativeReferencePage(itemIndex, ua, NormalMode)
    case ReferenceTypePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.id, itemIndex, referenceIndex, NormalMode))
    case PreviousReferencePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.id, itemIndex, referenceIndex, NormalMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, NormalMode)
    case ExtraInformationPage(itemIndex, _) => ua => Some(previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherPreviousAdministrativeReferencePage(itemIndex) => ua => addAnotherPreviousAdministrativeReferenceRoute(itemIndex, ua, NormalMode)
    case ContainerNumberPage(itemIndex, containerIndex) => ua => Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherContainerPage(itemIndex) => ua => Some(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(ua.id, itemIndex, NormalMode))
    case ConfirmRemoveContainerPage(index, _) => ua => Some(confirmRemoveContainerRoute(ua, index, NormalMode))
    case ContainerNumberPage(itemIndex, containerIndex) => ua => Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherContainerPage(itemIndex) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case ConfirmRemoveContainerPage(index, _) => ua => Some(confirmRemoveContainerRoute(ua, index, NormalMode))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index) => ua => addTotalNetMassRoute(index, ua, CheckMode)
    case IsCommodityCodeKnownPage(index) => ua => isCommodityKnownRouteCheckMode(index, ua)
    case CommodityCodePage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case TotalNetMassPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case PackageTypePage(itemIndex, packageIndex) => ua => packageType(itemIndex, packageIndex, ua, CheckMode)
    case HowManyPackagesPage(itemIndex, packageIndex) => ua => howManyPackages(itemIndex, packageIndex, ua, CheckMode)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua, CheckMode)
    case TotalPiecesPage(itemIndex, packageIndex) => ua => Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, CheckMode))
    case AddMarkPage(itemIndex, packageIndex) => ua => addMark(itemIndex, packageIndex, ua, CheckMode)
    case DeclareMarkPage(itemIndex, packageIndex) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case AddAnotherPackagePage(itemIndex) => ua => addAnotherPackage(itemIndex, ua, CheckMode)
    case ItemDescriptionPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index) => ua => addTotalNetMassRoute(index, ua, CheckMode)
    case TotalNetMassPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case TraderDetailsConsignorEoriKnownPage(index) => ua => consignorEoriKnown(ua, index, CheckMode)
    case TraderDetailsConsignorEoriNumberPage(index) => ua => consignorEoriNumberCheckMode(ua, index)
    case TraderDetailsConsignorNamePage(index) => ua => consignorName(ua, index, CheckMode)
    case TraderDetailsConsignorAddressPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case TraderDetailsConsigneeEoriKnownPage(index) => ua => consigneeEoriKnown(ua, index, CheckMode)
    case TraderDetailsConsigneeEoriNumberPage(index) => ua => consigneeEoriNumberCheckMode(ua, index)
    case TraderDetailsConsigneeNamePage(index) => ua => consigneeName(ua, index, CheckMode)
    case TraderDetailsConsigneeAddressPage(index) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case DeclareMarkPage(itemIndex, packageIndex) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case AddAnotherPackagePage(itemIndex) => ua => addAnotherPackage(itemIndex, ua, CheckMode)
    case RemovePackagePage(itemIndex) => ua => Some(removePackage(itemIndex, CheckMode)(ua))
    case AddAdministrativeReferencePage(itemIndex) => ua => addAdministrativeReferencePage(itemIndex, ua, CheckMode)
    case ReferenceTypePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.id, itemIndex, referenceIndex, CheckMode))
    case PreviousReferencePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.id, itemIndex, referenceIndex, CheckMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, CheckMode)
    case ExtraInformationPage(itemIndex, _) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex) => ua => Some(removePreviousAdministrativeReference(itemIndex, CheckMode)(ua))
    case ContainerNumberPage(itemIndex, containerIndex) => ua => Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.id, itemIndex, CheckMode))
    case AddAnotherContainerPage(itemIndex) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case ConfirmRemoveContainerPage(index, _) => ua => Some(confirmRemoveContainerRoute(ua, index, CheckMode))
    case AddAnotherPreviousAdministrativeReferencePage(itemIndex) => ua => addAnotherPreviousAdministrativeReferenceRoute(itemIndex, ua, CheckMode)
  }

  private def consigneeName(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(TraderDetailsConsigneeAddressPage(index)), mode) match {
      case (Some(_), CheckMode) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(ua.id, index, mode))
    }

  private def consigneeEoriNumberCheckMode(ua: UserAnswers, index: Index) =
    (ua.get(TraderDetailsConsigneeNamePage(index))) match {
      case Some(value) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.id, index, CheckMode))
    }

  private def consigneeEoriKnown(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(TraderDetailsConsigneeEoriKnownPage(index)), ua.get(TraderDetailsConsigneeEoriNumberPage(index)), ua.get(TraderDetailsConsigneeNamePage(index)), mode) match {
      case (Some(true), _, _, NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(ua.id, index, mode))
      case (Some(false), _, _, NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.id, index, mode))
      case (Some(true), None, _, CheckMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(ua.id, index, mode))
      case (Some(false), _, None, CheckMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.id, index, mode))
      case _ => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def consignorAddressNormalMode(ua: UserAnswers, index: Index) =
    ua.get(AddConsigneePage) match {
      case Some(false) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, NormalMode))
      case Some(true) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), NormalMode))
      case None => None
    }

  private def consignorName(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(TraderDetailsConsignorAddressPage(index)), mode) match {
      case (Some(value), CheckMode) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (_, _) => Some(traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(ua.id, index, mode))
    }

  private def consignorEoriNumberCheckMode(ua: UserAnswers, index: Index) =
    (ua.get(TraderDetailsConsignorNamePage(index))) match {
      case Some(value) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.id, index, CheckMode))
    }

  private def consignorEoriKnown(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(TraderDetailsConsignorEoriKnownPage(index)), ua.get(TraderDetailsConsignorEoriNumberPage(index)), ua.get(TraderDetailsConsignorNamePage(index)), mode) match {
      case (Some(true), None, _, _) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(ua.id, index, mode))
      case (Some(true), _, _, NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(ua.id, index, mode))
      case (Some(true), _, _, CheckMode) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false), _, _, NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.id, index, mode))
      case (Some(false), _, Some(value), CheckMode) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false), _, None, CheckMode) => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.id, index, mode))
      case _ if mode == CheckMode => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.id, index, CheckMode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def addItemsSameConsignorForAllItems(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(TraderDetailsConsignorEoriKnownPage(index)) match {
      case None => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, mode))
      case _ if mode == CheckMode => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def isCommodityKnownRouteNormalMode(index: Index, ua: UserAnswers) =
    (ua.get(IsCommodityCodeKnownPage(index)),
      ua.get(AddConsignorPage),
      ua.get(AddConsigneePage)
    ) match {
      case (Some(true), _, _) => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), Some(false), _) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), Some(true), Some(false)) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(false), Some(true), Some(true)) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), NormalMode))
      case _ => None
    }


  private def isCommodityKnownRouteCheckMode(index: Index, ua: UserAnswers) =
    (ua.get(IsCommodityCodeKnownPage(index)), ua.get(CommodityCodePage(index))) match {
      case (Some(true), None) => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, CheckMode))
      case (Some(true), Some(_)) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false), _) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => None
    }

  private def commodityCodeRouteNormalMode(index: Index, ua: UserAnswers) =
    (ua.get(AddConsignorPage), ua.get(AddConsigneePage)) match {
      case (Some(false), _) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), Some(false)) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), Some(true)) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), NormalMode))
      case _ => None
    }

  private def addTotalNetMassRoute(index: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index)), mode) match {
      case (Some(false), _, NormalMode) => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), None, _) => Some(addItemsRoutes.TotalNetMassController.onPageLoad(ua.id, index, mode))
      case _ => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def addAnotherItemRoute(userAnswers: UserAnswers): Call = {
    val count = userAnswers.get(DeriveNumberOfItems).getOrElse(0)
    userAnswers.get(AddAnotherItemPage) match {
      case Some(true) =>
        addItemsRoutes.ItemDescriptionController.onPageLoad(userAnswers.id, Index(count), NormalMode)
      case _ =>
        mainRoutes.DeclarationSummaryController.onPageLoad(userAnswers.id)
    }
  }

  private def removeItem(mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfItems) match {
      case None | Some(0) => addItemsRoutes.ItemDescriptionController.onPageLoad(ua.id, Index(0), mode)
      case _ => addItemsRoutes.AddAnotherItemController.onPageLoad(ua.id)
    }

  def packageType(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    ua.get(PackageTypePage(itemIndex, packageIndex)) match {
      case Some(packageType) if bulkAndUnpackedCodes.contains(packageType.code) =>
        Some(addItemsRoutes.DeclareNumberOfPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case Some(_) =>
        Some(addItemsRoutes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ => None
    }

  def howManyPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(HowManyPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(_), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(_), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(addItemsRoutes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(_), Some(_)) =>
        Some(addItemsRoutes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def declareNumberOfPackages(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(DeclareNumberOfPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(true), _) =>
        Some(addItemsRoutes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(addItemsRoutes.TotalPiecesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addMark(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddMarkPage(itemIndex, packageIndex)), mode) match {
      case (Some(true), _) => Some(addItemsRoutes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), NormalMode) => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, mode))
      case (Some(false), CheckMode) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addAnotherPackage(itemIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddAnotherPackagePage(itemIndex)), ua.get(ContainersUsedPage), ua.get(DeriveNumberOfContainers(itemIndex)).getOrElse(0)) match {
      case (Some(true), _, _) =>
        val nextPackageIndex: Int = ua.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
        Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, itemIndex, Index(nextPackageIndex), mode))
      case (Some(false), Some(false), _) if mode == NormalMode =>
        Some(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(ua.id, itemIndex, mode))
      case (Some(false), _, 0) =>
        Some(containerRoutes.ContainerNumberController.onPageLoad(ua.id, itemIndex, Index(0), mode))
      case (Some(false), _, _) if mode == CheckMode =>
        Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
      case (Some(false), _, _) =>
        Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.id, itemIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def removePackage(itemIndex: Index, mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfPackages(itemIndex)) match {
      case None | Some(0) => addItemsRoutes.PackageTypeController.onPageLoad(ua.id, itemIndex, Index(0), mode)
      case _ => addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, mode)
    }

  private def addAdministrativeReferencePage(itemIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val referenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAdministrativeReferencePage(itemIndex)) map {
      case true => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, itemIndex, Index(referenceIndex), mode)
      case _ if mode == CheckMode => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex)
      case _ => (ua.get(AddSecurityDetailsPage), ua.get(AddTransportChargesPaymentMethodPage)) match {
        case (Some(true), Some(false)) => controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(ua.id, itemIndex, NormalMode)
        case (Some(true), Some(true)) => ua.get(AddCommercialReferenceNumberAllItemsPage) match {
          case Some(true) => controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(ua.id, itemIndex, NormalMode)
          case Some(false) => controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(ua.id, itemIndex, NormalMode)
        }
        case (Some(false), _) => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex)
      }
    }
  }

  private def addExtraInformationPage(ua: UserAnswers, itemIndex: Index, referenceIndex: Index, mode: Mode): Option[Call] =
    ua.get(AddExtraInformationPage(itemIndex, referenceIndex)) map {
      case true =>
        previousReferencesRoutes.ExtraInformationController.onPageLoad(ua.id, itemIndex, referenceIndex, mode)
      case false =>
        previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, mode)
    }


  private def addAnotherPreviousAdministrativeReferenceRoute(itemIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val newReferenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAnotherPreviousAdministrativeReferencePage(itemIndex)) map {
      case true => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, itemIndex, Index(newReferenceIndex), mode)
      case _ if mode == CheckMode => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex)
      case _ => (ua.get(AddSecurityDetailsPage), ua.get(AddTransportChargesPaymentMethodPage)) match {
        case (Some(true), Some(false)) => controllers.addItems.securityDetails.routes.TransportChargesController.onPageLoad(ua.id, itemIndex, NormalMode)
        case (Some(true), Some(true)) => ua.get(AddCommercialReferenceNumberAllItemsPage) match {
          case Some(true) => controllers.addItems.securityDetails.routes.AddDangerousGoodsCodeController.onPageLoad(ua.id, itemIndex, NormalMode)
          case Some(false) => controllers.addItems.securityDetails.routes.CommercialReferenceNumberController.onPageLoad(ua.id, itemIndex, NormalMode)
        }
        case (Some(false), _) => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex)
      }
    }
  }

  private def removePreviousAdministrativeReference(itemIndex: Index, mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)) match {
      case None | Some(0) => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, itemIndex, Index(0), mode)
      case _ => previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, mode)
    }

  private def confirmRemoveContainerRoute(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(DeriveNumberOfContainers(index)).getOrElse(0) match {
      case 0 => containerRoutes.ContainerNumberController.onPageLoad(ua.id, index, Index(0), mode)
      case _ => containerRoutes.AddAnotherContainerController.onPageLoad(ua.id, index, mode)
    }

  // format: on
}
