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

package navigation

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.traderDetails.{routes => traderDetailsRoute}
import generators.Generators
import models.DeclarationType.{Option1, Option4}
import models.ProcedureType.{Normal, Simplified}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails._
import pages._

class TraderDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  val navigator = new TraderDetailsNavigator

  val genNormalProcedureUserAnswers: Gen[UserAnswers] =
    arbitrary[UserAnswers].map(_.unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal))

  val genSimplifiedProcedureUserAnswers: Gen[UserAnswers] =
    arbitrary[UserAnswers].map(_.unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified))

  val genUserAnswersWithProcedure: Gen[UserAnswers] =
    Gen.oneOf(
      genNormalProcedureUserAnswers,
      genSimplifiedProcedureUserAnswers
    )

  "TraderDetailsNavigator" - {

    "in Normal mode" - {

      "Principal trader section" - {

        "for a Normal procedure declaration" - {

          "must go from Is principal eori known page " - {

            "to what is eori number page when 'YES' is selected" in {

              forAll(genNormalProcedureUserAnswers) {
                answers =>
                  val updatedAnswers = answers
                    .unsafeSetVal(IsPrincipalEoriKnownPage)(true)

                  navigator
                    .nextPage(IsPrincipalEoriKnownPage, NormalMode, updatedAnswers)
                    .mustBe(traderDetailsRoute.WhatIsPrincipalEoriController.onPageLoad(answers.lrn, NormalMode))
              }
            }

            "to principal name page when 'NO' is selected" in {

              forAll(genNormalProcedureUserAnswers) {
                answers =>
                  val updatedAnswers = answers
                    .unsafeSetVal(IsPrincipalEoriKnownPage)(false)

                  navigator
                    .nextPage(IsPrincipalEoriKnownPage, NormalMode, updatedAnswers)
                    .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.lrn, NormalMode))
              }
            }
          }

          "must go from Principal eori page to Add consignor page if principals EORI starts with prefix 'GB' and Declaration type is not TIR " in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("GB123456")
                  .unsafeSetVal(DeclarationTypePage)(Option1)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(answers.lrn, NormalMode))
            }
          }

          "must go from Principal eori page to Add consignor page if principals EORI starts with prefix 'gb' and Declaration type is not TIR" in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("gb123456")
                  .unsafeSetVal(DeclarationTypePage)(Option1)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(answers.lrn, NormalMode))
            }
          }

          "must go from Principal eori page to Add consignor page if principals EORI starts with prefix 'XI' and Declaration type is not TIR" in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("XI123456")
                  .unsafeSetVal(DeclarationTypePage)(Option1)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(answers.lrn, NormalMode))
            }
          }

          "must go from Principal eori page to Principal name page if principals EORI starts with prefix 'XI' and Declaration type is TIR" in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("XI123456")
                  .unsafeSetVal(DeclarationTypePage)(Option4)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.lrn, NormalMode))
            }
          }

          "must go from Principal eori page to Principal name page if principals EORI starts with prefix 'GB' and Declaration type is TIR" in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("GB123456")
                  .unsafeSetVal(DeclarationTypePage)(Option4)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.lrn, NormalMode))
            }
          }

          "must go from Principal eori page to Principal name page if principals EORI does not start with 'XI' or 'GB'' and Declaration type is TIR" in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("AD123456")
                  .unsafeSetVal(DeclarationTypePage)(Option1)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.lrn, NormalMode))
            }
          }

          "must go from Principal eori page to Add Principal's Name page if principals EORI does not start with prefix 'GB' or 'XI' " in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("AD123456")

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.lrn, NormalMode))
            }
          }

          "must go from Principal name page to Principal address page" in {
            forAll(genNormalProcedureUserAnswers) {
              answers =>
                navigator
                  .nextPage(PrincipalNamePage, NormalMode, answers)
                  .mustBe(traderDetailsRoute.PrincipalAddressController.onPageLoad(answers.lrn, NormalMode))
            }
          }

          "must go from Principal address page to Add consignor page when declaration type is not TIR" in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers.unsafeSetVal(DeclarationTypePage)(Option1)
                navigator
                  .nextPage(PrincipalAddressPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(ua.lrn, NormalMode))
            }
          }

          "must go from Principal address page to What is Principal's TIR Holder Id page when declaration type is TIR" in {

            forAll(genNormalProcedureUserAnswers) {
              answers =>
                val ua = answers.unsafeSetVal(DeclarationTypePage)(Option4)
                navigator
                  .nextPage(PrincipalAddressPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.PrincipalTirHolderIdController.onPageLoad(ua.lrn, NormalMode))
            }
          }

        }

        "for a Simplified procedure declaration" - {

          "must go from Principal eori page to Add consignor page and Declaration type is not TIR" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val ua = answers
                  .unsafeSetVal(DeclarationTypePage)(Option1)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("GB")
                  .unsafeSetVal(ProcedureTypePage)(Simplified)
                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(ua.lrn, NormalMode))
            }
          }

          "must go from Principal eori page to Principal name page and Declaration type is TIR" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val ua = answers
                  .unsafeSetVal(DeclarationTypePage)(Option4)
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("GB")
                  .unsafeSetVal(ProcedureTypePage)(Simplified)
                navigator
                  .nextPage(WhatIsPrincipalEoriPage, NormalMode, ua)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(ua.lrn, NormalMode))
            }
          }

        }

      }

      "must go from Add consignor page to Is consignor eori known page when 'YES' is selected" in {

        forAll(genUserAnswersWithProcedure) {
          answers =>
            val updatedAnswers = answers
              .unsafeSetVal(AddConsignorPage)(true)

            navigator
              .nextPage(AddConsignorPage, NormalMode, updatedAnswers)
              .mustBe(traderDetailsRoute.IsConsignorEoriKnownController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Add consignor page to Add consignee page when 'NO' is selected" in {

        forAll(genUserAnswersWithProcedure) {
          answers =>
            val updatedAnswers = answers
              .unsafeSetVal(AddConsignorPage)(false)

            navigator
              .nextPage(AddConsignorPage, NormalMode, updatedAnswers)
              .mustBe(traderDetailsRoute.AddConsigneeController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "when consignor eori is known" - {

        "must go from Is consignor eori known page to Consignor eori page when 'YES' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)

              navigator
                .nextPage(IsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorEoriController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from EORI Number page to Consignor Name page" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              navigator
                .nextPage(ConsignorEoriPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.ConsignorNameController.onPageLoad(answers.lrn, NormalMode))
          }
        }

      }

      "when consignor eori is not known" - {
        "must go from Is consignor eori known page to Consignor name page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(false)

              navigator
                .nextPage(IsConsignorEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorNameController.onPageLoad(answers.lrn, NormalMode))
          }
        }
      }

      "must go from Consignor name page to Consignor address page" in {

        forAll(genUserAnswersWithProcedure) {
          answers =>
            navigator
              .nextPage(ConsignorNamePage, NormalMode, answers)
              .mustBe(traderDetailsRoute.ConsignorAddressController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from Consignor address page to Add consignee page" in {

        forAll(genUserAnswersWithProcedure) {
          answers =>
            navigator
              .nextPage(ConsignorAddressPage, NormalMode, answers)
              .mustBe(traderDetailsRoute.AddConsigneeController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "when consignee eori is known" - {
        "must go from Add consignee page to Is consignee eori known page when 'YES' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(true)

              navigator
                .nextPage(AddConsigneePage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.IsConsigneeEoriKnownController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from Is consignee eori known page to Consignee eori page when 'YES' is selected" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(true)

              navigator
                .nextPage(IsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.WhatIsConsigneeEoriController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from Is consignee eori known page to Consignee name page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(false)

              navigator
                .nextPage(IsConsigneeEoriKnownPage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsigneeNameController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from What Is Consignee Eori Page Number page to Consignee Name page" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              navigator
                .nextPage(WhatIsConsigneeEoriPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.ConsigneeNameController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from Consignee name page to Consignee address page" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              navigator
                .nextPage(ConsigneeNamePage, NormalMode, answers)
                .mustBe(traderDetailsRoute.ConsigneeAddressController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from Consignee address page to Trader details cya page" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              navigator
                .nextPage(ConsigneeAddressPage, NormalMode, answers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

      }

      "when consignee eori is not known" - {
        "must go from Add consignee page to Trader details cya page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(false)

              navigator
                .nextPage(AddConsigneePage, NormalMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }

      "PrincipalTirHolderid page must go to AddConsignor page" in {
        forAll(genUserAnswersWithProcedure) {
          answers =>
            navigator
              .nextPage(PrincipalTirHolderIdPage, NormalMode, answers)
              .mustBe(traderDetailsRoute.AddConsignorController.onPageLoad(answers.lrn, NormalMode))
        }
      }

    }
    "in Check mode" - {

      "must go from Is principal eori known page " - {

        "to What is principal eori page if the option selected is 'YES', if it was not previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeRemove(WhatIsPrincipalEoriPage)

              navigator
                .nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.WhatIsPrincipalEoriController.onPageLoad(answers.lrn, CheckMode))
          }
        }

        "to Check Your Answers Page if the option selected is 'YES', if principal eori previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(WhatIsPrincipalEoriPage)("principalEori")

              navigator
                .nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "to What is principal's name page if the option selected is 'NO', if it was not previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                .unsafeRemove(PrincipalNamePage)

              navigator
                .nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(answers.lrn, CheckMode))
          }
        }

        "to Check Your Answers Page if the option selected is 'NO', if principal's name previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)
                .unsafeSetVal(PrincipalNamePage)("principalName")

              navigator
                .nextPage(IsPrincipalEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

      }

      "must go from What is Principal's Eori page" - {
        "on a Normal Journey" - {
          "to PrincipalName  Page if Prefix is 'GB' (in uppercase) and declaration type is TIR" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("GB123456")
                  .unsafeSetVal(ProcedureTypePage)(Normal)
                  .unsafeSetVal(DeclarationTypePage)(Option4)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, CheckMode, updatedAnswers)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(updatedAnswers.lrn, CheckMode))
            }
          }

          "to Principal Name if Prefix is 'gb' in (lowercase) and declaration type is TIR " in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("gb123456")
                  .unsafeSetVal(ProcedureTypePage)(Normal)
                  .unsafeSetVal(DeclarationTypePage)(Option4)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, CheckMode, updatedAnswers)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(updatedAnswers.lrn, CheckMode))
            }
          }

          "to PrincipleNamePage if Prefix is 'XI' and declaration type was type TIR " in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("XI123456")
                  .unsafeSetVal(ProcedureTypePage)(Normal)
                  .unsafeSetVal(DeclarationTypePage)(Option4)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, CheckMode, updatedAnswers)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(updatedAnswers.lrn, CheckMode))
            }
          }

          "to Principal NamePage if Prefix is not 'GB', is not 'XI' and there is  data for Principal Name" in {

            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(WhatIsPrincipalEoriPage)("AD123456")
                  .unsafeSetVal(PrincipalNamePage)("TestName")
                  .unsafeSetVal(ProcedureTypePage)(Normal)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, CheckMode, updatedAnswers)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(updatedAnswers.lrn, CheckMode))
            }
          }
        }

        "on a Simplified journey" - {
          "to Principal name page when declaration type is TIR" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .unsafeSetVal(ProcedureTypePage)(Simplified)
                  .unsafeSetVal(DeclarationTypePage)(Option4)

                navigator
                  .nextPage(WhatIsPrincipalEoriPage, CheckMode, updatedAnswers)
                  .mustBe(traderDetailsRoute.PrincipalNameController.onPageLoad(updatedAnswers.lrn, CheckMode))
            }
          }
        }
      }
      "must go from Principal name page" - {
        "to Check Your Answers page if Principal's Address previously answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[CommonAddress]) {

            (answers, principalAddress) =>
              val updatedAnswers =
                answers.unsafeSetVal(PrincipalAddressPage)(principalAddress)

              navigator
                .nextPage(PrincipalNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "to Principals Address page if Principal's Address not previously answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers =
                answers.unsafeRemove(PrincipalAddressPage)

              navigator
                .nextPage(PrincipalNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.PrincipalAddressController.onPageLoad(updatedAnswers.lrn, CheckMode))
          }
        }
      }

      "must go from Add consignor page" - {
        "to Is consignor eori known page when 'YES' is selected, if it was not previously answered" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeRemove(IsConsignorEoriKnownPage)

              navigator
                .nextPage(AddConsignorPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.IsConsignorEoriKnownController.onPageLoad(answers.lrn, CheckMode))
          }
        }

        "to Check Your Answers Page when 'YES' is selected, if Is consignor eori known was previously answered" in {

          forAll(genUserAnswersWithProcedure, arbitrary[Boolean]) {
            (answers, consignorEoriKnown) =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(true)
                .unsafeSetVal(IsConsignorEoriKnownPage)(consignorEoriKnown)

              navigator
                .nextPage(AddConsignorPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))

          }
        }

        "to Check Your Answers Page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsignorPage)(false)

              navigator
                .nextPage(AddConsignorPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))

          }
        }

      }

      "must go from Is consignor eori known page" - {
        "to Consignor EORI Number page when 'YES' is selected, if it was not previously answered" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeRemove(ConsignorEoriPage)

              navigator
                .nextPage(IsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorEoriController.onPageLoad(answers.lrn, CheckMode))

          }
        }

        "to Check Your Answers Page when 'YES' is selected, if Consignor EORI Number was previously answered" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(true)
                .unsafeSetVal(ConsignorEoriPage)("consignorEori")

              navigator
                .nextPage(IsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))

          }
        }

        "to Consignor Name page when 'NO' is selected, if it was not previously answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(false)
                .unsafeRemove(ConsignorNamePage)

              navigator
                .nextPage(IsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorNameController.onPageLoad(answers.lrn, CheckMode))
          }
        }

        "to Check Your Answers page when 'NO' is selected, and when Consignor Name exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsignorEoriKnownPage)(false)
                .unsafeSetVal(ConsignorNamePage)("Davey Jones")

              navigator
                .nextPage(IsConsignorEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

      }

      "must go from Consignor Eori Number page" - {
        "to Consignor Name page, if it was not previously answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeRemove(ConsignorNamePage)

              navigator
                .nextPage(ConsignorEoriPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorNameController.onPageLoad(answers.lrn, CheckMode))
          }
        }

        "to Check Your Answers page when Consignor Name exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(ConsignorNamePage)("Davey Jones")

              navigator
                .nextPage(ConsignorEoriPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }

      "must go from Consignor Name page" - {
        "to Check Your Answers page, when Consignor Address exists" in {
          forAll(arbitrary[UserAnswers], arbitrary[CommonAddress]) {
            (answers, address) =>
              val updatedAnswers = answers
                .unsafeSetVal(ConsignorAddressPage)(address)

              navigator
                .nextPage(ConsignorNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }

        "to Consignor Address page, when Consignor Address does not exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeRemove(ConsignorAddressPage)

              navigator
                .nextPage(ConsignorNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsignorAddressController.onPageLoad(answers.lrn, CheckMode))
          }
        }

      }

      "must go from Consignor Address page to Check Your Answers page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers

            navigator
              .nextPage(ConsignorAddressPage, CheckMode, updatedAnswers)
              .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from Add consignee page" - {
        "to Is consignee eori known page when 'YES' is selected, if it was not previously answered" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(true)
                .unsafeRemove(IsConsigneeEoriKnownPage)

              navigator
                .nextPage(AddConsigneePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.IsConsigneeEoriKnownController.onPageLoad(answers.lrn, CheckMode))
          }
        }

        "to Check Your Answers Page when 'YES' is selected, if is consignee eori was previously answered" in {

          forAll(genUserAnswersWithProcedure, arbitrary[Boolean]) {
            (answers, consigneeEoriKnown) =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(true)
                .unsafeSetVal(IsConsigneeEoriKnownPage)(consigneeEoriKnown)

              navigator
                .nextPage(AddConsigneePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))

          }
        }

        "to Check Your Answers Page when 'NO' is selected" in {

          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(AddConsigneePage)(false)

              navigator
                .nextPage(AddConsigneePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))

          }
        }

      }

      "must go from Is consignee eori known page" - {
        "to Consignee EORI Number page when 'YES' is selected, if it was not previously answered" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
                .unsafeRemove(WhatIsConsigneeEoriPage)

              navigator
                .nextPage(IsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.WhatIsConsigneeEoriController.onPageLoad(answers.lrn, CheckMode))

          }
        }

        "to Check Your Answers Page when 'YES' is selected, if Consignee EORI Number was previously answered" in {
          forAll(genUserAnswersWithProcedure) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(true)
                .unsafeSetVal(WhatIsConsigneeEoriPage)("consigneeEori")

              navigator
                .nextPage(IsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))

          }
        }

        "to Consignee Name page when 'NO' is selected, if it was not previously answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
                .unsafeRemove(ConsigneeNamePage)

              navigator
                .nextPage(IsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsigneeNameController.onPageLoad(answers.lrn, CheckMode))
          }
        }

        "to Check Your Answers page when 'NO' is selected, and when Consignee Name exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(IsConsigneeEoriKnownPage)(false)
                .unsafeSetVal(ConsigneeNamePage)("Davey Jones")

              navigator
                .nextPage(IsConsigneeEoriKnownPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }

      "must go from Consignee Eori Number page" - {
        "to Consignee Name page, if it was not previously answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeRemove(ConsigneeNamePage)

              navigator
                .nextPage(WhatIsConsigneeEoriPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsigneeNameController.onPageLoad(answers.lrn, CheckMode))
          }
        }

        "to Check Your Answers page when Consignee Name exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(ConsigneeNamePage)("Davey Jones")

              navigator
                .nextPage(WhatIsConsigneeEoriPage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }
        }
      }

      "must go from Consignee Name page to Check Your Answers page" - {

        "to Check Your Answers page, when Consignee Address exists" in {
          forAll(arbitrary[UserAnswers], arbitrary[CommonAddress]) {
            (answers, address) =>
              val updatedAnswers = answers
                .unsafeSetVal(ConsigneeAddressPage)(address)

              navigator
                .nextPage(ConsigneeNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
          }

        }

        "to Consignee Address page, when Consignee Address does not exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeRemove(ConsigneeAddressPage)

              navigator
                .nextPage(ConsigneeNamePage, CheckMode, updatedAnswers)
                .mustBe(traderDetailsRoute.ConsigneeAddressController.onPageLoad(answers.lrn, CheckMode))
          }

        }

      }

      "must go from Consignee Address page to Check Your Answers page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
            navigator
              .nextPage(ConsigneeAddressPage, CheckMode, updatedAnswers)
              .mustBe(traderDetailsRoute.TraderDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
