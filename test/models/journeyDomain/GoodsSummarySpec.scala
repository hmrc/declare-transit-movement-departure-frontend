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
import models.Index
import models.ProcedureType.{Normal, Simplified}
import models.domain.SealDomain
import models.journeyDomain.GoodsSummary.{GoodSummaryNormalDetailsWithPreLodge, GoodSummaryNormalDetailsWithoutPreLodge, GoodSummarySimplifiedDetails}
import org.scalacheck.Gen
import pages._
import pages.movementDetails.PreLodgeDeclarationPage

import java.time.LocalDate

class GoodsSummarySpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  "GoodsSummary" - {

    "can be parsed from UserAnswers" - {

      "when Simplified" in {

        val dateNow = LocalDate.now()

        val expectedResult = GoodsSummary(
          loadingPlace = None,
          goodSummaryDetails = GoodSummarySimplifiedDetails("authLocation", dateNow),
          sealNumbers = Seq.empty
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(ProcedureTypePage)(Simplified)
          .unsafeSetVal(AddSecurityDetailsPage)(false)
          .unsafeSetVal(AuthorisedLocationCodePage)("authLocation")
          .unsafeSetVal(ControlResultDateLimitPage)(dateNow)
          .unsafeSetVal(AddSealsPage)(false)

        val result = UserAnswersReader[GoodsSummary].run(userAnswers)

        result.right.value mustBe expectedResult
      }

      "when Normal" - {
        "When prelodge is false and user adds a Custom Approved Location" in {

          val expectedResult = GoodsSummary(
            loadingPlace = Some("loadingPlace"),
            goodSummaryDetails = GoodSummaryNormalDetailsWithoutPreLodge(None, Some("approvedLocation")),
            sealNumbers = Seq.empty
          )

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(LoadingPlacePage)("loadingPlace")
            .unsafeSetVal(AddCustomsApprovedLocationPage)(true)
            .unsafeSetVal(CustomsApprovedLocationPage)("approvedLocation")
            .unsafeSetVal(AddSealsPage)(false)

          val result = UserAnswersReader[GoodsSummary].run(userAnswers)

          result.right.value mustBe expectedResult
        }

        "when prelodge is false an user does not add custom approved location and adds Agreed Location of Goods" in {

          val expectedResult = GoodsSummary(
            loadingPlace = None,
            goodSummaryDetails = GoodSummaryNormalDetailsWithoutPreLodge(Some("agreedLocationOfGoods"), None),
            sealNumbers = Seq.empty
          )

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(false)
            .unsafeSetVal(AddCustomsApprovedLocationPage)(false)
            .unsafeSetVal(AddAgreedLocationOfGoodsPage)(true)
            .unsafeSetVal(AgreedLocationOfGoodsPage)("agreedLocationOfGoods")
            .unsafeSetVal(CustomsApprovedLocationPage)("approvedLocation")
            .unsafeSetVal(AddSealsPage)(false)

          val result = UserAnswersReader[GoodsSummary].run(userAnswers)

          result.right.value mustBe expectedResult
        }

        "when prelodge is false an user does not add custom approved location but does not add Agreed Location of Goods" in {

          val expectedResult = GoodsSummary(
            loadingPlace = None,
            goodSummaryDetails = GoodSummaryNormalDetailsWithoutPreLodge(None, None),
            sealNumbers = Seq.empty
          )

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(false)
            .unsafeSetVal(AddCustomsApprovedLocationPage)(false)
            .unsafeSetVal(AddAgreedLocationOfGoodsPage)(false)
            .unsafeSetVal(CustomsApprovedLocationPage)("approvedLocation")
            .unsafeSetVal(AddSealsPage)(false)

          val result = UserAnswersReader[GoodsSummary].run(userAnswers)

          result.right.value mustBe expectedResult
        }

        "when prelodge is true and customer does add Agreed Location of Goods" in {

          val expectedResult = GoodsSummary(
            loadingPlace = None,
            goodSummaryDetails = GoodSummaryNormalDetailsWithPreLodge(Some("agreedLocationOfGoods")),
            sealNumbers = Seq.empty
          )

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(true)
            .unsafeSetVal(AddSecurityDetailsPage)(false)
            .unsafeSetVal(AddAgreedLocationOfGoodsPage)(true)
            .unsafeSetVal(AgreedLocationOfGoodsPage)("agreedLocationOfGoods")
            .unsafeSetVal(AddSealsPage)(false)

          val result = UserAnswersReader[GoodsSummary].run(userAnswers)

          result.right.value mustBe expectedResult
        }

        "when prelodge is true and customer does not add Agreed Location of Goods and seals have been added" in {

          val expectedResult = GoodsSummary(
            loadingPlace = None,
            goodSummaryDetails = GoodSummaryNormalDetailsWithPreLodge(None),
            sealNumbers = Seq(SealDomain("numberOrMark1"), SealDomain("numberOrMark1"))
          )

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(true)
            .unsafeSetVal(AddSecurityDetailsPage)(false)
            .unsafeSetVal(AddAgreedLocationOfGoodsPage)(false)
            .unsafeSetVal(AddSealsPage)(true)
            .unsafeSetVal(SealIdDetailsPage(index))(SealDomain("numberOrMark1"))
            .unsafeSetVal(SealIdDetailsPage(Index(1)))(SealDomain("numberOrMark1"))

          val result = UserAnswersReader[GoodsSummary].run(userAnswers)

          result.right.value mustBe expectedResult
        }
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory answers is not defined" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(ProcedureTypePage)(Normal)
          .unsafeSetVal(PreLodgeDeclarationPage)(false)
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(LoadingPlacePage)("loadingPlace")
          .unsafeSetVal(AddCustomsApprovedLocationPage)(true)
          .unsafeSetVal(CustomsApprovedLocationPage)("approvedLocation")
          .unsafeSetVal(AddSealsPage)(false)
          .unsafeRemove(AddSecurityDetailsPage)

        val result = UserAnswersReader[GoodsSummary].run(userAnswers)

        result.left.value.page mustBe AddSecurityDetailsPage
      }

      "when Normal" - {

        "and prelodge is false and AddCustomsApprovedLocationPage is not defined" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(LoadingPlacePage)("loadingPlace")
            .unsafeSetVal(CustomsApprovedLocationPage)("approvedLocation")
            .unsafeSetVal(AddSealsPage)(false)

          val result = UserAnswersReader[GoodsSummary].run(userAnswers)

          result.left.value.page mustBe AddCustomsApprovedLocationPage
        }

        "and prelodge is false and AddCustomsApprovedLocationPage is true but CustomsApprovedLocationPage is not defined" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(LoadingPlacePage)("loadingPlace")
            .unsafeSetVal(AddCustomsApprovedLocationPage)(true)
            .unsafeSetVal(AddSealsPage)(false)

          val result = UserAnswersReader[GoodsSummary].run(userAnswers)

          result.left.value.page mustBe CustomsApprovedLocationPage
        }

        "and prelodge is true and AddAgreedLocationOfGoodsPage is not defined" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(true)
            .unsafeSetVal(AddSecurityDetailsPage)(false)
            .unsafeSetVal(AgreedLocationOfGoodsPage)("agreedLocationOfGoods")
            .unsafeSetVal(AddSealsPage)(false)

          val result = UserAnswersReader[GoodsSummary].run(userAnswers)

          result.left.value.page mustBe AddAgreedLocationOfGoodsPage
        }

