/*
 * Copyright 2020 HM Revenue & Customs
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

import base.{GeneratorSpec, SpecBase, UserAnswersSpecHelper}
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

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual expected

        }

      }

      "when inland mode is 'Postal Consignment' or 'Fixed transport installations'" in {

        forAll(arb[UserAnswers], arb[Mode5or7]) {
          (baseUserAnswers, mode) =>
            val expected = TransportDetails(mode, SameDetailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual expected

        }
      }

      "when inland mode is anything other than 'Rail', 'Postal Consignment' or 'Fixed transport installations'" in {

        forAll(arb[UserAnswers], arb[NonSpecialMode]) {
          (baseUserAnswers, mode) =>
            val expected = TransportDetails(mode, SameDetailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

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

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual expected

        }

      }

      "when inland mode is 'Postal Consignment' or 'Fixed transport installations'" in {

        forAll(arb[UserAnswers], arb[Mode5or7], arb[NewDetailsAtBorder]) {
          (baseUserAnswers, mode, detailsAtBorder) =>
            val expected = TransportDetails(mode, detailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual expected

        }
      }

      "when inland mode is anything other than 'Rail', 'Postal Consignment' or 'Fixed transport installations'" in {

        forAll(arb[UserAnswers], arb[NonSpecialMode], arb[NewDetailsAtBorder]) {
          (baseUserAnswers, mode, detailsAtBorder) =>
            val expected = TransportDetails(mode, detailsAtBorder)

            val userAnswers = setTransportDetail(expected)(baseUserAnswers)

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual expected

        }
      }
    }
  }

}

object TransportDetailsSpec extends UserAnswersSpecHelper {

  def setTransportDetail(transportDetails: TransportDetails)(startUserAnswers: UserAnswers): UserAnswers =
    transportDetails.inlandMode match {
      case InlandMode.Rail(code) =>
        startUserAnswers
          .unsafeSetVal(ChangeAtBorderPage)(!transportDetails.detailsAtBorder.isInstanceOf[SameDetailsAtBorder.type])
          .unsafeSetVal(InlandModePage)(code.toString)
          .unsafeSetPFn(ModeAtBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(mode, _, _) => mode
          })
          .unsafeSetPFn(IdCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, idCrossing, _) => idCrossing
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, _, ModeExemptNationality) => InlandMode.Constants.codes.head
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, _, ModeWithNationality(_)) => "ZZ"
          })
          .unsafeSetPFn(NationalityCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, _, ModeWithNationality(nationalityCrossingBorder)) => nationalityCrossingBorder
          })

      case Mode5or7(code, nationalityAtDeparture) =>
        startUserAnswers
          .unsafeSetVal(ChangeAtBorderPage)(!transportDetails.detailsAtBorder.isInstanceOf[SameDetailsAtBorder.type])
          .unsafeSetVal(InlandModePage)(code.toString)
          .unsafeSetVal(NationalityAtDeparturePage)(nationalityAtDeparture)
          .unsafeSetPFn(ModeAtBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(mode, _, _) => mode
          })
          .unsafeSetPFn(IdCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, idCrossing, _) => idCrossing
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, _, ModeExemptNationality) => InlandMode.Constants.codes.head
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, _, ModeWithNationality(_)) => "ZZ"
          })
          .unsafeSetPFn(NationalityCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, _, ModeWithNationality(nationalityCrossingBorder)) => nationalityCrossingBorder
          })

      case NonSpecialMode(code, nationalityAtDeparture, departureId) =>
        startUserAnswers
          .unsafeSetVal(ChangeAtBorderPage)(!transportDetails.detailsAtBorder.isInstanceOf[SameDetailsAtBorder.type])
          .unsafeSetVal(InlandModePage)(code.toString)
          .unsafeSetVal(NationalityAtDeparturePage)(nationalityAtDeparture)
          .unsafeSetVal(AddIdAtDeparturePage)(departureId.isDefined)
          .unsafeSetOpt(IdAtDeparturePage)(departureId)
          .unsafeSetPFn(ModeAtBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(mode, _, _) => mode
          })
          .unsafeSetPFn(IdCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, idCrossing, _) => idCrossing
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, _, ModeExemptNationality) => InlandMode.Constants.codes.head
          })
          .unsafeSetPFn(ModeCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, _, ModeWithNationality(_)) => "ZZ"
          })
          .unsafeSetPFn(NationalityCrossingBorderPage)(transportDetails.detailsAtBorder)({
            case NewDetailsAtBorder(_, _, ModeWithNationality(nationalityCrossingBorder)) => nationalityCrossingBorder
          })
    }

  def setTransportDetailsRail(changeAtBorder: Boolean, mode: String = Rail.Constants.codes.head)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
      .unsafeSetVal(ChangeAtBorderPage)(changeAtBorder)
      .unsafeSetVal(InlandModePage)(mode)

}
