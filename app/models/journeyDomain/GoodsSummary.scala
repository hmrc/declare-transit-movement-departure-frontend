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

import java.time.LocalDate

import cats.implicits._
import derivable.DeriveNumberOfSeals
import models.ProcedureType.{Normal, Simplified}
import models.domain.SealDomain
import models.journeyDomain.GoodsSummary.GoodSummaryDetails
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

case class GoodsSummary(
  loadingPlace: Option[String],
  goodSummaryDetails: GoodSummaryDetails,
  sealNumbers: Seq[SealDomain]
)

object GoodsSummary {

  implicit val parser: UserAnswersReader[GoodsSummary] =
    (
      AddSecurityDetailsPage.filterOptionalDependent(identity)(LoadingPlacePage.optionalReader).map(_.flatten),
      UserAnswersReader[GoodSummaryDetails],
      DeriveNumberOfSeals.reader orElse List.empty[SealDomain].pure[UserAnswersReader]
    ).tupled.map((GoodsSummary.apply _).tupled)

  sealed trait GoodSummaryDetails

  final case class GoodSummaryNormalDetailsWithoutPreLodge(agreedLocationOfGoods: Option[String], customsApprovedLocation: Option[String])
      extends GoodSummaryDetails

  object GoodSummaryNormalDetailsWithoutPreLodge {

    implicit val goodSummaryNormalDetailsWithoutPreLodgeReader: UserAnswersReader[GoodSummaryNormalDetailsWithoutPreLodge] =
      (
        AddCustomsApprovedLocationPage
          .filterOptionalDependent(_ equals false) {
            AddAgreedLocationOfGoodsPage.filterOptionalDependent(identity) {
              AgreedLocationOfGoodsPage.reader
            }
          }
          .map(_.flatten),
        AddCustomsApprovedLocationPage.filterOptionalDependent(identity) {
          CustomsApprovedLocationPage.reader
        }
      ).tupled.map((GoodSummaryNormalDetailsWithoutPreLodge.apply _).tupled)
  }

  final case class GoodSummaryNormalDetailsWithPreLodge(agreedLocationOfGoods: Option[String]) extends GoodSummaryDetails

  object GoodSummaryNormalDetailsWithPreLodge {

    implicit val goodSummaryNormalDetailsWithPreLodgeReader: UserAnswersReader[GoodSummaryNormalDetailsWithPreLodge] =
      AddAgreedLocationOfGoodsPage
        .filterOptionalDependent(identity) {
          AgreedLocationOfGoodsPage.reader
        }
        .map(GoodSummaryNormalDetailsWithPreLodge.apply)
  }

  final case class GoodSummarySimplifiedDetails(authorisedLocationCode: String, controlResultDateLimit: LocalDate) extends GoodSummaryDetails

  object GoodSummarySimplifiedDetails {

    implicit val goodSummarySimplifiedDetailsReader: UserAnswersReader[GoodSummarySimplifiedDetails] =
      (
        AuthorisedLocationCodePage.reader,
        ControlResultDateLimitPage.reader
      ).tupled.map((GoodSummarySimplifiedDetails.apply _).tupled)
  }

  object GoodSummaryDetails {

    implicit val goodSummaryDetailsReader: UserAnswersReader[GoodSummaryDetails] =
      ProcedureTypePage.reader.flatMap {
        case Normal =>
          PreLodgeDeclarationPage.reader.flatMap {
            case true  => UserAnswersReader[GoodSummaryNormalDetailsWithPreLodge].widen[GoodSummaryDetails]
            case false => UserAnswersReader[GoodSummaryNormalDetailsWithoutPreLodge].widen[GoodSummaryDetails]
          }
        case Simplified => UserAnswersReader[GoodSummarySimplifiedDetails].widen[GoodSummaryDetails]
      }
  }

}
