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

package navigation

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.specialMentions.routes
import generators.Generators
import models.DeclarationType.{Option1, Option4}
import models.reference.CircumstanceIndicator
import models.{CheckMode, Index, NormalMode}
import navigation.annotations.addItemsNavigators.AddItemsSpecialMentionsNavigator
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.specialMentions._
import pages.addItems.{AddExtraDocumentInformationPage, DocumentExtraInformationPage, DocumentReferencePage, DocumentTypePage}
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}
import pages.{AddSecurityDetailsPage, DeclarationTypePage}

class AddItemsSpecialMentionsCheckmodeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  val navigator = new AddItemsSpecialMentionsNavigator

  "Special Mentions section" - {

    "in check mode" - {

      "must go from SpecialMentionAdditionalInfoPage to AddAnotherSpecialMentionController" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(index, itemIndex), CheckMode, emptyUserAnswers)
          .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(emptyUserAnswers.lrn, index, CheckMode))
      }

      "must go from AddSpecialMentionPage to SpecialMentionTypeController when" - {

        "AddSpecialMentionPage is true and no special mentions exist" in {
          val userAnswers = emptyUserAnswers.set(AddSpecialMentionPage(index), true).success.value

          navigator
            .nextPage(AddSpecialMentionPage(index), CheckMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, index, index, CheckMode))
        }

        "AddSpecialMentionPage is true and 1 special mention exists" in {

          val userAnswers = emptyUserAnswers
            .set(AddSpecialMentionPage(index), true)
            .success
            .value
            .set(SpecialMentionTypePage(index, index), "value")
            .success
            .value

          navigator
            .nextPage(AddSpecialMentionPage(index), CheckMode, userAnswers)
            .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.lrn, index, CheckMode))
        }

        "AddSpecialMentionPage is false" in {

          val userAnswers = emptyUserAnswers
            .set(AddSpecialMentionPage(index), false)
            .success
            .value

          navigator
            .nextPage(AddSpecialMentionPage(index), CheckMode, userAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.lrn, index))
        }
      }

      "must go from SpecialMentionType to SpecialMentionAdditionalInfo" in {
        navigator
          .nextPage(SpecialMentionTypePage(index, index), CheckMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionAdditionalInfoController.onPageLoad(emptyUserAnswers.lrn, index, index, CheckMode))
      }

      "must go from SpecialMentionAdditionalInfo to AddAnotherSpecialMention" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(index, index), CheckMode, emptyUserAnswers)
          .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(emptyUserAnswers.lrn, index, CheckMode))
      }

      "must go from AddAnotherSpecialMention" - {

        "to SpecialMentionType when set to true" in {

          val userAnswers = emptyUserAnswers.set(AddAnotherSpecialMentionPage(index), true).success.value

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), CheckMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, index, index, CheckMode))
        }

        "to ItemsCheckYourAnswers when set to false" in {

          val userAnswers = emptyUserAnswers.set(AddAnotherSpecialMentionPage(index), false).success.value

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), CheckMode, userAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.lrn, index))
        }
      }

      "must go from RemoveSpecialMentionController" - {

        "to AddAnotherSpecialMentionController when at least one special mention exists" in {

          val userAnswers = emptyUserAnswers
            .set(SpecialMentionTypePage(index, index), "value")
            .success
            .value

          navigator
            .nextPage(RemoveSpecialMentionPage(index, index), CheckMode, userAnswers)
            .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.lrn, index, CheckMode))
        }

        "to AddSpecialMentionPage when no special mentions exist" in {
          navigator
            .nextPage(RemoveSpecialMentionPage(index, index), CheckMode, emptyUserAnswers)
            .mustBe(routes.AddSpecialMentionController.onPageLoad(emptyUserAnswers.lrn, index, CheckMode))
        }
      }
    }

  }
}
