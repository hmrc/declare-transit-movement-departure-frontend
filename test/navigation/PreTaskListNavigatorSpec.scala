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
import controllers.routes
import generators.Generators
import models._
import models.reference.{CountryCode, CountryOfDispatch}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class PreTaskListNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  val navigator = new PreTaskListNavigator

  "Navigator" - {
    "in Normal mode" - {
      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(UnknownPage, NormalMode, answers)
              .mustBe(routes.LocalReferenceNumberController.onPageLoad())
        }
      }

      "must go from Local Reference Number page to Office of Departure page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(LocalReferenceNumberPage, NormalMode, answers)
              .mustBe(routes.OfficeOfDepartureController.onPageLoad(answers.id, NormalMode))
        }
      }

      "must go from Office of Departure page to Procedure Type page when Country of Dispatch has not been answered" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.unsafeRemove(CountryOfDispatchPage)
            navigator
              .nextPage(OfficeOfDeparturePage, NormalMode, updatedAnswers)
              .mustBe(routes.ProcedureTypeController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from Office of Departure page to Destination Country page when Country of Dispatch has been answered" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val countryOfDispatch = CountryOfDispatch(CountryCode("GB"), false)
            val updatedAnswers    = answers.unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)
            navigator
              .nextPage(OfficeOfDeparturePage, NormalMode, updatedAnswers)
              .mustBe(controllers.routeDetails.routes.DestinationCountryController.onPageLoad(updatedAnswers.id, NormalMode))
        }
      }

      "must go from Procedure Type to Add Security Details page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ProcedureTypePage, NormalMode, answers)
              .mustBe(routes.AddSecurityDetailsController.onPageLoad(answers.id, NormalMode))
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
              .mustBe(routes.SessionExpiredController.onPageLoad())
        }
      }
    }
  }
}
