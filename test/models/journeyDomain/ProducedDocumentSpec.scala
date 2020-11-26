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
import models.journeyDomain.PackagesSpec.UserAnswersNoErrorSet
import models.journeyDomain.ProducedDocumentSpec.setProducedDocumentsUserAnswers
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.addItems._

class ProducedDocumentSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "ProducedDocument" - {

    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[ProducedDocument], arbitrary[UserAnswers]) {
          case (specialMention, userAnswers) =>
            val updatedUserAnswers = setProducedDocumentsUserAnswers(specialMention, index, referenceIndex)(userAnswers)
            val result             = UserAnswersReader[ProducedDocument](ProducedDocument.producedDocumentReader(index, referenceIndex)).run(updatedUserAnswers)

            result.value mustEqual specialMention
        }
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val updatedUserAnswers = userAnswers.remove(DocumentTypePage(index, referenceIndex)).success.value
            val result: Option[ProducedDocument] =
              UserAnswersReader[ProducedDocument](ProducedDocument.producedDocumentReader(index, referenceIndex)).run(updatedUserAnswers)

            result mustBe None
        }
      }
    }
  }
}

object ProducedDocumentSpec extends UserAnswersSpecHelper {

  def setProducedDocumentsUserAnswers(document: ProducedDocument, index: Index, referenceIndex: Index)(statUserAnswers: UserAnswers): UserAnswers = {
    val ua = statUserAnswers
      .set(DocumentTypePage(index, referenceIndex),document.documentType).toOption.get
      .unsafeSetVal(AddExtraInformationPage(index, referenceIndex))(document.extraInformation.isDefined)

    val userAnswers: UserAnswers = document.documentReference.fold(ua) {
        ref =>
          ua.unsafeSetVal(DocumentReferencePage(index, referenceIndex))(ref)
      }

    document.extraInformation.fold(userAnswers) {
      info =>
        userAnswers.unsafeSetVal(DocumentExtraInformationPage(index, referenceIndex))(info)
    }
  }
}
