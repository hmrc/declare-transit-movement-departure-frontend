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
import models.reference.{DocumentType, MethodOfPayment}
import pages._
import pages.addItems._
import pages.addItems.containers._
import pages.addItems.securityDetails._
import pages.addItems.traderDetails._
import pages.addItems.traderSecurityDetails._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

// scalastyle:off number.of.methods
class AddItemsCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def transportCharges(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[MethodOfPayment](
    page = TransportChargesPage(itemIndex),
    format = x => lit"$x",
    prefix = "transportCharges",
    id = None,
    call = securityDetailsRoutes.TransportChargesController.onPageLoad(lrn, itemIndex, CheckMode)
  )

  def containerRow(itemIndex: Index, containerIndex: Index, userAnswers: UserAnswers): Option[Row] =
    userAnswers.get(ContainerNumberPage(itemIndex, containerIndex)).map {
      answer =>
        Row(
          key = Key(lit"$answer"),
          value = Value(lit""),
          actions = List(
            Action(
              content = msg"site.change",
              href = containerRoutes.ContainerNumberController.onPageLoad(userAnswers.lrn, itemIndex, containerIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer)),
              attributes = Map("id" -> s"change-container-${containerIndex.display}")
            )
          )
        )
    }

  def addAnotherContainer(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherContainerHref = containerRoutes.AddAnotherContainerController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherContainerHref, content)
  }

  def documentRows(index: Index, documentIndex: Index, documentType: DocumentTypeList): Option[Row] = {

    def actions(documentType: String): List[Action] = userAnswers.get(DeclarationTypePage) match {
      case Some(Option4) if index.position == 0 & documentIndex.position == 0 =>
        List(
          Action(
            content = msg"site.change",
            href = controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(userAnswers.lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"addAnotherDocument.documentList.change.hidden".withArgs(documentType)),
            attributes = Map("id" -> s"change-document-${index.display}")
          )
        )
      case _ =>
        List(
          Action(
            content = msg"site.change",
            href = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(userAnswers.lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"addAnotherDocument.documentList.change.hidden".withArgs(documentType)),
            attributes = Map("id" -> s"change-document-${index.display}")
          ),
          Action(
            content = msg"site.delete",
            href = controllers.addItems.documents.routes.ConfirmRemoveDocumentController.onPageLoad(userAnswers.lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"addAnotherDocument.documentList.delete.hidden".withArgs(documentType)),
            attributes = Map("id" -> s"remove-document-${index.display}")
          )
        )
    }

    userAnswers.get(DocumentTypePage(index, documentIndex)).flatMap {
      answer =>
        documentType.getDocumentType(answer) map {
          case DocumentType(code, description, _) =>
            Row(
              key = Key(lit"($code) $description"),
              value = Value(lit""),
              actions = actions(answer)
            )
        }
    }
  }

  def itemRows(index: Index): Option[Row] =
    userAnswers.get(ItemDescriptionPage(index)).map {
      answer =>
        Row(
          key = Key(lit"$answer"),
          value = Value(lit""),
          actions = List(
            Action(
              content = msg"site.change",
              href = routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.lrn, index).url,
              visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.change.hidden".withArgs(answer)),
              attributes = Map("id" -> s"change-item-${index.display}")
            ),
            Action(
              content = msg"site.delete",
              href = routes.ConfirmRemoveItemController.onPageLoad(userAnswers.lrn, index).url,
              visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.delete.hidden".withArgs(answer)),
              attributes = Map("id" -> s"remove-item-${index.display}")
            )
          )
        )
    }

  def addDocuments(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddDocumentsPage(itemIndex),
    format = yesOrNo,
    prefix = "addDocuments",
    id = None,
    call = controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(lrn, itemIndex, CheckMode),
    args = itemIndex.display
  )

  def traderDetailsConsignorName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsignorNamePage(index),
    format = x => lit"$x",
    prefix = "traderDetailsConsignorName",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsignorEoriNumber(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsignorEoriNumberPage(index),
    format = x => lit"$x",
    prefix = "traderDetailsConsignorEoriNumber",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsignorEoriKnown(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = TraderDetailsConsignorEoriKnownPage(index),
    format = yesOrNo,
    prefix = "traderDetailsConsignorEoriKnown",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsignorAddress(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = TraderDetailsConsignorAddressPage(itemIndex),
    format = address,
    prefix = "traderDetailsConsignorAddress",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(lrn, itemIndex, CheckMode),
    args = userAnswers.get(TraderDetailsConsignorNamePage(itemIndex)).getOrElse(msg"traderDetailsConsignorAddress.checkYourAnswersLabel.fallback")
  )

  def traderDetailsConsigneeName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsigneeNamePage(index),
    format = x => lit"$x",
    prefix = "traderDetailsConsigneeName",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsigneeEoriNumber(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TraderDetailsConsigneeEoriNumberPage(index),
    format = x => lit"$x",
    prefix = "traderDetailsConsigneeEoriNumber",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsigneeEoriKnown(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = TraderDetailsConsigneeEoriKnownPage(index),
    format = yesOrNo,
    prefix = "traderDetailsConsigneeEoriKnown",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def traderDetailsConsigneeAddress(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = TraderDetailsConsigneeAddressPage(itemIndex),
    format = address,
    prefix = "traderDetailsConsigneeAddress",
    id = None,
    call = traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(lrn, itemIndex, CheckMode),
    args = userAnswers.get(TraderDetailsConsigneeNamePage(itemIndex)).getOrElse(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel.fallback")
  )

  def commodityCode(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = CommodityCodePage(index),
    format = x => lit"$x",
    prefix = "commodityCode",
    id = Some("change-commodity-code"),
    call = controllers.addItems.itemDetails.routes.CommodityCodeController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def totalNetMass(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = TotalNetMassPage(index),
    format = x => lit"$x",
    prefix = "totalNetMass",
    id = Some("change-total-net-mass"),
    call = controllers.addItems.itemDetails.routes.TotalNetMassController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def isCommodityCodeKnown(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = IsCommodityCodeKnownPage(index),
    format = yesOrNo,
    prefix = "isCommodityCodeKnown",
    id = Some("change-is-commodity-known"),
    call = controllers.addItems.itemDetails.routes.IsCommodityCodeKnownController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def addTotalNetMass(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddTotalNetMassPage(index),
    format = yesOrNo,
    prefix = "addTotalNetMass",
    id = Some("change-add-total-net-mass"),
    call = controllers.addItems.itemDetails.routes.AddTotalNetMassController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def itemTotalGrossMass(index: Index): Option[Row] = getAnswerAndBuildRow[Double](
    page = ItemTotalGrossMassPage(index),
    format = x => lit"$x",
    prefix = "itemTotalGrossMass",
    id = Some("change-item-total-gross-mass"),
    call = controllers.addItems.itemDetails.routes.ItemTotalGrossMassController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def itemDescription(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = ItemDescriptionPage(index),
    format = x => lit"$x",
    prefix = "itemDescription",
    id = Some("change-item-description"),
    call = controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(lrn, index, CheckMode),
    args = index.display
  )

  def previousReferenceRows(index: Index, referenceIndex: Index, previousDocumentType: PreviousReferencesDocumentTypeList): Option[Row] =
    userAnswers.get(ReferenceTypePage(index, referenceIndex)) flatMap {
      answer =>
        previousDocumentType.getPreviousReferencesDocumentType(answer) map {
          referenceType =>
            Row(
              key = Key(lit"(${referenceType.code}) ${referenceType.description.getOrElse("")}"),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.change",
                  href = previousReferencesRoutes.ReferenceTypeController.onPageLoad(userAnswers.lrn, index, referenceIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer)),
                  attributes = Map("id" -> s"change-item-${index.display}")
                )
              )
            )
        }
    }

  def previousAdministrativeReferenceRows(
    index: Index,
    referenceIndex: Index,
    previousDocumentType: PreviousReferencesDocumentTypeList,
    mode: Mode
  ): Option[Row] =
    userAnswers.get(ReferenceTypePage(index, referenceIndex)) flatMap {
      answer =>
        previousDocumentType.getPreviousReferencesDocumentType(answer) map {
          referenceType =>
            Row(
              key = Key(lit"(${referenceType.code}) ${referenceType.description.getOrElse("")}"),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.change",
                  href = previousReferencesRoutes.ReferenceTypeController.onPageLoad(userAnswers.lrn, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer)),
                  attributes = Map("id" -> s"change-reference-document-type-${index.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = previousReferencesRoutes.ConfirmRemovePreviousAdministrativeReferenceController
                    .onPageLoad(userAnswers.lrn, index, referenceIndex, mode)
                    .url,
                  visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(answer)),
                  attributes = Map("id" -> s"remove-reference-document-type-${index.display}")
                )
              )
            )
        }
    }

  def addAdministrativeReference(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddAdministrativeReferencePage(index),
    format = yesOrNo,
    prefix = "addAdministrativeReference",
    id = None,
    call = previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(lrn, index, CheckMode)
  )

  def addAnotherPreviousReferences(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherPackageHref = previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherPackageHref, content)
  }

  def packageRow(itemIndex: Index, packageIndex: Index): Option[Row] =
    userAnswers.get(PackageTypePage(itemIndex, packageIndex)).map {
      answer =>
        Row(
          key = Key(msg"packageType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(lit"$answer"),
          actions = List(
            Action(
              content = msg"site.edit",
              href = controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(userAnswers.lrn, itemIndex, packageIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer.toString)),
              attributes = Map("id" -> s"change-package-${packageIndex.display}")
            )
          )
        )
    }

  def numberOfPackages(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[Int](
    page = HowManyPackagesPage(itemIndex, packageIndex),
    format = x => lit"$x",
    prefix = "declareNumberOfPackages",
    id = None,
    call = controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(userAnswers.lrn, itemIndex, packageIndex, CheckMode)
  )

  def totalPieces(itemIndex: Index, packageIndex: Index): Option[Row] = getAnswerAndBuildRow[Int](
    page = TotalPiecesPage(itemIndex, packageIndex),
    format = x => lit"$x",
    prefix = "totalPieces",
    id = None,
    call = controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(userAnswers.lrn, itemIndex, packageIndex, CheckMode)
  )

  def addAnotherPackage(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherPackageHref = controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherPackageHref, content)
  }

  def addAnotherDocument(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherDocumentHref = controllers.addItems.documents.routes.AddAnotherDocumentController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherDocumentHref, content)
  }

  def documentRow(itemIndex: Index, documentIndex: Index, userAnswers: UserAnswers, documentTypeList: DocumentTypeList): Option[Row] = {

    def actions(updatedAnswer: String): List[Action] = userAnswers.get(DeclarationTypePage) match {
      case Some(Option4) if itemIndex.position == 0 & documentIndex.position == 0 =>
        List(
          Action(
            content = msg"site.change",
            href = controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(userAnswers.lrn, itemIndex, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(updatedAnswer)),
            attributes = Map("id" -> s"change-document-${documentIndex.display}")
          )
        )
      case _ =>
        List(
          Action(
            content = msg"site.change",
            href = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(userAnswers.lrn, itemIndex, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(updatedAnswer)),
            attributes = Map("id" -> s"change-document-${documentIndex.display}")
          )
        )
    }

    userAnswers.get(DocumentTypePage(itemIndex, documentIndex)).flatMap {
      answer =>
        documentTypeList.getDocumentType(answer).map {
          documentType =>
            val updatedAnswer = s"(${documentType.code}) ${documentType.description}"
            Row(
              key = Key(lit"$updatedAnswer"),
              value = Value(lit""),
              actions = actions(answer)
            )
        }
    }
  }

  def commercialReferenceNumber(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = CommercialReferenceNumberPage(itemIndex),
    format = x => lit"$x",
    prefix = "commercialReferenceNumber",
    id = None,
    call = securityDetailsRoutes.CommercialReferenceNumberController.onPageLoad(lrn, itemIndex, CheckMode)
  )

  def addDangerousGoodsCode(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddDangerousGoodsCodePage(itemIndex),
    format = yesOrNo,
    prefix = "addDangerousGoodsCode",
    id = None,
    call = securityDetailsRoutes.AddDangerousGoodsCodeController.onPageLoad(lrn, itemIndex, CheckMode)
  )

  def dangerousGoodsCode(itemIndex: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = DangerousGoodsCodePage(itemIndex),
    format = x => lit"$x",
    prefix = "dangerousGoodsCode",
    id = None,
    call = securityDetailsRoutes.DangerousGoodsCodeController.onPageLoad(lrn, itemIndex, CheckMode)
  )

  def addSecurityConsignorsEori(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSecurityConsignorsEoriPage(index),
    format = yesOrNo,
    prefix = "addSecurityConsignorsEori",
    id = None,
    call = tradersSecurityDetailsRoutes.AddSecurityConsignorsEoriController.onPageLoad(lrn, index, CheckMode)
  )

  def addSecurityConsigneesEori(index: Index): Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddSecurityConsigneesEoriPage(index),
    format = yesOrNo,
    prefix = "addSecurityConsigneesEori",
    id = None,
    call = tradersSecurityDetailsRoutes.AddSecurityConsigneesEoriController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsigneeName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsigneeNamePage(index),
    format = x => lit"$x",
    prefix = "securityConsigneeName",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsigneeNameController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsignorName(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsignorNamePage(index),
    format = x => lit"$x",
    prefix = "securityConsignorName",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsignorNameController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsigneeAddress(index: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SecurityConsigneeAddressPage(index),
    format = address,
    prefix = "securityConsigneeAddress",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsigneeAddressController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsignorAddress(index: Index): Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = SecurityConsignorAddressPage(index),
    format = address,
    prefix = "securityConsignorAddress",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsignorAddressController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsigneeEori(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsigneeEoriPage(index),
    format = x => lit"$x",
    prefix = "securityConsigneeEori",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsigneeEoriController.onPageLoad(lrn, index, CheckMode)
  )

  def securityConsignorEori(index: Index): Option[Row] = getAnswerAndBuildRow[String](
    page = SecurityConsignorEoriPage(index),
    format = x => lit"$x",
    prefix = "securityConsignorEori",
    id = None,
    call = tradersSecurityDetailsRoutes.SecurityConsignorEoriController.onPageLoad(lrn, index, CheckMode)
  )

}
// scalastyle:on number.of.methods
