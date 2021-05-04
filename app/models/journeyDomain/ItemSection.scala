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

package models.journeyDomain

import cats.data._
import cats.implicits._
import derivable._
import models.{Index, UserAnswers}
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.addItems.ItemsSecurityTraderDetails
import models.reference.CircumstanceIndicator
import pages.{AddSecurityDetailsPage, ContainersUsedPage}
import pages.addItems.AddDocumentsPage
import pages.addItems.specialMentions.AddSpecialMentionPage
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}
case class ItemSection(
  itemDetails: ItemDetails,
  consignor: Option[RequiredDetails],
  consignee: Option[RequiredDetails],
  packages: NonEmptyList[Packages],
  containers: Option[NonEmptyList[Container]],
  specialMentions: Option[NonEmptyList[SpecialMentionDomain]],
  producedDocuments: Option[NonEmptyList[ProducedDocument]],
  itemSecurityTraderDetails: Option[ItemsSecurityTraderDetails],
  previousReferences: Option[NonEmptyList[PreviousReferences]]
)

object ItemSection {

  private def derivePackage(itemIndex: Index): UserAnswersReader[NonEmptyList[Packages]] =
    DeriveNumberOfPackages(itemIndex).mandatoryNonEmptyListReader.flatMap {
      _.zipWithIndex
        .traverse[UserAnswersReader, Packages]({
          case (_, index) =>
            Packages.packagesReader(itemIndex, Index(index))
        })
    }

  private def deriveContainers(itemIndex: Index): UserAnswersReader[Option[NonEmptyList[Container]]] =
    ContainersUsedPage.filterOptionalDependent(identity) {
      DeriveNumberOfContainers(itemIndex).mandatoryNonEmptyListReader.flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, Container]({
            case (_, index) =>
              Container.containerReader(itemIndex, Index(index))
          })
      }
    }

  private def deriveSpecialMentions(itemIndex: Index): UserAnswersReader[Option[NonEmptyList[SpecialMentionDomain]]] =
    AddSpecialMentionPage(itemIndex).filterOptionalDependent(identity) {
      DeriveNumberOfSpecialMentions(itemIndex).mandatoryNonEmptyListReader.flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, SpecialMentionDomain]({
            case (_, index) =>
              SpecialMentionDomain.specialMentionsReader(itemIndex, Index(index))
          })
      }
    }

  private def producedDocumentsWithConditionalIndicator(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    AddSecurityDetailsPage
      .filterMandatoryDependent(identity) {
        AddCommercialReferenceNumberPage.filterMandatoryDependent(_ == false) {
          AddCircumstanceIndicatorPage.filterMandatoryDependent(_ == true) {
            CircumstanceIndicatorPage.filterMandatoryDependent(x => CircumstanceIndicator.conditionalIndicators.contains(x)) {
              DeriveNumberOfDocuments(itemIndex).mandatoryNonEmptyListReader.flatMap {
                _.zipWithIndex
                  .traverse[UserAnswersReader, ProducedDocument]({
                    case (_, index) =>
                      ProducedDocument.producedDocumentReader(itemIndex, Index(index))
                  })
              }
            }
          }
        }
      }
      .map(_.some)

  private def producedDocumentsWithoutConditionalIndicator(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    AddSecurityDetailsPage
      .filterMandatoryDependent(identity) {
        AddCommercialReferenceNumberPage.filterMandatoryDependent(_ == false) {
          AddCircumstanceIndicatorPage.filterMandatoryDependent(_ == false) {
            DeriveNumberOfDocuments(itemIndex).mandatoryNonEmptyListReader.flatMap {
              _.zipWithIndex
                .traverse[UserAnswersReader, ProducedDocument]({
                  case (_, index) =>
                    ProducedDocument.producedDocumentReader(itemIndex, Index(index))
                })
            }
          }
        }
      }
      .map(_.some)

  private def producedDocumentsOther(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    AddDocumentsPage(itemIndex).filterOptionalDependent(identity) {
      DeriveNumberOfDocuments(itemIndex).mandatoryNonEmptyListReader.flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, ProducedDocument]({
            case (_, index) =>
              ProducedDocument.producedDocumentReader(itemIndex, Index(index))
          })
      }
    }

  def deriveProducedDocuments(itemIndex: Index): ReaderT[EitherType, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    if (itemIndex.position == 0) {
      producedDocumentsWithConditionalIndicator(itemIndex) orElse
        producedDocumentsWithoutConditionalIndicator(itemIndex) orElse
        producedDocumentsOther(itemIndex)
    } else {
      producedDocumentsOther(itemIndex)
    }

  implicit def readerItemSection(index: Index): UserAnswersReader[ItemSection] =
    (
      ItemDetails.itemDetailsReader(index),
      ItemTraderDetails.consignorDetails(index),
      ItemTraderDetails.consigneeDetails(index),
      derivePackage(index),
      deriveContainers(index),
      deriveSpecialMentions(index),
      deriveProducedDocuments(index),
      ItemsSecurityTraderDetails.parser(index),
      PreviousReferences.derivePreviousReferences(index)
    ).tupled.map((ItemSection.apply _).tupled)

  implicit def readerItemSections: UserAnswersReader[NonEmptyList[ItemSection]] =
    DeriveNumberOfItems.mandatoryNonEmptyListReader.flatMap {
      _.zipWithIndex
        .traverse[UserAnswersReader, ItemSection]({
          case (_, index) =>
            readerItemSection(Index(index))
        })
    }
}
