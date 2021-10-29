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
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.routeDetails._
import queries.OfficeOfTransitQuery

class RouteDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  val navigator = new RouteDetailsNavigator

  "RouteDetailsNavigator" - {

    "in Normal mode" - {

      "Route Details section" - {

        "Must go from Movement Destination Country to Destination Office when Declaration Type is not Option 4 (TIR)" in {

          val generatedOption = Gen.oneOf(DeclarationType.Option1, DeclarationType.Option2, DeclarationType.Option3).sample.value

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers
                .set(DeclarationTypePage, generatedOption)
                .toOption
                .value

              navigator
                .nextPage(MovementDestinationCountryPage, NormalMode, userAnswers)
                .mustBe(routes.DestinationOfficeController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from Country of dispatch page to Destination Country page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(CountryOfDispatchPage, NormalMode, answers)
                .mustBe(routes.DestinationCountryController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from Destination Country page to Movement Destination Country page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(DestinationCountryPage, NormalMode, answers)
                .mustBe(routes.MovementDestinationCountryController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "Must go from Destination Office Page to Office Of Transit Country Controller when Country Code is GB" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
                .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option1)

              navigator
                .nextPage(DestinationOfficePage, NormalMode, updatedAnswers)
                .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "Must go from Destination Office Page to Check Your Answers page when Declaration Type is Option 4 (TIR)" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
                .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option4)

              navigator
                .nextPage(DestinationOfficePage, NormalMode, updatedAnswers)
                .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
          }
        }

        "Must go from Destination Office Page to Add Office Of Transit page when Country Code is XI" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))
                .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option2)

              navigator
                .nextPage(DestinationOfficePage, NormalMode, updatedAnswers)
                .mustBe(routes.AddOfficeOfTransitController.onPageLoad(updatedAnswers.lrn, NormalMode))
          }
        }

        "must go from Add Office Of Transit page to Transit Country Page when user selects 'Yes' " in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.unsafeSetVal(AddOfficeOfTransitPage)(true)
              navigator
                .nextPage(AddOfficeOfTransitPage, NormalMode, updatedAnswers)
                .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(updatedAnswers.lrn, index, NormalMode))
          }
        }

        "must go from Add Office Of Transit page to CYA Page when user selects 'No' " in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.unsafeSetVal(AddOfficeOfTransitPage)(false)
              navigator
                .nextPage(AddOfficeOfTransitPage, NormalMode, updatedAnswers)
                .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
          }
        }

        "must go from Add Office Of Transit page to session expired when selection undefined " in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              lazy val updatedAnswers = answers.unsafeRemove(AddOfficeOfTransitPage)
              navigator
                .nextPage(AddOfficeOfTransitPage, NormalMode, updatedAnswers)
                .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
          }
        }

        "must go from Office Of Transit Country to Add Another Transit Office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(OfficeOfTransitCountryPage(index), NormalMode, answers)
                .mustBe(routes.AddAnotherTransitOfficeController.onPageLoad(answers.lrn, index, NormalMode))
          }
        }

        "must go from Add another transit office to arrival times at office of transit page when AddSecurityDetailsPage value is true" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(AddSecurityDetailsPage, true).toOption.value

              navigator
                .nextPage(AddAnotherTransitOfficePage(index), NormalMode, updatedUserAnswers)
                .mustBe(routes.ArrivalDatesAtOfficeController.onPageLoad(answers.lrn, index, NormalMode))
          }
        }

        "must go from Add another transit office to Added transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.set(AddSecurityDetailsPage, false).toOption.value

              navigator
                .nextPage(AddAnotherTransitOfficePage(index), NormalMode, updatedUserAnswers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from Arrival times at office of transit page to Added transit office page" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ArrivalDatesAtOfficePage(index), NormalMode, answers)
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.lrn, NormalMode))
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
                .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(answers.lrn, Index(2), NormalMode))
          }
        }

        "must go from Added transit office page to router details check your answers page when selected option 'No'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val userAnswers = answers.set(AddTransitOfficePage, false).toOption.value

              navigator
                .nextPage(AddTransitOfficePage, NormalMode, userAnswers)
                .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
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
                .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
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
                .mustBe(routes.AddTransitOfficeController.onPageLoad(answers.lrn, NormalMode))
          }
        }

        "must go from Confirm Remove OfficeOfTransit Page to Add another offices of transit when all records are removed for a GB movement" in {
          val userAnswers = emptyUserAnswers
            .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("GB"), None))
            .toOption
            .value
            .remove(OfficeOfTransitQuery(index))
            .toOption
            .value
            .set(ConfirmRemoveOfficeOfTransitPage, true)
            .toOption
            .value

          navigator
            .nextPage(ConfirmRemoveOfficeOfTransitPage, NormalMode, userAnswers)
            .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(emptyUserAnswers.lrn, index, NormalMode))

        }

        "must go from Confirm Remove OfficeOfTransit Page to AddOfficeOfTransit page when all records are removed for a XI movement" in {
          val userAnswers = emptyUserAnswers
            .set(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("XI"), None))
            .toOption
            .value
            .remove(OfficeOfTransitQuery(index))
            .toOption
            .value
            .set(ConfirmRemoveOfficeOfTransitPage, true)
            .toOption
            .value

          navigator
            .nextPage(ConfirmRemoveOfficeOfTransitPage, NormalMode, userAnswers)
            .mustBe(routes.AddOfficeOfTransitController.onPageLoad(emptyUserAnswers.lrn, NormalMode))

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
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "Must go from Destination Country to Router Details Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(DestinationCountryPage, CheckMode, answers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "Must go from Movement Destination Country to Destination Office" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
              .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option1)
            navigator
              .nextPage(MovementDestinationCountryPage, NormalMode, updatedAnswers)
              .mustBe(routes.DestinationOfficeController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "Must go from Destination Office Page to Check Your Answers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
              .unsafeSetVal(DeclarationTypePage)(DeclarationType.Option1)

            navigator
              .nextPage(DestinationOfficePage, CheckMode, updatedAnswers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "Must go from Add Office of Transit to Route Details Check Your Answers when user selects 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.unsafeSetVal(AddOfficeOfTransitPage)(false)
            navigator
              .nextPage(AddOfficeOfTransitPage, CheckMode, updatedAnswers)
              .mustBe(routes.RouteDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))

        }
      }

      "Must go from Add Office of Transit to OfficeOfTransitCountry page when user selects 'Yes' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.unsafeSetVal(AddOfficeOfTransitPage)(true)
            navigator
              .nextPage(AddOfficeOfTransitPage, CheckMode, updatedAnswers)
              .mustBe(routes.OfficeOfTransitCountryController.onPageLoad(updatedAnswers.lrn, Index(0), CheckMode))

        }
      }

      "Must go from Add Office of Transit to session expired when selection undefined " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            lazy val updatedAnswers = answers.unsafeRemove(AddOfficeOfTransitPage)
            navigator
              .nextPage(AddOfficeOfTransitPage, CheckMode, updatedAnswers)
              .mustBe(controllers.routes.SessionExpiredController.onPageLoad())

        }
      }

      "Must go from Office Of Transit Country to Add Another Transit Office" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(OfficeOfTransitCountryPage(index), CheckMode, answers)
              .mustBe(routes.AddAnotherTransitOfficeController.onPageLoad(answers.lrn, index, CheckMode))
        }
      }
    }
  }
}
