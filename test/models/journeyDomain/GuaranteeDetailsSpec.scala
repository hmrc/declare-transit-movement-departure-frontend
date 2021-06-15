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
import cats.data.NonEmptyList
import generators.JourneyModelGenerators
import models.{Index, UserAnswers}
import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}

class GuaranteeDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "GuaranteeDetails" - {

    "cannot be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(nonEmptyListOf[GuaranteeDetails](1), arbitrary[UserAnswers]) {
          case (guarantees, userAnswers) =>
            val updatedUserAnswer = GuaranteeDetailsSpec.setGuaranteeDetails(guarantees)(userAnswers)

            val result: EitherType[GuaranteeDetails] = UserAnswersReader[GuaranteeDetails](GuaranteeDetails.parseGuaranteeDetails(index)).run(updatedUserAnswer)

            result.right.value mustEqual guarantees.head
        }
      }
      "when there are multiple GuaranteeDetails all details for section have been answered" in {
        forAll(arb[GuaranteeDetails], arb[GuaranteeDetails], arbitrary[GuaranteeDetails], arbitrary[UserAnswers]) {
          case (guarantee1, guarantee2, guarantee3, userAnswers) =>
            val guarantees = NonEmptyList(guarantee1, List(guarantee2, guarantee3))

            val updatedUserAnswer                                  = GuaranteeDetailsSpec.setGuaranteeDetails(guarantees)(userAnswers)
            val result: EitherType[NonEmptyList[GuaranteeDetails]] = UserAnswersReader[NonEmptyList[GuaranteeDetails]].run(updatedUserAnswer)

            result.right.value mustEqual guarantees
        }
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when procedureType in missing" in {

        forAll(nonEmptyListOf[GuaranteeDetails](1), arbitrary[UserAnswers]) {
          case (guarantees, ua) =>
            val uaWithGuaranteeDetails = GuaranteeDetailsSpec.setGuaranteeDetails(guarantees)(ua)
            val invalidUa              = uaWithGuaranteeDetails.unsafeRemove(GuaranteeTypePage(Index(0)))

            val result: EitherType[GuaranteeDetails] = UserAnswersReader[GuaranteeDetails](GuaranteeDetails.parseGuaranteeDetails(index)).run(invalidUa)

            result.left.value mustEqual ReaderError(GuaranteeTypePage(Index(0)))
        }
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

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer =
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected, index)(userAnswers)
                  .remove(DefaultAmountPage(index))
                  .toOption
                  .value
              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer).right.value

              result mustEqual expected
          }
        }

        "when all mandatory field are defined and use DefaultLiability amount when DefaultLiability is defined as true" in {

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer =
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected, index)(userAnswers)
                  .set(DefaultAmountPage(index), true)
                  .toOption
                  .value
                  .remove(LiabilityAmountPage(index))
                  .toOption
                  .value
              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer).right.value

              result.liabilityAmount mustEqual DefaultLiabilityAmount
          }
        }

        "when all mandatory field are defined and use LiabilityAmount when DefaultLiability is defined as false" in {

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer =
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected, index)(userAnswers)
                  .set(DefaultAmountPage(index), false)
                  .toOption
                  .value
              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer).right.value

              result.liabilityAmount mustEqual expected.liabilityAmount
          }
        }
      }

      "cannot be parsed" - {

        "when an answer is missing" in {

          forAll(arbitrary[UserAnswers], mandatoryPages, arbitrary[GuaranteeReference]) {
            case (ua, mandatoryPage, guaranteeReference) =>
              val userAnswers        = GuaranteeDetailsSpec.setGuaranteeReferenceUserAnswers(guaranteeReference, index)(ua)
              val updatedUserAnswers = userAnswers.remove(mandatoryPage).success.value
              val result             = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswers)

              result.left.value mustBe ReaderError(mandatoryPage)
          }
        }

        "when LiabilityAmount is missing and DefaultLiability is false" in {

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer =
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected, index)(userAnswers)
                  .set(DefaultAmountPage(index), false)
                  .toOption
                  .value
                  .remove(LiabilityAmountPage(index))
                  .toOption
                  .value

              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer).left.value

              result mustBe ReaderError(LiabilityAmountPage(index))
          }
        }

        "when LiabilityAmount is missing and DefaultLiability is missing" in {

          forAll(arbitrary[GuaranteeReference], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer =
                GuaranteeDetailsSpec
                  .setGuaranteeReferenceUserAnswers(expected, index)(userAnswers)
                  .remove(DefaultAmountPage(index))
                  .toOption
                  .value
                  .remove(LiabilityAmountPage(index))
                  .toOption
                  .value

              val result = UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index)).run(updatedUserAnswer)

              result.left.value mustBe ReaderError(LiabilityAmountPage(index))
          }
        }
      }
    }

    "GuaranteeOther" - {

      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        GuaranteeTypePage(index),
        OtherReferencePage(index)
      )

      "can be parsed" - {

        "when all details for section have been answered" in {

          forAll(arbitrary[GuaranteeOther], arbitrary[UserAnswers]) {
            case (expected, userAnswers) =>
              val updatedUserAnswer = GuaranteeDetailsSpec.setGuaranteeOtherUserAnswers(expected, index)(userAnswers)
              val result            = UserAnswersReader[GuaranteeOther](GuaranteeOther.parseGuaranteeOther(index)).run(updatedUserAnswer).right.value

              result mustEqual expected
          }
        }
      }

      "cannot be parsed" - {

        "when an answer is missing" in {

          forAll(arbitrary[UserAnswers], mandatoryPages) {
            case (ua, mandatoryPage) =>
              val userAnswers = ua.remove(mandatoryPage).success.value
              val result      = UserAnswersReader[GuaranteeOther](GuaranteeOther.parseGuaranteeOther(index)).run(userAnswers).isLeft

              result mustBe true
          }
        }
      }
    }
  }
}

