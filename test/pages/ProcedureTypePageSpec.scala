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

package pages

import models.ProcedureType._
import models.{ProcedureType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.generalInformation.PreLodgeDeclarationPage

class ProcedureTypeSpec extends PageBehaviours {
  // format: off
  "ProcedureTypePage" - {

    beRetrievable[ProcedureType](ProcedureTypePage)

    beSettable[ProcedureType](ProcedureTypePage)

    beRemovable[ProcedureType](ProcedureTypePage)

    "cleanup" - {
      "must clean down PreLodgedDeclarationPage when changing to Simplified" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result = userAnswers
              .set(ProcedureTypePage, Normal).success.value
              .set(PreLodgeDeclarationPage, true).success.value
              .set(ProcedureTypePage, Simplified).success.value

            result.get(PreLodgeDeclarationPage) must not be defined
        }
      }
    }
  }
  // format: on
}
