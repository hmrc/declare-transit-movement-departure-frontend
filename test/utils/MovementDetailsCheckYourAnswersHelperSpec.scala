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

package utils

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.movementDetails.routes
import models.{CheckMode, Mode, RepresentativeCapacity}
import pages.generalInformation._
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

class MovementDetailsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper {

  val mode: Mode = CheckMode

  "MovementDetailsCheckYourAnswersHelper" - {

    "preLodgeDeclarationPage" - {

      "return None" - {
        "PreLodgeDeclarationPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.preLodgeDeclarationPage
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PreLodgeDeclarationPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PreLodgeDeclarationPage)(true)

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.preLodgeDeclarationPage

          val label = msg"preLodgeDeclaration.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.PreLodgeDeclarationController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-pre-lodge-declaration")
                )
              )
            )
          )
        }
      }
    }

    "representativeCapacity" - {

      val representativeCapacity: RepresentativeCapacity = RepresentativeCapacity.Direct

      "return None" - {
        "RepresentativeCapacityPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.representativeCapacity
          result mustBe None
        }
      }

      "return Some(row)" - {
        "RepresentativeCapacityPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(RepresentativeCapacityPage)(representativeCapacity)

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.representativeCapacity

          val label = msg"representativeCapacity.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"representativeCapacity.$representativeCapacity"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.RepresentativeCapacityController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-representative-capacity")
                )
              )
            )
          )
        }
      }
    }

    "representativeName" - {

      val representativeName: String = "REPRESENTATIVE NAME"

      "return None" - {
        "RepresentativeNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.representativeName
          result mustBe None
        }
      }

      "return Some(row)" - {
        "RepresentativeNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(RepresentativeNamePage)(representativeName)

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.representativeName

          val label = msg"representativeName.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$representativeName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.RepresentativeNameController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-representative-name")
                )
              )
            )
          )
        }
      }
    }

    "containersUsedPage" - {

      "return None" - {
        "ContainersUsedPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.containersUsedPage
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ContainersUsedPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ContainersUsedPage)(true)

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.containersUsedPage

          val label = msg"containersUsed.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ContainersUsedController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-containers-used")
                )
              )
            )
          )
        }
      }
    }

    "declarationForSomeoneElse" - {

      "return None" - {
        "DeclarationForSomeoneElsePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.declarationForSomeoneElse
          result mustBe None
        }
      }

      "return Some(row)" - {
        "DeclarationForSomeoneElsePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(DeclarationForSomeoneElsePage)(true)

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.declarationForSomeoneElse

          val label = msg"declarationForSomeoneElse.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.DeclarationForSomeoneElseController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-declaration-for-someone-else")
                )
              )
            )
          )
        }
      }
    }

    "declarationPlace" - {

      val declarationPlace: String = "DECLARATION PLACE"

      "return None" - {
        "DeclarationPlacePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.declarationPlace
          result mustBe None
        }
      }

      "return Some(row)" - {
        "DeclarationPlacePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(DeclarationPlacePage)(declarationPlace)

          val helper = new MovementDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.declarationPlace

          val label = msg"declarationPlace.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$declarationPlace"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.DeclarationPlaceController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-declaration-place")
                )
              )
            )
          )
        }
      }
    }

  }
}
