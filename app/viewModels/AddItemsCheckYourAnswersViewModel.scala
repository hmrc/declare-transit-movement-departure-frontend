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

package viewModels

import derivable._
import models.{CountryList, DocumentTypeList, Index, PreviousReferencesDocumentTypeList, SpecialMentionList, UserAnswers}
import uk.gov.hmrc.viewmodels.{MessageInterpolators, SummaryList}
import utils.{AddItemsCheckYourAnswersHelper, SpecialMentionsCheckYourAnswers}
import viewModels.sections.Section

object AddItemsCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers,
            index: Index,
            documentTypeList: DocumentTypeList,
            previousDocumentTypes: PreviousReferencesDocumentTypeList,
            specialMentionList: SpecialMentionList,
            countryList: CountryList): AddItemsCheckYourAnswersViewModel = {

    val checkYourAnswersHelper = new AddItemsCheckYourAnswersHelper(userAnswers)

    val specialMentionsCheckYourAnswers = new SpecialMentionsCheckYourAnswers(userAnswers)

    AddItemsCheckYourAnswersViewModel(
      Seq(
        itemsDetailsSection(checkYourAnswersHelper, index),
        traderDetailsSection(checkYourAnswersHelper, index),
        packagesSection(checkYourAnswersHelper, index)(userAnswers),
        containersSection(checkYourAnswersHelper, index)(userAnswers),
        specialMentionsSection(specialMentionsCheckYourAnswers, index, specialMentionList)(userAnswers),
        documentsSection(checkYourAnswersHelper, index, documentTypeList)(userAnswers),
        referencesSection(checkYourAnswersHelper, index, previousDocumentTypes)(userAnswers),
        securitySection(checkYourAnswersHelper, index),
        traderSecuritySection(checkYourAnswersHelper, countryList, index)
      )
    )
  }

  private def traderSecuritySection(checkYourAnswersHelper: AddItemsCheckYourAnswersHelper, countryList: CountryList, index: Index) = Section(
    msg"addItems.checkYourAnswersLabel.security",
    Seq(
      checkYourAnswersHelper.addSecurityConsignorsEori(index),
      checkYourAnswersHelper.securityConsignorEori(index),
      checkYourAnswersHelper.securityConsignorName(index),
      checkYourAnswersHelper.securityConsignorAddress(index, countryList),
      checkYourAnswersHelper.addSecurityConsigneesEori(index),
      checkYourAnswersHelper.securityConsigneeEori(index),
      checkYourAnswersHelper.securityConsigneeName(index),
      checkYourAnswersHelper.securityConsigneeAddress(index, countryList)
    ).flatten
  )

  private def securitySection(checkYourAnswersHelper: AddItemsCheckYourAnswersHelper, index: Index) = Section(
    msg"addItems.checkYourAnswersLabel.safetyAndSecurity",
    Seq(
      checkYourAnswersHelper.transportCharges(index),
      checkYourAnswersHelper.commercialReferenceNumber(index),
      checkYourAnswersHelper.AddDangerousGoodsCode(index),
      checkYourAnswersHelper.dangerousGoodsCode(index)
    ).flatten
  )

  private def itemsDetailsSection(checkYourAnswersHelper: AddItemsCheckYourAnswersHelper, index: Index) = Section(
    msg"addItems.checkYourAnswersLabel.itemDetails",
    Seq(
      checkYourAnswersHelper.itemDescription(index),
      checkYourAnswersHelper.itemTotalGrossMass(index),
      checkYourAnswersHelper.addTotalNetMass(index),
      checkYourAnswersHelper.totalNetMass(index),
      checkYourAnswersHelper.isCommodityCodeKnown(index),
      checkYourAnswersHelper.commodityCode(index)
    ).flatten
  )

  private def traderDetailsSection(checkYourAnswersHelper: AddItemsCheckYourAnswersHelper, index: Index) = Section(
    msg"addItems.checkYourAnswersLabel.traderDetails",
    Seq(
      //todo: add in subheading for consignor H3
      checkYourAnswersHelper.traderDetailsConsignorEoriKnown(index),
      checkYourAnswersHelper.traderDetailsConsignorEoriNumber(index),
      checkYourAnswersHelper.traderDetailsConsignorName(index),
      checkYourAnswersHelper.traderDetailsConsignorAddress(index),
      //todo: add in subheading for consignee H3
      checkYourAnswersHelper.traderDetailsConsigneeEoriKnown(index),
      checkYourAnswersHelper.traderDetailsConsigneeEoriNumber(index),
      checkYourAnswersHelper.traderDetailsConsigneeName(index),
      checkYourAnswersHelper.traderDetailsConsigneeAddress(index)
    ).flatten
  )

  private def packagesSection(checkYourAnswersHelper: AddItemsCheckYourAnswersHelper, index: Index)(implicit userAnswers: UserAnswers): Section = {
    val packageRows: Seq[SummaryList.Row] =
      List.range(0, userAnswers.get(DeriveNumberOfPackages(index)).getOrElse(0)).flatMap {
        packagePosition =>
          checkYourAnswersHelper.packageRow(index, Index(packagePosition), userAnswers)
      }

    Section(
      msg"addItems.checkYourAnswersLabel.packages",
      packageRows,
      checkYourAnswersHelper.addAnotherPackage(index, msg"addItems.checkYourAnswersLabel.packages.addRemove")
    )
  }

  private def referencesSection(checkYourAnswersHelper: AddItemsCheckYourAnswersHelper,
                                index: Index,
                                previousDocumentTypes: PreviousReferencesDocumentTypeList)(implicit userAnswers: UserAnswers) = {
    val referencesRows: Seq[SummaryList.Row] =
      List.range(0, userAnswers.get(DeriveNumberOfPreviousAdministrativeReferences(index)).getOrElse(0)).flatMap {
        position =>
          checkYourAnswersHelper.previousReferenceRows(index, Index(position), previousDocumentTypes)
      }

    Section(
      msg"addItems.checkYourAnswersLabel.references",
      Seq(checkYourAnswersHelper.addAdministrativeReference(index).toSeq, referencesRows).flatten,
      checkYourAnswersHelper.addAnotherPreviousReferences(index, msg"addItems.checkYourAnswersLabel.references.addRemove")
    )
  }

  private def documentsSection(checkYourAnswersHelper: AddItemsCheckYourAnswersHelper, index: Index, documentTypeList: DocumentTypeList)(
    implicit userAnswers: UserAnswers) = {
    val documentRows: Seq[SummaryList.Row] =
      List.range(0, userAnswers.get(DeriveNumberOfDocuments(index)).getOrElse(0)).flatMap {
        documentPosition =>
          checkYourAnswersHelper.documentRow(index, Index(documentPosition), userAnswers, documentTypeList)
      }

    Section(
      msg"addItems.checkYourAnswersLabel.documents",
      Seq(checkYourAnswersHelper.addDocuments(index).toSeq, documentRows).flatten,
      checkYourAnswersHelper.addAnotherDocument(index, msg"addItems.checkYourAnswersLabel.documents.addRemove")
    )
  }

  private def containersSection(checkYourAnswersHelper: AddItemsCheckYourAnswersHelper, index: Index)(implicit userAnswers: UserAnswers): Section = {
    val containerRows: Seq[SummaryList.Row] =
      List.range(0, userAnswers.get(DeriveNumberOfContainers(index)).getOrElse(0)).flatMap {
        containerPosition =>
          checkYourAnswersHelper.containerRow(index, Index(containerPosition), userAnswers)
      }

    Section(
      msg"addItems.checkYourAnswersLabel.containers",
      containerRows,
      checkYourAnswersHelper.addAnotherContainer(index, msg"addItems.checkYourAnswersLabel.containers.addRemove")
    )
  }

  private def specialMentionsSection(checkYourAnswersHelper: SpecialMentionsCheckYourAnswers, index: Index, specialMentionList: SpecialMentionList)(
    implicit userAnswers: UserAnswers): Section = {
    val containerRows: Seq[SummaryList.Row] =
      List.range(0, userAnswers.get(DeriveNumberOfSpecialMentions(index)).getOrElse(0)).flatMap {
        containerPosition =>
          checkYourAnswersHelper.specialMentionTypeNoRemoval(index, Index(containerPosition), specialMentionList)
      }

    Section(
      msg"addItems.checkYourAnswersLabel.specialMentions",
      Seq(checkYourAnswersHelper.addSpecialMention(index).toSeq, containerRows).flatten,
      checkYourAnswersHelper.addAnother(index, msg"addItems.checkYourAnswersLabel.specialMentions.addRemove")
    )
  }

}

case class AddItemsCheckYourAnswersViewModel(sections: Seq[Section])
