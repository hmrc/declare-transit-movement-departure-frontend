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

import base.{GeneratorSpec, SpecBase}
import models.journeyDomain.TransportDetails._
import models.journeyDomain.TransportDetails.InlandMode._
import models.journeyDomain.TransportDetails.DetailsAtBorder._
import models.journeyDomain.TransportDetails.ModeCrossingBorder._
import models.{Index, LocalDateTimeWithAMPM, UserAnswers}
import org.scalacheck.Gen
import org.scalatest.TryValues
import pages._

class TransportDetailsSpec extends SpecBase with GeneratorSpec with TryValues {

  "TransportDetail can be parser from UserAnswers" - {
    "when there are no change at the border" - {
      val willNotChangeAnswers: Gen[UserAnswers] =
        arbitrary[UserAnswers].map(_.set(ChangeAtBorderPage, false).success.value)

      "when inland mode is 'Rail'" in {
        val railModeGen = Gen.oneOf(Rail.Constants.codes)

        forAll(willNotChangeAnswers, railModeGen) {
          (baseUserAnswers, railMode) =>
            val userAnswers = baseUserAnswers
              .set(InlandModePage, railMode)
              .success
              .value

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual TransportDetails(inlandMode = Rail, detailsAtBorder = SameDetailsAtBorder)

        }

      }

      "when inland mode is 'Postal Consignment' or 'Fixed transport installations'" in {
        val modeGen = Gen.oneOf(Mode5or7.Constants.codes)

        forAll(willNotChangeAnswers, modeGen) {
          (baseUserAnswers, mode) =>
            val userAnswers = baseUserAnswers
              .set(InlandModePage, mode)
              .success
              .value

            val result = UserAnswersParser[Option, TransportDetails].run(userAnswers).value

            result mustEqual TransportDetails(inlandMode = Rail, detailsAtBorder = SameDetailsAtBorder)

        }
      }

    }
  }

}
