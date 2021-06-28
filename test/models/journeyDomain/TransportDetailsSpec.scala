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

package models.journeyDomain

import base.{GeneratorSpec, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import models.UserAnswers
import models.journeyDomain.TransportDetails.DetailsAtBorder.{SameDetailsAtBorder, _}
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, _}
import models.journeyDomain.TransportDetails.ModeCrossingBorder.{ModeExemptNationality, ModeWithNationality}
import models.journeyDomain.TransportDetails._
import models.reference.CountryCode
import org.scalacheck.Gen
import org.scalatest.TryValues
import pages._

class TransportDetailsSpec extends SpecBase with GeneratorSpec with TryValues with UserAnswersSpecHelper {

  "TransportDetails" - {

    "can be parsed for UserAnswers" - {

      "when InlandModePage is a Rail code" in {

        val railInlandModeCode = Gen.oneOf(Rail.Constants.codes)

        forAll(railInlandModeCode) {
          code =>
            val expectedResult = TransportDetails(Rail(code, None), SameDetailsAtBorder)

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)(code.toString)
              .unsafeSetVal(AddIdAtDeparturePage)(false)
              .unsafeSetVal(ChangeAtBorderPage)(false)

            val result = UserAnswersReader[TransportDetails].run(userAnswers).right.value

            result mustBe expectedResult
        }
      }

      "when InlandModePage is a Mode5or7 code" in {

        val mode5or7Code = Gen.oneOf(Mode5or7.Constants.codes)

        forAll(mode5or7Code) {
          code =>
            val expectedResult = TransportDetails(Mode5or7(code), SameDetailsAtBorder)

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)(code.toString)
              .unsafeSetVal(AddIdAtDeparturePage)(false)
              .unsafeSetVal(ChangeAtBorderPage)(false)

            val result = UserAnswersReader[TransportDetails].run(userAnswers).right.value

            result mustBe expectedResult
        }
      }

      "when InlandModePage is any other code" in {

        val otherCode = arb[Int].suchThat(
          value => !Mode5or7.Constants.codes.contains(value) && !Rail.Constants.codes.contains(value)
        )

        forAll(otherCode) {
          code =>
            val expectedResult = TransportDetails(NonSpecialMode(code, None, None), SameDetailsAtBorder)

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)(code.toString)
              .unsafeSetVal(AddIdAtDeparturePage)(false)
              .unsafeSetVal(ChangeAtBorderPage)(false)

            val result = UserAnswersReader[TransportDetails].run(userAnswers).right.value

            result mustBe expectedResult
        }
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when InlandModePage is empty" in {

        val result = UserAnswersReader[TransportDetails].run(emptyUserAnswers).left.value

