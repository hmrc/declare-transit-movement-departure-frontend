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
import models.reference.{Country, CountryCode, CountryOfDispatch, CustomsOffice}
import models.{CheckMode, CountryList, CustomsOfficeList, Mode, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails._
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

import java.time.LocalDateTime

class RouteDetailsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val mode: Mode               = NormalMode
  private val countryCode: CountryCode = CountryCode("COUNTRY CODE")
  private val country: Country         = Country(countryCode, "COUNTRY DESCRIPTION")

  private val customsOffice = CustomsOffice("OFFICE ID", "OFFICE NAME", countryCode, None)

  "RouteDetailsCheckYourAnswersHelper" - {

    "officeOfTransitRow" - {

      "return None" - {

        "AddAnotherTransitOfficePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.officeOfTransitRow(index, CustomsOfficeList(Nil), mode)
          result mustBe None
        }

        "customs office ID not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.officeOfTransitRow(index, CustomsOfficeList(Nil), mode)
          result mustBe None
        }
      }

      "return Some(Row)" - {

        "AddAnotherTransitOfficePage defined at index and customs office ID found" - {

          "arrival time unknown" in {

            val answers = emptyUserAnswers
              .unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)

            val helper = new RouteDetailsCheckYourAnswersHelper(answers)
            val result = helper.officeOfTransitRow(index, CustomsOfficeList(Seq(customsOffice)), mode)

            val key = s"${customsOffice.name} (${customsOffice.id})"

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
              .unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)
              .unsafeSetVal(ArrivalTimesAtOfficePage(index))(arrivalTime)

            val helper = new RouteDetailsCheckYourAnswersHelper(answers)
            val result = helper.officeOfTransitRow(index, CustomsOfficeList(Seq(customsOffice)), mode)

            val key = s"${customsOffice.name} (${customsOffice.id})"

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

    "movementDestinationCountry" - {

      "return None" - {

        "MovementDestinationCountryPage undefined" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.movementDestinationCountry(CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(Row)" - {

        "country name found" in {

          val answers = emptyUserAnswers.unsafeSetVal(MovementDestinationCountryPage)(countryCode)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.movementDestinationCountry(CountryList(Seq(country)))

          result mustBe Some(
            Row(
              key = Key(msg"movementDestinationCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${country.description}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.MovementDestinationCountryController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"movementDestinationCountry.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-movement-destination-country")
                )
              )
            )
          )
        }

        "country name not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(MovementDestinationCountryPage)(countryCode)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.movementDestinationCountry(CountryList(Nil))

          result mustBe Some(
            Row(
              key = Key(msg"movementDestinationCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${countryCode.code}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.MovementDestinationCountryController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"movementDestinationCountry.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-movement-destination-country")
                )
              )
            )
          )
        }
      }
    }

    "destinationCountry" - {

      "return None" - {

        "DestinationCountryPage undefined" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.destinationCountry(CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(Row)" - {

        "country name found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DestinationCountryPage)(countryCode)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.destinationCountry(CountryList(Seq(country)))

          result mustBe Some(
            Row(
              key = Key(msg"destinationCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${country.description}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.DestinationCountryController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"destinationCountry.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-destination-country")
                )
              )
            )
          )
        }

        "country name not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DestinationCountryPage)(countryCode)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.destinationCountry(CountryList(Nil))

          result mustBe Some(
            Row(
              key = Key(msg"destinationCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${countryCode.code}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.DestinationCountryController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"destinationCountry.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-destination-country")
                )
              )
            )
          )
        }
      }
    }

    "countryOfDispatch" - {

      "return None" - {

        "CountryOfDispatchPage undefined" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.countryOfDispatch(CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(Row)" - {

        val countryOfDispatch: CountryOfDispatch = CountryOfDispatch(countryCode, isNotEu = false)

        "country name found" in {

          val answers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.countryOfDispatch(CountryList(Seq(country)))

          result mustBe Some(
            Row(
              key = Key(msg"countryOfDispatch.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${country.description}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.CountryOfDispatchController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"countryOfDispatch.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-country-of-dispatch")
                )
              )
            )
          )
        }

        "country name not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.countryOfDispatch(CountryList(Nil))

          result mustBe Some(
            Row(
              key = Key(msg"countryOfDispatch.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${countryCode.code}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.CountryOfDispatchController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"countryOfDispatch.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-country-of-dispatch")
                )
              )
            )
          )
        }
      }
    }

    "destinationOffice" - {

      "return None" - {

        "DestinationOfficePage undefined" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.destinationOffice(CustomsOfficeList(Nil))
          result mustBe None
        }

        "customs office not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DestinationOfficePage)(customsOffice)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.destinationOffice(CustomsOfficeList(Nil))

          result mustBe None
        }
      }

      "return Some(Row)" - {

        "customs office found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DestinationOfficePage)(customsOffice)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.destinationOffice(CustomsOfficeList(Seq(customsOffice)))

          result mustBe Some(
            Row(
              key = Key(msg"destinationOffice.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${customsOffice.name} (${customsOffice.id})"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.DestinationOfficeController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"destinationOffice.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-destination-office")
                )
              )
            )
          )
        }
      }
    }

    "addAnotherTransitOffice" - {

      "return None" - {

        "AddAnotherTransitOfficePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.addAnotherTransitOffice(index, CustomsOfficeList(Nil))
          result mustBe None
        }

        "customs office not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.addAnotherTransitOffice(index, CustomsOfficeList(Nil))

          result mustBe None
        }
      }

      "return Some(Row)" - {

        "customs office found" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.addAnotherTransitOffice(index, CustomsOfficeList(Seq(customsOffice)))

          result mustBe Some(
            Row(
              key = Key(msg"addAnotherTransitOffice.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${customsOffice.name} (${customsOffice.id})"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.OfficeOfTransitCountryController.onPageLoad(lrn = lrn, index = index, mode = CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addAnotherTransitOffice.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-office-of-transit")
                )
              )
            )
          )
        }
      }
    }

    "arrivalTimesAtOffice" - {

      "return None" - {

        "ArrivalTimesAtOfficePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.arrivalTimesAtOffice(index)
          result mustBe None
        }
      }

      "return Some(Row)" - {

        "ArrivalTimesAtOfficePage defined at index" in {

          val arrivalTime          = arbitrary[LocalDateTime].sample.value
          val formattedArrivalTime = Format.dateTimeFormattedAMPM(arrivalTime).toLowerCase

          val answers = emptyUserAnswers.unsafeSetVal(ArrivalTimesAtOfficePage(index))(arrivalTime)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers)
          val result = helper.arrivalTimesAtOffice(index)

          result mustBe Some(
            Row(
              key = Key(msg"arrivalTimesAtOffice.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$formattedArrivalTime"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ArrivalTimesAtOfficeController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"arrivalTimesAtOffice.checkYourAnswersLabel")),
                  attributes = Map("id" -> "change-arrival-times-at-office-of-transit")
                )
              )
            )
          )
        }
      }
    }
  }

}
