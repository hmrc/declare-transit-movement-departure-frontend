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
import models.{CheckMode, Mode, SpecialMentionList, UserAnswers}
import pages.addItems.specialMentions.{AddSpecialMentionPage, SpecialMentionTypePage}
import uk.gov.hmrc.viewmodels.Text.{Literal, Message}
import uk.gov.hmrc.viewmodels._
import viewModels.AddAnotherViewModel

class SpecialMentionsCheckYourAnswersHelperSpec extends SpecBase with GeneratorSpec with ReferenceDataGenerators {

  val mode: Mode = CheckMode

  "SpecialMentionsCheckYourAnswers" - {

    "specialMentionType must " - {

      "display row if answer exists in reference data" in {

        forAll(arb[UserAnswers],
               arb[SpecialMentionList].retryUntil(
                 x => x.list.nonEmpty
               )
        ) {
          (userAnswers, specialMentionList) =>
            val updatedAnswers = userAnswers
              .set(SpecialMentionTypePage(itemIndex, referenceIndex), specialMentionList.list.head.code)
              .success
              .value

            val rowContent = s"(${specialMentionList.list.head.code}) ${specialMentionList.list.head.description}"

            val cya = new SpecialMentionsCheckYourAnswersHelper(updatedAnswers, mode)

            val row = cya.specialMentionType(itemIndex, referenceIndex, specialMentionList)

            row.value.key.content mustBe Literal(rowContent)

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

            val cya = new SpecialMentionsCheckYourAnswersHelper(updatedAnswers, mode)

            val row = cya.specialMentionType(itemIndex, referenceIndex, SpecialMentionList(List(SpecialMention("code", "description"))))

            row mustBe None
        }
      }
    }

    "specialMentionSectionRow must " - {

      "display row if answer exists in reference data" in {

        forAll(arb[UserAnswers],
               arb[SpecialMentionList].retryUntil(
                 x => x.list.nonEmpty
               )
        ) {
          (userAnswers, specialMentionList) =>
            val updatedAnswers = userAnswers
              .set(SpecialMentionTypePage(itemIndex, referenceIndex), specialMentionList.list.head.code)
              .success
              .value

            val rowContent = s"(${specialMentionList.list.head.code}) ${specialMentionList.list.head.description}"

            val cya = new SpecialMentionsCheckYourAnswersHelper(updatedAnswers, mode)

            val row = cya.specialMentionSectionRow(itemIndex, referenceIndex, specialMentionList)

            row.value.key.content mustBe Message("addAnotherSpecialMention.specialMentionList.label", referenceIndex.display)
            row.value.value.content mustBe Literal(rowContent)

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

            val cya = new SpecialMentionsCheckYourAnswersHelper(updatedAnswers, mode)

            val row = cya.specialMentionSectionRow(itemIndex, referenceIndex, SpecialMentionList(List(SpecialMention("code", "description"))))

            row mustBe None
        }
      }
    }

    "addAnother" - {

      "must link to AddAnotherSpecialMention provided they have answered 'Yes' to AddSpecialMention" - {

        val updatedUserAnswers = emptyUserAnswers.set(AddSpecialMentionPage(itemIndex), true).success.value

        val cya = new SpecialMentionsCheckYourAnswersHelper(updatedUserAnswers, mode)

        cya.addAnother(index, msg"addItems.checkYourAnswersLabel.specialMentions") mustBe
          AddAnotherViewModel(
            controllers.addItems.specialMentions.routes.AddAnotherSpecialMentionController.onPageLoad(lrn, itemIndex, mode).url,
            msg"addItems.checkYourAnswersLabel.specialMentions"
          )

      }

      "must link to AddSpecialMention provided they have answered 'No' to AddSpecialMention" - {

        val updatedUserAnswers = emptyUserAnswers.set(AddSpecialMentionPage(itemIndex), false).success.value

        val cya = new SpecialMentionsCheckYourAnswersHelper(updatedUserAnswers, mode)

        cya.addAnother(index, msg"addItems.checkYourAnswersLabel.specialMentions") mustBe
          AddAnotherViewModel(
            controllers.addItems.specialMentions.routes.AddSpecialMentionController.onPageLoad(lrn, itemIndex, mode).url,
            msg"addItems.checkYourAnswersLabel.specialMentions"
          )

      }

      "must link to AddSpecialMention if they have not answered AddSpecialMention" - {

        val cya = new SpecialMentionsCheckYourAnswersHelper(emptyUserAnswers, mode)

        cya.addAnother(index, msg"addItems.checkYourAnswersLabel.specialMentions") mustBe
          AddAnotherViewModel(
            controllers.addItems.specialMentions.routes.AddSpecialMentionController.onPageLoad(lrn, itemIndex, mode).url,
            msg"addItems.checkYourAnswersLabel.specialMentions"
          )

      }

    }

  }
}
