/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.addItems.{routes => addItemsRoutes}
import controllers.addItems.{routes => addAnotherPackageRoutes}
import controllers.{routes => mainRoutes}
import derivable.{DeriveNumberOfItems, DeriveNumberOfPackages, DeriveNumberOfPreviousAdministrativeReferences}
import javax.inject.{Inject, Singleton}
import models._
import models.reference.CountryCode
import models.reference.PackageType.{bulkAndUnpackedCodes, bulkCodes, unpackedCodes}
import pages._
import pages.addItems.traderDetails._
import pages.addItems.{AddAnotherPreviousAdministrativeReferencePage, _}
import play.api.mvc.Call

@Singleton
class AddItemsNavigator @Inject()() extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index)                           => ua => Some(addItemsRoutes.ItemTotalGrossMassController.onPageLoad(ua.id, index, NormalMode))
    case ItemTotalGrossMassPage(index)                        => ua => Some(addItemsRoutes.AddTotalNetMassController.onPageLoad(ua.id, index, NormalMode))
    case AddTotalNetMassPage(index)                           => ua => addTotalNetMassRoute(index, ua, NormalMode)
    case TotalNetMassPage(index)                              => ua => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
    case IsCommodityCodeKnownPage(index)                      => ua => isCommodityKnownRoute(index, ua, NormalMode)
    case AddAnotherItemPage                                   => ua => Some(addAnotherItemRoute(ua))
    case ConfirmRemoveItemPage                                => ua => Some(removeItem(NormalMode)(ua))
    case CommodityCodePage(index)                             => ua => commodityCodeRoute(index, ua, NormalMode)
    case PackageTypePage(itemIndex, packageIndex)             => ua => packageType(itemIndex, packageIndex, ua, NormalMode) // TODO add modes functionality when tests are created
    case HowManyPackagesPage(itemIndex, packageIndex)         => ua => howManyPackages(itemIndex, packageIndex, ua, NormalMode)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua, NormalMode)
    case TotalPiecesPage(itemIndex, packageIndex)             => ua => Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, NormalMode))
    case AddMarkPage(itemIndex, packageIndex)                 => ua => addMark(itemIndex, packageIndex, ua, NormalMode)
    case DeclareMarkPage(itemIndex, packageIndex)             => ua => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherPackagePage(itemIndex)       => ua => addAnotherPackage(itemIndex, ua, NormalMode)
    case CommodityCodePage(index)                    => ua => Some(addItemsRoutes.AddItemsSameConsignorForAllItemsController.onPageLoad(ua.id,index, NormalMode))
    case AddItemsSameConsignorForAllItemsPage(index) => ua => addItemsSameConsignorForAllItems(ua, index, NormalMode)
    case TraderDetailsConsignorEoriKnownPage(index)  => ua => consignorEoriKnown(ua, index, NormalMode)
    case TraderDetailsConsignorEoriNumberPage(index) => ua => consignorEoriNumber(ua, index, NormalMode)
    case TraderDetailsConsignorNamePage(index)       => ua => consignorName(ua, index, NormalMode)
    case TraderDetailsConsignorAddressPage(index)    => ua => consignorAddress(ua, index, NormalMode)
    case AddItemsSameConsigneeForAllItemsPage(index) => ua => addItemsSameConsigneeForAllItems(ua, index, NormalMode)
    case TraderDetailsConsigneeEoriKnownPage(index)  => ua => consigneeEoriKnown(ua, index, NormalMode)
    case TraderDetailsConsigneeEoriNumberPage(index) => ua => consigneeEoriNumber(ua, index, NormalMode)
    case TraderDetailsConsigneeNamePage(index)       => ua => consigneeName(ua, index, NormalMode)
    case TraderDetailsConsigneeAddressPage(index)    => ua => consigneeAddress(ua, index, NormalMode)
    case DeclareMarkPage(itemIndex, _)                        => ua => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherPackagePage(itemIndex)                     => ua => addAnotherPackage(itemIndex, ua, NormalMode)
    case RemovePackagePage(itemIndex)                         => ua => Some(removePackage(itemIndex, NormalMode)(ua))
    case ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex)     => ua => Some(removePreviousAdministrativeReference(itemIndex, NormalMode)(ua))
    case DummyPage(itemIndex, packageIndex)                   => ua => directToPreviousReferencesPage(itemIndex, packageIndex, ua, NormalMode) //TODO replace dummy page with add another document page
    case AddAdministrativeReferencePage(itemIndex) => ua => addAdministrativeReferencePage(itemIndex, ua, NormalMode)
    case ReferenceTypePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.id, itemIndex, referenceIndex, NormalMode))
    case PreviousReferencePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.id, itemIndex, referenceIndex, NormalMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, NormalMode)
    case ExtraInformationPage(itemIndex, _)    => ua => Some(previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, NormalMode))
    case AddAnotherPreviousAdministrativeReferencePage(itemIndex)   => ua => addAnotherPreviousAdministrativeReferenceRoute(itemIndex, ua, NormalMode)
  }

  //TODO: Need to refactor this code
  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case ItemDescriptionPage(index)                           => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index)                        => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index)                           => ua => addTotalNetMassRoute(index, ua, CheckMode)
    case IsCommodityCodeKnownPage(index)                      => ua => isCommodityKnownRoute(index, ua, CheckMode)
    case CommodityCodePage(index)                             => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case TotalNetMassPage(index)                              => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case PackageTypePage(itemIndex, packageIndex)             => ua => packageType(itemIndex, packageIndex, ua, CheckMode) // TODO add modes functionality when tests are created
    case HowManyPackagesPage(itemIndex, packageIndex)         => ua => howManyPackages(itemIndex, packageIndex, ua, CheckMode)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackages(itemIndex, packageIndex, ua, CheckMode)
    case TotalPiecesPage(itemIndex, packageIndex)             => ua => Some(addItemsRoutes.AddMarkController.onPageLoad(ua.id, itemIndex, packageIndex, CheckMode))
    case AddMarkPage(itemIndex, packageIndex)                 => ua => addMark(itemIndex, packageIndex, ua, CheckMode)
    case DeclareMarkPage(itemIndex, packageIndex)             => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case AddAnotherPackagePage(itemIndex)       => ua => addAnotherPackage(itemIndex, ua, CheckMode)
    case ItemDescriptionPage(index)                  => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case ItemTotalGrossMassPage(index)               => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    case AddTotalNetMassPage(index)                  => ua => addTotalNetMassRoute(index, ua,  CheckMode)
    case IsCommodityCodeKnownPage(index)             => ua => isCommodityKnownRoute(index, ua, CheckMode)
    case CommodityCodePage(index)                    => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))
    case TotalNetMassPage(index)                     => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))
    case AddItemsSameConsignorForAllItemsPage(index) => ua => addItemsSameConsignorForAllItems(ua, index, CheckMode)
    case TraderDetailsConsignorEoriKnownPage(index)  => ua => consignorEoriKnown(ua, index, CheckMode)
    case TraderDetailsConsignorEoriNumberPage(index) => ua => consignorEoriNumber(ua, index, CheckMode)
    case TraderDetailsConsignorNamePage(index)       => ua => consignorName(ua, index, CheckMode)
    case TraderDetailsConsignorAddressPage(index)    => ua => consignorAddress(ua, index, CheckMode)
    case AddItemsSameConsigneeForAllItemsPage(index) => ua => addItemsSameConsigneeForAllItems(ua, index, CheckMode)
    case TraderDetailsConsigneeEoriKnownPage(index)  => ua => consigneeEoriKnown(ua, index, CheckMode)
    case TraderDetailsConsigneeEoriNumberPage(index) => ua => consigneeEoriNumber(ua, index, CheckMode)
    case TraderDetailsConsigneeNamePage(index)       => ua => consigneeName(ua, index, CheckMode)
    case TraderDetailsConsigneeAddressPage(index)    => ua => consigneeAddress(ua, index, CheckMode)
    case DeclareMarkPage(itemIndex, packageIndex)             => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case AddAnotherPackagePage(itemIndex)                     => ua => addAnotherPackage(itemIndex, ua, CheckMode)
    case RemovePackagePage(itemIndex)                         => ua => Some(removePackage(itemIndex, CheckMode)(ua))
    case RemovePackagePage(itemIndex)                         => ua => Some(addAnotherPackageRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, CheckMode))
    case AddAdministrativeReferencePage(itemIndex)            => ua =>  addAdministrativeReferencePage(itemIndex, ua, CheckMode)
    case ReferenceTypePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.PreviousReferenceController.onPageLoad(ua.id, itemIndex, referenceIndex, CheckMode))
    case PreviousReferencePage(itemIndex, referenceIndex) => ua => Some(previousReferencesRoutes.AddExtraInformationController.onPageLoad(ua.id, itemIndex, referenceIndex, CheckMode))
    case AddExtraInformationPage(itemIndex, referenceIndex) => ua => addExtraInformationPage(ua, itemIndex, referenceIndex, CheckMode)
    case ExtraInformationPage(itemIndex, _)    => ua =>  Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
    case ConfirmRemovePreviousAdministrativeReferencePage(itemIndex, referenceIndex)     => ua => Some(removePreviousAdministrativeReference(itemIndex, CheckMode)(ua))
  }

  private def consigneeAddress(ua: UserAnswers, index: Index, mode: Mode) =
    mode match {
      case NormalMode => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case CheckMode => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))
    }

  private def consigneeName(ua: UserAnswers, index: Index, mode: Mode) =
    Some(traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(ua.id, index, mode))

  private def consigneeEoriNumber(ua: UserAnswers, index: Index, mode: Mode) =
    mode match {
      case NormalMode => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case CheckMode => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id,index))
    }

  private def consigneeEoriKnown(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(TraderDetailsConsigneeEoriKnownPage(index)) match {
      case Some(true) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(ua.id, index, mode))
      case Some(false) => Some(traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(ua.id, index, mode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def addItemsSameConsigneeForAllItems(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddItemsSameConsigneeForAllItemsPage(index)), ua.get(AddItemsSameConsignorForAllItemsPage(index))) match {
      case (Some(true), Some(true)) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case (Some(false), _) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (_, Some(false)) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def consignorAddress(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(ConsigneeForAllItemsPage), ua.get(AddConsigneePage), mode) match {
      case (Some(false), Some(false), NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, mode))
      case (None, None, NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, mode))
      case (Some(false), Some(true), NormalMode) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case (Some(true), None, NormalMode) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case (_, _, CheckMode) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def consignorName(ua: UserAnswers, index: Index, mode: Mode) =
    Some(traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(ua.id, index, mode))

  private def consignorEoriNumber(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(ConsigneeForAllItemsPage), ua.get(AddConsigneePage),mode) match {
      case (Some(false), Some(false), NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, mode))
      case (None, None, NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, mode))
      case (Some(false), Some(true), NormalMode) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case (Some(true), None, NormalMode) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case (_, _, CheckMode) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  private def consignorEoriKnown(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(TraderDetailsConsignorEoriKnownPage(index)), ua.get(TraderDetailsConsignorEoriNumberPage(index)), ua.get(TraderDetailsConsignorNamePage(index)), mode) match {
      case (Some(true),None,_,_) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(ua.id, index, mode))
      case (Some(true),_,_,_) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false),_,Some(value),_) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false),_,None,_) => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.id, index, mode))
      case (Some(false),_,_,NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.id, index, mode))
      case _ if mode == CheckMode => Some(traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(ua.id, index, CheckMode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def addItemsSameConsignorForAllItems(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(AddItemsSameConsignorForAllItemsPage(index)), ua.get(TraderDetailsConsignorEoriKnownPage(index))) match {
      case (Some(true), _) => Some(addItemsRoutes.AddItemsSameConsigneeForAllItemsController.onPageLoad(ua.id, index, mode))
      case (Some(false), _) if mode == NormalMode => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, mode))
      case (Some(false), None) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, mode))
      case _ if mode == CheckMode=> Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

