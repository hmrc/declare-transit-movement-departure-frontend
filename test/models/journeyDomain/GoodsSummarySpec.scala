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

import base.{GeneratorSpec, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import generators.JourneyModelGenerators
import models.journeyDomain.GoodsSummary.{
  GoodSummaryDetails,
  GoodSummaryNormalDetailsWithPreLodge,
  GoodSummaryNormalDetailsWithoutPreLodge,
  GoodSummarySimplifiedDetails
}
import models.{Index, ProcedureType, UserAnswers}
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

import java.time.LocalDate

class GoodsSummarySpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with UserAnswersSpecHelper {

  "GoodsSummary can be parsed" - {

    "when Simplified" in {
      forAll(arb[UserAnswers]) {
        ua =>
          val goodsSummary = GoodsSummary(
            numberOfPackages   = 1,
            totalMass          = "11.1",
            loadingPlace       = None,
            goodSummaryDetails = GoodSummarySimplifiedDetails("Auth Location Code", LocalDate.now()),
            sealNumbers        = Seq.empty
          )

          val userAnswers =
            GoodsSummarySpec
              .setGoodsSummary(goodsSummary)(ua)
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(PreLodgeDeclarationPage)(false)
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)

          UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
      }
    }

    "when Normal" - {
      "When prelodge is false and user adds a Custom Approved Location" in {
        forAll(arb[UserAnswers]) {
          ua =>
            val goodsSummary = GoodsSummary(
              numberOfPackages   = 1,
              totalMass          = "11.1",
              loadingPlace       = Some("loadingPlaceValue"),
              goodSummaryDetails = GoodSummaryNormalDetailsWithoutPreLodge(None, Some("Approved location")),
              sealNumbers        = Seq.empty
            )
            val userAnswers =
              GoodsSummarySpec
                .setGoodsSummary(goodsSummary)(ua)
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(PreLodgeDeclarationPage)(false)
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)

            UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
        }
      }
      "when prelodge is false an user does not add custom approved location and adds Agreed Location of Goods" in {
        forAll(arb[UserAnswers]) {
          ua =>
            val goodsSummary = GoodsSummary(
              numberOfPackages   = 1,
              totalMass          = "11.1",
              loadingPlace       = None,
              goodSummaryDetails = GoodSummaryNormalDetailsWithoutPreLodge(Some("Agreed location of goods"), None),
              sealNumbers        = Seq.empty
            )
            val userAnswers =
              GoodsSummarySpec
                .setGoodsSummary(goodsSummary)(ua)
                .unsafeSetVal(AddSecurityDetailsPage)(false)
                .unsafeSetVal(PreLodgeDeclarationPage)(false)
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)

            UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
        }
      }
      "when prelodge is false an user does not add custom approved location but does not add Agreed Location of Goods" in {
        forAll(arb[UserAnswers]) {
          ua =>
            val goodsSummary = GoodsSummary(
              numberOfPackages   = 1,
              totalMass          = "11.1",
              loadingPlace       = None,
              goodSummaryDetails = GoodSummaryNormalDetailsWithoutPreLodge(None, None),
              sealNumbers        = Seq.empty
            )
            val userAnswers =
              GoodsSummarySpec
                .setGoodsSummary(goodsSummary)(ua)
                .unsafeSetVal(AddSecurityDetailsPage)(false)
                .unsafeSetVal(PreLodgeDeclarationPage)(false)
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)

            UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
        }
      }

      "when prelodge is true and customer does add Agreed Location of Goods" in {
        forAll(arb[UserAnswers]) {
          ua =>
            val goodsSummary = GoodsSummary(
              numberOfPackages   = 1,
              totalMass          = "11.1",
              loadingPlace       = None,
              goodSummaryDetails = GoodSummaryNormalDetailsWithPreLodge(Some("Agreed location of goods")),
              sealNumbers        = Seq.empty
            )
            val userAnswers =
              GoodsSummarySpec
                .setGoodsSummary(goodsSummary)(ua)
                .unsafeSetVal(AddSecurityDetailsPage)(false)
                .unsafeSetVal(PreLodgeDeclarationPage)(true)
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)

            UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary

        }
      }

      "when prelodge is true and customer does not add Agreed Location of Goods" in {
        forAll(arb[UserAnswers]) {
          ua =>
            val goodsSummary = GoodsSummary(
              numberOfPackages   = 1,
              totalMass          = "11.1",
              loadingPlace       = None,
              goodSummaryDetails = GoodSummaryNormalDetailsWithPreLodge(None),
              sealNumbers        = Seq.empty
            )
            val userAnswers =
              GoodsSummarySpec
                .setGoodsSummary(goodsSummary)(ua)
                .unsafeSetVal(AddSecurityDetailsPage)(false)
                .unsafeSetVal(PreLodgeDeclarationPage)(true)
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)

            UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
        }
      }

    }
  }

}

object GoodsSummarySpec extends UserAnswersSpecHelper {

  private def sealIdDetailsPageForIndex(index: Int): SealIdDetailsPage =
    SealIdDetailsPage(Index(index))

  def setGoodsSummary(goodsSummary: GoodsSummary)(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .unsafeSetVal(TotalPackagesPage)(goodsSummary.numberOfPackages)
      .unsafeSetVal(TotalGrossMassPage)(goodsSummary.totalMass)
      .unsafeSetVal(AddSealsPage)(goodsSummary.sealNumbers.nonEmpty)
      .unsafeSetSeq(sealIdDetailsPageForIndex)(goodsSummary.sealNumbers)
      .unsafeSetPFn(AddCustomsApprovedLocationPage)(goodsSummary.goodSummaryDetails) {
        case GoodSummaryNormalDetailsWithoutPreLodge(_, Some(_)) => true
        case _                                                   => false
      }
      .unsafeSetPFnOpt(CustomsApprovedLocationPage)(goodsSummary.goodSummaryDetails) {
        case GoodSummaryNormalDetailsWithoutPreLodge(_, customsApprovedLocation) => customsApprovedLocation
      }
      .unsafeSetPFn(AuthorisedLocationCodePage)(goodsSummary.goodSummaryDetails) {
        case GoodSummarySimplifiedDetails(authorisedLocationCode, _) => authorisedLocationCode
      }
      .unsafeSetPFn(ControlResultDateLimitPage)(goodsSummary.goodSummaryDetails) {
        case GoodSummarySimplifiedDetails(_, controlResultDateLimit) => controlResultDateLimit
      }
      .unsafeSetOpt(LoadingPlacePage)(goodsSummary.loadingPlace)
      .unsafeSetPFn(AgreedLocationOfGoodsPage)(goodsSummary.goodSummaryDetails) {
        case GoodSummaryNormalDetailsWithoutPreLodge(Some(agreedLocationOfGoods), _)        => agreedLocationOfGoods
        case GoodsSummary.GoodSummaryNormalDetailsWithPreLodge(Some(agreedLocationOfGoods)) => agreedLocationOfGoods

      }
      .unsafeSetPFn(AddAgreedLocationOfGoodsPage)(goodsSummary.goodSummaryDetails) {
        case GoodSummaryNormalDetailsWithoutPreLodge(agreedLocationOfGoods, _)        => agreedLocationOfGoods.isDefined
        case GoodsSummary.GoodSummaryNormalDetailsWithPreLodge(agreedLocationOfGoods) => agreedLocationOfGoods.isDefined

      }
}
