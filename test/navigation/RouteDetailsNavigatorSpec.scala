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
import controllers.routeDetails.routes
import controllers.{routes => mainRoutes}
import generators.Generators
import models._
import models.reference.{CountryCode, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import queries.OfficeOfTransitQuery

class RouteDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  val navigator = new RouteDetailsNavigator

  "RouteDetailsNavigator" - {

    "in Normal mode" - {

      "Route Details section" - {
        "must go from Country of dispatch page to Destination Country page" in {
          val customsOffice = CustomsOffice("id", "name", CountryCode("GB"), None)
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(CountryOfDispatchPage, NormalMode, answers)
                .mustBe(routes.DestinationCountryController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Destination Country page to Movement Destination Country page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(DestinationCountryPage, NormalMode, answers)
                .mustBe(routes.MovementDestinationCountryController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Movement Destination Country page to Destination Office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(MovementDestinationCountryPage, NormalMode, answers)
                .mustBe(routes.DestinationOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Destination Office Page to Office Of Transit Country page when not an EU movement eg 'gb' " in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
              navigator
                .nextPage(DestinationOfficePage, NormalMode, updatedAnswers)
                .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(updatedAnswers.id, index, NormalMode))
          }
        }

        "must go from Destination Office Page to Add Office Of Transit page when an EU movement eg'NI' " in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))
              navigator
                .nextPage(DestinationOfficePage, NormalMode, updatedAnswers)
                .mustBe(routes.AddOfficeOfTransitController.onPageLoad(updatedAnswers.id, NormalMode))
          }
        }

        "must go from Add Office Of Transit page to Transit Country Page when user selects 'Yes' " in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.unsafeSetVal(AddOfficeOfTransitPage)(true)
              navigator
                .nextPage(AddOfficeOfTransitPage, NormalMode, updatedAnswers)
                .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(updatedAnswers.id, index, NormalMode))
          }
        }

        "must go from Add Office Of Transit page to CYA Page when user selects 'Yes' " in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.unsafeSetVal(AddOfficeOfTransitPage)(false)
              navigator
                .nextPage(AddOfficeOfTransitPage, NormalMode, updatedAnswers)
                .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))
          }
        }

        "must go from Office Of Transit Country to Add Another Transit Office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(OfficeOfTransitCountryPage(index), NormalMode, answers)
                .mustBe(routes.AddAnotherTransitOfficeController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from Add another transit office to arrival times at office of transit page when AddSecurityDetailsPage value is true" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(AddSecurityDetailsPage, true).toOption.value

              navigator
                .nextPage(AddAnotherTransitOfficePage(index), NormalMode, updatedUserAnswers)
                .mustBe(routes.ArrivalTimesAtOfficeController.onPageLoad(answers.id, index, NormalMode))
          }
        }

        "must go from Add another transit office to Added transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(AddSecurityDetailsPage, false).toOption.value

              navigator
                .nextPage(AddAnotherTransitOfficePage(index), NormalMode, updatedUserAnswers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Arrival times at office of transit page to Added transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ArrivalTimesAtOfficePage(index), NormalMode, answers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Added transit office page to Office of Transit Country page when selected option 'Yes'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .set(AddTransitOfficePage, true)
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(index), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(1)), "id2")
                .toOption
                .value

              navigator
                .nextPage(AddTransitOfficePage, NormalMode, userAnswers)
                .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(answers.id, Index(2), NormalMode))
          }
        }

        "must go from Added transit office page to router details check your answers page when selected option 'No'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers.set(AddTransitOfficePage, false).toOption.value

              navigator
                .nextPage(AddTransitOfficePage, NormalMode, userAnswers)
                .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Added transit office page to router details check your answers page when number of offices added exceeds 5" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .remove(AddTransitOfficePage)
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(index), "id")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(1)), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(2)), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(3)), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(4)), "id1")
                .toOption
                .value

              navigator
                .nextPage(AddTransitOfficePage, NormalMode, userAnswers)
                .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))
          }
        }

        "must go from Confirm Remove OfficeOfTransit Page to Added office of transit page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .set(AddTransitOfficePage, true)
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(index), "id1")
                .toOption
                .value
                .set(AddAnotherTransitOfficePage(Index(1)), "id2")
                .toOption
                .value
                .set(ConfirmRemoveOfficeOfTransitPage, true)
                .toOption
                .value

              navigator
                .nextPage(ConfirmRemoveOfficeOfTransitPage, NormalMode, userAnswers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.id, NormalMode))
          }
        }

        "must go from Confirm Remove OfficeOfTransit Page to Add another offices of transit when all records are removed" in {
          val userAnswers = emptyUserAnswers
            .remove(OfficeOfTransitQuery(index))
            .toOption
            .value
            .set(ConfirmRemoveOfficeOfTransitPage, true)
            .toOption
            .value

          navigator
            .nextPage(ConfirmRemoveOfficeOfTransitPage, NormalMode, userAnswers)
            .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(emptyUserAnswers.id, index, NormalMode))

        }
      }

    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map  to Check Your Answers" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(UnknownPage, CheckMode, answers)
              .mustBe(mainRoutes.SessionExpiredController.onPageLoad())
        }
      }

      "Must go from Country of dispatch to Route Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CountryOfDispatchPage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "Must go from Destination Country to Router Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(DestinationCountryPage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))
        }
      }

      "Must go from Movement Destination Country to Destination Office" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(MovementDestinationCountryPage, CheckMode, answers)
              .mustBe(routes.DestinationOfficeController.onPageLoad(answers.id, CheckMode))

        }

      }

      "Must go from Destination Office to Router Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(DestinationOfficePage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.id))

        }
      }

      "Must go from Add Office of Transit to Route Details Check Your Answers when user selects 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.unsafeSetVal(AddOfficeOfTransitPage)(false)
            navigator
              .nextPage(AddOfficeOfTransitPage, CheckMode, updatedAnswers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.id))

        }
      }

      "Must go from Add Office of Transit to OfficeOfTransitCountry page when user selects 'Yes' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.unsafeSetVal(AddOfficeOfTransitPage)(true)
            navigator
              .nextPage(AddOfficeOfTransitPage, CheckMode, updatedAnswers)
              .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(updatedAnswers.id, Index(0), CheckMode))

        }
      }

      "Must go from Office Of Transit Country to Add Another Transit Office" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(OfficeOfTransitCountryPage(index), CheckMode, answers)
              .mustBe(routes.AddAnotherTransitOfficeController.onPageLoad(answers.id, index, CheckMode))

        }
      }
    }
  }
}
