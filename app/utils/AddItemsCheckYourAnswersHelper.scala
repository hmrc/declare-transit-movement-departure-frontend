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
import models.reference.DocumentType
import pages.addItems._
import pages.addItems.containers._
import pages.addItems.securityDetails._
import pages.addItems.traderDetails._
import pages.addItems.traderSecurityDetails._
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

class AddItemsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def transportCharges(itemIndex: Index): Option[Row] = userAnswers.get(TransportChargesPage(itemIndex)) map {
    answer =>
      Row(
        key = Key(msg"transportCharges.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"${answer.toString}"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = securityDetailsRoutes.TransportChargesController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"transportCharges.checkYourAnswersLabel"))
          )
        )
      )
  }

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
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer.toString)),
              attributes = Map("id" -> s"""change-container-${containerIndex.display}""")
            )
          )
        )
    }

  def addAnotherContainer(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherContainerHref = containerRoutes.AddAnotherContainerController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherContainerHref, content)
  }

  def documentRows(index: Index, documentIndex: Index, documentType: DocumentTypeList): Option[Row] = {

    def actions(documentType: String) = userAnswers.get(DeclarationTypePage) match {
      case Some(Option4) if index.position == 0 & documentIndex.position == 0 =>
        List(
          Action(
            content = msg"site.change",
            href = controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(userAnswers.lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"addAnotherDocument.documentList.change.hidden".withArgs(documentType)),
            attributes = Map("id" -> s"""change-document-${index.display}""")
          )
        )
      case _ =>
        List(
          Action(
            content = msg"site.change",
            href = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(userAnswers.lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"addAnotherDocument.documentList.change.hidden".withArgs(documentType)),
            attributes = Map("id" -> s"""change-document-${index.display}""")
          ),
          Action(
            content = msg"site.delete",
            href = controllers.addItems.documents.routes.ConfirmRemoveDocumentController.onPageLoad(userAnswers.lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"addAnotherDocument.documentList.delete.hidden".withArgs(documentType)),
            attributes = Map("id" -> s"""remove-document-${index.display}""")
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
              attributes = Map("id" -> s"""change-item-${index.display}""")
            ),
            Action(
              content = msg"site.delete",
              href = routes.ConfirmRemoveItemController.onPageLoad(userAnswers.lrn, index).url,
              visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.delete.hidden".withArgs(answer)),
              attributes = Map("id" -> s"""remove-item-${index.display}""")
            )
          )
        )
    }

  def addDocuments(itemIndex: Index): Option[Row] = userAnswers.get(AddDocumentsPage(itemIndex)) map {
    answer =>
      Row(
        key = Key(msg"addDocuments.checkYourAnswersLabel".withArgs(itemIndex.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addDocuments.checkYourAnswersLabel"))
          )
        )
      )
  }

  def documentReference(itemIndex: Index, documentIndex: Index): Option[Row] = userAnswers.get(DocumentReferencePage(itemIndex, documentIndex)) map {
    answer =>
      Row(
        key = Key(msg"documentReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.documents.routes.DocumentReferenceController.onPageLoad(lrn, itemIndex, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"documentReference.checkYourAnswersLabel"))
          )
        )
      )
  }

  def documentExtraInformation(index: Index, documentIndex: Index): Option[Row] = userAnswers.get(DocumentExtraInformationPage(index, documentIndex)) map {
    answer =>
      Row(
        key = Key(msg"documentExtraInformation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.documents.routes.DocumentExtraInformationController.onPageLoad(lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"documentExtraInformation.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorName(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorNamePage(index)) map {
    answer =>
      Row(
        key = Key(msg"traderDetailsConsignorName.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorEoriNumber(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorEoriNumberPage(index)) map {
    answer =>
      Row(
        key = Key(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorEoriKnown(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorEoriKnownPage(index)) map {
    answer =>
      Row(
        key = Key(msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorAddress(itemIndex: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorAddressPage(itemIndex)) map {
    answer =>
      val consignorsName =
        userAnswers.get(TraderDetailsConsignorNamePage(itemIndex)).getOrElse(msg"traderDetailsConsignorAddress.checkYourAnswersLabel.fallback")
      val address = Html(
        Seq(answer.AddressLine1, answer.AddressLine2, answer.postalCode, answer.country.description)
          .mkString("<br>")
      )
      Row(
        key = Key(msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consignorsName), classes = Seq("govuk-!-width-one-half")),
        value = Value(address),
        actions = List(
          Action(
            content = msg"site.edit",
            href = traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consignorsName)))
          )
        )
      )
  }

  def traderDetailsConsigneeName(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeNamePage(index)) map {
    answer =>
      Row(
        key = Key(msg"traderDetailsConsigneeName.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeEoriNumber(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeEoriNumberPage(index)) map {
    answer =>
      Row(
        key = Key(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeEoriKnown(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeEoriKnownPage(index)) map {
    answer =>
      Row(
        key = Key(msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeAddress(itemIndex: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeAddressPage(itemIndex)) map {
    answer =>
      val consigneesName =
        userAnswers.get(TraderDetailsConsigneeNamePage(itemIndex)).getOrElse(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel.fallback")
      val address = Html(
        Seq(answer.AddressLine1, answer.AddressLine2, answer.postalCode, answer.country.description)
          .mkString("<br>")
      )
      Row(
        key = Key(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneesName), classes = Seq("govuk-!-width-one-half")),
        value = Value(address),
        actions = List(
          Action(
            content = msg"site.edit",
            href = traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneesName)))
          )
        )
      )
  }

  def commodityCode(index: Index): Option[Row] = userAnswers.get(CommodityCodePage(index)) map {
    answer =>
      Row(
        key = Key(msg"commodityCode.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.itemDetails.routes.CommodityCodeController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"commodityCode.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-commodity-code")
          )
        )
      )
  }

  def totalNetMass(index: Index): Option[Row] = userAnswers.get(TotalNetMassPage(index)) map {
    answer =>
      Row(
        key = Key(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.itemDetails.routes.TotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display))),
            attributes = Map("id" -> "change-total-net-mass")
          )
        )
      )
  }

  def isCommodityCodeKnown(index: Index): Option[Row] = userAnswers.get(IsCommodityCodeKnownPage(index)) map {
    answer =>
      Row(
        key = Key(msg"isCommodityCodeKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.itemDetails.routes.IsCommodityCodeKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isCommodityCodeKnown.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-is-commodity-known")
          )
        )
      )
  }

  def addTotalNetMass(index: Index): Option[Row] = userAnswers.get(AddTotalNetMassPage(index)) map {
    answer =>
      Row(
        key = Key(msg"addTotalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.itemDetails.routes.AddTotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addTotalNetMass.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-add-total-net-mass")
          )
        )
      )
  }

  def itemTotalGrossMass(index: Index): Option[Row] = userAnswers.get(ItemTotalGrossMassPage(index)) map {
    answer =>
      Row(
        key = Key(msg"itemTotalGrossMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.itemDetails.routes.ItemTotalGrossMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemTotalGrossMass.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-item-total-gross-mass")
          )
        )
      )
  }

  def itemDescription(index: Index): Option[Row] = userAnswers.get(ItemDescriptionPage(index)) map {
    answer =>
      Row(
        key = Key(msg"itemDescription.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemDescription.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-item-description")
          )
        )
      )
  }

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
                  attributes = Map("id" -> s"""change-item-${index.display}""")
                )
              )
            )
        }
    }

  def previousAdministrativeReferenceRows(index: Index,
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
                  attributes = Map("id" -> s"""change-reference-document-type-${index.display}""")
                ),
                Action(
                  content = msg"site.delete",
                  href = previousReferencesRoutes.ConfirmRemovePreviousAdministrativeReferenceController
                    .onPageLoad(userAnswers.lrn, index, referenceIndex, mode)
                    .url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer)),
                  attributes = Map("id" -> s"""remove-reference-document-type-${index.display}""")
                )
              )
            )
        }
    }

  def addAdministrativeReference(index: Index): Option[Row] =
    userAnswers.get(AddAdministrativeReferencePage(index)) map {
      answer =>
        Row(
          key = Key(msg"addAdministrativeReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(yesOrNo(answer)),
          actions = List(
            Action(
              content = msg"site.edit",
              href = previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(lrn, index, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addAdministrativeReference.checkYourAnswersLabel"))
            )
          )
        )
    }

  def addExtraInformation(itemIndex: Index, referenceIndex: Index): Option[Row] = userAnswers.get(AddExtraInformationPage(itemIndex, referenceIndex)) map {
    answer =>
      Row(
        key = Key(msg"addExtraInformation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = previousReferencesRoutes.AddExtraInformationController.onPageLoad(lrn, itemIndex, referenceIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addExtraInformation.checkYourAnswersLabel"))
          )
        )
      )
  }

  def referenceType(itemIndex: Index, referenceIndex: Index): Option[Row] = userAnswers.get(ReferenceTypePage(itemIndex, referenceIndex)) map {
    answer =>
      Row(
        key = Key(msg"referenceType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = previousReferencesRoutes.ReferenceTypeController.onPageLoad(lrn, itemIndex, referenceIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"referenceType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def previousReference(index: Index, referenceIndex: Index): Option[Row] = userAnswers.get(addItems.PreviousReferencePage(index, referenceIndex)) map {
    answer =>
      Row(
        key = Key(msg"previousReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = previousReferencesRoutes.PreviousReferenceController.onPageLoad(lrn, index, referenceIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"previousReference.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addAnotherPreviousReferences(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherPackageHref = previousReferencesRoutes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherPackageHref, content)
  }

  def packageRow(itemIndex: Index, packageIndex: Index, userAnswers: UserAnswers): Option[Row] =
    userAnswers.get(PackageTypePage(itemIndex, packageIndex)).map {
      answer =>
        Row(
          key = Key(msg"packageType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(lit"$answer"),
          actions = List(
            Action(
              content = msg"site.change",
              href = controllers.addItems.packagesInformation.routes.PackageTypeController.onPageLoad(userAnswers.lrn, itemIndex, packageIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer.toString)),
              attributes = Map("id" -> s"""change-package-${packageIndex.display}""")
            )
          )
        )
    }

  def numberOfPackages(itemIndex: Index, packageIndex: Index, userAnswers: UserAnswers): Option[Row] =
    userAnswers.get(HowManyPackagesPage(itemIndex, packageIndex)).map {
      answer =>
        Row(
          key = Key(msg"declareNumberOfPackages.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(lit"$answer"),
          actions = List(
            Action(
              content = msg"site.edit",
              href =
                controllers.addItems.packagesInformation.routes.HowManyPackagesController.onPageLoad(userAnswers.lrn, itemIndex, packageIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declareNumberOfPackages.checkYourAnswersLabel"))
            )
          )
        )
    }

  def totalPieces(itemIndex: Index, packageIndex: Index, userAnswers: UserAnswers): Option[Row] =
    userAnswers.get(TotalPiecesPage(itemIndex, packageIndex)).map {
      answer =>
        Row(
          key = Key(msg"totalPieces.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(lit"$answer"),
          actions = List(
            Action(
              content = msg"site.edit",
              href = controllers.addItems.packagesInformation.routes.TotalPiecesController.onPageLoad(userAnswers.lrn, itemIndex, packageIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalPieces.checkYourAnswersLabel"))
            )
          )
        )
    }

  def addAnotherPackage(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherPackageHref = controllers.addItems.packagesInformation.routes.AddAnotherPackageController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherPackageHref, content)
  }

  def extraInformation(itemIndex: Index, referenceIndex: Index): Option[Row] = userAnswers.get(ExtraInformationPage(itemIndex, referenceIndex)) map {
    answer =>
      Row(
        key = Key(msg"extraInformation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = previousReferencesRoutes.ExtraInformationController.onPageLoad(lrn, itemIndex, referenceIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"extraInformation.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addExtraDocumentInformation(index: Index, documentIndex: Index): Option[Row] =
    userAnswers.get(AddExtraDocumentInformationPage(index, documentIndex)) map {
      answer =>
        Row(
          key = Key(msg"addExtraDocumentInformation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(yesOrNo(answer)),
          actions = List(
            Action(
              content = msg"site.edit",
              href = controllers.addItems.documents.routes.AddExtraDocumentInformationController.onPageLoad(lrn, index, documentIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addExtraDocumentInformation.checkYourAnswersLabel"))
            )
          )
        )
    }

  def addAnotherDocument(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherDocumentHref = controllers.addItems.documents.routes.AddAnotherDocumentController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherDocumentHref, content)
  }

  def documentRow(itemIndex: Index, documentIndex: Index, userAnswers: UserAnswers, documentTypeList: DocumentTypeList): Option[Row] = {

    def actions(updatedAnswer: String) = userAnswers.get(DeclarationTypePage) match {
      case Some(Option4) if itemIndex.position == 0 & documentIndex.position == 0 =>
        List(
          Action(
            content = msg"site.change",
            href = controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(userAnswers.lrn, itemIndex, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(updatedAnswer)),
            attributes = Map("id" -> s"""change-document-${documentIndex.display}""")
          )
        )
      case _ =>
        List(
          Action(
            content = msg"site.change",
            href = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(userAnswers.lrn, itemIndex, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(updatedAnswer)),
            attributes = Map("id" -> s"""change-document-${documentIndex.display}""")
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

  def confirmRemoveDocument(index: Index, documentIndex: Index): Option[Row] = userAnswers.get(ConfirmRemoveDocumentPage(index, documentIndex)) map {
    answer =>
      Row(
        key = Key(msg"confirmRemoveDocument.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.addItems.documents.routes.ConfirmRemoveDocumentController.onPageLoad(lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"confirmRemoveDocument.checkYourAnswersLabel"))
          )
        )
      )
  }

  def commercialReferenceNumber(itemIndex: Index): Option[Row] = userAnswers.get(CommercialReferenceNumberPage(itemIndex)) map {
    answer =>
      Row(
        key = Key(msg"commercialReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = securityDetailsRoutes.CommercialReferenceNumberController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"commercialReferenceNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def AddDangerousGoodsCode(itemIndex: Index): Option[Row] = userAnswers.get(AddDangerousGoodsCodePage(itemIndex)) map {
    answer =>
      Row(
        key = Key(msg"addDangerousGoodsCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = securityDetailsRoutes.AddDangerousGoodsCodeController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addDangerousGoodsCode.checkYourAnswersLabel"))
          )
        )
      )
  }

  def dangerousGoodsCode(itemIndex: Index): Option[Row] = userAnswers.get(DangerousGoodsCodePage(itemIndex)) map {
    answer =>
      Row(
        key = Key(msg"dangerousGoodsCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = securityDetailsRoutes.DangerousGoodsCodeController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"dangerousGoodsCode.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addSecurityConsignorsEori(index: Index): Option[Row] = userAnswers.get(AddSecurityConsignorsEoriPage(index)) map {
    answer =>
      Row(
        key = Key(msg"addSecurityConsignorsEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = tradersSecurityDetailsRoutes.AddSecurityConsignorsEoriController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSecurityConsignorsEori.checkYourAnswersLabel"))
          )
        )
      )

  }

  def addSecurityConsigneesEori(index: Index): Option[Row] = userAnswers.get(AddSecurityConsigneesEoriPage(index)) map {
    answer =>
      Row(
        key = Key(msg"addSecurityConsigneesEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = tradersSecurityDetailsRoutes.AddSecurityConsignorsEoriController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSecurityConsigneesEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def securityConsigneeName(index: Index): Option[Row] = userAnswers.get(SecurityConsigneeNamePage(index)) map {
    answer =>
      Row(
        key = Key(msg"securityConsigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = tradersSecurityDetailsRoutes.SecurityConsigneeNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsigneeName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def securityConsignorName(index: Index): Option[Row] = userAnswers.get(SecurityConsignorNamePage(index)) map {
    answer =>
      Row(
        key = Key(msg"securityConsignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = tradersSecurityDetailsRoutes.SecurityConsignorNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsignorName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def securityConsigneeAddress(index: Index): Option[Row] = userAnswers.get(SecurityConsigneeAddressPage(index)) map {
    answer =>
      val consigneesName =
        userAnswers.get(SecurityConsigneeNamePage(index)).getOrElse(msg"securityDetailsConsigneeAddress.checkYourAnswersLabel.fallback")
      val address = Html(
        Seq(answer.AddressLine1, answer.AddressLine2, answer.postalCode, answer.country.description)
          .mkString("<br>")
      )
      Row(
        key = Key(msg"securityConsigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(address),
        actions = List(
          Action(
            content = msg"site.edit",
            href = tradersSecurityDetailsRoutes.SecurityConsigneeAddressController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsigneeAddress.checkYourAnswersLabel".withArgs(consigneesName)))
          )
        )
      )
  }

  def securityConsignorAddress(index: Index): Option[Row] = userAnswers.get(SecurityConsignorAddressPage(index)) map {
    answer =>
      val consignorsName =
        userAnswers.get(SecurityConsignorNamePage(index)).getOrElse(msg"securityDetailsConsignorAddress.checkYourAnswersLabel.fallback")
      val address = Html(
        Seq(answer.AddressLine1, answer.AddressLine2, answer.postalCode, answer.country.description)
          .mkString("<br>")
      )
      Row(
        key = Key(msg"securityConsignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(address),
        actions = List(
          Action(
            content = msg"site.edit",
            href = tradersSecurityDetailsRoutes.SecurityConsignorAddressController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsignorAddress.checkYourAnswersLabel".withArgs(consignorsName)))
          )
        )
      )
  }

  def securityConsigneeEori(index: Index): Option[Row] = userAnswers.get(SecurityConsigneeEoriPage(index)) map {
    answer =>
      Row(
        key = Key(msg"securityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = tradersSecurityDetailsRoutes.SecurityConsigneeEoriController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsigneeEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def securityConsignorEori(index: Index): Option[Row] = userAnswers.get(SecurityConsignorEoriPage(index)) map {
    answer =>
      Row(
        key = Key(msg"securityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = tradersSecurityDetailsRoutes.SecurityConsignorEoriController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsignorEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.lrn

}
