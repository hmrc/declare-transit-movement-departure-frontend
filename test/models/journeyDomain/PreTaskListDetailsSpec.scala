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
import models.journeyDomain.GuaranteeDetails.GuaranteeReference
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.{AddSecurityDetailsPage, LocalReferenceNumberPage, ProcedureTypePage, QuestionPage}

class PreTaskListDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  "PreTaskListDetails" - {

    "can be parsed UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[PreTaskListDetails], arbitrary[UserAnswers]) {
          case (preTaskListDetails, userAnswers) =>
            val updatedUserAnswer                  = PreTaskListDetailsSpec.setPreTaskListDetails(preTaskListDetails)(userAnswers)
            val result: Option[PreTaskListDetails] = UserAnswersReader[PreTaskListDetails].run(updatedUserAnswer)

            result.value mustEqual preTaskListDetails
        }
      }
    }

    "cannot be parsed" - {
      val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
        ProcedureTypePage,
        AddSecurityDetailsPage
      )

      "when an answer is missing" in {

        forAll(arbitrary[PreTaskListDetails], arbitrary[UserAnswers], mandatoryPages) {
          case (preTaskListDetails, ua, mandatoryPage) =>
            val userAnswers = PreTaskListDetailsSpec.setPreTaskListDetails(preTaskListDetails)(ua).remove(mandatoryPage).success.value
            val result      = UserAnswersReader[GuaranteeReference].run(userAnswers)

            result mustBe None
        }
      }
    }
  }
}

object PreTaskListDetailsSpec extends UserAnswersSpecHelper {

  def setPreTaskListDetails(preTaskListDetails: PreTaskListDetails)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
      .copy(id = preTaskListDetails.lrn)
      .unsafeSetVal(ProcedureTypePage)(preTaskListDetails.procedureType)
      .unsafeSetVal(AddSecurityDetailsPage)(preTaskListDetails.addSecurityDetails)
}
