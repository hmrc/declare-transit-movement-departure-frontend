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
import controllers.addItems.documents.{routes => documentRoutes}
import controllers.addItems.packagesInformation.{routes => packageRoutes}
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
class AddItemsCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode) extends CheckYourAnswersHelper(userAnswers) {

  def transportCharges(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[MethodOfPayment](
    page = TransportChargesPage(itemIndex),
    formatAnswer = formatAsLiteral,
    prefix = "transportCharges",
    id = None,
    call = securityDetailsRoutes.TransportChargesController.onPageLoad(lrn, itemIndex, mode)
  )

  def containerSectionRow(itemIndex: Index, containerIndex: Index): Option[Row] = getAnswerAndBuildSectionRow[String](
    page = ContainerNumberPage(itemIndex, containerIndex),
    formatAnswer = formatAsLiteral,
    label = msg"addAnotherContainer.containerList.label".withArgs(containerIndex.display),
    id = Some(s"change-container-${containerIndex.display}"),
    call = containerRoutes.ContainerNumberController.onPageLoad(lrn, itemIndex, containerIndex, mode)
  )

  def addAnotherContainer(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherContainerHref = containerRoutes.AddAnotherContainerController.onPageLoad(lrn, itemIndex, mode).url

    AddAnotherViewModel(addAnotherContainerHref, content)
  }

  def documentRow(index: Index, documentIndex: Index, documentTypes: DocumentTypeList): Option[Row] =
    getAnswerAndBuildDocumentRow(index, documentIndex, documentTypes)(
      label =>
        buildRemovableRow(
          label = label,
          id = s"document-${index.display}-${documentIndex.display}",
          changeCall = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(lrn, index, documentIndex, mode),
          removeCall = controllers.addItems.documents.routes.ConfirmRemoveDocumentController.onPageLoad(lrn, index, documentIndex, mode)
        )
    )

  def documentSectionRow(index: Index, documentIndex: Index, documentTypes: DocumentTypeList): Option[Row] =
    getAnswerAndBuildDocumentRow(index, documentIndex, documentTypes)(
      label =>
        buildSectionRow(
          label = msg"addDocuments.documentList.label".withArgs(documentIndex.display),
          answer = label,
          id = Some(s"change-document-${index.display}-${documentIndex.display}"),
          call = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(lrn, index, documentIndex, mode)
        )
    )

  def itemRow(index: Index): Option[Row] = getAnswerAndBuildRemovableRow[String](
    page = ItemDescriptionPage(index),
    formatAnswer = formatAsLiteral,
    id = s"item-${index.display}",
    changeCall = routes.ItemsCheckYourAnswersController.onPageLoad(lrn, index),
    removeCall = routes.ConfirmRemoveItemController.onPageLoad(lrn, index)
  )

  def addDocuments(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddDocumentsPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "addDocuments",
    id = None,
    call = controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(lrn, itemIndex, mode),
    args = itemIndex.display
  )

  def traderDetailsConsignorName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsignorNamePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "traderDetailsConsignorName",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def traderDetailsConsignorEoriNumber(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsignorEoriNumberPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "traderDetailsConsignorEoriNumber",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def traderDetailsConsignorEoriKnown(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = TraderDetailsConsignorEoriKnownPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetailsConsignorEoriKnown",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def traderDetailsConsignorAddress(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = TraderDetailsConsignorAddressPage(itemIndex),
    formatAnswer = formatAsAddress,
    prefix = "traderDetailsConsignorAddress",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(lrn, itemIndex, mode),
    args = userAnswers.get(TraderDetailsConsignorNamePage(itemIndex)).getOrElse(msg"traderDetailsConsignorAddress.checkYourAnswersLabel.fallback")
  )

  def traderDetailsConsigneeName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsigneeNamePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "traderDetailsConsigneeName",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def traderDetailsConsigneeEoriNumber(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsigneeEoriNumberPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "traderDetailsConsigneeEoriNumber",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def traderDetailsConsigneeEoriKnown(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = TraderDetailsConsigneeEoriKnownPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetailsConsigneeEoriKnown",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def traderDetailsConsigneeAddress(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = TraderDetailsConsigneeAddressPage(itemIndex),
    formatAnswer = formatAsAddress,
    prefix = "traderDetailsConsigneeAddress",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(lrn, itemIndex, mode),
    args = userAnswers.get(TraderDetailsConsigneeNamePage(itemIndex)).getOrElse(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel.fallback")
  )

  def commodityCode(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = CommodityCodePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "commodityCode",
    id = Some("change-commodity-code"),
    call = controllers.addItems.itemDetails.routes.CommodityCodeController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def totalNetMass(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TotalNetMassPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "totalNetMass",
    id = Some("change-total-net-mass"),
    call = controllers.addItems.itemDetails.routes.TotalNetMassController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def isCommodityCodeKnown(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = IsCommodityCodeKnownPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "isCommodityCodeKnown",
    id = Some("change-is-commodity-known"),
    call = controllers.addItems.itemDetails.routes.IsCommodityCodeKnownController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def addTotalNetMass(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddTotalNetMassPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "addTotalNetMass",
    id = Some("change-add-total-net-mass"),
    call = controllers.addItems.itemDetails.routes.AddTotalNetMassController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def itemTotalGrossMass(index: Index): Option[Row] = getAnswerAndBuildRow[Double](
    page = ItemTotalGrossMassPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "itemTotalGrossMass",
    id = Some("change-item-total-gross-mass"),
    call = controllers.addItems.itemDetails.routes.ItemTotalGrossMassController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def itemDescription(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = ItemDescriptionPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "itemDescription",
    id = Some("change-item-description"),
    call = controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(lrn, index, mode),
    args = index.display
  )

  def previousReferenceSectionRow(index: Index, referenceIndex: Index, documents: PreviousReferencesDocumentTypeList): Option[Row] =
    getAnswerAndBuildPreviousReferenceRow(
      page = ReferenceTypePage(index, referenceIndex),
      documents = documents,
      buildRow = answer =>
        buildSectionRow(
          label = msg"addAdministrativeReference.administrativeReferenceList.label".withArgs(referenceIndex.display),
          answer = answer,
          id = Some(s"change-item-${index.display}-${referenceIndex.display}"),
          call = previousReferencesRoutes.ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, mode)
        )
    )

  def previousAdministrativeReferenceRow(index: Index, referenceIndex: Index, documents: PreviousReferencesDocumentTypeList): Option[Row] =
    getAnswerAndBuildPreviousReferenceRow(
      page = ReferenceTypePage(index, referenceIndex),
      documents = documents,
      buildRow = label =>
        buildRemovableRow(
          label = label,
          id = s"reference-document-type-${index.display}-${referenceIndex.display}",
          changeCall = previousReferencesRoutes.ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, mode),
          removeCall = previousReferencesRoutes.ConfirmRemovePreviousAdministrativeReferenceController.onPageLoad(lrn, index, referenceIndex, mode)
        )
    )

  def addAdministrativeReference(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddAdministrativeReferencePage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "addAdministrativeReference",
    id = None,
    call = previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(lrn, index, mode)
  )

  def addAnotherPreviousReferences(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherPackageHref = userAnswers.get(AddAdministrativeReferencePage(itemIndex)) match {
      case Some(true) => previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(lrn, itemIndex, mode).url
      case _          => previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(lrn, itemIndex, mode).url
    }

    AddAnotherViewModel(addAnotherPackageHref, content)
  }

  def packageRow(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRemovableRow[PackageType](
    page = PackageTypePage(itemIndex, packageIndex),
    formatAnswer = formatAsLiteral,
    id = s"package-${packageIndex.display}",
    changeCall = controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(lrn, itemIndex, packageIndex, mode),
    removeCall = controllers.addItems.packagesInformation.routes.RemovePackageController.onPageLoad(lrn, itemIndex, packageIndex, mode)
  )

  def packageSectionRow(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildSectionRow[PackageType](
    page = PackageTypePage(itemIndex, packageIndex),
    formatAnswer = formatAsLiteral,
    label = msg"addAnotherPackage.packageList.label".withArgs(packageIndex.display),
    id = Some(s"change-package-${packageIndex.display}"),
    call = packageRoutes.PackagesCheckYourAnswersController.onPageLoad(lrn, itemIndex, packageIndex, mode)
  )

  def packageType(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[PackageType](
    page = PackageTypePage(itemIndex, packageIndex),
    formatAnswer = formatAsLiteral,
    prefix = "packageType",
    id = Some("change-package-type"),
    call = controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(lrn, itemIndex, packageIndex, mode)
  )

  def numberOfPackages(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[Int](
    page = HowManyPackagesPage(itemIndex, packageIndex),
    formatAnswer = formatAsLiteral,
    prefix = "howManyPackages",
    id = Some("change-number-of-packages"),
    call = controllers.addItems.packagesInformation.routes.HowManyPackagesController.onPageLoad(lrn, itemIndex, packageIndex, mode)
  )

  def totalPieces(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[Int](
    page = TotalPiecesPage(itemIndex, packageIndex),
    formatAnswer = formatAsLiteral,
    prefix = "totalPieces",
    id = Some("change-total-pieces"),
    call = controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(lrn, itemIndex, packageIndex, mode)
  )

  def addMark(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddMarkPage(itemIndex, packageIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "addMark",
    id = Some("change-add-mark"),
    call = controllers.addItems.packagesInformation.routes.AddMarkController.onPageLoad(lrn, itemIndex, packageIndex, mode)
  )

  def mark(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = DeclareMarkPage(itemIndex, packageIndex),
    formatAnswer = formatAsLiteral,
    prefix = "declareMark",
    id = Some("change-mark"),
    call = controllers.addItems.packagesInformation.routes.DeclareMarkController.onPageLoad(lrn, itemIndex, packageIndex, mode)
  )

  def addAnotherPackage(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherPackageHref = controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(lrn, itemIndex, mode).url

    AddAnotherViewModel(addAnotherPackageHref, content)
  }

  def addAnotherDocument(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherDocumentHref = userAnswers.get(AddDocumentsPage(itemIndex)) match {
      case Some(true) => controllers.addItems.documents.routes.AddAnotherDocumentController.onPageLoad(lrn, itemIndex, mode).url
      case _          => controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(lrn, itemIndex, mode).url
    }

    AddAnotherViewModel(addAnotherDocumentHref, content)
  }

  def commercialReferenceNumber(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = CommercialReferenceNumberPage(itemIndex),
    formatAnswer = formatAsLiteral,
    prefix = "commercialReferenceNumber",
    id = None,
    call = securityDetailsRoutes.CommercialReferenceNumberController.onPageLoad(lrn, itemIndex, mode)
  )

  def addDangerousGoodsCode(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddDangerousGoodsCodePage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "addDangerousGoodsCode",
    id = None,
    call = securityDetailsRoutes.AddDangerousGoodsCodeController.onPageLoad(lrn, itemIndex, mode)
  )

  def dangerousGoodsCode(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = DangerousGoodsCodePage(itemIndex),
    formatAnswer = formatAsLiteral,
    prefix = "dangerousGoodsCode",
    id = None,
    call = securityDetailsRoutes.DangerousGoodsCodeController.onPageLoad(lrn, itemIndex, mode)
  )

  def addSecurityConsignorsEori(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSecurityConsignorsEoriPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "addSecurityConsignorsEori",
    id = None,
    call = tradersSecurityDetailsRoutes.AddSecurityConsignorsEoriController.onPageLoad(lrn, index, mode)
  )

  def addSecurityConsigneesEori(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSecurityConsigneesEoriPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "addSecurityConsigneesEori",
    id = None,
    call = tradersSecurityDetailsRoutes.AddSecurityConsigneesEoriController.onPageLoad(lrn, index, mode)
  )

  def securityConsigneeName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsigneeNamePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "securityConsigneeName",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsigneeNameController.onPageLoad(lrn, index, mode)
  )

  def securityConsignorName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsignorNamePage(index),
    formatAnswer = formatAsLiteral,
    prefix = "securityConsignorName",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsignorNameController.onPageLoad(lrn, index, mode)
  )

  def securityConsigneeAddress(index: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SecurityConsigneeAddressPage(index),
    formatAnswer = formatAsAddress,
    prefix = "securityConsigneeAddress",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsigneeAddressController.onPageLoad(lrn, index, mode)
  )

  def securityConsignorAddress(index: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SecurityConsignorAddressPage(index),
    formatAnswer = formatAsAddress,
    prefix = "securityConsignorAddress",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsignorAddressController.onPageLoad(lrn, index, mode)
  )

  def securityConsigneeEori(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsigneeEoriPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "securityConsigneeEori",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsigneeEoriController.onPageLoad(lrn, index, mode)
  )

  def securityConsignorEori(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsignorEoriPage(index),
    formatAnswer = formatAsLiteral,
    prefix = "securityConsignorEori",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsignorEoriController.onPageLoad(lrn, index, mode)
  )

  def referenceTypeRow(index: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = ReferenceTypePage(index, referenceIndex),
    formatAnswer = formatAsLiteral,
    prefix = "referenceType",
    id = Some(s"change-reference-type-${referenceIndex.display}"),
    call = previousReferencesRoutes.ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, mode)
  )

  def previousReferenceRow(index: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = PreviousReferencePage(index, referenceIndex),
    formatAnswer = formatAsLiteral,
    prefix = "previousReference",
    id = Some(s"change-previous-reference-${referenceIndex.display}"),
    call = previousReferencesRoutes.PreviousReferenceController.onPageLoad(lrn, index, referenceIndex, mode)
  )

  def addExtraReferenceInformationRow(index: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddExtraInformationPage(index, referenceIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "addExtraInformation",
    id = Some(s"change-add-extra-reference-information-${referenceIndex.display}"),
    call = previousReferencesRoutes.AddExtraInformationController.onPageLoad(lrn, index, referenceIndex, mode)
  )

  def extraReferenceInformationRow(index: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = ExtraInformationPage(index, referenceIndex),
    formatAnswer = formatAsLiteral,
    prefix = "extraInformation",
    id = Some(s"change-extra-reference-information-${referenceIndex.display}"),
    call = previousReferencesRoutes.ExtraInformationController.onPageLoad(lrn, index, referenceIndex, mode)
  )

  def tirCarnetReferenceRow(itemIndex: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TIRCarnetReferencePage(itemIndex, referenceIndex),
    formatAnswer = formatAsLiteral,
    prefix = "tirCarnetReference",
    id = Some(s"change-tir-carnet-reference-${referenceIndex.display}"),
    call = documentRoutes.TIRCarnetReferenceController.onPageLoad(lrn, itemIndex, referenceIndex, mode)
  )

  def documentTypeRow(itemIndex: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = DocumentTypePage(itemIndex, referenceIndex),
    formatAnswer = formatAsLiteral,
    prefix = "documentType",
    id = Some(s"change-document-type-${referenceIndex.display}"),
    call = documentRoutes.DocumentTypeController.onPageLoad(lrn, itemIndex, referenceIndex, mode)
  )

  def documentReferenceRow(itemIndex: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = DocumentReferencePage(itemIndex, referenceIndex),
    formatAnswer = formatAsLiteral,
    prefix = "documentReference",
    id = Some(s"change-document-reference-${referenceIndex.display}"),
    call = documentRoutes.DocumentReferenceController.onPageLoad(lrn, itemIndex, referenceIndex, mode)
  )

  def addExtraDocumentInformationRow(itemIndex: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddExtraDocumentInformationPage(itemIndex, referenceIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "addExtraDocumentInformation",
    id = Some(s"change-add-extra-document-information-${referenceIndex.display}"),
    call = documentRoutes.AddExtraDocumentInformationController.onPageLoad(lrn, itemIndex, referenceIndex, mode)
  )

  def extraDocumentInformationRow(itemIndex: Index, referenceIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = DocumentExtraInformationPage(itemIndex, referenceIndex),
    formatAnswer = formatAsLiteral,
    prefix = "documentExtraInformation",
    id = Some(s"change-extra-document-information-${referenceIndex.display}"),
    call = documentRoutes.DocumentExtraInformationController.onPageLoad(lrn, itemIndex, referenceIndex, mode)
  )

  private def getAnswerAndBuildPreviousReferenceRow(
    page: QuestionPage[String],
    documents: PreviousReferencesDocumentTypeList,
    buildRow: Text => Row
  ): Option[Row] = userAnswers.get(page) flatMap {
    answer =>
      documents.getPreviousReferencesDocumentType(answer) map {
        doc =>
          buildRow(lit"(${doc.code}) ${doc.description.getOrElse("")}")
      }
  }

  private def getAnswerAndBuildDocumentRow(
    index: Index,
    documentIndex: Index,
    documentTypes: DocumentTypeList
  )(
    buildAlternativeRow: Text => Row
  ): Option[Row] =
    userAnswers.get(DocumentTypePage(index, documentIndex)).flatMap {
      answer =>
        documentTypes.getDocumentType(answer).map {
          documentType =>
            val label = lit"(${documentType.code}) ${documentType.description}"

            userAnswers.get(DeclarationTypePage) match {
              case Some(Option4) if index.position == 0 & documentIndex.position == 0 =>
                buildValuelessRow(
                  label = label,
                  id = Some(s"change-document-${index.display}-${documentIndex.display}"),
                  call = controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(lrn, index, documentIndex, mode)
                )
              case _ =>
                buildAlternativeRow(label)
            }
        }
    }

}
// scalastyle:on number.of.methods
