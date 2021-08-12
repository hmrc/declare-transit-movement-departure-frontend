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
import controllers.transportDetails.{routes => transportDetailsRoute}
import generators.Generators
import models._
import models.journeyDomain.TransportDetails.ModeCrossingBorder
import models.reference.CountryCode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.generalInformation.ContainersUsedPage

class TransportDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TransportDetailsNavigator

  "TransportDetailsNavigator" - {

    "in Normal Mode" - {
      "must go from InlandMode page to AddIdAtDeparture Page if value is not 5,7 or 50,70 and selected yes to using containers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ContainersUsedPage, true).toOption.value
            navigator
              .nextPage(InlandModePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddIdAtDepartureController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from InlandMode page to AddIdAtDeparture Page if value is not 5,7 or 50,70 and selected no to using containers" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ContainersUsedPage, false).toOption.value
            navigator
              .nextPage(InlandModePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from InlandMode page to Will these details change at border Page if value is 5/50 or 7/70" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            Seq("5", "50", "7", "70") foreach {
              inlandModeAnswer =>
                val updatedAnswers = answers.set(InlandModePage, inlandModeAnswer).success.value
                navigator
                  .nextPage(InlandModePage, NormalMode, updatedAnswers)
                  .mustBe(transportDetailsRoute.ChangeAtBorderController.onPageLoad(answers.lrn, NormalMode))
            }
        }
      }

      "must go from AddIdAtDeparture page to AddIdAtDepartureLater page when user selects 'no' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(AddIdAtDeparturePage, false).toOption.value

            navigator
              .nextPage(AddIdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddIdAtDepartureLaterController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from AddIdAtDeparture page to IdAtDeparture page when user selects 'yes' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(AddIdAtDeparturePage, true)
              .toOption
              .value
              .remove(IdAtDeparturePage)
              .success
              .value

            navigator
              .nextPage(AddIdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from AddIdAtDepartureLater page to change at border page if Inland Mode is 2,5 or 7" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(InlandModePage, "2")
              .success
              .value
            navigator
              .nextPage(AddIdAtDepartureLaterPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.ChangeAtBorderController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from IdAtDeparture page to NationalityAtDeparture Page if selected no to using containers and inland mode is not 2,5 or 7" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(InlandModePage, "1")
              .toOption
              .value
              .set(ContainersUsedPage, false)
              .toOption
              .value
            navigator
              .nextPage(IdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.NationalityAtDepartureController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from IdAtDeparture page to AddNationalityAtDeparture Page if selected yes to using containers and inland mode is not 2,5 or 7" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(ContainersUsedPage, true)
              .toOption
              .value
              .set(InlandModePage, "1")
              .toOption
              .value
            navigator
              .nextPage(IdAtDeparturePage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddNationalityAtDepartureController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from NationalityAtDeparture page to ChangeAtBorder Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(NationalityAtDeparturePage, NormalMode, answers)
              .mustBe(transportDetailsRoute.ChangeAtBorderController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from ChangeAtBorder page to TraderDetailsCheckYourAnswers page when user selects 'no' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ChangeAtBorderPage, false).toOption.value

            navigator
              .nextPage(ChangeAtBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(updatedAnswers.lrn))
        }
      }

      "must go from ChangeAtBorder page to ModeAtBorder page when user selects 'yes' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ChangeAtBorderPage, true).toOption.value

            navigator
              .nextPage(ChangeAtBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.ModeAtBorderController.onPageLoad(updatedAnswers.lrn, NormalMode))
        }
      }

      "must go from ModeAtBorder page to ModeCrossingBorder Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ModeAtBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.ModeCrossingBorderController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from IdCrossingBorder page to NationalityCrossingBorder Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(IdCrossingBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.NationalityCrossingBorderController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from ModeCrossingBorder to TransportDetailsCheckYourAnswers when inlandMode starts with 2, 5, 7" in {

        forAll(arbitrary[UserAnswers], genExemptNationalityCode) {
          (answers, inlandMode) =>
            val updatedAnswers = answers.set(ModeCrossingBorderPage, inlandMode.toString).success.value

            navigator
              .nextPage(ModeCrossingBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from ModeCrossingBorder to NationalityCrossingBorder Page when answer does not start with 2, 5 or 7" in {
        val inlandModesGen = Gen.numStr.suchThat(
          num => !ModeCrossingBorder.isExemptFromNationality(num)
        )
        forAll(arbitrary[UserAnswers], inlandModesGen) {
          (answers, inlandMode) =>
            val updatedAnswers = answers.set(ModeCrossingBorderPage, inlandMode).success.value

            navigator
              .nextPage(ModeCrossingBorderPage, NormalMode, updatedAnswers)
              .mustBe(transportDetailsRoute.IdCrossingBorderController.onPageLoad(answers.lrn, NormalMode))
        }
      }

      "must go from NationalityCrossingBorder page to TransportDetailsCheckYourAnswers Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(NationalityCrossingBorderPage, NormalMode, answers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

    }

    "in Check Mode" - {

      "must go from InlandMode page to Add Id at Departure Page if add id at departure page was not asked previously" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers
              .set(ContainersUsedPage, true)
              .toOption
              .value
              .remove(AddIdAtDeparturePage)
              .success
              .value
            navigator
              .nextPage(InlandModePage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.AddIdAtDepartureController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "must go from InlandMode page to Change at Border Page if 5/50 or 7/70" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            Seq("5", "50", "7", "70") foreach {
              inlandModeAnswer =>
                val updatedAnswers = answers.set(InlandModePage, inlandModeAnswer).success.value
                navigator
                  .nextPage(InlandModePage, CheckMode, updatedAnswers)
                  .mustBe(transportDetailsRoute.ChangeAtBorderController.onPageLoad(answers.lrn, CheckMode))
            }
        }
      }

      "must go from AddIdAtDeparture page to IdAtDeparture page on selecting option 'Yes' and IdAtDeparture has no data " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers
              .set(AddIdAtDeparturePage, true)
              .toOption
              .value
              .remove(IdAtDeparturePage)
              .success
              .value

            navigator
              .nextPage(AddIdAtDeparturePage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.IdAtDepartureController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "must go from NationalityAtDeparturePage to TransportDetailsCheckYourAnswers Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(NationalityAtDeparturePage, CheckMode, answers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from ChangeAtBorderPage to TransportDetailsCheckYourAnswers on selecting option 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers.set(ChangeAtBorderPage, false).toOption.value

            navigator
              .nextPage(ChangeAtBorderPage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from ChangeAtBorderPage to ModeAtBorderPage on selecting option 'Yes' and no answers exist" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers
              .set(ChangeAtBorderPage, true)
              .toOption
              .value
              .remove(ModeAtBorderPage)
              .success
              .value
              .remove(IdCrossingBorderPage)
              .success
              .value
              .remove(ModeCrossingBorderPage)
              .success
              .value
              .remove(NationalityCrossingBorderPage)
              .success
              .value

            navigator
              .nextPage(ChangeAtBorderPage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.ModeAtBorderController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "must go from ChangeAtBorderPage to TransportDetailsCheckYourAnswers on selecting option 'Yes' and answers already exist for ModeAtBorder" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedUserAnswers = answers
              .set(ChangeAtBorderPage, true)
              .toOption
              .value
              .set(ModeAtBorderPage, "Bob")
              .success
              .value

            navigator
              .nextPage(ChangeAtBorderPage, CheckMode, updatedUserAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from ModeAtBorderPage to TransportDetailsCheckYourAnswers Page when answer exists for ModeCrossingBorderPage" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(ModeCrossingBorderPage, "Foo").success.value
            navigator
              .nextPage(ModeAtBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from ModeAtBorderPage to IdCrossingBorder Page when no answer exists for ModeCrossingBorderPage" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.remove(ModeCrossingBorderPage).success.value
            navigator
              .nextPage(ModeAtBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.ModeCrossingBorderController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "must go from IdCrossingBorder to TransportDetailsCheckYourAnswers Page when answer exists for ModeCrossingBorder" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(NationalityCrossingBorderPage, CountryCode("EN")).success.value
            navigator
              .nextPage(IdCrossingBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from IdCrossingBorder to NationalityCrossingBorderPage Page when no answer exists for ModeCrossingBorder" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.remove(NationalityCrossingBorderPage).success.value
            navigator
              .nextPage(IdCrossingBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.NationalityCrossingBorderController.onPageLoad(updatedAnswers.lrn, CheckMode))
        }
      }

      "must go from ModeCrossingBorder to TransportDetailsCheckYourAnswers answer starts with 2, 5 or 7" in {

        forAll(arbitrary[UserAnswers], genExemptNationalityCode) {
          (answers, inlandMode) =>
            val updatedAnswers = answers.set(ModeCrossingBorderPage, inlandMode.toString).success.value

            navigator
              .nextPage(ModeCrossingBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }

      "must go from ModeCrossingBorder to IdCrossingBorderPage when answer does not start with 2, 5, 7" in {
        val inlandModesGen = Gen.numStr.suchThat(
          num => !ModeCrossingBorder.isExemptFromNationality(num)
        )
        forAll(arbitrary[UserAnswers], inlandModesGen) {
          (answers, inlandMode) =>
            val updatedAnswers = answers.set(ModeCrossingBorderPage, inlandMode).success.value

            navigator
              .nextPage(ModeCrossingBorderPage, CheckMode, updatedAnswers)
              .mustBe(transportDetailsRoute.IdCrossingBorderController.onPageLoad(answers.lrn, CheckMode))
        }
      }

      "must go from NationalityCrossingBorderPage to TransportDetailsCheckYourAnswers Page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(NationalityCrossingBorderPage, CheckMode, answers)
              .mustBe(transportDetailsRoute.TransportDetailsCheckYourAnswersController.onPageLoad(answers.lrn))
        }
      }
    }
  }
}
