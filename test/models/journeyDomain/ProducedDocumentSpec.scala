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
import models.Index
import models.reference.CircumstanceIndicator
import org.scalacheck.Gen
import pages.addItems._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}
import pages.{AddSecurityDetailsPage, QuestionPage}

class ProducedDocumentSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  private val producedDocumentUa = emptyUserAnswers
    .unsafeSetVal(DocumentTypePage(index, referenceIndex))("documentType")
    .unsafeSetVal(DocumentReferencePage(index, referenceIndex))("documentReference")
    .unsafeSetVal(AddExtraDocumentInformationPage(index, referenceIndex))(true)
    .unsafeSetVal(DocumentExtraInformationPage(index, referenceIndex))("documentExtraInformation")
    .unsafeSetVal(DocumentTypePage(index, Index(1)))("documentType")
    .unsafeSetVal(DocumentReferencePage(index, Index(1)))("documentReference")
    .unsafeSetVal(AddExtraDocumentInformationPage(index, Index(1)))(false)

  "ProducedDocument" - {
    "producedDocumentReader" - {
      "can be parsed from UserAnswers" - {
        "when all details for section have been answered" in {

          val expectedResult = ProducedDocument("documentType", "documentReference", Some("documentExtraInformation"))

          val result = UserAnswersReader[ProducedDocument](ProducedDocument.producedDocumentReader(index, referenceIndex)).run(producedDocumentUa)

          result.right.value mustBe expectedResult
        }

        "when AddExtraDocumentInformationPage is false" in {

          val expectedResult = ProducedDocument("documentType", "documentReference", None)

          val userAnswers = producedDocumentUa.unsafeSetVal(AddExtraDocumentInformationPage(index, referenceIndex))(false)

          val result = UserAnswersReader[ProducedDocument](ProducedDocument.producedDocumentReader(index, referenceIndex)).run(userAnswers)

          result.right.value mustBe expectedResult
        }
      }

      "cannot be parsed from UserAnswers" - {
        "when a mandatory answer is missing" in {

          val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
            DocumentTypePage(index, referenceIndex),
            DocumentReferencePage(index, referenceIndex),
            AddExtraDocumentInformationPage(index, referenceIndex)
          )

          forAll(mandatoryPages) {
            mandatoryPage =>
              val userAnswers = producedDocumentUa.unsafeRemove(mandatoryPage)

              val result = UserAnswersReader[ProducedDocument](ProducedDocument.producedDocumentReader(index, referenceIndex)).run(userAnswers)
              result.left.value.page mustBe mandatoryPage
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

          val producedDoc1 = ProducedDocument("documentType", "documentReference", Some("documentExtraInformation"))
          val producedDoc2 = ProducedDocument("documentType", "documentReference", None)

          val expectedResult = NonEmptyList(producedDoc1, List(producedDoc2))

          val userAnswers = producedDocumentUa
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(AddCircumstanceIndicatorPage)(false)

          val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(index)).run(userAnswers)

          result.right.value.value mustBe expectedResult
        }

      "must return List of produced documents when " +
        "AddSecurityDetailsPage is true, " +
        "AddCommercialReferenceNumberPage is false, " +
        "AddCircumstanceIndicatorPage is true and " +
        "Index position is 0 and " +
        "CircumstanceIndicator is one of the conditional indicators" in {

          val validCircumstanceIndicator = Gen.oneOf(CircumstanceIndicator.conditionalIndicators).sample.value

          val producedDoc1 = ProducedDocument("documentType", "documentReference", Some("documentExtraInformation"))
          val producedDoc2 = ProducedDocument("documentType", "documentReference", None)

          val expectedResult = NonEmptyList(producedDoc1, List(producedDoc2))

          val userAnswers = producedDocumentUa
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)(validCircumstanceIndicator)

          val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(index)).run(userAnswers)

          result.right.value.value mustBe expectedResult
        }

      "must return List of produced documents when AddSecurityDetailsPage is false and AddDocumentsPage is true" in {

        val producedDoc1 = ProducedDocument("documentType", "documentReference", Some("documentExtraInformation"))
        val producedDoc2 = ProducedDocument("documentType", "documentReference", None)

        val expectedResult = NonEmptyList(producedDoc1, List(producedDoc2))

        val userAnswers = producedDocumentUa
          .unsafeSetVal(AddSecurityDetailsPage)(false)
          .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
          .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
          .unsafeSetVal(AddDocumentsPage(index))(true)

        val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(index)).run(userAnswers)

        result.right.value.value mustBe expectedResult
      }

      "must return List of produced documents when AddCommercialReferenceNumberPage is true and addDocumentsPage is true" in {

        val producedDoc1 = ProducedDocument("documentType", "documentReference", Some("documentExtraInformation"))
        val producedDoc2 = ProducedDocument("documentType", "documentReference", None)

        val expectedResult = NonEmptyList(producedDoc1, List(producedDoc2))

        val userAnswers = producedDocumentUa
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
          .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
          .unsafeSetVal(AddDocumentsPage(index))(true)

        val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(index)).run(userAnswers)

        result.right.value.value mustBe expectedResult
      }

      "must return List of produced documents when Index position is not 0 and AddDocumentsPage is true" in {

        val expectedResult = NonEmptyList(ProducedDocument("documentType", "documentReference", None), List.empty)

        val userAnswers = producedDocumentUa
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
          .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
          .unsafeSetVal(AddDocumentsPage(Index(1)))(true)
          .unsafeSetVal(DocumentTypePage(Index(1), index))("documentType")
          .unsafeSetVal(DocumentReferencePage(Index(1), index))("documentReference")
          .unsafeSetVal(AddExtraDocumentInformationPage(Index(1), index))(false)

        val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(Index(1))).run(userAnswers)

        result.right.value.value mustBe expectedResult
      }

      "must return List of produced documents when " +
        "AddSecurityDetailsPage is true, " +
        "AddCommercialReferenceNumberPage is false, " +
        "AddCircumstanceIndicatorPage is true and " +
        "Index position is 0 and " +
        "CircumstanceIndicator is not one of the conditional indicators and " +
        "AddDocumentPage is true" in {

          val invalidCircumstanceIndicator = arb[String]
            .suchThat(
              string => !CircumstanceIndicator.conditionalIndicators.forall(_.contains(string))
            )
            .sample
            .value

          val producedDoc1 = ProducedDocument("documentType", "documentReference", Some("documentExtraInformation"))
          val producedDoc2 = ProducedDocument("documentType", "documentReference", None)

          val expectedResult = NonEmptyList(producedDoc1, List(producedDoc2))

          val userAnswers = producedDocumentUa
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)(invalidCircumstanceIndicator)
            .unsafeSetVal(AddDocumentsPage(index))(true)

          val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(index)).run(userAnswers)

          result.right.value.value mustBe expectedResult
        }

      "must return None when " +
        "AddSecurityDetailsPage is true, " +
        "AddCommercialReferenceNumberPage is false, " +
        "AddCircumstanceIndicatorPage is true and " +
        "Index position is 0 and " +
        "CircumstanceIndicator is not one of the conditional indicators and " +
        "AddDocumentPage is false" in {

          val invalidCircumstanceIndicator = arb[String]
            .suchThat(
              string => !CircumstanceIndicator.conditionalIndicators.forall(_.contains(string))
            )
            .sample
            .value

          val userAnswers = producedDocumentUa
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)(invalidCircumstanceIndicator)
            .unsafeSetVal(AddDocumentsPage(index))(false)

          val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(index)).run(userAnswers)

          result.right.value mustBe None
        }

      "must return None when AddSecurityDetailsPage is false and AddDocumentsPage is false" in {

        val userAnswers = producedDocumentUa
          .unsafeSetVal(AddSecurityDetailsPage)(false)
          .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
          .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
          .unsafeSetVal(AddDocumentsPage(index))(false)

        val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(index)).run(userAnswers)

        result.right.value mustBe None
      }

      "must return None when AddCommercialReferenceNumberPage is true and addDocumentsPage is false" in {

        val userAnswers = producedDocumentUa
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(AddCommercialReferenceNumberPage)(true)
          .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
          .unsafeSetVal(AddDocumentsPage(index))(false)

        val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(index)).run(userAnswers)

        result.right.value mustBe None
      }

      "must return None when Index position is not 0 and AddDocumentsPage is false" in {

        val userAnswers = producedDocumentUa
          .unsafeSetVal(AddSecurityDetailsPage)(true)
          .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
          .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
          .unsafeSetVal(AddDocumentsPage(index))(false)
          .unsafeSetVal(AddDocumentsPage(Index(1)))(false)

        val result = UserAnswersReader[Option[NonEmptyList[ProducedDocument]]](ProducedDocument.deriveProducedDocuments(Index(1))).run(userAnswers)

        result.right.value mustBe None
      }
    }
  }
}
