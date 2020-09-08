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

package navigation

import base.SpecBase
import controllers.transportDetails.{routes => transportDetailsRoute}
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class TransportDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TransportDetailsNavigator

  "TransportDetailsNavigator" - {

    "in Normal Mode" - {
      "must go from InlandMode page to AddIdAtDeparture Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(InlandModePage, NormalMode, answers)
              .mustBe(transportDetailsRoute.AddIdAtDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from AddIdAtDeparture page to AddIdAtDepartureLater page when user selects 'no' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddIdAtDeparturePage, false).toOption.value

            navigator.nextPage(AddIdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddIdAtDepartureLaterController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from AddIdAtDeparture page to IdAtDeparture page when user selects 'yes' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddIdAtDeparturePage, true).toOption.value
              .remove(IdAtDeparturePage).success.value

            navigator.nextPage(AddIdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from AddIdAtDepartureLater page to NationalityAtDeparture Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(AddIdAtDepartureLaterPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.NationalityAtDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from IdAtDeparture page to NationalityAtDeparture Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(IdAtDeparturePage, NormalMode, answers)
              .mustBe(transportDetailsRoute.NationalityAtDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from NationalityAtDeparture page to ChangeAtBorder Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(NationalityAtDeparturePage, NormalMode, answers)
              .mustBe(transportDetailsRoute.ChangeAtBorderController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from ChangeAtBorder page to TraderDetailsCheckYourAnswers page when user selects 'no' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ChangeAtBorderPage, false).toOption.value

            navigator.nextPage(ChangeAtBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
        }
      }

      "must go from ChangeAtBorder page to ModeAtBorder page when user selects 'yes' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ChangeAtBorderPage, true).toOption.value

            navigator.nextPage(ChangeAtBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.ModeAtBorderController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from ModeAtBorder page to IdCrossingBorder Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(ModeAtBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.IdCrossingBorderController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from IdCrossingBorder page to ModeCrossingBorder Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(IdCrossingBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.ModeCrossingBorderController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from ModeCrossingBorder page to NationalityCrossingBorder Page" in { //TODO needs refactoring with reference data

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(ModeCrossingBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.NationalityCrossingBorderController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from NationalityCrossingBorder page to TransportDetailsCheckYourAnswers Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(NationalityCrossingBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

    }

    "in Check Mode" - {

      "must go from InlandMode page to TransportDetailsCheckYourAnswers Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(InlandModePage, CheckMode, answers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "must go from AddIdAtDeparture page to IdAtDeparture page on selecting option 'Yes' and IdAtDeparture has no data " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers.set(AddIdAtDeparturePage, true).toOption.value
              .remove(IdAtDeparturePage).success.value

            navigator.nextPage(AddIdAtDeparturePage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(answers.id, CheckMode))
        }
      }

      "must go from AddIdAtDeparture page to CYA page on selecting option 'Yes' and IdAtDeparture has data" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers.set(AddIdAtDeparturePage, true).toOption.value
              .set(IdAtDeparturePage, "Bob").toOption.value

            navigator.nextPage(AddIdAtDeparturePage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }


      "must go from AddIdAtDeparture page to AddIdAtDepartureLater on selecting option 'No' and IdAtDeparture has data" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers.set(AddIdAtDeparturePage, false).toOption.value
              .set(IdAtDeparturePage, "Bob").toOption.value

            navigator.nextPage(AddIdAtDeparturePage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.AddIdAtDepartureLaterController.onPageLoad(answers.id))
        }
      }

    }
  }
}