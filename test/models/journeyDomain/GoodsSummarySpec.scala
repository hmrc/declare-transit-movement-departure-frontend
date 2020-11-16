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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
import generators.JourneyModelGenerators
import models.journeyDomain.GoodsSummary.{GoodSummaryDetails, GoodSummaryNormalDetails, GoodSummarySimplifiedDetails}
import models.{Index, ProcedureType, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import pages._

class GoodsSummarySpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  import GoodsSummarySpec._

  "GoodsSummary can be parsed" - {

    "when number of packages is declared" in {

      val arbGoodsSummary = arb[GoodsSummary].map(_.copy(numberOfPackages = Some(123)))

      forAll(arbGoodsSummary, arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers = setGoodsSummary(goodsSummary, Some(false))(ua)

          UserAnswersOptionalParser[GoodsSummary].run(userAnswers).value mustEqual goodsSummary

      }
    }

    "when number of packages is not declared" in {

      val arbGoodsSummary = arb[GoodsSummary].map(_.copy(numberOfPackages = None))

      forAll(arbGoodsSummary, arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers = setGoodsSummary(goodsSummary, Some(false))(ua)

          UserAnswersOptionalParser[GoodsSummary].run(userAnswers).value mustEqual goodsSummary

      }

    }

    "when safety and security doesn't need to be declared" in {

      forAll(arb[GoodsSummary], arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers = setGoodsSummary(goodsSummary, Some(false))(ua)

          UserAnswersOptionalParser[GoodsSummary].run(userAnswers).value mustEqual goodsSummary

      }
    }

    "when safety and security does need to be declared" ignore {}

    "when the declaration is Normal procedure" - {
      "and when there are no customs approved location" in {

        val normalDetail: Arbitrary[GoodSummaryDetails] =
          Arbitrary(Gen.const(GoodSummaryNormalDetails(None)))

        val genGoodsSummary = arbitraryGoodsSummary(normalDetail)

        forAll(genGoodsSummary.arbitrary, arb[UserAnswers]) {
          (goodsSummary, ua) =>
            val userAnswers = setGoodsSummary(goodsSummary, Some(false))(ua)

            UserAnswersOptionalParser[GoodsSummary].run(userAnswers).value mustEqual goodsSummary

        }
      }

      "and when there is a customs approved location" in {
        val normalDetail: Arbitrary[GoodSummaryDetails] =
          Arbitrary(
            stringsWithMaxLength(stringMaxLength).map(
              x => GoodSummaryNormalDetails(Some(x))
            )
          )

        val genGoodsSummary = arbitraryGoodsSummary(normalDetail)

        forAll(genGoodsSummary.arbitrary, arb[UserAnswers]) {
          (goodsSummary, ua) =>
            val userAnswers = setGoodsSummary(goodsSummary, Some(false))(ua)

            UserAnswersOptionalParser[GoodsSummary].run(userAnswers).value mustEqual goodsSummary

        }

      }
    }

    "when the declaration is Simplified procedure" in {

      val simplifiedDetail: Arbitrary[GoodSummaryDetails] =
        Arbitrary(arbitraryGoodSummarySimplifiedDetails.arbitrary.map(identity[GoodSummaryDetails]))

      val genGoodsSummary = arbitraryGoodsSummary(simplifiedDetail)

      forAll(genGoodsSummary.arbitrary, arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers = setGoodsSummary(goodsSummary, Some(false))(ua)

          UserAnswersOptionalParser[GoodsSummary].run(userAnswers).value mustEqual goodsSummary

      }

    }

    "when there are no seals" in {
      val arbGoodsSummary = arb[GoodsSummary].map(_.copy(sealNumbers = Seq.empty))

      forAll(arbGoodsSummary, arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers = setGoodsSummary(goodsSummary, Some(false))(ua)

          UserAnswersOptionalParser[GoodsSummary].run(userAnswers).value mustEqual goodsSummary

      }

    }

    "when there are seals" in {
      val arbGoodsSummary = arb[GoodsSummary].suchThat(_.sealNumbers.nonEmpty)

      forAll(arbGoodsSummary, arb[UserAnswers]) {
        (goodsSummary, ua) =>
          val userAnswers = setGoodsSummary(goodsSummary, Some(false))(ua)

          UserAnswersOptionalParser[GoodsSummary].run(userAnswers).value mustEqual goodsSummary

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
  def setGoodsSummary(goodsSummary: GoodsSummary, addSecurityDetails: Option[Boolean])(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .unsafeSetVal(ProcedureTypePage)(procedureType(goodsSummary.goodSummaryDetails))
      .unsafeSetVal(DeclarePackagesPage)(goodsSummary.numberOfPackages.isDefined)
      .unsafeSetOpt(TotalPackagesPage)(goodsSummary.numberOfPackages)
      .unsafeSetVal(TotalGrossMassPage)(goodsSummary.totalMass)
      .unsafeSetVal(AddSecurityDetailsPage)(addSecurityDetails.getOrElse(goodsSummary.loadingPlace.isDefined))
//      .unsafeSetVal(LoadingPlacePage)(goodsSummary.loadingPlace)
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
}
