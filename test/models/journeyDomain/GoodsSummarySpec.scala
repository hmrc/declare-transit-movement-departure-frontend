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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.journeyDomain.GoodsSummary.{GoodSummaryDetails, GoodSummaryNormalDetails, GoodSummarySimplifiedDetails}
import models.{Index, ProcedureType, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

class GoodsSummarySpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  import GoodsSummarySpec._

  "GoodsSummary can be parsed" - {

    val isSecurityDefined: Boolean = arb[Boolean].sample.value

    // TODO need to add left test here

    "when number of packages is declared and SafetyAndSecurity is True and Pre-lodge is false" in {

      val arbGoodsSummary = arb(arbitraryGoodsSummary(isSecurityDefined)).map(_.copy(numberOfPackages = Some(123)))

      forAll(arbGoodsSummary, arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers =
            setGoodsSummary(goodsSummary)(ua).unsafeSetVal(AddSecurityDetailsPage)(isSecurityDefined).unsafeSetVal(PreLodgeDeclarationPage)(false)

          UserAnswersReader[GoodsSummary].run(userAnswers).right.value mustEqual goodsSummary
      }
    }

    "when number of packages is not declared and SafetyAndSecurity is False and Pre-lodge is false" in {

      val arbGoodsSummary = arb(arbitraryGoodsSummary(isSecurityDefined)).map(_.copy(numberOfPackages = None))

      forAll(arbGoodsSummary, arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers =
            setGoodsSummary(goodsSummary)(ua).unsafeSetVal(AddSecurityDetailsPage)(isSecurityDefined).unsafeSetVal(PreLodgeDeclarationPage)(false)

          UserAnswersReader[GoodsSummary].run(userAnswers).right.value mustEqual goodsSummary
      }
    }

    "when the declaration is Normal procedure" - {
      "and Pre-lodge is true and when there are no customs approved location" in {

        val normalDetail: Arbitrary[GoodSummaryDetails] =
          Arbitrary(Gen.const(GoodSummaryNormalDetails(None)))

        val arbGoodsSummary = arb(arbitraryGoodsSummary(isSecurityDefined)(normalDetail))

        forAll(arbGoodsSummary, arb[UserAnswers]) {
          (goodsSummary, ua) =>
            val userAnswers =
              setGoodsSummary(goodsSummary)(ua).unsafeSetVal(AddSecurityDetailsPage)(isSecurityDefined).unsafeSetVal(PreLodgeDeclarationPage)(true)

            UserAnswersReader[GoodsSummary].run(userAnswers).right.value mustEqual goodsSummary
        }
      }

      "and Pre-lodge is false and when there is a customs approved location" in {
        val normalDetail: Arbitrary[GoodSummaryDetails] =
          Arbitrary(
            stringsWithMaxLength(stringMaxLength).map(
              x => GoodSummaryNormalDetails(Some(x))
            )
          )

        val arbGoodsSummary = arb(arbitraryGoodsSummary(isSecurityDefined)(normalDetail))

        forAll(arbGoodsSummary, arb[UserAnswers]) {
          (goodsSummary, ua) =>
            val userAnswers =
              setGoodsSummary(goodsSummary)(ua).unsafeSetVal(AddSecurityDetailsPage)(isSecurityDefined).unsafeSetVal(PreLodgeDeclarationPage)(false)

            UserAnswersReader[GoodsSummary].run(userAnswers).right.value mustEqual goodsSummary
        }

      }
    }

    "when the declaration is Simplified procedure" in {

      val simplifiedDetail: Arbitrary[GoodSummaryDetails] =
        Arbitrary(arbitraryGoodSummarySimplifiedDetails.arbitrary.map(identity[GoodSummaryDetails]))

      val arbGoodsSummary = arb(arbitraryGoodsSummary(isSecurityDefined)(simplifiedDetail))
      forAll(arbGoodsSummary, arb[UserAnswers]) {

        (goodsSummary, ua) =>
          val userAnswers =
            setGoodsSummary(goodsSummary)(ua).unsafeSetVal(AddSecurityDetailsPage)(isSecurityDefined)

          UserAnswersReader[GoodsSummary].run(userAnswers).right.value mustEqual goodsSummary
      }

    }

    "when there are no seals and Pre-lodge is false" in {

      val arbGoodsSummary = arb(arbitraryGoodsSummary(isSecurityDefined)).map(_.copy(sealNumbers = Seq.empty))

      forAll(arbGoodsSummary, arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers =
            setGoodsSummary(goodsSummary)(ua).unsafeSetVal(AddSecurityDetailsPage)(isSecurityDefined).unsafeSetVal(PreLodgeDeclarationPage)(false)

          UserAnswersReader[GoodsSummary].run(userAnswers).right.value mustEqual goodsSummary
      }

    }

    "when there are seals and Pre-lodge is false" in {
      val arbGoodsSummary = arb(arbitraryGoodsSummary(isSecurityDefined)).suchThat(_.sealNumbers.nonEmpty)

      forAll(arbGoodsSummary, arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers =
            setGoodsSummary(goodsSummary)(ua).unsafeSetVal(AddSecurityDetailsPage)(isSecurityDefined).unsafeSetVal(PreLodgeDeclarationPage)(false)

          UserAnswersReader[GoodsSummary].run(userAnswers).right.value mustEqual goodsSummary
      }

    }
  }
}

object GoodsSummarySpec extends UserAnswersSpecHelper {

  private def sealIdDetailsPageForIndex(index: Int): SealIdDetailsPage =
    SealIdDetailsPage(Index(index))

  private def procedureType(goodSummaryDetails: GoodSummaryDetails): ProcedureType =
    goodSummaryDetails match {
      case _: GoodSummaryNormalDetails     => ProcedureType.Normal
      case _: GoodSummarySimplifiedDetails => ProcedureType.Simplified
    }

  // Note: overrides procedure type
  def setGoodsSummary(goodsSummary: GoodsSummary)(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .unsafeSetVal(ProcedureTypePage)(procedureType(goodsSummary.goodSummaryDetails))
      .unsafeSetVal(DeclarePackagesPage)(goodsSummary.numberOfPackages.isDefined)
      .unsafeSetOpt(TotalPackagesPage)(goodsSummary.numberOfPackages)
      .unsafeSetVal(TotalGrossMassPage)(goodsSummary.totalMass)
      .unsafeSetSeq(sealIdDetailsPageForIndex)(goodsSummary.sealNumbers)
      .unsafeSetPFn(AddCustomsApprovedLocationPage)(goodsSummary.goodSummaryDetails) {
        case GoodSummaryNormalDetails(customsApprovedLocation) => customsApprovedLocation.isDefined
      }
      .unsafeSetPFnOpt(CustomsApprovedLocationPage)(goodsSummary.goodSummaryDetails) {
        case GoodSummaryNormalDetails(customsApprovedLocation) => customsApprovedLocation
      }
      .unsafeSetPFn(AuthorisedLocationCodePage)(goodsSummary.goodSummaryDetails) {
        case GoodSummarySimplifiedDetails(authorisedLocationCode, _) => authorisedLocationCode
      }
      .unsafeSetPFn(ControlResultDateLimitPage)(goodsSummary.goodSummaryDetails) {
        case GoodSummarySimplifiedDetails(_, controlResultDateLimit) => controlResultDateLimit
      }
      .unsafeSetOpt(LoadingPlacePage)(goodsSummary.loadingPlace)

}
