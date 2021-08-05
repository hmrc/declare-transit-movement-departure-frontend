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
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.{CommonAddress, EoriNumber, Index, UserAnswers}
import pages._
import pages.addItems.traderDetails._
import pages.traderDetails.{AddConsigneePage, AddConsignorPage}

case class ItemTraderDetails(consignor: Option[RequiredDetails], consignee: Option[RequiredDetails])

object ItemTraderDetails {

  final case class RequiredDetails(name: String, address: CommonAddress, eori: Option[EoriNumber])

  def consignorDetails(index: Index): UserAnswersReader[Option[RequiredDetails]] = {
    def readConsignorEoriPage: UserAnswersReader[Option[EoriNumber]] =
      TraderDetailsConsignorEoriKnownPage(index).filterOptionalDependent(identity) {
        TraderDetailsConsignorEoriNumberPage(index).reader.map(EoriNumber(_))
      }

    val consignorInformation: ReaderT[EitherType, UserAnswers, RequiredDetails] =
      (
        TraderDetailsConsignorNamePage(index).reader,
        TraderDetailsConsignorAddressPage(index).reader,
        readConsignorEoriPage
      ).tupled
        .map {
          case (name, address, eori) =>
            RequiredDetails(name, address, eori)
        }

    AddConsignorPage.filterOptionalDependent(_ == false)(consignorInformation)
  }

  def consigneeDetails(index: Index): UserAnswersReader[Option[RequiredDetails]] = {

    def readConsigneeEoriPage: UserAnswersReader[Option[EoriNumber]] =
      TraderDetailsConsigneeEoriKnownPage(index).filterOptionalDependent(identity) {
        TraderDetailsConsigneeEoriNumberPage(index).reader.map(EoriNumber(_))
      }

    val consigneeInformation: ReaderT[EitherType, UserAnswers, RequiredDetails] =
      (
        TraderDetailsConsigneeNamePage(index).reader,
        TraderDetailsConsigneeAddressPage(index).reader,
        readConsigneeEoriPage
      ).tupled
        .map {
          case (name, address, eori) =>
            RequiredDetails(name, address, eori)
        }

    AddConsigneePage.filterOptionalDependent(_ == false)(consigneeInformation)
  }

  def userAnswersParser(index: Index): UserAnswersReader[ItemTraderDetails] =
    (consignorDetails(index), consigneeDetails(index)).tupled.map(
      x => ItemTraderDetails(x._1, x._2)
    )
}
