/*
 * Copyright 2023 HM Revenue & Customs
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
import models.reference.{CountryCode, CustomsOffice}
import org.scalacheck.Gen
import pages._
import pages.addItems.specialMentions.{SpecialMentionAdditionalInfoPage, SpecialMentionTypePage}

class SpecialMentionDomainSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  "SpecialMention" - {

    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {
        val xiCustomsOffice1: CustomsOffice = CustomsOffice("xi", "ni", CountryCode("XI"), None)

        val expectedResult = SpecialMentionDomain("specialMentionType", "additionalInfo", xiCustomsOffice1)

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(SpecialMentionTypePage(index, referenceIndex))("specialMentionType")
          .unsafeSetVal(SpecialMentionAdditionalInfoPage(index, referenceIndex))("additionalInfo")
          .unsafeSetVal(OfficeOfDeparturePage)(xiCustomsOffice1)
        val result: EitherType[SpecialMentionDomain] =
          UserAnswersReader[SpecialMentionDomain](SpecialMentionDomain.specialMentionsReader(index, referenceIndex)).run(userAnswers)

        result.value mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory answer is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          SpecialMentionTypePage(index, referenceIndex),
          SpecialMentionAdditionalInfoPage(index, referenceIndex)
        )

        forAll(mandatoryPages) {
          mandatoryPage =>
            val userAnswers = emptyUserAnswers
              .unsafeSetVal(SpecialMentionTypePage(index, referenceIndex))("specialMentionType")
              .unsafeSetVal(SpecialMentionAdditionalInfoPage(index, referenceIndex))("additionalInfo")
              .unsafeRemove(mandatoryPage)

            val result: EitherType[SpecialMentionDomain] =
              UserAnswersReader[SpecialMentionDomain](SpecialMentionDomain.specialMentionsReader(index, referenceIndex)).run(userAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
