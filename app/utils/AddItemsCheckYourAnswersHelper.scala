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

package utils

import controllers.addItems.containers.{routes => containerRoutes}
import controllers.addItems.previousReferences.{routes => previousReferencesRoutes}
import controllers.addItems.routes
import controllers.addItems.securityDetails.{routes => securityDetailsRoutes}
import controllers.addItems.traderDetails.{routes => traderDetailsRoutes}
import controllers.addItems.traderSecurityDetails.{routes => tradersSecurityDetailsRoutes}
import models.DeclarationType.Option4
import models._
import models.reference.{MethodOfPayment, PackageType}
import pages._
import pages.addItems._
import pages.addItems.containers._
import pages.addItems.securityDetails._
import pages.addItems.traderDetails._
import pages.addItems.traderSecurityDetails._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

// scalastyle:off number.of.methods
class AddItemsCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def transportCharges(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[MethodOfPayment](
    page = TransportChargesPage(itemIndex),
    formatAnswer = formatAsLiteral,
    prefix = "transportCharges",
    id = None,
    call = securityDetailsRoutes.TransportChargesController.onPageLoad(lrn, itemIndex, CheckMode)
  )

  def containerNumber(itemIndex: Index, containerIndex: Index): Option[Row] = getAnswerAndBuildValuelessRow[String](
    page = ContainerNumberPage(itemIndex, containerIndex),
    formatAnswer = formatAsSelf,
    id = Some(s"change-container-${containerIndex.display}"),
    call = containerRoutes.ContainerNumberController.onPageLoad(lrn, itemIndex, containerIndex, CheckMode)
  )

  def addAnotherContainer(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherContainerHref = containerRoutes.AddAnotherContainerController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherContainerHref, content)
  }

  def documentRow(index: Index, documentIndex: Index, documentType: DocumentTypeList, removable: Boolean): Option[Row] =
    userAnswers.get(DocumentTypePage(index, documentIndex)).flatMap {
      answer =>
        documentType.getDocumentType(answer).map {
          documentType =>
            val updatedAnswer = s"(${documentType.code}) ${documentType.description}"

            userAnswers.get(DeclarationTypePage) match {
              case Some(Option4) if index.position == 0 & documentIndex.position == 0 =>
                buildValuelessRow(
                  key = updatedAnswer,
                  id = Some(s"change-document-${index.display}"),
                  call = controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(lrn, index, documentIndex, CheckMode)
                )
              case _ if removable =>
                buildRemovableRow(
                  key = updatedAnswer,
                  id = s"document-${index.display}",
                  changeCall = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(lrn, index, documentIndex, CheckMode),
                  removeCall = controllers.addItems.documents.routes.ConfirmRemoveDocumentController.onPageLoad(lrn, index, documentIndex, CheckMode)
                )
              case _ =>
                buildValuelessRow(
                  key = updatedAnswer,
                  id = Some(s"change-document-${documentIndex.display}"),
                  call = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(lrn, index, documentIndex, CheckMode)
                )
            }
        }
    }

  def itemRow(index: Index): Option[Row] = getAnswerAndBuildRemovableRow[String](
    page = ItemDescriptionPage(index),
    formatAnswer = formatAsSelf,
    id = s"item-${index.display}",
    changeCall = routes.ItemsCheckYourAnswersController.onPageLoad(lrn, index),
    removeCall = routes.ConfirmRemoveItemController.onPageLoad(lrn, index)
  )

  def addDocuments(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddDocumentsPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "addDocuments",
    id = None,
    call = controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(lrn, itemIndex, CheckMode),
    args = itemIndex.display
  )

  def traderDetailsConsignorName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsignorNamePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "traderDetailsConsignorName",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsignorEoriNumber(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsignorEoriNumberPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "traderDetailsConsignorEoriNumber",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsignorEoriKnown(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = TraderDetailsConsignorEoriKnownPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetailsConsignorEoriKnown",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsignorAddress(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = TraderDetailsConsignorAddressPage(itemIndex),
    formatAnswer = formatAsAddress,
    prefix = "traderDetailsConsignorAddress",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(lrn, itemIndex, CheckMode),
    args = userAnswers.get(TraderDetailsConsignorNamePage(itemIndex)).getOrElse(msg"traderDetailsConsignorAddress.checkYourAnswersLabel.fallback")
  )

  def traderDetailsConsigneeName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsigneeNamePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "traderDetailsConsigneeName",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsigneeEoriNumber(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsigneeEoriNumberPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "traderDetailsConsigneeEoriNumber",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsigneeEoriKnown(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = TraderDetailsConsigneeEoriKnownPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetailsConsigneeEoriKnown",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsigneeAddress(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = TraderDetailsConsigneeAddressPage(itemIndex),
    formatAnswer = formatAsAddress,
    prefix = "traderDetailsConsigneeAddress",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(lrn, itemIndex, CheckMode),
    args = userAnswers.get(TraderDetailsConsigneeNamePage(itemIndex)).getOrElse(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel.fallback")
  )

  def commodityCode(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = CommodityCodePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "commodityCode",
    id = Some("change-commodity-code"),
    call = controllers.addItems.itemDetails.routes.CommodityCodeController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def totalNetMass(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TotalNetMassPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "totalNetMass",
    id = Some("change-total-net-mass"),
    call = controllers.addItems.itemDetails.routes.TotalNetMassController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def isCommodityCodeKnown(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = IsCommodityCodeKnownPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "isCommodityCodeKnown",
    id = Some("change-is-commodity-known"),
    call = controllers.addItems.itemDetails.routes.IsCommodityCodeKnownController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def addTotalNetMass(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddTotalNetMassPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "addTotalNetMass",
    id = Some("change-add-total-net-mass"),
    call = controllers.addItems.itemDetails.routes.AddTotalNetMassController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def itemTotalGrossMass(index: Index): Option[Row] = getAnswerAndBuildRow[Double](
    page = ItemTotalGrossMassPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "itemTotalGrossMass",
    id = Some("change-item-total-gross-mass"),
    call = controllers.addItems.itemDetails.routes.ItemTotalGrossMassController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def itemDescription(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = ItemDescriptionPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "itemDescription",
    id = Some("change-item-description"),
    call = controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def previousReferenceType(index: Index, referenceIndex: Index, previousDocumentType: PreviousReferencesDocumentTypeList): Option[Row] =
    userAnswers.get(ReferenceTypePage(index, referenceIndex)) flatMap {
      answer =>
        previousDocumentType.getPreviousReferencesDocumentType(answer) map {
          referenceType =>
            buildValuelessRow(
              key = s"(${referenceType.code}) ${referenceType.description.getOrElse("")}",
              id = Some(s"change-item-${index.display}"),
              call = previousReferencesRoutes.ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, CheckMode)
            )
        }
    }

  def previousAdministrativeReferenceRow(
    index: Index,
    referenceIndex: Index,
    previousDocumentType: PreviousReferencesDocumentTypeList
  ): Option[Row] =
    userAnswers.get(ReferenceTypePage(index, referenceIndex)) flatMap {
      answer =>
        previousDocumentType.getPreviousReferencesDocumentType(answer) map {
          referenceType =>
            buildRemovableRow(
              key = s"(${referenceType.code}) ${referenceType.description.getOrElse("")}",
              id = s"reference-document-type-${index.display}",
              changeCall = previousReferencesRoutes.ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, CheckMode),
              removeCall = previousReferencesRoutes.ConfirmRemovePreviousAdministrativeReferenceController.onPageLoad(lrn, index, referenceIndex, CheckMode)
            )
        }
    }

  def addAdministrativeReference(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddAdministrativeReferencePage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "addAdministrativeReference",
    id = None,
    call = previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(lrn, index, CheckMode)
  )

  def addAnotherPreviousReferences(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherPackageHref = previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherPackageHref, content)
  }

  def packageType(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[PackageType](
    page = PackageTypePage(itemIndex, packageIndex),
    formatAnswer = formatAsLiteral,
    prefix = "packageType",
    id = Some(s"change-package-${packageIndex.display}"),
    call = controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(lrn, itemIndex, packageIndex, CheckMode)
  )

  def numberOfPackages(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[Int](
    page = HowManyPackagesPage(itemIndex, packageIndex),
    formatAnswer = formatAsLiteral,
    prefix = "declareNumberOfPackages",
    id = None,
    call = controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(lrn, itemIndex, packageIndex, CheckMode)
  )

  def totalPieces(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[Int](
    page = TotalPiecesPage(itemIndex, packageIndex),
    formatAnswer = formatAsLiteral,
    prefix = "totalPieces",
    id = None,
    call = controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(lrn, itemIndex, packageIndex, CheckMode)
  )

  def addAnotherPackage(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherPackageHref = controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherPackageHref, content)
  }

  def addAnotherDocument(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherDocumentHref = controllers.addItems.documents.routes.AddAnotherDocumentController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherDocumentHref, content)
  }

  def commercialReferenceNumber(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = CommercialReferenceNumberPage(itemIndex),
    formatAnswer = formatAsLiteral,
    prefix = "commercialReferenceNumber",
    id = None,
    call = securityDetailsRoutes.CommercialReferenceNumberController.onPageLoad(lrn, itemIndex, CheckMode)
  )

  def addDangerousGoodsCode(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddDangerousGoodsCodePage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "addDangerousGoodsCode",
    id = None,
    call = securityDetailsRoutes.AddDangerousGoodsCodeController.onPageLoad(lrn, itemIndex, CheckMode)
  )

  def dangerousGoodsCode(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = DangerousGoodsCodePage(itemIndex),
    formatAnswer = formatAsLiteral,
    prefix = "dangerousGoodsCode",
    id = None,
    call = securityDetailsRoutes.DangerousGoodsCodeController.onPageLoad(lrn, itemIndex, CheckMode)
  )

  def addSecurityConsignorsEori(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSecurityConsignorsEoriPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "addSecurityConsignorsEori",
    id = None,
    call = tradersSecurityDetailsRoutes.AddSecurityConsignorsEoriController.onPageLoad(lrn, index, CheckMode)
  )

  def addSecurityConsigneesEori(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSecurityConsigneesEoriPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "addSecurityConsigneesEori",
    id = None,
    call = tradersSecurityDetailsRoutes.AddSecurityConsigneesEoriController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsigneeName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsigneeNamePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "securityConsigneeName",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsigneeNameController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsignorName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsignorNamePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "securityConsignorName",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsignorNameController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsigneeAddress(index: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SecurityConsigneeAddressPage(index),
    formatAnswer = formatAsAddress,
    prefix = "securityConsigneeAddress",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsigneeAddressController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsignorAddress(index: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SecurityConsignorAddressPage(index),
    formatAnswer = formatAsAddress,
    prefix = "securityConsignorAddress",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsignorAddressController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsigneeEori(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsigneeEoriPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "securityConsigneeEori",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsigneeEoriController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsignorEori(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsignorEoriPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "securityConsignorEori",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsignorEoriController.onPageLoad(lrn, index, CheckMode)
  )

}
// scalastyle:on number.of.methods
