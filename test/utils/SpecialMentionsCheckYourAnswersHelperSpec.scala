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

package utils

import base.{GeneratorSpec, SpecBase}
import generators.ReferenceDataGenerators
import models.reference.SpecialMention
import models.{CheckMode, NormalMode, SpecialMentionList, UserAnswers}
import pages.addItems.specialMentions.SpecialMentionTypePage
import uk.gov.hmrc.viewmodels.Text.Message
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

class SpecialMentionsCheckYourAnswersHelperSpec extends SpecBase with GeneratorSpec with ReferenceDataGenerators {

  "SpecialMentionsCheckYourAnswers" - {

    "specialMentionType must " - {

      "display row if answer exists in reference data" in {

        forAll(arb[UserAnswers],
               arb[SpecialMentionList].suchThat(
                 x => x.list.nonEmpty
               )
        ) {
          (userAnswers, specialMentionList) =>
            val updatedAnswers = userAnswers
              .set(SpecialMentionTypePage(itemIndex, referenceIndex), specialMentionList.list.head.code)
              .success
              .value

            val rowContent = s"(${specialMentionList.list.head.code}) ${specialMentionList.list.head.description}"

            val cya = new SpecialMentionsCheckYourAnswersHelper(updatedAnswers)

            val row = cya.specialMentionType(itemIndex, referenceIndex, specialMentionList, NormalMode)

            row.value.key.content mustBe Message(rowContent)

            row.value.actions.length mustBe 2
        }
      }

      "not display row if answer does not exist in reference data" in {

        forAll(arb[UserAnswers]) {
          userAnswers =>
            val updatedAnswers = userAnswers
              .set(SpecialMentionTypePage(itemIndex, referenceIndex), "invalid")
              .success
              .value

            val cya = new SpecialMentionsCheckYourAnswersHelper(updatedAnswers)

            val row = cya.specialMentionType(itemIndex, referenceIndex, SpecialMentionList(List(SpecialMention("code", "description"))), NormalMode)

            row mustBe None
        }
      }
    }

    "specialMentionTypeNoRemoval must " - {

      "display row if answer exists in reference data" in {

        forAll(arb[UserAnswers],
               arb[SpecialMentionList].suchThat(
                 x => x.list.nonEmpty
               )
        ) {
          (userAnswers, specialMentionList) =>
            val updatedAnswers = userAnswers
              .set(SpecialMentionTypePage(itemIndex, referenceIndex), specialMentionList.list.head.code)
              .success
              .value

            val rowContent = s"(${specialMentionList.list.head.code}) ${specialMentionList.list.head.description}"

            val cya = new SpecialMentionsCheckYourAnswersHelper(updatedAnswers)

            val row = cya.specialMentionTypeNoRemoval(itemIndex, referenceIndex, specialMentionList)

            row.value.key.content mustBe Message(rowContent)

            row.value.actions.length mustBe 1
        }
      }

      "not display row if answer does not exist in reference data" in {

        forAll(arb[UserAnswers]) {
          userAnswers =>
            val updatedAnswers = userAnswers
              .set(SpecialMentionTypePage(itemIndex, referenceIndex), "invalid")
              .success
              .value

            val cya = new SpecialMentionsCheckYourAnswersHelper(updatedAnswers)

            val row = cya.specialMentionTypeNoRemoval(itemIndex, referenceIndex, SpecialMentionList(List(SpecialMention("code", "description"))))

            row mustBe None
        }
      }
    }

    "addAnother" - {

      "must link to correct page" - {

        val cya = new SpecialMentionsCheckYourAnswersHelper(emptyUserAnswers)

        cya.addAnother(index, msg"addItems.checkYourAnswersLabel.specialMentions") mustBe
          AddAnotherViewModel(
            controllers.addItems.specialMentions.routes.AddAnotherSpecialMentionController.onPageLoad(lrn, itemIndex, CheckMode).url,
            msg"addItems.checkYourAnswersLabel.specialMentions"
          )

      }

    }

  }
}
