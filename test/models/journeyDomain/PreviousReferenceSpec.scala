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
import cats.data.{NonEmptyList, ReaderT}
import commonTestUtils.UserAnswersSpecHelper
import generators.JourneyModelGenerators
import models.DeclarationType.{Option1, Option2}
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.journeyDomain.PreviousReferenceSpec.setPreviousReferenceUserAnswers
import models.reference.CountryCode
import models.{DeclarationType, Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.addItems._
import pages.{CountryOfDispatchPage, DeclarationTypePage, QuestionPage}

class PreviousReferenceSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "previousReference" - {

    "can be parsed from UserAnswers when" - {

      "when all details for the section have been answered" in {
        forAll(arb[PreviousReferences], nonEmptyString, arb[UserAnswers]) {
          (previousReference, extraInformation, userAnswers) =>
            val expectedResult = previousReference.copy(extraInformation = Some(extraInformation))

            val setUserAnswers = setPreviousReferenceUserAnswers(expectedResult, index, referenceIndex)(userAnswers)
              .unsafeSetVal(AddExtraInformationPage(index, referenceIndex))(true)

            val result = UserAnswersReader[PreviousReferences](PreviousReferences.previousReferenceReader(index, referenceIndex)).run(setUserAnswers)

            result.right.value mustBe expectedResult
        }
      }

      "when AddExtraInformation is false" in {
        forAll(arb[PreviousReferences], arb[UserAnswers]) {
          (previousReference, userAnswers) =>
            val expectedResult = previousReference.copy(extraInformation = None)

            val setUserAnswers = setPreviousReferenceUserAnswers(expectedResult, index, referenceIndex)(userAnswers)
              .unsafeSetVal(AddExtraInformationPage(index, referenceIndex))(false)

            val result = UserAnswersReader[PreviousReferences](PreviousReferences.previousReferenceReader(index, referenceIndex)).run(setUserAnswers)

            result.right.value mustBe expectedResult
        }
      }
    }

    "cannot be parsed from UserAnswers when" - {

      "a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          ReferenceTypePage(index, referenceIndex),
          PreviousReferencePage(index, referenceIndex),
          AddExtraInformationPage(index, referenceIndex)
        )

        forAll(arb[PreviousReferences], arb[UserAnswers], mandatoryPages) {
          (previousReference, userAnswers, mandatoryPage) =>
            val setUserAnswers = setPreviousReferenceUserAnswers(previousReference, index, referenceIndex)(userAnswers)
              .unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[PreviousReferences](PreviousReferences.previousReferenceReader(index, referenceIndex)).run(setUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }

  "derivePreviousReferences" - {

    val genT2DeclarationType    = Option2
    val genOtherDeclarationType = Option1
    val genNonEUCountry         = Gen.oneOf(nonEUCountries)

    "can be parsed from UserAnswers when" - {

      "when all mandatory pages have been answered " +
        "and DeclarationTypePage is either 'T2' or 'T2F'" +
        "and CountryOfDispatchPage is a none EU Country" in {

          forAll(arbitrary[PreviousReferences], arbitrary[UserAnswers], genT2DeclarationType, genCountryOfDispatchNonEu) {

            case (previousReferences, userAnswers, declarationType, countryOfDispatch) =>
              val setPreviousReferences1: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, referenceIndex)(userAnswers)
              val setPreviousReferences2: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, Index(1))(setPreviousReferences1)

              val updatedUserAnswers = setPreviousReferences2
                .unsafeSetVal(DeclarationTypePage)(declarationType)
                .unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)

              val userAnswerReader: UserAnswersReader[Option[NonEmptyList[PreviousReferences]]] =
                PreviousReferences.derivePreviousReferences(index)

              val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](userAnswerReader).run(updatedUserAnswers)

              result.right.value.value mustEqual NonEmptyList(previousReferences, List(previousReferences))
          }
        }

      "when all mandatory pages have been answered " +
        "and DeclarationTypePage is either 'T1' or 'T-' " +
        "and CountryOfDispatchPage is an EU Country " +
        "and AddAdministrativeReferencePage is true" in {

          forAll(arbitrary[PreviousReferences], arbitrary[UserAnswers], genOtherDeclarationType, genCountryOfDispatchIsEu) {
            case (previousReferences, userAnswers, declarationType, countryOfDispatch) =>
              val setPreviousReferences1: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, referenceIndex)(userAnswers)
              val setPreviousReferences2: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, Index(1))(setPreviousReferences1)

              val updatedUserAnswers = setPreviousReferences2
                .unsafeSetVal(AddAdministrativeReferencePage(index))(true)
                .unsafeSetVal(DeclarationTypePage)(declarationType)
                .unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)

              val userAnswerReader: UserAnswersReader[Option[NonEmptyList[PreviousReferences]]] =
                PreviousReferences.derivePreviousReferences(index)

              val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](userAnswerReader).run(updatedUserAnswers)

              result.right.value.value mustEqual NonEmptyList(previousReferences, List(previousReferences))
          }
        }

      "when all mandatory pages have been answered " +
        "and DeclarationTypePage is either 'T1' or 'T-' " +
        "and CountryOfDispatchPage is an EU Country " +
        "and AddAdministrativeReferencePage is false" in {

          forAll(arbitrary[PreviousReferences], arbitrary[UserAnswers], genOtherDeclarationType, genCountryOfDispatchIsEu) {
            case (previousReferences, userAnswers, declarationType, countryOfDispatch) =>
              val setPreviousReferences1: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, referenceIndex)(userAnswers)
              val setPreviousReferences2: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, Index(1))(setPreviousReferences1)

              val updatedUserAnswers = setPreviousReferences2
                .unsafeSetVal(DeclarationTypePage)(declarationType)
                .unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)
                .unsafeSetVal(AddAdministrativeReferencePage(index))(false)

              val userAnswerReader: UserAnswersReader[Option[NonEmptyList[PreviousReferences]]] =
                PreviousReferences.derivePreviousReferences(index)

              val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](userAnswerReader).run(updatedUserAnswers)

              result.right.value must be(None)
          }
        }
    }

    "cannot be parsed from UserAnswers when" - {

      "a mandatory interdependent page is missing" in {

        val genMandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          ReferenceTypePage(index, referenceIndex),
          PreviousReferencePage(index, referenceIndex),
          AddExtraInformationPage(index, referenceIndex)
        )

        forAll(arbitrary[PreviousReferences], arbitrary[UserAnswers], genT2DeclarationType, genCountryOfDispatchNonEu, genMandatoryPages) {
          case (previousReferences, userAnswers, declarationType, countryOfDispatch, mandatoryPage) =>
            val setPreviousReferences1: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, referenceIndex)(userAnswers)
            val setPreviousReferences2: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, Index(1))(setPreviousReferences1)

            val updatedUserAnswers = setPreviousReferences2
              .unsafeSetVal(DeclarationTypePage)(declarationType)
              .unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)
              .unsafeRemove(mandatoryPage)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[PreviousReferences]]] =
              PreviousReferences.derivePreviousReferences(index)

            val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](userAnswerReader).run(updatedUserAnswers)

            result.left.value.page mustEqual mandatoryPage
        }
      }

      "when all mandatory pages have been answered " +
        "and DeclarationTypePage is either 'T1' or 'T-' " +
        "and CountryOfDispatchPage is an EU Country " +
        "and AddAdministrativeReferencePage is missing" in {

          forAll(arbitrary[PreviousReferences], arbitrary[UserAnswers], genOtherDeclarationType, genCountryOfDispatchIsEu) {
            case (previousReferences, userAnswers, declarationType, countryOfDispatch) =>
              val setPreviousReferences1: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, referenceIndex)(userAnswers)
              val setPreviousReferences2: UserAnswers = setPreviousReferenceUserAnswers(previousReferences, index, Index(1))(setPreviousReferences1)

              val updatedUserAnswers = setPreviousReferences2
                .unsafeSetVal(DeclarationTypePage)(declarationType)
                .unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)
                .unsafeRemove(AddAdministrativeReferencePage(index))

              val userAnswerReader: UserAnswersReader[Option[NonEmptyList[PreviousReferences]]] =
                PreviousReferences.derivePreviousReferences(index)

              val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](userAnswerReader).run(updatedUserAnswers)

              result.left.value.page mustEqual AddAdministrativeReferencePage(index)
          }
        }
    }
  }
}

object PreviousReferenceSpec extends UserAnswersSpecHelper {

  def setPreviousReferenceUserAnswers(previousReference: PreviousReferences, index: Index, referenceIndex: Index)(statUserAnswers: UserAnswers): UserAnswers = {
    val ua = statUserAnswers
      .unsafeSetVal(ReferenceTypePage(index, referenceIndex))(previousReference.referenceType)
      .unsafeSetVal(PreviousReferencePage(index, referenceIndex))(previousReference.previousReference)
      .unsafeSetVal(AddExtraInformationPage(index, referenceIndex))(previousReference.extraInformation.isDefined)

    previousReference.extraInformation.fold(ua) {
      info =>
        ua.unsafeSetVal(ExtraInformationPage(index, referenceIndex))(info)
    }
  }
}