object GuaranteeDetailsSpec {

  def setGuaranteeDetails(guaranteeDetails: NonEmptyList[GuaranteeDetails])(startUserAnswers: UserAnswers): UserAnswers =
    guaranteeDetails.zipWithIndex.foldLeft[UserAnswers](startUserAnswers) {
      case (updatedUserAnswers, (guaranteeReference: GuaranteeReference, index)) =>
        setGuaranteeReferenceUserAnswers(guaranteeReference, Index(index))(updatedUserAnswers)
      case (updatedUserAnswers, (guaranteeOther: GuaranteeOther, index)) =>
        setGuaranteeOtherUserAnswers(guaranteeOther, Index(index))(updatedUserAnswers)
    }

  def setGuaranteeOtherUserAnswers(otherGuarantee: GuaranteeOther, index: Index)(startUserAnswers: UserAnswers): UserAnswers = {

    val guaranteeOtherUserAnswers = startUserAnswers
      .unsafeSetVal(GuaranteeTypePage(index))(otherGuarantee.guaranteeType)
      .unsafeSetVal(OtherReferencePage(index))(otherGuarantee.otherReference)

    guaranteeOtherUserAnswers
  }

  def setGuaranteeReferenceUserAnswers(guaranteeReference: GuaranteeReference, index: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val liabilityAmountAmount = guaranteeReference.liabilityAmount match {
      case DefaultLiabilityAmount          => "10000"
      case OtherLiabilityAmount(amount, _) => amount
    }
    startUserAnswers
      .unsafeSetVal(GuaranteeTypePage(index))(guaranteeReference.guaranteeType)
      .unsafeSetVal(GuaranteeReferencePage(index))(guaranteeReference.guaranteeReferenceNumber)
      .unsafeSetVal(LiabilityAmountPage(index))(liabilityAmountAmount)
      .unsafeSetVal(AccessCodePage(index))(guaranteeReference.accessCode)
  }
}