        "and prelodge is true and AddAgreedLocationOfGoodsPage is true but AgreedLocationOfGoodsPage is not defined" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(true)
            .unsafeSetVal(AddSecurityDetailsPage)(false)
            .unsafeSetVal(AddAgreedLocationOfGoodsPage)(true)
            .unsafeSetVal(AddSealsPage)(false)

          val result = UserAnswersReader[GoodsSummary].run(userAnswers)

          result.left.value.page mustBe AgreedLocationOfGoodsPage
        }
      }

      "when simplified" - {

        "and a mandatory page is missing" in {

          val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
            AuthorisedLocationCodePage,
            ControlResultDateLimitPage
          )

          forAll(mandatoryPages) {
            mandatoryPage =>
              val dateNow = LocalDate.now()

              val userAnswers = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(Simplified)
                .unsafeSetVal(AddSecurityDetailsPage)(false)
                .unsafeSetVal(AuthorisedLocationCodePage)("authLocation")
                .unsafeSetVal(ControlResultDateLimitPage)(dateNow)
                .unsafeSetVal(AddSealsPage)(false)
                .unsafeRemove(mandatoryPage)

              val result = UserAnswersReader[GoodsSummary].run(userAnswers)

              result.left.value.page mustBe mandatoryPage
          }
        }
      }
    }
  }
}
