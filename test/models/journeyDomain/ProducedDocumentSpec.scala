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
import cats.data.{NonEmptyList, ReaderT}
import generators.JourneyModelGenerators
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.journeyDomain.ProducedDocumentSpec.setProducedDocumentsUserAnswers
import models.reference.CircumstanceIndicator
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.AddSecurityDetailsPage
import pages.addItems._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}

class ProducedDocumentSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "ProducedDocument" - {

    "producedDocumentReader" - {
      "can be parsed from UserAnswers" - {
        "when all details for section have been answered" in {
          forAll(arbitrary[ProducedDocument], arbitrary[UserAnswers]) {
            case (producedDocument, userAnswers) =>
              val updatedUserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, referenceIndex)(userAnswers)
              val result             = UserAnswersReader[ProducedDocument](ProducedDocument.producedDocumentReader(index, referenceIndex)).run(updatedUserAnswers)

              result.right.value mustEqual producedDocument
          }
        }
      }

      "cannot be parsed from UserAnswers" - {
        "when a mandatory answer is missing" in {
          forAll(arbitrary[ProducedDocument], arbitrary[UserAnswers]) {
            case (producedDocument, userAnswers) =>
              val updatedUserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, referenceIndex)(userAnswers)
                .remove(DocumentTypePage(index, referenceIndex))
                .success
                .value

              val result: EitherType[ProducedDocument] =
                UserAnswersReader[ProducedDocument](ProducedDocument.producedDocumentReader(index, referenceIndex)).run(updatedUserAnswers)

              result.left.value.page mustBe DocumentTypePage(index, referenceIndex)
          }
        }
      }
    }

    "deriveProducedDocuments" - {

      "must return List of produced documents when " +
        "AddSecurityDetailsPage is true, " +
        "AddCommercialReferenceNumberPage is false, " +
        "AddCircumstanceIndicatorPage is false and " +
        "Index position is 0" in {

        forAll(arbitrary[ProducedDocument], arbitrary[UserAnswers]) {
          case (producedDocument, userAnswers) =>
            val setProducedDocument1: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, referenceIndex)(userAnswers)
            val setProducedDocument2: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, Index(1))(setProducedDocument1)

            val updatedUserAnswers = setProducedDocument2
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(index)

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value.value mustEqual NonEmptyList(producedDocument, List(producedDocument))
        }
      }

      "must return List of produced documents when " +
        "AddSecurityDetailsPage is true, " +
        "AddCommercialReferenceNumberPage is false, " +
        "AddCircumstanceIndicatorPage is true and " +
        "Index position is 0 and " +
        "CircumstanceIndicator is one of the conditional indicators" in {

        val genValidCircumstanceIndicator = Gen.oneOf(CircumstanceIndicator.conditionalIndicators)

        forAll(arbitrary[ProducedDocument], arbitrary[UserAnswers], genValidCircumstanceIndicator) {
          case (producedDocument, userAnswers, circumstanceIndicator) =>
            val setProducedDocument1: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, referenceIndex)(userAnswers)
            val setProducedDocument2: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, Index(1))(setProducedDocument1)

            val updatedUserAnswers = setProducedDocument2
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)(circumstanceIndicator)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(index)

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value.value mustEqual NonEmptyList(producedDocument, List(producedDocument))
        }
      }

      "must return List of produced documents when AddSecurityDetailsPage is false and AddDocumentsPage is true" in {

        forAll(arbitrary[ProducedDocument], arbitrary[UserAnswers]) {
          case (producedDocument, userAnswers) =>
            val setProducedDocument1: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, referenceIndex)(userAnswers)
            val setProducedDocument2: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, Index(1))(setProducedDocument1)

            val updatedUserAnswers = setProducedDocument2
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddDocumentsPage(index))(true)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(index)

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value.value mustEqual NonEmptyList(producedDocument, List(producedDocument))
        }
      }

      "must return List of produced documents when AddCommercialReferenceNumberPage is true and addDocumentsPage is true" in {

        forAll(arbitrary[ProducedDocument], arbitrary[UserAnswers]) {
          case (producedDocument, userAnswers) =>
            val setProducedDocument1: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, referenceIndex)(userAnswers)
            val setProducedDocument2: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, Index(1))(setProducedDocument1)

            val updatedUserAnswers = setProducedDocument2
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddDocumentsPage(index))(true)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(index)

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value.value mustEqual NonEmptyList(producedDocument, List(producedDocument))
        }
      }

      "must return List of produced documents when Index position is not 0 and AddDocumentsPage is true" in {

        forAll(arbitrary[ProducedDocument], arbitrary[UserAnswers]) {
          case (producedDocument, userAnswers) =>
            val setProducedDocument1: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, referenceIndex)(userAnswers)
            val setProducedDocument2: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, Index(1))(setProducedDocument1)

            val updatedUserAnswers = setProducedDocument2
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddDocumentsPage(index))(true)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(Index(0))

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value.value mustEqual NonEmptyList(producedDocument, List(producedDocument))
        }
      }

      "must return List of produced documents when " +
        "AddSecurityDetailsPage is true, " +
        "AddCommercialReferenceNumberPage is false, " +
        "AddCircumstanceIndicatorPage is true and " +
        "Index position is 0 and " +
        "CircumstanceIndicator is not one of the conditional indicators and " +
        "AddDocumentPage is true" in {

        val genInvalidCircumstanceIndicator = arb[String].suchThat(
          string => !CircumstanceIndicator.conditionalIndicators.forall(_.contains(string))
        )

        forAll(arbitrary[ProducedDocument], arbitrary[UserAnswers], genInvalidCircumstanceIndicator) {
          case (producedDocument, userAnswers, invalidCircumstanceIndicator) =>
            val setProducedDocument1: UserAnswers = setProducedDocumentsUserAnswers(producedDocument, index, referenceIndex)(userAnswers)

            val updatedUserAnswers = setProducedDocument1
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)(invalidCircumstanceIndicator)
              .unsafeSetVal(AddDocumentsPage(index))(true)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(index)

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value.value mustEqual NonEmptyList(producedDocument, List.empty)
        }
      }

      "must return None when " +
        "AddSecurityDetailsPage is true, " +
        "AddCommercialReferenceNumberPage is false, " +
        "AddCircumstanceIndicatorPage is true and " +
        "Index position is 0 and " +
        "CircumstanceIndicator is not one of the conditional indicators and " +
        "AddDocumentPage is false" in {

        val genInvalidCircumstanceIndicator = arb[String].suchThat(
          string => !CircumstanceIndicator.conditionalIndicators.forall(_.contains(string))
        )

        forAll(arbitrary[UserAnswers], genInvalidCircumstanceIndicator) {
          case (userAnswers, invalidCircumstanceIndicator) =>
            val updatedUserAnswers = userAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)(invalidCircumstanceIndicator)
              .unsafeSetVal(AddDocumentsPage(index))(false)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(index)

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value must be(None)
        }
      }

      "must return None when AddSecurityDetailsPage is false and AddDocumentsPage is false" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val updatedUserAnswers = userAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddDocumentsPage(index))(false)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(index)

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value must be(None)
        }
      }

      "must return None when AddCommercialReferenceNumberPage is true and addDocumentsPage is false" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val updatedUserAnswers = userAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddDocumentsPage(index))(false)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(index)

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value must be(None)
        }
      }

      "must return None when Index position is not 0 and AddDocumentsPage is false" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val updatedUserAnswers = userAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddDocumentsPage(index))(false)
              .unsafeSetVal(AddDocumentsPage(Index(1)))(false)

            val userAnswerReader: UserAnswersReader[Option[NonEmptyList[ProducedDocument]]] = ProducedDocument.deriveProducedDocuments(Index(1))

            val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](userAnswerReader).run(updatedUserAnswers)

            result.right.value must be(None)
        }
      }
    }
  }
}

object ProducedDocumentSpec extends UserAnswersSpecHelper {

  def setProducedDocumentsUserAnswers(document: ProducedDocument, index: Index, referenceIndex: Index)(statUserAnswers: UserAnswers): UserAnswers = {
    val ua = statUserAnswers
      .unsafeSetVal(DocumentTypePage(index, referenceIndex))(document.documentType)
      .unsafeSetVal(DocumentReferencePage(index, referenceIndex))(document.documentReference)
      .unsafeSetVal(AddExtraDocumentInformationPage(index, referenceIndex))(document.extraInformation.isDefined)

    document.extraInformation.fold(ua) {
      info =>
        ua.unsafeSetVal(DocumentExtraInformationPage(index, referenceIndex))(info)
    }
  }
}
