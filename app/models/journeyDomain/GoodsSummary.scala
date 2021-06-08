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

import cats.implicits._
import derivable.DeriveNumberOfSeals
import models.ProcedureType
import models.domain.SealDomain
import models.journeyDomain.GoodsSummary.GoodSummaryDetails
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

import java.time.LocalDate

case class GoodsSummary(
  numberOfPackages: Int,
  totalMass: String,
  loadingPlace: Option[String],
  goodSummaryDetails: GoodSummaryDetails,
  sealNumbers: Seq[SealDomain]
)

object GoodsSummary {

  implicit val parser: UserAnswersReader[GoodsSummary] =
    (
      TotalPackagesPage.reader,
      TotalGrossMassPage.reader,
      AddSecurityDetailsPage.filterOptionalDependent(identity)(LoadingPlacePage.optionalReader).map(_.flatten),
      UserAnswersReader[GoodSummaryDetails],
      DeriveNumberOfSeals.reader orElse List.empty[SealDomain].pure[UserAnswersReader]
    ).tupled.map((GoodsSummary.apply _).tupled)

  sealed trait GoodSummaryDetails

  final case class GoodSummaryNormalDetailsWithoutPreLodge(agreedLocationOfGoods: Option[String], customsApprovedLocation: Option[String])
      extends GoodSummaryDetails

  object GoodSummaryNormalDetailsWithoutPreLodge {

    implicit val goodSummaryNormalDetailsWithoutPreLodgeReader: UserAnswersReader[GoodSummaryNormalDetailsWithoutPreLodge] =
      ProcedureTypePage.filterMandatoryDependent(_ == ProcedureType.Normal) {
        PreLodgeDeclarationPage.filterMandatoryDependent(_ == false) {
          (
            AddCustomsApprovedLocationPage
              .filterOptionalDependent(_ == false) {
                AddAgreedLocationOfGoodsPage.filterOptionalDependent(_ == true) {
                  AgreedLocationOfGoodsPage.reader
                }
              }
              .map(_.flatten),
            AddCustomsApprovedLocationPage.filterOptionalDependent(_ == true) {
              CustomsApprovedLocationPage.reader
            }
          ).tupled.map((GoodSummaryNormalDetailsWithoutPreLodge.apply _).tupled)
        }
      }
  }

  final case class GoodSummaryNormalDetailsWithPreLodge(agreedLocationOfGoods: Option[String]) extends GoodSummaryDetails

  object GoodSummaryNormalDetailsWithPreLodge {

    implicit val goodSummaryNormalDetailsWithPreLodgeReader: UserAnswersReader[GoodSummaryNormalDetailsWithPreLodge] =
      ProcedureTypePage
        .filterMandatoryDependent(_ == ProcedureType.Normal) {
          PreLodgeDeclarationPage.filterMandatoryDependent(_ == true) {
            AddAgreedLocationOfGoodsPage.filterOptionalDependent(_ == true) {
              AgreedLocationOfGoodsPage.reader
            }
          }
        }
        .map(GoodSummaryNormalDetailsWithPreLodge.apply)
  }

  final case class GoodSummarySimplifiedDetails(authorisedLocationCode: String, controlResultDateLimit: LocalDate) extends GoodSummaryDetails

  object GoodSummarySimplifiedDetails {

    implicit val goodSummarySimplifiedDetailsReader: UserAnswersReader[GoodSummarySimplifiedDetails] =
      ProcedureTypePage.filterMandatoryDependent(_ == ProcedureType.Simplified) {
        (
          AuthorisedLocationCodePage.reader,
          ControlResultDateLimitPage.reader
        ).tupled.map((GoodSummarySimplifiedDetails.apply _).tupled)
      }
  }

  object GoodSummaryDetails {

    implicit val goodSummaryDetailsReader: UserAnswersReader[GoodSummaryDetails] =
      UserAnswersReader[GoodSummaryNormalDetailsWithPreLodge].widen[GoodSummaryDetails] orElse
        UserAnswersReader[GoodSummaryNormalDetailsWithoutPreLodge].widen[GoodSummaryDetails] orElse
        UserAnswersReader[GoodSummarySimplifiedDetails].widen[GoodSummaryDetails]
  }

}
