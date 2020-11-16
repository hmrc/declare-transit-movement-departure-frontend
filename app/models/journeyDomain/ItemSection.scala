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
import derivable.{DeriveNumberOfItems, DeriveNumberOfPackages}
import models.{Index, UserAnswers}

case class ItemSection(
  itemDetails: ItemDetails,
  packages: NonEmptyList[Packages]
)

object ItemSection {

  private def derivePackage(itemIndex: Index): ReaderT[Option, UserAnswers, NonEmptyList[Packages]] =
    DeriveNumberOfPackages(itemIndex).reader
      .filter(_.size > 0)
      .flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, Packages]({
            case (_, index) =>
              Packages.packagesReader(itemIndex, Index(index))
          })
          .map(NonEmptyList.fromListUnsafe _)
      }

  implicit def readerItemSection(index: Index): UserAnswersReader[ItemSection] =
    (
      ItemDetails.itemDetailsReader(index),
      derivePackage(index)
    ).tupled.map((ItemSection.apply _).tupled)

  implicit def readerItemSections: UserAnswersReader[NonEmptyList[ItemSection]] =
    DeriveNumberOfItems.reader
      .filter(_.size > 0)
      .flatMap {
        _.zipWithIndex
          .traverse[UserAnswersReader, ItemSection]({
            case (_, index) =>
              readerItemSection(Index(index))
          })
          .map(NonEmptyList.fromListUnsafe _)
      }

}
