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
import cats.data.{NonEmptyList, NonEmptyMap}
import commonTestUtils.UserAnswersSpecHelper
import models.DeclarationType.{Option2, Option4}
import models.GuaranteeType.{guaranteeReferenceRoute, nonGuaranteeReferenceRoute, TIR}
import models.journeyDomain.CurrencyCode.GBP
import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.{Index, UserAnswers}
import org.scalacheck.Gen
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage, TIRGuaranteeReferencePage}

class GuaranteeDetailsSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  private val guaranteeReferenceType      = Gen.oneOf(guaranteeReferenceRoute).sample.value
  private val otherGuaranteeReferenceType = Gen.oneOf(nonGuaranteeReferenceRoute).sample.value

  private val tirGuaranteeReferenceUa = emptyUserAnswers
    .unsafeSetVal(DeclarationTypePage)(Option4)
    .unsafeSetVal(TIRGuaranteeReferencePage(index))("tirRefNumber")

  private val guaranteeReferenceUa: UserAnswers = emptyUserAnswers
    .unsafeSetVal(GuaranteeTypePage(index))(guaranteeReferenceType)
    .unsafeSetVal(GuaranteeReferencePage(index))("refNumber")
    .unsafeSetVal(LiabilityAmountPage(index))("5000")
    .unsafeSetVal(AccessCodePage(index))("1234")

  private val otherGuaranteeUa: UserAnswers = emptyUserAnswers
    .unsafeSetVal(DeclarationTypePage)(Option2)
    .unsafeSetVal(GuaranteeTypePage(index))(otherGuaranteeReferenceType)
    .unsafeSetVal(OtherReferencePage(index))("otherRefNumber")

  private val listOfGuaranteeDetailsWithTIR = emptyUserAnswers
    .unsafeSetVal(DeclarationTypePage)(Option4)
    .unsafeSetVal(GuaranteeTypePage(index))(TIR)
    .unsafeSetVal(TIRGuaranteeReferencePage(index))("tirRefNumber1")
    .unsafeSetVal(GuaranteeTypePage(Index(1)))(TIR)
    .unsafeSetVal(TIRGuaranteeReferencePage(Index(1)))("tirRefNumber2")
    .unsafeSetVal(GuaranteeTypePage(Index(2)))(TIR)
    .unsafeSetVal(TIRGuaranteeReferencePage(Index(2)))("tirRefNumber3")
    .unsafeSetVal(GuaranteeTypePage(Index(3)))(TIR)
    .unsafeSetVal(TIRGuaranteeReferencePage(Index(3)))("tirRefNumber4")
    .unsafeSetVal(GuaranteeTypePage(Index(4)))(TIR)
    .unsafeSetVal(TIRGuaranteeReferencePage(Index(4)))("tirRefNumber5")

  private val listOfGuaranteeDetails = emptyUserAnswers
    .unsafeSetVal(DeclarationTypePage)(Option2)
    .unsafeSetVal(GuaranteeTypePage(index))(guaranteeReferenceType)
    .unsafeSetVal(GuaranteeReferencePage(index))("refNumber")
    .unsafeSetVal(LiabilityAmountPage(index))("5000")
    .unsafeSetVal(AccessCodePage(index))("1234")
    .unsafeSetVal(GuaranteeTypePage(Index(1)))(otherGuaranteeReferenceType)
    .unsafeSetVal(OtherReferencePage(Index(1)))("otherRefNumber")

  "GuaranteeDetails" - {
    "can be parsed UserAnswers" - {

      "when guarantee type is a valid guarantee reference and all answers are defined" in {

        val expectedResult = GuaranteeReference(guaranteeReferenceType, "refNumber", OtherLiabilityAmount("5000", GBP), "1234")

        val result: EitherType[GuaranteeDetails] = UserAnswersReader[GuaranteeDetails](GuaranteeDetails.parseGuaranteeDetails(index)).run(guaranteeReferenceUa)

        result.right.value mustBe expectedResult
      }

      "when guarantee type is a valid other type of guarantee and all answers are defined" in {

        val expectedResult = GuaranteeOther(otherGuaranteeReferenceType, "otherRefNumber")

        val result: EitherType[GuaranteeDetails] = UserAnswersReader[GuaranteeDetails](GuaranteeDetails.parseGuaranteeDetails(index)).run(otherGuaranteeUa)

        result.right.value mustBe expectedResult
      }

      "when there are multiple GuaranteeDetails all details for section have been answered" in {

        val expectedResult = NonEmptyList(
          GuaranteeReference(guaranteeReferenceType, "refNumber", OtherLiabilityAmount("5000", GBP), "1234"),
          List(GuaranteeOther(otherGuaranteeReferenceType, "otherRefNumber"))
        )

        val result: EitherType[NonEmptyList[GuaranteeDetails]] = UserAnswersReader[NonEmptyList[GuaranteeDetails]].run(listOfGuaranteeDetails)

        result.right.value mustBe expectedResult
      }

      "when there are multiple GuaranteeDetails with a TIR Declaration Type" in {

        val expectedResult = NonEmptyList(
          GuaranteeOther(TIR, "tirRefNumber1"),
          List(
            GuaranteeOther(TIR, "tirRefNumber2"),
            GuaranteeOther(TIR, "tirRefNumber3"),
            GuaranteeOther(TIR, "tirRefNumber4"),
            GuaranteeOther(TIR, "tirRefNumber5")
          )
        )

        val result: EitherType[NonEmptyList[GuaranteeDetails]] = UserAnswersReader[NonEmptyList[GuaranteeDetails]].run(listOfGuaranteeDetailsWithTIR)

        result.right.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when GuaranteeTypePage in missing" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[GuaranteeDetails] = UserAnswersReader[GuaranteeDetails](GuaranteeDetails.parseGuaranteeDetails(index)).run(userAnswers)

        result.left.value.page mustBe GuaranteeTypePage(index)
      }

      "when GuaranteeTypePage is missing when multiple GuaranteeDetails" in {

        val userAnswers = listOfGuaranteeDetails.unsafeRemove(GuaranteeTypePage(Index(1)))

        val result: EitherType[NonEmptyList[GuaranteeDetails]] = UserAnswersReader[NonEmptyList[GuaranteeDetails]].run(userAnswers)

        result.left.value.page mustBe GuaranteeTypePage(Index(1))
      }
    }

    "GuaranteeReference" - {

      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        GuaranteeTypePage(index),
        GuaranteeReferencePage(index),
        LiabilityAmountPage(index),
        AccessCodePage(index)
      )

      "can be parsed" - {

        "when all mandatory field are defined and DefaultLiability is not defined" in {

          val expectedResult = GuaranteeReference(guaranteeReferenceType, "refNumber", OtherLiabilityAmount("5000", GBP), "1234")

          val userAnswers = guaranteeReferenceUa.unsafeRemove(DefaultAmountPage(index))

          val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(userAnswers).right.value

          result mustBe expectedResult
        }

        "when all mandatory field are defined and use DefaultLiability amount when DefaultLiability is defined as true" in {

          val expectedResult = GuaranteeReference(guaranteeReferenceType, "refNumber", DefaultLiabilityAmount, "1234")

          val userAnswers = guaranteeReferenceUa
            .unsafeSetVal(DefaultAmountPage(index))(true)
            .unsafeRemove(LiabilityAmountPage(index))

          val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(userAnswers).right.value

          result mustBe expectedResult
        }

        "when all mandatory field are defined and use LiabilityAmount when DefaultLiability is defined as false" in {

          val expectedResult = GuaranteeReference(guaranteeReferenceType, "refNumber", OtherLiabilityAmount("5000", GBP), "1234")

          val userAnswers = guaranteeReferenceUa
            .unsafeSetVal(DefaultAmountPage(index))(false)

          val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(userAnswers).right.value

          result mustBe expectedResult
        }
      }

      "cannot be parsed" - {

        "when an answer is missing" in {

          forAll(mandatoryPages) {
            mandatoryPage =>
              val userAnswers = guaranteeReferenceUa.unsafeRemove(mandatoryPage)
              val result      = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(userAnswers)

              result.left.value.page mustBe mandatoryPage
          }
        }

        "when LiabilityAmount is missing and DefaultLiability is false" in {

          val userAnswers = guaranteeReferenceUa
            .unsafeSetVal(DefaultAmountPage(index))(false)
            .unsafeRemove(LiabilityAmountPage(index))

          val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(userAnswers)

          result.left.value.page mustBe LiabilityAmountPage(index)
        }

        "when LiabilityAmount is missing and DefaultLiability is missing" in {

          val userAnswers = guaranteeReferenceUa
            .unsafeRemove(DefaultAmountPage(index))
            .unsafeRemove(LiabilityAmountPage(index))

          val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(userAnswers)

          result.left.value.page mustBe LiabilityAmountPage(index)
        }
      }
    }

    "GuaranteeOther" - {

      "can be parsed" - {

        "when all details for section have been answered when not a TIR Declaration Type" in {

          val expectedResult = GuaranteeOther(otherGuaranteeReferenceType, "otherRefNumber")

          val result = UserAnswersReader[GuaranteeOther](GuaranteeOther.parseGuaranteeOther(index)).run(otherGuaranteeUa).right.value

          result mustBe expectedResult
        }

        "when all details for section have been answered when a TIR Declaration Type" in {

          val expectedResult = GuaranteeOther(TIR, "tirRefNumber")

          val result = UserAnswersReader[GuaranteeOther](GuaranteeOther.parseGuaranteeOther(index)).run(tirGuaranteeReferenceUa).right.value

          result mustBe expectedResult
        }
      }

      "cannot be parsed" - {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          DeclarationTypePage,
          GuaranteeTypePage(index),
          OtherReferencePage(index)
        )

        "when an answer is missing for non TIR Declaration Type" in {

          forAll(mandatoryPages) {
            mandatoryPage =>
              val userAnswers = otherGuaranteeUa.unsafeRemove(mandatoryPage)
              val result      = UserAnswersReader[GuaranteeOther](GuaranteeOther.parseGuaranteeOther(index)).run(userAnswers)

              result.left.value.page mustBe mandatoryPage
          }
        }

        "when an answer is missing for TIR Declaration Type" in {

          val userAnswers = tirGuaranteeReferenceUa
            .unsafeRemove(TIRGuaranteeReferencePage(index))

          val result = UserAnswersReader[GuaranteeOther](GuaranteeOther.parseGuaranteeOther(index)).run(userAnswers)

          result.left.value.page mustBe TIRGuaranteeReferencePage(index)
        }
      }
    }
  }
}
