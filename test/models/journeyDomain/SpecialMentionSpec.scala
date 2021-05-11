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
import generators.JourneyModelGenerators
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.journeyDomain.SpecialMentionSpec.setSpecialMentionsUserAnswers
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages._
import pages.addItems.specialMentions.{SpecialMentionAdditionalInfoPage, SpecialMentionTypePage}

class SpecialMentionSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "SpecialMention" - {

    val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
      SpecialMentionTypePage(index, referenceIndex),
      SpecialMentionAdditionalInfoPage(index, referenceIndex)
    )

    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[SpecialMentionDomain], arbitrary[UserAnswers]) {
          case (specialMention, userAnswers) =>
            val updatedUserAnswers = setSpecialMentionsUserAnswers(specialMention, index, referenceIndex)(userAnswers)
            val result             = UserAnswersReader[SpecialMentionDomain](SpecialMentionDomain.specialMentionsReader(index, referenceIndex)).run(updatedUserAnswers)

            result.right.value mustEqual specialMention
        }
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {
        forAll(arbitrary[SpecialMentionDomain], arbitrary[UserAnswers], mandatoryPages) {
          case (specialMention, userAnswers, mandatoryPage) =>
            val updatedUserAnswers = setSpecialMentionsUserAnswers(specialMention, index, referenceIndex)(userAnswers)
              .unsafeRemove(mandatoryPage)

            val result: EitherType[SpecialMentionDomain] =
              UserAnswersReader[SpecialMentionDomain](SpecialMentionDomain.specialMentionsReader(index, referenceIndex)).run(updatedUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }

}

object SpecialMentionSpec extends UserAnswersSpecHelper {

  def setSpecialMentionsUserAnswers(specialMention: SpecialMentionDomain, index: Index, referenceIndex: Index)(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .unsafeSetVal(SpecialMentionTypePage(index, referenceIndex))(specialMention.specialMentionType)
      .unsafeSetVal(SpecialMentionAdditionalInfoPage(index, referenceIndex))(specialMention.additionalInfo)

}
