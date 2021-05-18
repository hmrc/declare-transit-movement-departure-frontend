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
import models.domain.SealDomain
import models.journeyDomain.GoodsSummary.{GoodSummaryDetails, GoodSummaryNormalDetailsWithPreLodge, GoodSummaryNormalDetailsWithoutPreLodge, GoodSummarySimplifiedDetails}
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
            numberOfPackages = 1,
            totalMass = "11.1",
            loadingPlace = None,
            goodSummaryDetails = GoodSummarySimplifiedDetails("Auth Location Code", LocalDate.now()),
            sealNumbers = Seq.empty
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
            "When prelodge is true and user adds a custom approved location" in {
              forAll(arb[UserAnswers]) {
                ua =>
                  val goodsSummary = GoodsSummary(
                    numberOfPackages = 1,
                    totalMass = "11.1",
                    loadingPlace = Some("loadingPlaceValue"),
                    goodSummaryDetails = GoodSummaryNormalDetailsWithPreLodge(None),
                    sealNumbers = Seq.empty
                  )
                  val userAnswers =
                    GoodsSummarySpec
                      .setGoodsSummary(goodsSummary)(ua)
                      .unsafeSetVal(PreLodgeDeclarationPage)(true)
                      .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                      .unsafeSetVal(AddCustomsApprovedLocationPage)(true)
                      .unsafeSetVal(CustomsApprovedLocationPage)("Customs App Location")

                  UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
              }



                //    "when normal" - {
                //      "when pre-lodge is no a" - {
                //        "when custom approved location needs to be added" in {
                //          forAll(arb[UserAnswers]) {
                //            ua =>
                //              val goodsSummary = GoodsSummary(
                //                numberOfPackages   = 1,
                //                totalMass          = "11.1",
                //                loadingPlace       = None,
                //                goodSummaryDetails = GoodSummaryNormalDetailsWithPreLodge(Some("customsApprovedLocationValue")),
                //                sealNumbers        = Seq.empty
                //              )
                //
                //              val userAnswers =
                //                GoodsSummarySpec
                //                  .setGoodsSummary(goodsSummary)(ua)
                //                  .unsafeSetVal(AddSecurityDetailsPage)(false)
                //                  .unsafeSetVal(PreLodgeDeclarationPage)(false)
                //                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                //                  .unsafeSetVal(AddCustomsApprovedLocationPage)(true)
                //
                //              UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
                //          }
                //        }
                //
                //        "when custom approved location is not added" in {
                //          forAll(arb[UserAnswers]) {
                //            ua =>
                //              val goodsSummary = GoodsSummary(
                //                numberOfPackages   = 1,
                //                totalMass          = "11.1",
                //                loadingPlace       = None,
                //                goodSummaryDetails = GoodSummaryNormalDetailsWithPreLodge(None),
                //                sealNumbers        = Seq.empty
                //              )
                //
                //              val userAnswers =
                //                GoodsSummarySpec
                //                  .setGoodsSummary(goodsSummary)(ua)
                //                  .unsafeSetVal(AddSecurityDetailsPage)(false)
                //                  .unsafeSetVal(PreLodgeDeclarationPage)(true)
                //                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                //                  .unsafeSetVal(AddCustomsApprovedLocationPage)(false)
                //
                //              UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
                //          }
                //        }
                //      }
                //
                //      "when pre-lodge is yes" in {
                //        forAll(arb[UserAnswers]) {
                //          ua =>
                //            val goodsSummary = GoodsSummary(
                //              numberOfPackages   = 1,
                //              totalMass          = "11.1",
                //              loadingPlace       = None,
                //              goodSummaryDetails = GoodSummaryNormalDetails(None),
                //              sealNumbers        = Seq.empty
                //            )
                //
                //            val userAnswers =
                //              GoodsSummarySpec
                //                .setGoodsSummary(goodsSummary)(ua)
                //                .unsafeSetVal(AddSecurityDetailsPage)(false)
                //                .unsafeSetVal(PreLodgeDeclarationPage)(true)
                //                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                //
                //            UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
                //        }
                //      }
                //    }
                //
                //    "when simplified" in {
                //      forAll(arb[UserAnswers]) {
                //        ua =>
                //          val goodsSummary = GoodsSummary(
                //            numberOfPackages   = 1,
                //            totalMass          = "11.1",
                //            loadingPlace       = None,
                //            goodSummaryDetails = GoodSummarySimplifiedDetails("authorisedLocationCode", LocalDate.now()),
                //            sealNumbers        = Seq.empty
                //          )
                //
                //          val userAnswers =
                //            GoodsSummarySpec
                //              .setGoodsSummary(goodsSummary)(ua)
                //              .unsafeSetVal(AddSecurityDetailsPage)(false)
                //              .unsafeSetVal(PreLodgeDeclarationPage)(false)
                //              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
                //
                //          UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
                //      }
                //    }
                //
                //    "when customs seals are added by the user" in {
                //      forAll(arb[UserAnswers]) {
                //        ua =>
                //          val goodsSummary = GoodsSummary(
                //            numberOfPackages   = 1,
                //            totalMass          = "11.1",
                //            loadingPlace       = None,
                //            goodSummaryDetails = GoodSummarySimplifiedDetails("authorisedLocationCode", LocalDate.now()),
                //            sealNumbers = Seq(
                //              SealDomain("numberOrMarkValue")
                //            )
                //          )
                //
                //          val userAnswers =
                //            GoodsSummarySpec
                //              .setGoodsSummary(goodsSummary)(ua)
                //              .unsafeSetVal(AddSecurityDetailsPage)(false)
                //              .unsafeSetVal(PreLodgeDeclarationPage)(false)
                //              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
                //              .unsafeSetVal(AddSealsPage)(true)
                //
                //          UserAnswersReader[GoodsSummary].run(userAnswers).isSuccessful mustEqual goodsSummary
                //      }
                //    }
                //  }
                //}
                //
              }
            }
                object GoodsSummarySpec extends UserAnswersSpecHelper {

                  private def sealIdDetailsPageForIndex(index: Int): SealIdDetailsPage =
                    SealIdDetailsPage(Index(index))

                  private def procedureType(goodSummaryDetails: GoodSummaryDetails): ProcedureType =
                    goodSummaryDetails match {
                      case _: GoodSummaryNormalDetailsWithPreLodge => ProcedureType.Normal
                      case _: GoodSummarySimplifiedDetails => ProcedureType.Simplified
                    }

                  def setGoodsSummary(goodsSummary: GoodsSummary)(userAnswers: UserAnswers): UserAnswers =
                    userAnswers
                      .unsafeSetVal(ProcedureTypePage)(procedureType(goodsSummary.goodSummaryDetails))
                      .unsafeSetVal(TotalPackagesPage)(goodsSummary.numberOfPackages)
                      .unsafeSetVal(TotalGrossMassPage)(goodsSummary.totalMass)
                      .unsafeSetSeq(sealIdDetailsPageForIndex)(goodsSummary.sealNumbers)
                      .unsafeSetPFn(AddCustomsApprovedLocationPage)(goodsSummary.goodSummaryDetails) {
                        case GoodSummaryNormalDetailsWithPreLodge(Some(_)) => true
                        case GoodSummaryNormalDetailsWithPreLodge(None) => false
                      }
                      .unsafeSetPFnOpt(CustomsApprovedLocationPage)(goodsSummary.goodSummaryDetails) {
                        case GoodSummaryNormalDetailsWithPreLodge(customsApprovedLocation) => customsApprovedLocation
                      }
                      .unsafeSetPFn(AuthorisedLocationCodePage)(goodsSummary.goodSummaryDetails) {
                        case GoodSummarySimplifiedDetails(authorisedLocationCode, _) => authorisedLocationCode
                      }
                      .unsafeSetPFn(ControlResultDateLimitPage)(goodsSummary.goodSummaryDetails) {
                        case GoodSummarySimplifiedDetails(_, controlResultDateLimit) => controlResultDateLimit
                      }
                      .unsafeSetOpt(LoadingPlacePage)(goodsSummary.loadingPlace)

                }
              }





}