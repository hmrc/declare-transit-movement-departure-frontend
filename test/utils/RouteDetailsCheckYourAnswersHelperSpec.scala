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

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.routeDetails.routes
import generators.Generators
import models.reference.{CountryCode, CustomsOffice}
import models.{CustomsOfficeList, Mode, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.{AddAnotherTransitOfficePage, ArrivalTimesAtOfficePage}
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

import java.time.LocalDateTime

class RouteDetailsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val mode: Mode = NormalMode

  "RouteDetailsCheckYourAnswersHelper" - {

    "officeOfTransitRow" - {

      val office = CustomsOffice("OFFICE ID", "OFFICE NAME", CountryCode("COUNTRY CODE"), None)

      "return None" - {

        "AddAnotherTransitOfficePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.officeOfTransitRow(index, CustomsOfficeList(Nil), mode)
          result mustBe None
        }

        "customs office ID not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAnotherTransitOfficePage(index))(office.id)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.officeOfTransitRow(index, CustomsOfficeList(Nil), mode)
          result mustBe None
        }
      }

      "return Some(Row)" - {

        "AddAnotherTransitOfficePage defined at index and customs office ID found" - {

          "arrival time unknown" in {

            val answers = emptyUserAnswers
              .unsafeSetVal(AddAnotherTransitOfficePage(index))(office.id)

            val helper = new RouteDetailsCheckYourAnswersHelper(answers)
            val result = helper.officeOfTransitRow(index, CustomsOfficeList(Seq(office)), mode)

            val key = s"${office.name} (${office.id})"

            result mustBe Some(
              Row(
                key = Key(lit"$key"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.change",
                    href = routes.OfficeOfTransitCountryController.onPageLoad(answers.lrn, index, mode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                    attributes = Map("id" -> s"change-office-of-transit-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(answers.lrn, index, mode).url,
                    visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(key)),
                    attributes = Map("id" -> s"remove-office-of-transit-${index.display}")
                  )
                )
              )
            )
          }

          "arrival time known" in {

            val arrivalTime          = arbitrary[LocalDateTime].sample.value
            val formattedArrivalTime = Format.dateTimeFormattedAMPM(arrivalTime).toLowerCase

            val answers = emptyUserAnswers
              .unsafeSetVal(AddAnotherTransitOfficePage(index))(office.id)
              .unsafeSetVal(ArrivalTimesAtOfficePage(index))(arrivalTime)

            val helper = new RouteDetailsCheckYourAnswersHelper(answers)
            val result = helper.officeOfTransitRow(index, CustomsOfficeList(Seq(office)), mode)

            val key = s"${office.name} (${office.id})"

            result mustBe Some(
              Row(
                key = Key(lit"$key"),
                value = Value(lit"$formattedArrivalTime"),
                actions = List(
                  Action(
                    content = msg"site.change",
                    href = routes.OfficeOfTransitCountryController.onPageLoad(answers.lrn, index, mode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                    attributes = Map("id" -> s"change-office-of-transit-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(answers.lrn, index, mode).url,
                    visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(key)),
                    attributes = Map("id" -> s"remove-office-of-transit-${index.display}")
                  )
                )
              )
            )
          }
        }
      }
    }
  }

}
