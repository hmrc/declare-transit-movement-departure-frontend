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

package models.journeyDomain

import cats.data._
import cats.implicits._
import derivable._
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.reference.CircumstanceIndicator
import models.{Index, UserAnswers}
import pages.addItems.AddDocumentsPage
import pages.addItems.specialMentions.AddSpecialMentionPage
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, CircumstanceIndicatorPage}
import pages.{AddSecurityDetailsPage, ContainersUsedPage}

case class ItemSection(
  itemDetails: ItemDetails,
  consignor: Option[RequiredDetails],
  consignee: Option[RequiredDetails],
  packages: NonEmptyList[Packages],
  containers: Option[NonEmptyList[Container]],
  specialMentions: Option[NonEmptyList[SpecialMention]],
  producedDocuments: Option[NonEmptyList[ProducedDocument]]
)

object ItemSection {

  private def derivePackage(itemIndex: Index): ReaderT[Option, UserAnswers, NonEmptyList[Packages]] =
    DeriveNumberOfPackages(itemIndex).reader
      .filter(_.nonEmpty)
      .flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, Packages]({
            case (_, index) =>
              Packages.packagesReader(itemIndex, Index(index))
          })
          .map(NonEmptyList.fromListUnsafe)
      }

  private def deriveContainers(itemIndex: Index): ReaderT[Option, UserAnswers, Option[NonEmptyList[Container]]] =
    ContainersUsedPage.reader
      .flatMap {
        isTrue =>
          if (isTrue) {
            DeriveNumberOfContainers(itemIndex).reader
              .filter(_.nonEmpty)
              .flatMap {
                _.zipWithIndex
                  .traverse[UserAnswersReader, Container]({
                    case (_, index) =>
                      Container.containerReader(itemIndex, Index(index))
                  })
                  .map(NonEmptyList.fromList)
              }
          } else none[NonEmptyList[Container]].pure[UserAnswersReader]
      }

  private def deriveSpecialMentions(itemIndex: Index): ReaderT[Option, UserAnswers, Option[NonEmptyList[SpecialMention]]] =
    AddSpecialMentionPage(itemIndex).reader
      .flatMap {
        isTrue =>
          if (isTrue) {
            DeriveNumberOfSpecialMentions(itemIndex).reader
              .filter(_.nonEmpty)
              .flatMap {
                _.zipWithIndex
                  .traverse[UserAnswersReader, SpecialMention]({
                    case (_, index) =>
                      SpecialMention.specialMentionsReader(itemIndex, Index(index))
                  })
                  .map(NonEmptyList.fromList)
              }
          } else none[NonEmptyList[SpecialMention]].pure[UserAnswersReader]
      }

  private def readDocumentType(itemIndex: Index): ReaderT[Option, UserAnswers, Boolean] =
    AddSecurityDetailsPage.reader
      .flatMap {
        case true =>
          AddCircumstanceIndicatorPage.reader.flatMap {
            case true =>
              CircumstanceIndicatorPage.reader.map(x => CircumstanceIndicator.conditionalIndicators.contains(x))
            case false => true.pure[UserAnswersReader]
          }
        case false => AddDocumentsPage(itemIndex).reader
      }

  private def deriveProducedDocuments(itemIndex: Index): ReaderT[Option, UserAnswers, Option[NonEmptyList[ProducedDocument]]] =
    readDocumentType(itemIndex)
      .flatMap {
        isTrue =>
          if (isTrue) {
            DeriveNumberOfDocuments(itemIndex).reader
              .filter(_.nonEmpty)
              .flatMap {
                _.zipWithIndex
                  .traverse[UserAnswersReader, ProducedDocument]({
                    case (_, index) =>
                      ProducedDocument.producedDocumentReader(itemIndex, Index(index))
                  })
                  .map(NonEmptyList.fromList)
              }
          } else none[NonEmptyList[ProducedDocument]].pure[UserAnswersReader]
      }

  implicit def readerItemSection(index: Index): UserAnswersReader[ItemSection] =
    (
      ItemDetails.itemDetailsReader(index),
      ItemTraderDetails.consignorDetails(index),
      ItemTraderDetails.consigneeDetails(index),
      derivePackage(index),
      deriveContainers(index),
      deriveSpecialMentions(index),
      deriveProducedDocuments(index)
    ).tupled.map((ItemSection.apply _).tupled)

  implicit def readerItemSections: UserAnswersReader[NonEmptyList[ItemSection]] =
    DeriveNumberOfItems.reader
      .filter(_.nonEmpty)
      .flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, ItemSection]({
            case (_, index) =>
              readerItemSection(Index(index))
          })
          .map(NonEmptyList.fromListUnsafe)
      }

}
