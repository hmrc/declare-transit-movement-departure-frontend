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
import controllers.transportDetails.routes
import models.reference.{Country, CountryCode, TransportMode}
import models.{CheckMode, CountryList, Mode, TransportModeList}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

class TransportDetailsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper with ScalaCheckPropertyChecks {

  val mode: Mode = CheckMode

  "TransportDetailsCheckYourAnswersHelper" - {

    "modeAtBorder" - {

      val modeCode: String             = "MODE CODE"
      val transportMode: TransportMode = TransportMode(modeCode, "DESCRIPTION")

      "return None" - {
        "ModeAtBorderPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.modeAtBorder(TransportModeList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ModeAtBorderPage defined at index" - {

          "transport mode not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(ModeAtBorderPage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.modeAtBorder(TransportModeList(Nil))

            val label = msg"modeAtBorder.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"$modeCode"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.ModeAtBorderController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-mode-at-border")
                  )
                )
              )
            )
          }

          "transport mode found" in {

            val answers = emptyUserAnswers.unsafeSetVal(ModeAtBorderPage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.modeAtBorder(TransportModeList(Seq(transportMode)))

            val label = msg"modeAtBorder.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"(${transportMode.code}) ${transportMode.description}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.ModeAtBorderController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-mode-at-border")
                  )
                )
              )
            )
          }
        }
      }
    }

    "modeCrossingBorder" - {

      val modeCode: String             = "MODE CODE"
      val transportMode: TransportMode = TransportMode(modeCode, "DESCRIPTION")

      "return None" - {
        "ModeCrossingBorderPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.modeCrossingBorder(TransportModeList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ModeCrossingBorderPage defined at index" - {

          "transport mode not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(ModeCrossingBorderPage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.modeCrossingBorder(TransportModeList(Nil))

            val label = msg"modeCrossingBorder.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"$modeCode"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.ModeCrossingBorderController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-mode-crossing-border")
                  )
                )
              )
            )
          }

          "transport mode found" in {

            val answers = emptyUserAnswers.unsafeSetVal(ModeCrossingBorderPage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.modeCrossingBorder(TransportModeList(Seq(transportMode)))

            val label = msg"modeCrossingBorder.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"(${transportMode.code}) ${transportMode.description}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.ModeCrossingBorderController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-mode-crossing-border")
                  )
                )
              )
            )
          }
        }
      }
    }

    "inlandMode" - {

      val modeCode: String             = "MODE CODE"
      val transportMode: TransportMode = TransportMode(modeCode, "DESCRIPTION")

      "return None" - {
        "InlandModePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.inlandMode(TransportModeList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "InlandModePage defined at index" - {

          "transport mode not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(InlandModePage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.inlandMode(TransportModeList(Nil))

            val label = msg"inlandMode.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"$modeCode"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.InlandModeController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-inland-mode")
                  )
                )
              )
            )
          }

          "transport mode found" in {

            val answers = emptyUserAnswers.unsafeSetVal(InlandModePage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.inlandMode(TransportModeList(Seq(transportMode)))

            val label = msg"inlandMode.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"(${transportMode.code}) ${transportMode.description}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.InlandModeController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-inland-mode")
                  )
                )
              )
            )
          }
        }
      }
    }

    "idCrossingBorder" - {

      val id: String = "ID"

      "return None" - {
        "IdCrossingBorderPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.idCrossingBorder
          result mustBe None
        }
      }

      "return Some(row)" - {
        "IdCrossingBorderPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(IdCrossingBorderPage)(id)

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.idCrossingBorder

          val label = msg"idCrossingBorder.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$id"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.IdCrossingBorderController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-id-crossing-border")
                )
              )
            )
          )
        }
      }
    }

    "nationalityAtDeparture" - {

      val mode5or7AndRailCodes: Seq[Int] = Seq(5, 7, 50, 70, 2, 20)
      val code: String                   = "CODE"
      val countryCode: CountryCode       = CountryCode(code)
      val country: Country               = Country(countryCode, "DESCRIPTION")

      "return None" - {

        "NationalityAtDeparturePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.nationalityAtDeparture(CountryList(Nil), arbitrary[Int].sample.value.toString)
          result mustBe None
        }

        "inland mode code is Mode5or7 or Rail" in {

          val gen = Gen.oneOf(mode5or7AndRailCodes)

          forAll(gen) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(NationalityAtDeparturePage)(countryCode)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.nationalityAtDeparture(CountryList(Nil), modeCode.toString)
              result mustBe None
          }
        }
      }

      "return Some(row)" - {
        "NationalityAtDeparturePage defined at index and inland mode code is not Mode5or7 or Rail" - {

          "country not found" in {

            forAll(arbitrary[Int].retryUntil(!mode5or7AndRailCodes.contains(_))) {
              modeCode =>
                val answers = emptyUserAnswers.unsafeSetVal(NationalityAtDeparturePage)(countryCode)

                val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
                val result = helper.nationalityAtDeparture(CountryList(Nil), modeCode.toString)

                val label = msg"nationalityAtDeparture.checkYourAnswersLabel"

                result mustBe Some(
                  Row(
                    key = Key(label, classes = Seq("govuk-!-width-one-half")),
                    value = Value(lit"${countryCode.code}"),
                    actions = List(
                      Action(
                        content = msg"site.edit",
                        href = routes.NationalityAtDepartureController.onPageLoad(lrn, mode).url,
                        visuallyHiddenText = Some(label),
                        attributes = Map("id" -> "change-nationality-at-departure")
                      )
                    )
                  )
                )
            }
          }

          "country found" in {

            forAll(arbitrary[Int].retryUntil(!mode5or7AndRailCodes.contains(_))) {
              modeCode =>
                val answers = emptyUserAnswers.unsafeSetVal(NationalityAtDeparturePage)(countryCode)

                val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
                val result = helper.nationalityAtDeparture(CountryList(Seq(country)), modeCode.toString)

                val label = msg"nationalityAtDeparture.checkYourAnswersLabel"

                result mustBe Some(
                  Row(
                    key = Key(label, classes = Seq("govuk-!-width-one-half")),
                    value = Value(lit"${country.description}"),
                    actions = List(
                      Action(
                        content = msg"site.edit",
                        href = routes.NationalityAtDepartureController.onPageLoad(lrn, mode).url,
                        visuallyHiddenText = Some(label),
                        attributes = Map("id" -> "change-nationality-at-departure")
                      )
                    )
                  )
                )
            }
          }
        }
      }
    }

    "nationalityCrossingBorder" - {

      val code: String             = "CODE"
      val countryCode: CountryCode = CountryCode(code)
      val country: Country         = Country(countryCode, "DESCRIPTION")

      "return None" - {
        "NationalityCrossingBorderPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.nationalityCrossingBorder(CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "NationalityCrossingBorderPage defined at index" - {

          "country not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(NationalityCrossingBorderPage)(countryCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.nationalityCrossingBorder(CountryList(Nil))

            val label = msg"nationalityCrossingBorder.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"${countryCode.code}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.NationalityCrossingBorderController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-nationality-crossing-border")
                  )
                )
              )
            )
          }

          "country found" in {

            val answers = emptyUserAnswers.unsafeSetVal(NationalityCrossingBorderPage)(countryCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.nationalityCrossingBorder(CountryList(Seq(country)))

            val label = msg"nationalityCrossingBorder.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"${country.description}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.NationalityCrossingBorderController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-nationality-crossing-border")
                  )
                )
              )
            )
          }
        }
      }
    }

    "idAtDeparture" - {

      val id: String              = "ID"
      val mode5or7Codes: Seq[Int] = Seq(5, 7, 50, 70)

      "return None" - {

        "IdAtDeparturePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.idAtDeparture(arbitrary[Int].sample.value.toString)
          result mustBe None
        }

        "inland mode code is Mode5or7" in {

          val gen = Gen.oneOf(mode5or7Codes)

          forAll(gen) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(IdAtDeparturePage)(id)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.idAtDeparture(modeCode.toString)
              result mustBe None
          }
        }
      }

      "return Some(row)" - {
        "IdAtDeparturePage defined at index and inland mode code is not Mode5or7" in {

          forAll(arbitrary[Int].retryUntil(!mode5or7Codes.contains(_))) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(IdAtDeparturePage)(id)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.idAtDeparture(modeCode.toString)

              val label = msg"idAtDeparture.checkYourAnswersLabel"

              result mustBe Some(
                Row(
                  key = Key(label, classes = Seq("govuk-!-width-one-half")),
                  value = Value(lit"$id"),
                  actions = List(
                    Action(
                      content = msg"site.edit",
                      href = routes.IdAtDepartureController.onPageLoad(lrn, mode).url,
                      visuallyHiddenText = Some(label)
                    )
                  )
                )
              )
          }
        }
      }
    }

    "changeAtBorder" - {

      "return None" - {
        "ChangeAtBorderPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.changeAtBorder
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ChangeAtBorderPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ChangeAtBorderPage)(true)

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.changeAtBorder

          val label = msg"changeAtBorder.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ChangeAtBorderController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-change-at-border")
                )
              )
            )
          )
        }
      }
    }

    "addIdAtDeparture" - {

      val mode5or7Codes: Seq[Int] = Seq(5, 7, 50, 70)

      "return None" - {

        "AddIdAtDeparturePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.addIdAtDeparture(arbitrary[Int].sample.value.toString)
          result mustBe None
        }

        "inland mode code is Mode5or7" in {

          val gen = Gen.oneOf(mode5or7Codes)

          forAll(gen) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(AddIdAtDeparturePage)(true)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.addIdAtDeparture(modeCode.toString)
              result mustBe None
          }
        }
      }

      "return Some(row)" - {
        "AddIdAtDeparturePage defined at index and inland mode code is not Mode5or7" in {

          forAll(arbitrary[Int].retryUntil(!mode5or7Codes.contains(_))) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(AddIdAtDeparturePage)(true)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.addIdAtDeparture(modeCode.toString)

              val label = msg"addIdAtDeparture.checkYourAnswersLabel"

              result mustBe Some(
                Row(
                  key = Key(label, classes = Seq("govuk-!-width-one-half")),
                  value = Value(msg"site.yes"),
                  actions = List(
                    Action(
                      content = msg"site.edit",
                      href = routes.AddIdAtDepartureController.onPageLoad(lrn, mode).url,
                      visuallyHiddenText = Some(label),
                      attributes = Map("id" -> "change-add-id-at-departure")
                    )
                  )
                )
              )
          }
        }
      }
    }

    "addNationalityAtDeparture" - {

      val mode5or7AndRailCodes: Seq[Int] = Seq(5, 7, 50, 70, 2, 20)

      "return None" - {

        "AddNationalityAtDeparturePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.addNationalityAtDeparture(arbitrary[Int].sample.value.toString)
          result mustBe None
        }

        "inland mode code is Mode5or7" in {

          val gen = Gen.oneOf(mode5or7AndRailCodes)

          forAll(gen) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(AddNationalityAtDeparturePage)(true)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.addNationalityAtDeparture(modeCode.toString)
              result mustBe None
          }
        }
      }

      "return Some(row)" - {
        "AddNationalityAtDeparturePage defined at index and inland mode code is not Mode5or7" in {

          forAll(arbitrary[Int].retryUntil(!mode5or7AndRailCodes.contains(_))) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(AddNationalityAtDeparturePage)(true)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.addNationalityAtDeparture(modeCode.toString)

              val label = msg"addNationalityAtDeparture.checkYourAnswersLabel"

              result mustBe Some(
                Row(
                  key = Key(label, classes = Seq("govuk-!-width-one-half")),
                  value = Value(msg"site.yes"),
                  actions = List(
                    Action(
                      content = msg"site.edit",
                      href = routes.AddNationalityAtDepartureController.onPageLoad(lrn, mode).url,
                      visuallyHiddenText = Some(label),
                      attributes = Map("id" -> "change-add-nationality-at-departure")
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
