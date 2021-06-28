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
import commonTestUtils.UserAnswersSpecHelper
import models.DeclarationType.{Option1, Option2}
import models.Index
import models.reference.{CountryCode, CountryOfDispatch}
import org.scalacheck.Gen
import pages.addItems._
import pages.{CountryOfDispatchPage, DeclarationTypePage, QuestionPage}

class PreviousReferenceSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  private val previousReferenceUa = emptyUserAnswers
    .unsafeSetVal(ReferenceTypePage(index, referenceIndex))("referenceType")
    .unsafeSetVal(PreviousReferencePage(index, referenceIndex))("previousReference")
    .unsafeSetVal(AddExtraInformationPage(index, referenceIndex))(true)
    .unsafeSetVal(ExtraInformationPage(index, referenceIndex))("extraInformation")
    .unsafeSetVal(ReferenceTypePage(index, Index(1)))("referenceType")
    .unsafeSetVal(PreviousReferencePage(index, Index(1)))("previousReference")
    .unsafeSetVal(AddExtraInformationPage(index, Index(1)))(false)

  "previousReference" - {

    "can be parsed from UserAnswers" - {

      "when all details for the section have been answered" in {

        val expectedResult = PreviousReferences("referenceType", "previousReference", Some("extraInformation"))

        val result = UserAnswersReader[PreviousReferences](PreviousReferences.previousReferenceReader(index, referenceIndex)).run(previousReferenceUa)

        result.right.value mustBe expectedResult
      }

      "when AddExtraInformation is false" in {

        val expectedResult = PreviousReferences("referenceType", "previousReference", None)

        val userAnswers = previousReferenceUa.unsafeSetVal(AddExtraInformationPage(index, referenceIndex))(false)

        val result = UserAnswersReader[PreviousReferences](PreviousReferences.previousReferenceReader(index, referenceIndex)).run(userAnswers)

        result.right.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers when" - {

      "a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          ReferenceTypePage(index, referenceIndex),
          PreviousReferencePage(index, referenceIndex),
          AddExtraInformationPage(index, referenceIndex)
        )

        forAll(mandatoryPages) {
          mandatoryPage =>
            val userAnswers = previousReferenceUa.unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[PreviousReferences](PreviousReferences.previousReferenceReader(index, referenceIndex)).run(userAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }

  "derivePreviousReferences" - {

    "can be parsed from UserAnswers when" - {

      "when all mandatory pages have been answered " +
        "and DeclarationTypePage is either 'T2' or 'T2F'" +
        "and CountryOfDispatchPage is a none EU Country" in {

          val expectedResult = NonEmptyList(
            PreviousReferences("referenceType", "previousReference", Some("extraInformation")),
            List(PreviousReferences("referenceType", "previousReference", None))
          )

          val userAnswers = previousReferenceUa
            .unsafeSetVal(DeclarationTypePage)(Option2)
            .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), isNotEu = true))

          val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](PreviousReferences.derivePreviousReferences(index)).run(userAnswers)

          result.right.value.value mustBe expectedResult
        }

      "when all mandatory pages have been answered " +
        "and DeclarationTypePage is either 'T1' or 'T-' " +
        "and CountryOfDispatchPage is an EU Country " +
        "and AddAdministrativeReferencePage is true" in {

          val expectedResult = NonEmptyList(
            PreviousReferences("referenceType", "previousReference", Some("extraInformation")),
            List(PreviousReferences("referenceType", "previousReference", None))
          )

          val userAnswers = previousReferenceUa
            .unsafeSetVal(AddAdministrativeReferencePage(index))(true)
            .unsafeSetVal(DeclarationTypePage)(Option1)
            .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("IT"), isNotEu = false))

          val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](PreviousReferences.derivePreviousReferences(index)).run(userAnswers)

          result.right.value.value mustBe expectedResult
        }

      "when all mandatory pages have been answered " +
        "and DeclarationTypePage is either 'T1' or 'T-' " +
        "and CountryOfDispatchPage is an EU Country " +
        "and AddAdministrativeReferencePage is false" in {

          val userAnswers = previousReferenceUa
            .unsafeSetVal(AddAdministrativeReferencePage(index))(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)
            .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("IT"), isNotEu = false))

          val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](PreviousReferences.derivePreviousReferences(index)).run(userAnswers)

          result.right.value mustBe None
        }
    }

    "cannot be parsed from UserAnswers when" - {

      "a mandatory interdependent page is missing" in {

        val genMandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          DeclarationTypePage,
          CountryOfDispatchPage,
          ReferenceTypePage(index, referenceIndex),
          PreviousReferencePage(index, referenceIndex),
          AddExtraInformationPage(index, referenceIndex)
        )

        forAll(genMandatoryPages) {
          mandatoryPage =>
            val userAnswers = previousReferenceUa
              .unsafeSetVal(DeclarationTypePage)(Option2)
              .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), isNotEu = true))
              .unsafeRemove(mandatoryPage)

            val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](PreviousReferences.derivePreviousReferences(index)).run(userAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when all mandatory pages have been answered " +
        "and DeclarationTypePage is either 'T1' or 'T-' " +
        "and CountryOfDispatchPage is an EU Country " +
        "and AddAdministrativeReferencePage is missing" in {

          val userAnswers = previousReferenceUa
            .unsafeSetVal(DeclarationTypePage)(Option1)
            .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("IT"), isNotEu = false))
            .unsafeRemove(AddAdministrativeReferencePage(index))

          val result = UserAnswersReader[Option[NonEmptyList[PreviousReferences]]](PreviousReferences.derivePreviousReferences(index)).run(userAnswers)

          result.left.value.page mustBe AddAdministrativeReferencePage(index)
        }
    }
  }
}
