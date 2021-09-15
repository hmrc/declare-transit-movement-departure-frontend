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
import models.{CheckMode, CountryList, TransportModeList}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

class TransportDetailsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper with ScalaCheckPropertyChecks {

  "TransportDetailsCheckYourAnswersHelper" - {

    "modeAtBorder" - {

      val modeCode: String    = "MODE CODE"
      val mode: TransportMode = TransportMode(modeCode, "DESCRIPTION")

      "return None" - {
        "ModeAtBorderPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.modeAtBorder(TransportModeList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ModeAtBorderPage defined at index" - {

          "transport mode not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(ModeAtBorderPage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers)
            val result = helper.modeAtBorder(TransportModeList(Nil))

            result mustBe Some(
              Row(
                key = Key(msg"modeAtBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"$modeCode"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.ModeAtBorderController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"modeAtBorder.checkYourAnswersLabel")),
                    attributes = Map("id" -> "change-mode-at-border")
                  )
                )
              )
            )
          }

          "transport mode found" in {

            val answers = emptyUserAnswers.unsafeSetVal(ModeAtBorderPage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers)
            val result = helper.modeAtBorder(TransportModeList(Seq(mode)))

            result mustBe Some(
              Row(
                key = Key(msg"modeAtBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"(${mode.code}) ${mode.description}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.ModeAtBorderController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"modeAtBorder.checkYourAnswersLabel")),
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

      val modeCode: String    = "MODE CODE"
      val mode: TransportMode = TransportMode(modeCode, "DESCRIPTION")

      "return None" - {
        "ModeCrossingBorderPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.modeCrossingBorder(TransportModeList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ModeCrossingBorderPage defined at index" - {

          "transport mode not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(ModeCrossingBorderPage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers)
            val result = helper.modeCrossingBorder(TransportModeList(Nil))

            result mustBe Some(
              Row(
                key = Key(msg"modeCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"$modeCode"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.ModeCrossingBorderController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"modeCrossingBorder.checkYourAnswersLabel")),
                    attributes = Map("id" -> "change-mode-crossing-border")
                  )
                )
              )
            )
          }

          "transport mode found" in {

            val answers = emptyUserAnswers.unsafeSetVal(ModeCrossingBorderPage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers)
            val result = helper.modeCrossingBorder(TransportModeList(Seq(mode)))

            result mustBe Some(
              Row(
                key = Key(msg"modeCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"(${mode.code}) ${mode.description}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.ModeCrossingBorderController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"modeCrossingBorder.checkYourAnswersLabel")),
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

      val modeCode: String    = "MODE CODE"
      val mode: TransportMode = TransportMode(modeCode, "DESCRIPTION")

      "return None" - {
        "InlandModePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.inlandMode(TransportModeList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "InlandModePage defined at index" - {

          "transport mode not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(InlandModePage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers)
            val result = helper.inlandMode(TransportModeList(Nil))

            result mustBe Some(
              Row(
                key = Key(msg"inlandMode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"$modeCode"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.InlandModeController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"inlandMode.checkYourAnswersLabel")),
                    attributes = Map("id" -> "change-inland-mode")
                  )
                )
              )
            )
          }

          "transport mode found" in {

            val answers = emptyUserAnswers.unsafeSetVal(InlandModePage)(modeCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers)
            val result = helper.inlandMode(TransportModeList(Seq(mode)))

            result mustBe Some(
              Row(
                key = Key(msg"inlandMode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"(${mode.code}) ${mode.description}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.InlandModeController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"inlandMode.checkYourAnswersLabel")),
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

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.idCrossingBorder
          result mustBe None
        }
      }

      "return Some(row)" - {
        "IdCrossingBorderPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(IdCrossingBorderPage)(id)

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.idCrossingBorder

          result mustBe Some(
            Row(
              key = Key(msg"idCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$id"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.IdCrossingBorderController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"idCrossingBorder.checkYourAnswersLabel")),
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

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.nationalityAtDeparture(CountryList(Nil), arbitrary[Int].sample.value.toString)
          result mustBe None
        }

        "inland mode code is Mode5or7 or Rail" in {

          val gen = Gen.oneOf(mode5or7AndRailCodes)

          forAll(gen) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(NationalityAtDeparturePage)(countryCode)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers)
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

                val helper = new TransportDetailsCheckYourAnswersHelper(answers)
                val result = helper.nationalityAtDeparture(CountryList(Nil), modeCode.toString)

                result mustBe Some(
                  Row(
                    key = Key(msg"nationalityAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                    value = Value(lit"${countryCode.code}"),
                    actions = List(
                      Action(
                        content = msg"site.edit",
                        href = routes.NationalityAtDepartureController.onPageLoad(lrn, CheckMode).url,
                        visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"nationalityAtDeparture.checkYourAnswersLabel")),
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

                val helper = new TransportDetailsCheckYourAnswersHelper(answers)
                val result = helper.nationalityAtDeparture(CountryList(Seq(country)), modeCode.toString)

                result mustBe Some(
                  Row(
                    key = Key(msg"nationalityAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                    value = Value(lit"${country.description}"),
                    actions = List(
                      Action(
                        content = msg"site.edit",
                        href = routes.NationalityAtDepartureController.onPageLoad(lrn, CheckMode).url,
                        visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"nationalityAtDeparture.checkYourAnswersLabel")),
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

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.nationalityCrossingBorder(CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "NationalityCrossingBorderPage defined at index" - {

          "country not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(NationalityCrossingBorderPage)(countryCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers)
            val result = helper.nationalityCrossingBorder(CountryList(Nil))

            result mustBe Some(
              Row(
                key = Key(msg"nationalityCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"${countryCode.code}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.NationalityCrossingBorderController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"nationalityCrossingBorder.checkYourAnswersLabel")),
                    attributes = Map("id" -> "change-nationality-crossing-border")
                  )
                )
              )
            )
          }

          "country found" in {

            val answers = emptyUserAnswers.unsafeSetVal(NationalityCrossingBorderPage)(countryCode)

            val helper = new TransportDetailsCheckYourAnswersHelper(answers)
            val result = helper.nationalityCrossingBorder(CountryList(Seq(country)))

            result mustBe Some(
              Row(
                key = Key(msg"nationalityCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"${country.description}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.NationalityCrossingBorderController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"nationalityCrossingBorder.checkYourAnswersLabel")),
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

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.idAtDeparture(arbitrary[Int].sample.value.toString)
          result mustBe None
        }

        "inland mode code is Mode5or7" in {

          val gen = Gen.oneOf(mode5or7Codes)

          forAll(gen) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(IdAtDeparturePage)(id)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers)
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

              val helper = new TransportDetailsCheckYourAnswersHelper(answers)
              val result = helper.idAtDeparture(modeCode.toString)

              result mustBe Some(
                Row(
                  key = Key(msg"idAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                  value = Value(lit"$id"),
                  actions = List(
                    Action(
                      content = msg"site.edit",
                      href = routes.IdAtDepartureController.onPageLoad(lrn, CheckMode).url,
                      visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"idAtDeparture.checkYourAnswersLabel"))
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

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.changeAtBorder
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ChangeAtBorderPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ChangeAtBorderPage)(true)

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.changeAtBorder

          result mustBe Some(
            Row(
              key = Key(msg"changeAtBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ChangeAtBorderController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeAtBorder.checkYourAnswersLabel")),
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

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.addIdAtDeparture(arbitrary[Int].sample.value.toString)
          result mustBe None
        }

        "inland mode code is Mode5or7" in {

          val gen = Gen.oneOf(mode5or7Codes)

          forAll(gen) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(AddIdAtDeparturePage)(true)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers)
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

              val helper = new TransportDetailsCheckYourAnswersHelper(answers)
              val result = helper.addIdAtDeparture(modeCode.toString)

              result mustBe Some(
                Row(
                  key = Key(msg"addIdAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                  value = Value(msg"site.yes"),
                  actions = List(
                    Action(
                      content = msg"site.edit",
                      href = routes.AddIdAtDepartureController.onPageLoad(lrn, CheckMode).url,
                      visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addIdAtDeparture.checkYourAnswersLabel")),
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

          val helper = new TransportDetailsCheckYourAnswersHelper(answers)
          val result = helper.addNationalityAtDeparture(arbitrary[Int].sample.value.toString)
          result mustBe None
        }

        "inland mode code is Mode5or7" in {

          val gen = Gen.oneOf(mode5or7AndRailCodes)

          forAll(gen) {
            modeCode =>
              val answers = emptyUserAnswers.unsafeSetVal(AddNationalityAtDeparturePage)(true)

              val helper = new TransportDetailsCheckYourAnswersHelper(answers)
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

              val helper = new TransportDetailsCheckYourAnswersHelper(answers)
              val result = helper.addNationalityAtDeparture(modeCode.toString)

              result mustBe Some(
                Row(
                  key = Key(msg"addNationalityAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                  value = Value(msg"site.yes"),
                  actions = List(
                    Action(
                      content = msg"site.edit",
                      href = routes.AddNationalityAtDepartureController.onPageLoad(lrn, CheckMode).url,
                      visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addNationalityAtDeparture.checkYourAnswersLabel")),
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
