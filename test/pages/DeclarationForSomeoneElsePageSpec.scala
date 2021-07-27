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

import generators.UserAnswersGenerator
import models.{RepresentativeCapacity, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.generalInformation.{DeclarationForSomeoneElsePage, RepresentativeCapacityPage, RepresentativeNamePage}

class DeclarationForSomeoneElsePageSpec extends PageBehaviours with UserAnswersGenerator {

  "DeclarationForSomeoneElsePage" - {

    beRetrievable[Boolean](DeclarationForSomeoneElsePage)

    beSettable[Boolean](DeclarationForSomeoneElsePage)

    beRemovable[Boolean](DeclarationForSomeoneElsePage)

    "cleanup" - {
      "must remove RepresentativeNamePage and RepresentativeCapacityPage when there is a change of the answer to 'No'" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result = userAnswers
              .set(RepresentativeNamePage, "answer")
              .success
              .value
              .set(RepresentativeCapacityPage, RepresentativeCapacity.Direct)
              .success
              .value
              .set(DeclarationForSomeoneElsePage, false)
              .success
              .value

            result.get(RepresentativeCapacityPage) must not be defined
            result.get(RepresentativeNamePage) must not be defined
        }
      }

      "must keep RepresentativeNamePage and RepresentativeCapacityPage when there is a change of the answer to 'Yes'" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result = userAnswers
              .set(RepresentativeNamePage, "answer")
              .success
              .value
              .set(RepresentativeCapacityPage, RepresentativeCapacity.Direct)
              .success
              .value
              .set(DeclarationForSomeoneElsePage, true)
              .success
              .value

            result.get(RepresentativeCapacityPage) mustBe defined
            result.get(RepresentativeNamePage) mustBe defined
        }
      }
    }
  }
}