        result.page mustBe InlandModePage
      }
    }

    "InlandMode" - {

      "Rail" - {

        "can be parsed from UserAnswers" - {

          "when add id at departure is true" in {

            val expectedResult = Rail(1, Some("departureId"))

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)("1")
              .unsafeSetVal(AddIdAtDeparturePage)(true)
              .unsafeSetVal(IdAtDeparturePage)("departureId")

            val result = UserAnswersReader[Rail].run(userAnswers).right.value

            result mustBe expectedResult
          }

          "when add id at departure is false" in {

            val expectedResult = Rail(1, None)

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)("1")
              .unsafeSetVal(AddIdAtDeparturePage)(false)

            val result = UserAnswersReader[Rail].run(userAnswers).right.value

            result mustBe expectedResult
          }
        }

        "cannot be parsed from UserAnswers" - {

          "when InlandModePage is empty" in {

            val result = UserAnswersReader[Rail].run(emptyUserAnswers).left.value

            result.page mustBe InlandModePage
          }

          "when add id at departure is empty" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)("1")

            val result = UserAnswersReader[Rail].run(userAnswers).left.value

            result.page mustBe IdAtDeparturePage
          }

          "when add id at departure is true and departure id is empty" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)("1")
              .unsafeSetVal(AddIdAtDeparturePage)(true)

            val result = UserAnswersReader[Rail].run(userAnswers).left.value

            result.page mustBe IdAtDeparturePage
          }
        }
      }

      "Mode5or7" - {

        "can be parsed from UserAnswers" - {

          "when InlandMode has been defined" in {

            val expectedResult = Mode5or7(2)

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)("2")

            val result = UserAnswersReader[Mode5or7].run(userAnswers).right.value

            result mustBe expectedResult
          }
        }

        "cannot be parsed from UserAnswers" - {

          "when InlandMode is empty" in {

            val result = UserAnswersReader[Mode5or7].run(emptyUserAnswers).left.value

            result.page mustBe InlandModePage
          }
        }
      }

      "NonSpecialMode" - {

        "can be parsed from UserAnswers" - {

          "when InlandMode has been defined and no other answers are defined" in {

            val expectedResult = NonSpecialMode(3, None, None)

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)("3")

            val result = UserAnswersReader[NonSpecialMode].run(userAnswers).right.value

            result mustBe expectedResult
          }

          "when InlandMode has been defined and all other answers are defined " in {

            val expectedResult = NonSpecialMode(3, Some(CountryCode("code")), Some("departureId"))

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(InlandModePage)("3")
              .unsafeSetVal(NationalityAtDeparturePage)(CountryCode("code"))
              .unsafeSetVal(IdAtDeparturePage)("departureId")

            val result = UserAnswersReader[NonSpecialMode].run(userAnswers).right.value

            result mustBe expectedResult
          }
        }

        "cannot be parsed from UserAnswers" - {

          "when InlandMode is empty" in {

            val result = UserAnswersReader[NonSpecialMode].run(emptyUserAnswers).left.value

            result.page mustBe InlandModePage
          }
        }
      }
    }

    "DetailsAtBorder" - {

      "can be parsed from UserAnswers" - {

        "when change at border page is true" in {

          val expectedResult = NewDetailsAtBorder("1", ModeExemptNationality(2))

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ChangeAtBorderPage)(true)
            .unsafeSetVal(ModeAtBorderPage)("1")
            .unsafeSetVal(ModeCrossingBorderPage)("2")

          val result = UserAnswersReader[DetailsAtBorder].run(userAnswers).right.value

          result mustBe expectedResult
        }

        "when change at border page is false" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ChangeAtBorderPage)(false)

          val result = UserAnswersReader[DetailsAtBorder].run(userAnswers).right.value

          result mustBe an[SameDetailsAtBorder.type]
        }
      }

      "cannot be parsed from UserAnswers" - {

        "when change at border page is empty" in {

          val result = UserAnswersReader[DetailsAtBorder].run(emptyUserAnswers).left.value

          result.page mustBe ChangeAtBorderPage
        }
      }

      "NewDetailsAtBorder" - {

        "can be parsed from UserAnswers" - {

          "when all mandatory pages are defined" in {

            val expectedResult = NewDetailsAtBorder("1", ModeExemptNationality(2))

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(ModeAtBorderPage)("1")
              .unsafeSetVal(ModeCrossingBorderPage)("2")

            val result = UserAnswersReader[NewDetailsAtBorder].run(userAnswers).right.value

            result mustBe expectedResult
          }
        }

        "cannot be parsed from UserAnswers" - {

          "when a mandatory page is empty" in {

            val result = UserAnswersReader[NewDetailsAtBorder].run(emptyUserAnswers).left.value

            result.page mustBe ModeAtBorderPage
          }
        }

        "ModeCrossingBorder" - {

          "can be parsed from UserAnswers" - {

            "when mode crossing the border is defined and is exempt from nationality" in {

              val genExemptNationalityCode = Gen.oneOf(Seq(2, 5, 7, 20, 50, 70, 21, 51, 71))

              forAll(genExemptNationalityCode) {
                exemptNationalityCode =>
                  val expectedResult = ModeExemptNationality(exemptNationalityCode)

                  val userAnswers = emptyUserAnswers
                    .unsafeSetVal(ModeCrossingBorderPage)(exemptNationalityCode.toString)

                  val result = UserAnswersReader[ModeCrossingBorder].run(userAnswers).right.value

                  result mustBe expectedResult
              }
            }

            "when mode crossing the border is defined and is not exempt from nationality" in {

              val genNationalityCode = Gen.oneOf(Seq(1, 3, 4, 6, 10, 30, 40, 60))

              forAll(genNationalityCode) {
                nationalityCode =>
                  val expectedResult = ModeWithNationality(CountryCode("code"), nationalityCode, "idCrossing")

                  val userAnswers = emptyUserAnswers
                    .unsafeSetVal(ModeCrossingBorderPage)(nationalityCode.toString)
                    .unsafeSetVal(NationalityCrossingBorderPage)(CountryCode("code"))
                    .unsafeSetVal(IdCrossingBorderPage)("idCrossing")

                  val result = UserAnswersReader[ModeCrossingBorder].run(userAnswers).right.value

                  result mustBe expectedResult
              }
            }
          }

          "cannot be parsed from UserAnswers" - {

            "when ModeCrossingBorderPage is empty" in {

              val result = UserAnswersReader[ModeCrossingBorder].run(emptyUserAnswers).left.value

              result.page mustBe ModeCrossingBorderPage
            }

            "when mode crossing the border is defined and is not exempt from nationality but a mandatory page is missing" in {

              val genMandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
                NationalityCrossingBorderPage,
                IdCrossingBorderPage
              )

              val genNationalityCode = Gen.oneOf(Seq(1, 3, 4, 6, 10, 30, 40, 60))

              forAll(genNationalityCode, genMandatoryPages) {
                (nationalityCode, mandatoryPage) =>
                  val userAnswers = emptyUserAnswers
                    .unsafeSetVal(ModeCrossingBorderPage)(nationalityCode.toString)
                    .unsafeSetVal(NationalityCrossingBorderPage)(CountryCode("code"))
                    .unsafeSetVal(IdCrossingBorderPage)("idCrossing")

                  val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

                  val result = UserAnswersReader[ModeCrossingBorder].run(invalidUserAnswers).left.value

                  result.page mustBe mandatoryPage
              }
            }
          }
        }
      }
    }
  }

  "ModeCrossingBorder" - {

    "isExemptFromNationality" - {

      "must return true when string starts with 2" in {

        ModeCrossingBorder.isExemptFromNationality("2") mustBe true
        ModeCrossingBorder.isExemptFromNationality("22") mustBe true
        ModeCrossingBorder.isExemptFromNationality("234567") mustBe true
      }

      "must return true when string starts with 5" in {

        ModeCrossingBorder.isExemptFromNationality("5") mustBe true
        ModeCrossingBorder.isExemptFromNationality("55") mustBe true
        ModeCrossingBorder.isExemptFromNationality("56789") mustBe true
      }

      "must return true when string starts with 7" in {

        ModeCrossingBorder.isExemptFromNationality("7") mustBe true
        ModeCrossingBorder.isExemptFromNationality("77") mustBe true
        ModeCrossingBorder.isExemptFromNationality("78901") mustBe true
      }

      "must return false when string starts with anything else" in {

        ModeCrossingBorder.isExemptFromNationality("3") mustBe false
        ModeCrossingBorder.isExemptFromNationality("12") mustBe false
        ModeCrossingBorder.isExemptFromNationality("90") mustBe false
      }
    }
  }
}
