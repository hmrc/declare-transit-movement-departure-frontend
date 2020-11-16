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

package utils

import controllers.addItems.previousReferences.{routes => previousReferencesRoutes}
import controllers.addItems.routes
import controllers.addItems.traderDetails.{routes => traderDetailsRoutes}
import controllers.addItems.containers.{routes => containerRoutes}
import models._
import pages.addItems._
import pages.addItems.containers._
import pages.addItems.traderDetails._
import pages.{addItems, _}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

class AddItemsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def containerRow(itemIndex: Index, containerIndex: Index, userAnswers: UserAnswers): Option[Row] =
    userAnswers.get(ContainerNumberPage(itemIndex, containerIndex)).map {
      answer =>
        Row(
          key   = Key(lit"$answer"),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.change",
              href               = containerRoutes.ContainerNumberController.onPageLoad(userAnswers.id, itemIndex, containerIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer.toString)),
              attributes         = Map("id" -> s"""change-container-${containerIndex.display}""")
            )
          )
        )
    }

  def addAnotherContainer(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherContainerHref = containerRoutes.AddAnotherContainerController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherContainerHref, content)
  }

  def documentRows(index: Index, documentIndex: Index, documentType: DocumentTypeList): Option[Row] =
    userAnswers.get(DocumentTypePage(index, documentIndex)).flatMap {
      answer =>
        documentType.getDocumentType(answer) map {
          documentType =>
            Row(
              key   = Key(lit"(${documentType.code}) ${documentType.description}"),
              value = Value(lit""),
              actions = List(
                Action(
                  content            = msg"site.change",
                  href               = routes.DocumentTypeController.onPageLoad(userAnswers.id, index, documentIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"addAnotherDocument.documentList.change.hidden".withArgs(answer)),
                  attributes         = Map("id" -> s"""change-document-${index.display}""")
                ),
                Action(
                  content            = msg"site.delete",
                  href               = routes.ConfirmRemoveDocumentController.onPageLoad(userAnswers.id, index, documentIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"addSeal.documentList.delete.hidden".withArgs(answer)),
                  attributes         = Map("id" -> s"""remove-document-${index.display}""")
                )
              )
            )
        }
    }

  def itemRows(index: Index): Option[Row] =
    userAnswers.get(ItemDescriptionPage(index)).map {
      answer =>
        Row(
          key   = Key(lit"$answer"),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.change",
              href               = routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index).url,
              visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.change.hidden".withArgs(answer)),
              attributes         = Map("id" -> s"""change-item-${index.display}""")
            ),
            Action(
              content            = msg"site.delete",
              href               = routes.ConfirmRemoveItemController.onPageLoad(userAnswers.id, index).url,
              visuallyHiddenText = Some(msg"addTransitOffice.officeOfTransit.delete.hidden".withArgs(answer)),
              attributes         = Map("id" -> s"""remove-item-${index.display}""")
            )
          )
        )
    }

  def addDocuments(itemIndex: Index): Option[Row] = userAnswers.get(AddDocumentsPage(itemIndex)) map {
    answer =>
      Row(
        key   = Key(msg"addDocuments.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddDocumentsController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addDocuments.checkYourAnswersLabel"))
          )
        )
      )
  }

  def documentReference(itemIndex: Index, documentIndex: Index): Option[Row] = userAnswers.get(DocumentReferencePage(itemIndex, documentIndex)) map {
    answer =>
      Row(
        key   = Key(msg"documentReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DocumentReferenceController.onPageLoad(lrn, itemIndex, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"documentReference.checkYourAnswersLabel"))
          )
        )
      )
  }

  def documentExtraInformation(index: Index, documentIndex: Index): Option[Row] = userAnswers.get(DocumentExtraInformationPage(index, documentIndex)) map {
    answer =>
      Row(
        key   = Key(msg"documentExtraInformation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DocumentExtraInformationController.onPageLoad(lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"documentExtraInformation.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorName(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorNamePage(index)) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorName.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorEoriNumber(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorEoriNumberPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorEoriKnown(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorEoriKnownPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
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
        Seq(answer.AddressLine1, answer.AddressLine2, answer.AddressLine3, answer.country.description)
          .mkString("<br>"))
      Row(
        key   = Key(msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consignorsName), classes = Seq("govuk-!-width-one-half")),
        value = Value(address),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consignorsName)))
          )
        )
      )
  }

  def traderDetailsConsigneeName(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeNamePage(index)) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeName.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeEoriNumber(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeEoriNumberPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeEoriKnown(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeEoriKnownPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
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
        Seq(answer.AddressLine1, answer.AddressLine2, answer.AddressLine3, answer.country.description)
          .mkString("<br>"))
      Row(
        key   = Key(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneesName), classes = Seq("govuk-!-width-one-half")),
        value = Value(address),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(lrn, itemIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneesName)))
          )
        )
      )
  }

  def commodityCode(index: Index): Option[Row] = userAnswers.get(CommodityCodePage(index)) map {
    answer =>
      Row(
        key   = Key(msg"commodityCode.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CommodityCodeController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"commodityCode.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-commodity-code")
          )
        )
      )
  }

  def totalNetMass(index: Index): Option[Row] = userAnswers.get(TotalNetMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display))),
            attributes         = Map("id" -> "change-total-net-mass")
          )
        )
      )
  }

  def isCommodityCodeKnown(index: Index): Option[Row] = userAnswers.get(IsCommodityCodeKnownPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"isCommodityCodeKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsCommodityCodeKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isCommodityCodeKnown.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-is-commodity-known")
          )
        )
      )
  }

  def addTotalNetMass(index: Index): Option[Row] = userAnswers.get(AddTotalNetMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"addTotalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddTotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addTotalNetMass.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-add-total-net-mass")
          )
        )
      )
  }

  def itemTotalGrossMass(index: Index): Option[Row] = userAnswers.get(ItemTotalGrossMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"itemTotalGrossMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ItemTotalGrossMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemTotalGrossMass.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-item-total-gross-mass")
          )
        )
      )
  }

  def itemDescription(index: Index): Option[Row] = userAnswers.get(ItemDescriptionPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"itemDescription.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ItemDescriptionController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemDescription.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-item-description")
          )
        )
      )
  }

  def previousReferenceRows(index: Index, referenceIndex: Index, previousDocumentType: PreviousDocumentTypeList): Option[Row] =
    userAnswers.get(ReferenceTypePage(index, referenceIndex)) flatMap {
      answer =>
        previousDocumentType.getPreviousDocumentType(answer) map {
          referenceType =>
            Row(
              key   = Key(lit"(${referenceType.code}) ${referenceType.description}"),
              value = Value(lit""),
              actions = List(
                Action(
                  content            = msg"site.change",
                  href               = previousReferencesRoutes.ReferenceTypeController.onPageLoad(userAnswers.id, index, referenceIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer)),
                  attributes         = Map("id" -> s"""change-item-${index.display}""")
                )
              )
            )
        }
    }

  def previousAdministrativeReferenceRows(index: Index, referenceIndex: Index, previousDocumentType: PreviousDocumentTypeList, mode: Mode): Option[Row] =
    userAnswers.get(ReferenceTypePage(index, referenceIndex)) flatMap {
      answer =>
        previousDocumentType.getPreviousDocumentType(answer) map {
          referenceType =>
            Row(
              key   = Key(lit"(${referenceType.code}) ${referenceType.description}"),
              value = Value(lit""),
              actions = List(
                Action(
                  content            = msg"site.change",
                  href               = previousReferencesRoutes.ReferenceTypeController.onPageLoad(userAnswers.id, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer)),
                  attributes         = Map("id" -> s"""change-reference-document-type-${index.display}""")
                ),
                Action(
                  content = msg"site.delete",
                  href =
                    previousReferencesRoutes.ConfirmRemovePreviousAdministrativeReferenceController.onPageLoad(userAnswers.id, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer)),
                  attributes         = Map("id" -> s"""remove-reference-document-type-${index.display}""")
                )
              )
            )
        }
    }

  def addAdministrativeReference(index: Index): Option[Row] =
    userAnswers.get(AddAdministrativeReferencePage(index)) map {
      answer =>
        Row(
          key   = Key(msg"addAdministrativeReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(yesOrNo(answer)),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = previousReferencesRoutes.AddAdministrativeReferenceController.onPageLoad(lrn, index, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addAdministrativeReference.checkYourAnswersLabel"))
            )
          )
        )
    }

  def addExtraInformation(itemIndex: Index, referenceIndex: Index): Option[Row] = userAnswers.get(AddExtraInformationPage(itemIndex, referenceIndex)) map {
    answer =>
      Row(
        key   = Key(msg"addExtraInformation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = previousReferencesRoutes.AddExtraInformationController.onPageLoad(lrn, itemIndex, referenceIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addExtraInformation.checkYourAnswersLabel"))
          )
        )
      )
  }

  def referenceType(itemIndex: Index, referenceIndex: Index): Option[Row] = userAnswers.get(ReferenceTypePage(itemIndex, referenceIndex)) map {
    answer =>
      Row(
        key   = Key(msg"referenceType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = previousReferencesRoutes.ReferenceTypeController.onPageLoad(lrn, itemIndex, referenceIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"referenceType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def previousReference(index: Index, referenceIndex: Index): Option[Row] = userAnswers.get(addItems.PreviousReferencePage(index, referenceIndex)) map {
    answer =>
      Row(
        key   = Key(msg"previousReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = previousReferencesRoutes.PreviousReferenceController.onPageLoad(lrn, index, referenceIndex, CheckMode).url,
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
          key   = Key(lit"$answer"),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.change",
              href               = routes.PackageTypeController.onPageLoad(userAnswers.id, itemIndex, packageIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer.toString)),
              attributes         = Map("id" -> s"""change-package-${packageIndex.display}""")
            )
          )
        )
    }

  def addAnotherPackage(itemIndex: Index, content: Text): AddAnotherViewModel = {

    val addAnotherPackageHref = routes.AddAnotherPackageController.onPageLoad(lrn, itemIndex, CheckMode).url

    AddAnotherViewModel(addAnotherPackageHref, content)
  }

  def extraInformation(itemIndex: Index, referenceIndex: Index): Option[Row] = userAnswers.get(ExtraInformationPage(itemIndex, referenceIndex)) map {
    answer =>
      Row(
        key   = Key(msg"extraInformation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = previousReferencesRoutes.ExtraInformationController.onPageLoad(lrn, itemIndex, referenceIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"extraInformation.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addExtraDocumentInformation(index: Index, documentIndex: Index): Option[Row] =
    userAnswers.get(AddExtraDocumentInformationPage(index, documentIndex)) map {
      answer =>
        Row(
          key   = Key(msg"addExtraDocumentInformation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(yesOrNo(answer)),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = routes.AddExtraDocumentInformationController.onPageLoad(lrn, index, documentIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addExtraDocumentInformation.checkYourAnswersLabel"))
            )
          )
        )
    }

  def addAnotherDocument(itemIndex: Index, content: Text): AddAnotherViewModel = {

    //TODO: Update to use AddAnotherDocument controller
    val addAnotherDocumentHref = ???

    AddAnotherViewModel(addAnotherDocumentHref, content)
  }

  def documentRow(itemIndex: Index, documentIndex: Index, userAnswers: UserAnswers): Option[Row] =
    userAnswers.get(DocumentTypePage(itemIndex, documentIndex)).map {
      answer =>
        Row(
          key   = Key(lit"$answer"),
          value = Value(lit""),
          actions = List(
            Action(
              content            = msg"site.change",
              href               = routes.DocumentTypeController.onPageLoad(userAnswers.id, itemIndex, documentIndex, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer.toString)),
              attributes         = Map("id" -> s"""change-document-${documentIndex.display}""")
            )
          )
        )
    }

  def confirmRemoveDocument(index: Index, documentIndex: Index): Option[Row] = userAnswers.get(ConfirmRemoveDocumentPage(index, documentIndex)) map {
    answer =>
      Row(
        key   = Key(msg"confirmRemoveDocument.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConfirmRemoveDocumentController.onPageLoad(lrn, index, documentIndex, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"confirmRemoveDocument.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id

}