//  private def addAnotherPreviousAdministrativeReferenceRoute(index: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
//    val newReferenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(index)).getOrElse(0)
//    ua.get(AddAnotherPreviousAdministrativeReferencePage(index)) map {
//      case true => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, index, Index(newReferenceIndex), mode)
//      case false if mode == NormalMode => ??? //TODO must go to 'Has the user selected yes for safety and security?'
//      case _ => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index)
//    }
//  }

  private def isCommodityKnownRoute(index:Index, ua:UserAnswers, mode:Mode) =
    (ua.get(IsCommodityCodeKnownPage(index)),ua.get(CommodityCodePage(index)),
      ua.get(ConsignorForAllItemsPage), ua.get(AddConsignorPage),
      ua.get(ConsigneeForAllItemsPage), ua.get(AddConsigneePage),
      mode
    ) match {
      case (Some(true),_,_,_,_,_,NormalMode)    => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, mode))
      case (Some(true),None,_,_,_,_,CheckMode)    => Some(addItemsRoutes.CommodityCodeController.onPageLoad(ua.id, index, mode))
      case (Some(true),_,_,_,_,_,CheckMode)    => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false),_,_,_,_,_,CheckMode)    => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(false),_,
      Some(false),Some(false),
      Some(false),Some(false),
      NormalMode)      => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, mode))
      case (_,_,
      Some(true),None,
      Some(false),Some(false),
      NormalMode)      => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, mode))
      case (_,_,
      Some(false),Some(true),
      Some(false),Some(false),
      NormalMode)      => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, mode))
      case (_,_,
      Some(true),None,
      Some(true),None,
      NormalMode)      => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case (_,_,
      Some(false),Some(true),
      Some(true),None,
      NormalMode)      => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case _ => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, mode)) //TODO: Confirm design with Adam
    }

  private def commodityCodeRoute(index:Index, ua:UserAnswers, mode:Mode) =
    (ua.get(CommodityCodePage(index)),
      ua.get(ConsignorForAllItemsPage), ua.get(AddConsignorPage),
      ua.get(ConsigneeForAllItemsPage), ua.get(AddConsigneePage),
      mode) match {
      case (_,
      Some(false), Some(false),
      Some(false), Some(false),
      NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, NormalMode))
      case (_,
      Some(true), None,
      Some(false), Some(false),
      NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, NormalMode))
      case (_,
      Some(false), Some(true),
      Some(false), Some(false),
      NormalMode) => Some(traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(ua.id, index, NormalMode))
      case (_,
      Some(true), None,
      Some(true), None,
      NormalMode) => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case (_,
      Some(false),Some(true),
      Some(true),None,
      NormalMode)      => Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, index, Index(0), mode))
      case _           => Some(traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(ua.id, index, NormalMode)) //TODO: Confirm design with Adam
    }

  private def addTotalNetMassRoute(index: Index, ua: UserAnswers, mode: Mode) =
    (ua.get(AddTotalNetMassPage(index)), ua.get(TotalNetMassPage(index)), mode) match {
      case (Some(false), _, NormalMode)    => Some(addItemsRoutes.IsCommodityCodeKnownController.onPageLoad(ua.id, index, NormalMode))
      case (Some(true), None , _)          => Some(addItemsRoutes.TotalNetMassController.onPageLoad(ua.id, index, mode))
      case _                               => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index))
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
      case _              => addItemsRoutes.AddAnotherItemController.onPageLoad(ua.id)
    }

  // TODO add smarter PackageTypePage type for easier matching
  def packageType(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    ua.get(PackageTypePage(itemIndex, packageIndex)) match {
      case Some(packageType) if bulkAndUnpackedCodes.contains(packageType.code) =>
        Some(addItemsRoutes.DeclareNumberOfPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case Some(_) =>
        Some(addItemsRoutes.HowManyPackagesController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
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

  def addMark(itemIndex: Index, packageIndex: Index, ua: UserAnswers, mode: Mode): Option[Call]  =
    (ua.get(AddMarkPage(itemIndex, packageIndex)), mode) match {
      case (Some(true), _)            => Some(addItemsRoutes.DeclareMarkController.onPageLoad(ua.id, itemIndex, packageIndex, mode))
      case (Some(false), NormalMode)  => Some(addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, mode))
      case (Some(false), CheckMode)   => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
      case _                          => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addAnotherPackage(itemIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddAnotherPackagePage(itemIndex)), mode) match {
      case (Some(true), _) =>
        val nextPackageIndex: Int = ua.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
        Some(addItemsRoutes.PackageTypeController.onPageLoad(ua.id, itemIndex, Index(nextPackageIndex), mode))
      case (Some(false), CheckMode) =>
        Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex))
      case (Some(false), NormalMode) =>
        ??? //TODO hook into container journey
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def removePackage(itemIndex: Index, mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfPackages(itemIndex)) match {
      case None|Some(0) => addItemsRoutes.PackageTypeController.onPageLoad(ua.id, itemIndex, Index(0), mode)
      case _            => addItemsRoutes.AddAnotherPackageController.onPageLoad(ua.id, itemIndex, mode)
    }

  def directToPreviousReferencesPage(itemIndex: Index, referenceIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val nonEUCountries = Seq(CountryCode("AD"), CountryCode("IS"), CountryCode("LI"), CountryCode("NO"), CountryCode("SM"), CountryCode("SJ"), CountryCode("CH"))
    val declarationTypes = Seq(DeclarationType.Option2, DeclarationType.Option4)
    val isNonEUCountry: Boolean = ua.get(CountryOfDispatchPage).fold(false)(code => nonEUCountries.contains(code))
    val isAllowedDeclarationType: Boolean = ua.get(DeclarationTypePage).fold(false)(declarationTypes.contains(_))
    (isNonEUCountry, isAllowedDeclarationType) match {
      case (true, true) => Some(previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, itemIndex, referenceIndex, mode))
      case _ => Some(previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, mode))
    }
  }

  private def addAdministrativeReferencePage(itemIndex: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val referenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)).getOrElse(0)
    ua.get(AddAdministrativeReferencePage(itemIndex)) map {
      case true => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, itemIndex, Index(referenceIndex), mode)
      case _ if mode == CheckMode => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, itemIndex)
      case _ => ??? //TODO must go to 'Has the user selected yes for safety and security?'
    }
  }

  private def addExtraInformationPage(ua: UserAnswers, itemIndex: Index, referenceIndex: Index, mode: Mode): Option[Call] =
    ua.get(AddExtraInformationPage(itemIndex, referenceIndex)) map {
      case true =>
        previousReferencesRoutes.ExtraInformationController.onPageLoad(ua.id, itemIndex, referenceIndex, mode)
      case false =>
        previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, mode)
    }


  private def addAnotherPreviousAdministrativeReferenceRoute(index: Index, ua: UserAnswers, mode: Mode): Option[Call] = {
    val newReferenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(index)).getOrElse(0)
    ua.get(AddAnotherPreviousAdministrativeReferencePage(index)) map {
      case true => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, index, Index(newReferenceIndex), mode)
      case false if mode == NormalMode => ??? //TODO must go to 'Has the user selected yes for safety and security?'
      case _ => addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.id, index)
    }
  }

  private def removePreviousAdministrativeReference(itemIndex: Index, mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfPreviousAdministrativeReferences(itemIndex)) match {
      case None | Some(0) => previousReferencesRoutes.ReferenceTypeController.onPageLoad(ua.id, itemIndex, Index(0), mode)
      case _              => previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(ua.id, itemIndex, mode)
    }


  // format: on
}
