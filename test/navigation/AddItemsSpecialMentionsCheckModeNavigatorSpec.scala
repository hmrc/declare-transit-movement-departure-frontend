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

package navigation

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.specialMentions.routes
import generators.Generators
import models.CheckMode
import navigation.annotations.addItemsNavigators.AddItemsSpecialMentionsNavigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.specialMentions._

class AddItemsSpecialMentionsCheckModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  val navigator = new AddItemsSpecialMentionsNavigator

  "Special Mentions section" - {

    "in check mode" - {

      "must go from AddSpecialMentionPage to SpecialMentionTypeController when" - {

        "AddSpecialMentionPage is true and no special mentions exist" in {
          val userAnswers = emptyUserAnswers.set(AddSpecialMentionPage(itemIndex), true).success.value

          navigator
            .nextPage(AddSpecialMentionPage(itemIndex), CheckMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, CheckMode))
        }

        "AddSpecialMentionPage is true and 1 special mention exists" in {

          val userAnswers = emptyUserAnswers
            .set(AddSpecialMentionPage(itemIndex), true)
            .success
            .value
            .set(SpecialMentionTypePage(itemIndex, referenceIndex), "value")
            .success
            .value

          navigator
            .nextPage(AddSpecialMentionPage(itemIndex), CheckMode, userAnswers)
            .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, CheckMode))
        }

        "AddSpecialMentionPage is false" in {

          val userAnswers = emptyUserAnswers
            .set(AddSpecialMentionPage(itemIndex), false)
            .success
            .value

          navigator
            .nextPage(AddSpecialMentionPage(itemIndex), CheckMode, userAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.lrn, itemIndex))
        }
      }

      "must go from SpecialMentionType to SpecialMentionAdditionalInfo" in {
        navigator
          .nextPage(SpecialMentionTypePage(itemIndex, referenceIndex), CheckMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionAdditionalInfoController.onPageLoad(emptyUserAnswers.lrn, itemIndex, referenceIndex, CheckMode))
      }

      "must go from SpecialMentionAdditionalInfo to AddAnotherSpecialMention" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(itemIndex, referenceIndex), CheckMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionCheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn, itemIndex, referenceIndex, CheckMode))
      }

      "must go from AddAnotherSpecialMention" - {

        "to SpecialMentionType when set to true" in {

          val userAnswers = emptyUserAnswers.set(AddAnotherSpecialMentionPage(itemIndex), true).success.value

          navigator
            .nextPage(AddAnotherSpecialMentionPage(itemIndex), CheckMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, CheckMode))
        }

        "to ItemsCheckYourAnswers when set to false" in {

          val userAnswers = emptyUserAnswers.set(AddAnotherSpecialMentionPage(itemIndex), false).success.value

          navigator
            .nextPage(AddAnotherSpecialMentionPage(itemIndex), CheckMode, userAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.lrn, itemIndex))
        }
      }

      "must go from RemoveSpecialMentionController" - {

        "to AddAnotherSpecialMentionController when at least one special mention exists" in {

          val userAnswers = emptyUserAnswers
            .set(SpecialMentionTypePage(itemIndex, referenceIndex), "value")
            .success
            .value

          navigator
            .nextPage(RemoveSpecialMentionPage(itemIndex, referenceIndex), CheckMode, userAnswers)
            .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, CheckMode))
        }

        "to AddSpecialMentionPage when no special mentions exist" in {
          navigator
            .nextPage(RemoveSpecialMentionPage(itemIndex, referenceIndex), CheckMode, emptyUserAnswers)
            .mustBe(routes.AddSpecialMentionController.onPageLoad(emptyUserAnswers.lrn, itemIndex, CheckMode))
        }
      }
    }

  }
}
