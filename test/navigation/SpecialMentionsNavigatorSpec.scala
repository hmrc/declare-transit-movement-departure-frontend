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
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.{AddExtraDocumentInformationPage, DocumentExtraInformationPage, DocumentReferencePage, DocumentTypePage}
import pages.{AddSecurityDetailsPage, DeclarationTypePage}
import pages.addItems.specialMentions._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}

class SpecialMentionsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  val navigator = new SpecialMentionsNavigator

  "Special Mentions section" - {

    "in check mode" - {

      "must go from SpecialMentionAdditionalInfoPage to AddAnotherSpecialMentionController" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(index, itemIndex), CheckMode, emptyUserAnswers)
          .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(emptyUserAnswers.id, index, CheckMode))
      }

      "must go from AddSpecialMentionPage to SpecialMentionTypeController when" - {

        "AddSpecialMentionPage is true and no special mentions exist" in {
          val userAnswers = emptyUserAnswers.set(AddSpecialMentionPage(index), true).success.value

          navigator
            .nextPage(AddSpecialMentionPage(index), CheckMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.id, index, index, CheckMode))
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
            .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.id, index, CheckMode))
        }

        "AddSpecialMentionPage is false" in {

          val userAnswers = emptyUserAnswers
            .set(AddSpecialMentionPage(index), false)
            .success
            .value

          navigator
            .nextPage(AddSpecialMentionPage(index), CheckMode, userAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
        }
      }

      "must go from SpecialMentionType to SpecialMentionAdditionalInfo" in {
        navigator
          .nextPage(SpecialMentionTypePage(index, index), CheckMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionAdditionalInfoController.onPageLoad(emptyUserAnswers.id, index, index, CheckMode))
      }

      "must go from SpecialMentionAdditionalInfo to AddAnotherSpecialMention" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(index, index), CheckMode, emptyUserAnswers)
          .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(emptyUserAnswers.id, index, CheckMode))
      }

      "must go from AddAnotherSpecialMention" - {

        "to SpecialMentionType when set to true" in {

          val userAnswers = emptyUserAnswers.set(AddAnotherSpecialMentionPage(index), true).success.value

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), CheckMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.id, index, index, CheckMode))
        }

        "to ItemsCheckYourAnswers when set to false" in {

          val userAnswers = emptyUserAnswers.set(AddAnotherSpecialMentionPage(index), false).success.value

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), CheckMode, userAnswers)
            .mustBe(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.id, index))
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
            .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.id, index, CheckMode))
        }

        "to AddSpecialMentionPage when no special mentions exist" in {
          navigator
            .nextPage(RemoveSpecialMentionPage(index, index), CheckMode, emptyUserAnswers)
            .mustBe(routes.AddSpecialMentionController.onPageLoad(emptyUserAnswers.id, index, CheckMode))
        }
      }
    }

    "in normal mode" - {

      "must go from AddSpecialMention" - {

        "SpecialMentionType when true" in {

          val userAnswers = emptyUserAnswers.set(AddSpecialMentionPage(index), true).success.value

          navigator
            .nextPage(AddSpecialMentionPage(index), NormalMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.id, index, index, NormalMode))
        }

        "to TIRCarnetReference page when false and " +
          "its a TIR declaration type and " +
          "its the first document on the first item" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(DeclarationTypePage)(Option4)

            navigator
              .nextPage(AddSpecialMentionPage(index), NormalMode, userAnswers)
              .mustBe(controllers.addItems.routes.TIRCarnetReferenceController.onPageLoad(userAnswers.id, index, itemIndex, NormalMode))
          }

        "to standard add document journey when false and " +
          "its a TIR declaration type and " +
          "its not the first document on the first item" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option4)

            navigator
              .nextPage(AddSpecialMentionPage(Index(1)), NormalMode, userAnswers)
              .mustBe(controllers.addItems.routes.AddDocumentsController.onPageLoad(userAnswers.id, Index(1), NormalMode))

          }

        "to standard add document journey when false and " +
          "its a TIR declaration type and " +
          "its the first document but not the first item" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(DocumentTypePage(index, referenceIndex))("documentType")
              .unsafeSetVal(DocumentReferencePage(index, referenceIndex))("documentReference")
              .unsafeSetVal(AddExtraDocumentInformationPage(index, referenceIndex))(true)
              .unsafeSetVal(DocumentExtraInformationPage(index, referenceIndex))("documentExtraInformation")
              .unsafeSetVal(DeclarationTypePage)(Option4)

            navigator
              .nextPage(AddSpecialMentionPage(index), NormalMode, userAnswers)
              .mustBe(controllers.addItems.routes.AddDocumentsController.onPageLoad(userAnswers.id, index, NormalMode))
          }

        "to AddDocuments when set to false and safety " +
          "and security is selected as 'No'" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(index), NormalMode, userAnswers)
              .mustBe(controllers.addItems.routes.AddDocumentsController.onPageLoad(userAnswers.id, index, NormalMode))
          }

        "to DocumentType when set to false " +
          "and AddSecurityDetailsPage is 'Yes' " +
          "and  AddCircumstanceIndicatorPage is 'No' " +
          "and it is the first Item " +
          "and AddCommercialReferenceNumberPage is false" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(index), NormalMode, userAnswers)
              .mustBe(controllers.addItems.routes.DocumentTypeController.onPageLoad(userAnswers.id, index, itemIndex, NormalMode))
          }

        "to AddDocumentType when set to false and AddSecurityDetailsPage is 'Yes' " +
          "and  AddCircumstanceIndicatorPage is 'No' and it is the not the first Item " +
          "and AddCommercialReferenceNumberPage is false" in {

            val nextIndex = Index(1)

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(nextIndex), NormalMode, userAnswers)
              .mustBe(controllers.addItems.routes.AddDocumentsController.onPageLoad(userAnswers.id, nextIndex, NormalMode))
          }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and " +
          "AddCircumstanceIndicatorPage is 'Yes' and " +
          "CircumstanceIndicator is either E, D, C and B && AddCommercialReferenceNumberPage is false" in {

            val circumstanceIndicator = Gen.oneOf(CircumstanceIndicator.conditionalIndicators).sample.value

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)(circumstanceIndicator)
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(index), NormalMode, userAnswers)
              .mustBe(controllers.addItems.routes.DocumentTypeController.onPageLoad(userAnswers.id, index, itemIndex, NormalMode))
          }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and " +
          "AddCircumstanceIndicatorPage is 'Yes' and " +
          "CircumstanceIndicator other then E, D, C and B" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)("something")
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(index), NormalMode, userAnswers)
              .mustBe(controllers.addItems.routes.AddDocumentsController.onPageLoad(userAnswers.id, index, NormalMode))
          }

      }

      "must go from SpecialMentionType to SpecialMentionAdditionalInfo" in {
        navigator
          .nextPage(SpecialMentionTypePage(index, index), NormalMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionAdditionalInfoController.onPageLoad(emptyUserAnswers.id, index, index, NormalMode))
      }

      "must go from SpecialMentionAdditionalInfo to AddAnotherSpecialMention" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(index, index), NormalMode, emptyUserAnswers)
          .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(emptyUserAnswers.id, index, NormalMode))
      }

      "must go from RemoveSpecialMentionController" - {

        "to AddAnotherSpecialMentionController when at least one special mention exists" in {

          val userAnswers = emptyUserAnswers
            .set(SpecialMentionTypePage(index, index), "value")
            .success
            .value

          navigator
            .nextPage(RemoveSpecialMentionPage(index, index), NormalMode, userAnswers)
            .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.id, index, NormalMode))
        }

        "to AddSpecialMentionPage when no special mentions exist" in {
          navigator
            .nextPage(RemoveSpecialMentionPage(index, index), NormalMode, emptyUserAnswers)
            .mustBe(routes.AddSpecialMentionController.onPageLoad(emptyUserAnswers.id, index, NormalMode))
        }
      }

      "must go from SpecialMentionAdditionalInfoPage to AddAnotherSpecialMention" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(index, index), NormalMode, emptyUserAnswers)
          .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(emptyUserAnswers.id, index, NormalMode))
      }

      "must go from AddAnotherSpecialMention" - {

        "to SpecialMentionType when set to true" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddAnotherSpecialMentionPage(index))(true)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), NormalMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.id, index, index, NormalMode))
        }

        "to AddDocuments when set to false and safeTye and security is selected as 'No'" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddSecurityDetailsPage)(false)
            .unsafeSetVal(AddAnotherSpecialMentionPage(index))(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), NormalMode, userAnswers)
            .mustBe(controllers.addItems.routes.AddDocumentsController.onPageLoad(userAnswers.id, index, NormalMode))
        }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and  AddCircumstanceIndicatorPage is 'No' and it is the first Item and AddCommercialReferenceNumberPage is false" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddAnotherSpecialMentionPage(index))(false)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), NormalMode, userAnswers)
            .mustBe(controllers.addItems.routes.DocumentTypeController.onPageLoad(userAnswers.id, index, itemIndex, NormalMode))
        }

        "to AddDocumentType when set to false and AddSecurityDetailsPage is 'Yes' and  AddCircumstanceIndicatorPage is 'No' and it is the not the first Item and AddCommercialReferenceNumberPage is false" in {
          val nextIndex = Index(1)

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddAnotherSpecialMentionPage(index))(false)
            .unsafeSetVal(AddAnotherSpecialMentionPage(nextIndex))(false)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(nextIndex), NormalMode, userAnswers)
            .mustBe(controllers.addItems.routes.AddDocumentsController.onPageLoad(userAnswers.id, nextIndex, NormalMode))
        }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and  AddCircumstanceIndicatorPage is 'Yes' and CircumstanceIndicator is either E, D, C and B && AddCommercialReferenceNumberPage is false" in {

          val circumstanceIndicator = Gen.oneOf(CircumstanceIndicator.conditionalIndicators).sample.value

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)(circumstanceIndicator)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddAnotherSpecialMentionPage(index))(false)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), NormalMode, userAnswers)
            .mustBe(controllers.addItems.routes.DocumentTypeController.onPageLoad(userAnswers.id, index, itemIndex, NormalMode))
        }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and  AddCircumstanceIndicatorPage is 'Yes' and CircumstanceIndicator other then E, D, C and B" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)("something")
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddAnotherSpecialMentionPage(index))(false)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(index), NormalMode, userAnswers)
            .mustBe(controllers.addItems.routes.AddDocumentsController.onPageLoad(userAnswers.id, index, NormalMode))
        }
      }
    }
  }
}
