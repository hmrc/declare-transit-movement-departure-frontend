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
import models.ProcedureType.Normal
import models.reference.{CountryCode, CustomsOffice}
import org.scalacheck.Gen
import pages.{AddSecurityDetailsPage, OfficeOfDeparturePage, ProcedureTypePage, QuestionPage}

class PreTaskListDetailsSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  private val preTaskListUa = emptyUserAnswers
    .unsafeSetVal(ProcedureTypePage)(Normal)
    .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("code"), Seq.empty, None))
    .unsafeSetVal(AddSecurityDetailsPage)(false)

  "PreTaskListDetails" - {

    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {

        val expectedResult = PreTaskListDetails(
          lrn,
          Normal,
          CustomsOffice("id", "name", CountryCode("code"), Seq.empty, None),
          false
        )

        val result: EitherType[PreTaskListDetails] = UserAnswersReader[PreTaskListDetails].run(preTaskListUa)

        result.right.value mustEqual expectedResult
      }
    }

    "cannot be parsed" - {
      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        ProcedureTypePage,
        OfficeOfDeparturePage,
        AddSecurityDetailsPage
      )

      "when an answer is missing" in {
        forAll(mandatoryPages) {
          mandatoryPage =>
            val userAnswers = preTaskListUa
              .unsafeRemove(mandatoryPage)

            val result: EitherType[PreTaskListDetails] = UserAnswersReader[PreTaskListDetails].run(userAnswers)

            result.left.value.page mustEqual mandatoryPage
        }
      }
    }
  }
}
