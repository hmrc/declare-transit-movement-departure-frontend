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
import generators.JourneyModelGenerators
import models.UserAnswers
import models.journeyDomain.TransportDetails.DetailsAtBorder._
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, _}
import models.journeyDomain.TransportDetails.ModeCrossingBorder.{ModeExemptNationality, ModeWithNationality}
import models.journeyDomain.TransportDetails._
import org.scalatest.TryValues
import pages._

class TransportDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {
  import TransportDetailsSpec._

  "TransportDetail can be parser from UserAnswers" - {
    "when there are no change at the border" - {
      "when inland mode is 'Rail'" in {

        forAll(arb[UserAnswers], arb[Rail]) {
          (baseUserAnswers, railMode) =>
            val expected = TransportDetails(railMode, SameDetailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersReader[TransportDetails].run(userAnswers).right.value

            result mustEqual expected
        }
      }

      "when inland mode is 'Postal Consignment' or 'Fixed transport installations'" in {

        forAll(arb[UserAnswers], arb[Mode5or7]) {
          (baseUserAnswers, mode) =>
            val expected = TransportDetails(mode, SameDetailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersReader[TransportDetails].run(userAnswers).right.value

            result mustEqual expected

        }
      }

      "when inland mode is anything other than 'Rail', 'Postal Consignment' or 'Fixed transport installations'" in {

        forAll(arb[UserAnswers], arb[NonSpecialMode]) {
          (baseUserAnswers, mode) =>
            val expected = TransportDetails(mode, SameDetailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersReader[TransportDetails].run(userAnswers).right.value

            result mustEqual expected

        }
      }
    }

    "when there is a change at the border" - {
      "asdf when inland mode is 'Rail'" in {

        forAll(arb[UserAnswers], arb[Rail], arb[NewDetailsAtBorder]) {
          (baseUserAnswers, railMode, detailsAtBorder) =>
            val expected = TransportDetails(railMode, detailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersReader[TransportDetails].run(userAnswers).right.value

            result mustEqual expected

        }

      }

      "when inland mode is 'Postal Consignment' or 'Fixed transport installations'" in {

        forAll(arb[UserAnswers], arb[Mode5or7], arb[NewDetailsAtBorder]) {
          (baseUserAnswers, mode, detailsAtBorder) =>
            val expected = TransportDetails(mode, detailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersReader[TransportDetails].run(userAnswers).right.value

            result mustEqual expected

        }
      }

      "when inland mode is anything other than 'Rail', 'Postal Consignment' or 'Fixed transport installations'" in {

        forAll(arb[UserAnswers], arb[NonSpecialMode], arb[NewDetailsAtBorder]) {
          (baseUserAnswers, mode, detailsAtBorder) =>
            val expected = TransportDetails(mode, detailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersReader[TransportDetails].run(userAnswers).right.value

            result mustEqual expected

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

object TransportDetailsSpec extends UserAnswersSpecHelper {

  def setTransportDetail(transportDetails: TransportDetails)(startUserAnswers: UserAnswers): UserAnswers =
    transportDetails.inlandMode match {
      case InlandMode.Rail(code, departureId) =>
        startUserAnswers
          .unsafeSetVal(ChangeAtBorderPage)(!transportDetails.detailsAtBorder.isInstanceOf[SameDetailsAtBorder.type])
          .unsafeSetVal(InlandModePage)(code.toString)
          .unsafeSetVal(AddIdAtDeparturePage)(departureId.isDefined)
          .unsafeSetOpt(IdAtDeparturePage)(departureId)
          .unsafeSetPFn(ModeAtBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(mode, _) => mode
          })
          .unsafeSetPFn(IdCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeWithNationality(_, _, idCrossing)) => idCrossing
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeExemptNationality(modeCode)) => modeCode.toString
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeWithNationality(_, modeCode, _)) => modeCode.toString
          })
          .unsafeSetPFn(NationalityCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeWithNationality(nationalityCrossingBorder, _, _)) => nationalityCrossingBorder
          })

      case Mode5or7(code) =>
        startUserAnswers
          .unsafeSetVal(ChangeAtBorderPage)(!transportDetails.detailsAtBorder.isInstanceOf[SameDetailsAtBorder.type])
          .unsafeSetVal(InlandModePage)(code.toString)
          .unsafeSetPFn(ModeAtBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(mode, _) => mode
          })
          .unsafeSetPFn(IdCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeWithNationality(_, _, idCrossing)) => idCrossing
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeExemptNationality(modeCode)) => modeCode.toString
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeWithNationality(_, modeCode, _)) => modeCode.toString
          })
          .unsafeSetPFn(NationalityCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeWithNationality(nationalityCrossingBorder, _, _)) => nationalityCrossingBorder
          })

      case NonSpecialMode(code, nationalityAtDeparture, departureId) =>
        startUserAnswers
          .unsafeSetVal(ChangeAtBorderPage)(!transportDetails.detailsAtBorder.isInstanceOf[SameDetailsAtBorder.type])
          .unsafeSetVal(InlandModePage)(code.toString)
          .unsafeSetVal(AddIdAtDeparturePage)(departureId.isDefined)
          .unsafeSetOpt(IdAtDeparturePage)(departureId)
          .unsafeSetVal(AddNationalityAtDeparturePage)(nationalityAtDeparture.isDefined)
          .unsafeSetOpt(NationalityAtDeparturePage)(nationalityAtDeparture)
          .unsafeSetPFn(ModeAtBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(mode, _) => mode
          })
          .unsafeSetPFn(IdCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeWithNationality(_, _, idCrossing)) => idCrossing
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeExemptNationality(modeCode)) => modeCode.toString
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeWithNationality(_, modeCode, _)) => modeCode.toString
          })
          .unsafeSetPFn(NationalityCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, ModeWithNationality(nationalityCrossingBorder, _, _)) => nationalityCrossingBorder
          })
    }

}
