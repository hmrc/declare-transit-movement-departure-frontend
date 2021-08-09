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

import controllers.addItems.containers.{routes => containerRoutes}
import controllers.addItems.specialMentions.{routes => specialMentionsRoutes}
import controllers.addItems.{routes => addItemsRoutes}
import controllers.{routes => mainRoutes}
import derivable._
import models._
import models.reference.PackageType.{bulkCodes, unpackedCodes}
import navigation.Navigator
import pages._
import pages.addItems._
import pages.generalInformation.ContainersUsedPage
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsPackagesInfoNavigator @Inject() () extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {


    case PackageTypePage(itemIndex, packageIndex) => ua => packageTypeNormalMode(itemIndex, packageIndex, ua)
    case HowManyPackagesPage(itemIndex, packageIndex) => ua => howManyPackagesNormalMode(itemIndex, packageIndex, ua)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackagesNormalMode(itemIndex, packageIndex, ua)
    case TotalPiecesPage(itemIndex, packageIndex) => ua => Some(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
    case AddMarkPage(itemIndex, packageIndex) => ua => addMarkNormalMode(itemIndex, packageIndex, ua)
    case DeclareMarkPage(itemIndex, _) => ua => Some(controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(ua.lrn, itemIndex, NormalMode))
    case AddAnotherPackagePage(itemIndex) => ua => addAnotherPackageNormalMode(itemIndex, ua)
    case DeclareMarkPage(itemIndex, _) => ua => Some(controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(ua.lrn, itemIndex, NormalMode))
    case RemovePackagePage(itemIndex) => ua => Some(removePackage(itemIndex, NormalMode)(ua))

  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case PackageTypePage(itemIndex, packageIndex) => ua => packageTypeCheckMode(itemIndex, packageIndex, ua)
    case HowManyPackagesPage(itemIndex, packageIndex) => ua => howManyPackagesCheckMode(itemIndex, packageIndex, ua)
    case DeclareNumberOfPackagesPage(itemIndex, packageIndex) => ua => declareNumberOfPackagesCheckMode(itemIndex, packageIndex, ua)
    case TotalPiecesPage(itemIndex, packageIndex) => ua => Some(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
    case AddMarkPage(itemIndex, packageIndex) => ua => addMarkCheckMode(itemIndex, packageIndex, ua)
    case DeclareMarkPage(itemIndex, _) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
    case AddAnotherPackagePage(itemIndex) => ua => addAnotherPackageCheckMode(itemIndex, ua)
    case DeclareMarkPage(itemIndex, _) => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
    case RemovePackagePage(itemIndex) => ua => Some(removePackage(itemIndex, CheckMode)(ua))

  }

  def packageTypeNormalMode(itemIndex: Index, packageIndex: Index, ua: UserAnswers): Option[Call] =
    ua.get(PackageTypePage(itemIndex, packageIndex)) match {
      case Some(packageType) if bulkCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case Some(packageType) if unpackedCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case Some(_) => Some(controllers.addItems.packagesInformation.routes.HowManyPackagesController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def packageTypeCheckMode(itemIndex: Index, packageIndex: Index, ua: UserAnswers): Option[Call] =
    ua.get(PackageTypePage(itemIndex, packageIndex)) match {
      case Some(packageType) if bulkCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case Some(packageType) if unpackedCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case Some(_) => Some(controllers.addItems.packagesInformation.routes.HowManyPackagesController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def howManyPackagesNormalMode(itemIndex: Index, packageIndex: Index, ua: UserAnswers): Option[Call] =
    (ua.get(HowManyPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(_), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case (Some(_), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case (Some(_), Some(_)) => Some(controllers.addItems.packagesInformation.routes.DeclareMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def howManyPackagesCheckMode(itemIndex: Index, packageIndex: Index, ua: UserAnswers): Option[Call] =
    (ua.get(HowManyPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(_), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case (Some(_), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case (Some(_), Some(_)) => Some(controllers.addItems.packagesInformation.routes.DeclareMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def declareNumberOfPackagesNormalMode(itemIndex: Index, packageIndex: Index, ua: UserAnswers): Option[Call] =
    (ua.get(DeclareNumberOfPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(true), _) => Some(controllers.addItems.packagesInformation.routes.HowManyPackagesController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case (Some(false), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case (Some(false), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def declareNumberOfPackagesCheckMode(itemIndex: Index, packageIndex: Index, ua: UserAnswers): Option[Call] =
    (ua.get(DeclareNumberOfPackagesPage(itemIndex, packageIndex)), ua.get(PackageTypePage(itemIndex, packageIndex))) match {
      case (Some(true), _) => Some(controllers.addItems.packagesInformation.routes.HowManyPackagesController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case (Some(false), Some(packageType)) if bulkCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case (Some(false), Some(packageType)) if unpackedCodes.contains(packageType.code) =>
        Some(controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addMarkNormalMode(itemIndex: Index, packageIndex: Index, ua: UserAnswers): Option[Call] =
    (ua.get(AddMarkPage(itemIndex, packageIndex))) match {
      case Some(true) => Some(controllers.addItems.packagesInformation.routes.DeclareMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, NormalMode))
      case Some(false) => Some(controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(ua.lrn, itemIndex, NormalMode))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addMarkCheckMode(itemIndex: Index, packageIndex: Index, ua: UserAnswers): Option[Call] =
    ua.get(AddMarkPage(itemIndex, packageIndex)) match {
      case Some(true) => Some(controllers.addItems.packagesInformation.routes.DeclareMarkController.onPageLoad(ua.lrn, itemIndex, packageIndex, CheckMode))
      case Some(false) => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
      case _ => Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addAnotherPackageNormalMode(itemIndex: Index, ua: UserAnswers): Option[Call] =
    (ua.get(AddAnotherPackagePage(itemIndex)), ua.get(ContainersUsedPage), ua.get(DeriveNumberOfContainers(itemIndex)).getOrElse(0)) match {
      case (Some(true), _, _) =>
        val nextPackageIndex: Int = ua.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
        Some(controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(ua.lrn, itemIndex, Index(nextPackageIndex), NormalMode))
      case (Some(false), Some(false), _) =>
        Some(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(ua.lrn, itemIndex, NormalMode))
      case (Some(false), _, 0) =>
        Some(containerRoutes.ContainerNumberController.onPageLoad(ua.lrn, itemIndex, Index(0), NormalMode))
      case (Some(false), _, _) =>
        Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.lrn, itemIndex, NormalMode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  def addAnotherPackageCheckMode(itemIndex: Index, ua: UserAnswers): Option[Call] =
    (ua.get(AddAnotherPackagePage(itemIndex)), ua.get(ContainersUsedPage), ua.get(DeriveNumberOfContainers(itemIndex)).getOrElse(0)) match {
      case (Some(true), _, _) =>
        val nextPackageIndex: Int = ua.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
        Some(controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(ua.lrn, itemIndex, Index(nextPackageIndex), CheckMode))
      case (Some(false), _, 0) =>
        Some(containerRoutes.ContainerNumberController.onPageLoad(ua.lrn, itemIndex, Index(0), CheckMode))
      case (Some(false), _, _) =>
        Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
      case (Some(false), _, _) =>
        Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.lrn, itemIndex, CheckMode))
      case _ =>
        Some(mainRoutes.SessionExpiredController.onPageLoad())
    }

  private def removePackage(itemIndex: Index, mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfPackages(itemIndex)) match {
      case None | Some(0) => controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(ua.lrn, itemIndex, Index(0), mode)
      case _ => controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(ua.lrn, itemIndex, mode)
    }


  // format: on
}
