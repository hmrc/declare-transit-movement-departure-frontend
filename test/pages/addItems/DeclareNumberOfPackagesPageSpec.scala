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

package pages.addItems

import base.SpecBase
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class DeclareNumberOfPackagesPageSpec extends PageBehaviours with SpecBase {

  "DeclareNumberOfPackagesPage" - {

    beRetrievable[Boolean](DeclareNumberOfPackagesPage(index, index))

    beSettable[Boolean](DeclareNumberOfPackagesPage(index, index))

    beRemovable[Boolean](DeclareNumberOfPackagesPage(index, index))

    "cleanup" - {

      "must cleanup pages if answer changes to false" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result =
              userAnswers
                .set(DeclareNumberOfPackagesPage(index, index), true)
                .success
                .value
                .set(HowManyPackagesPage(index, index), 123)
                .success
                .value
                .set(TotalPiecesPage(index, index), 123)
                .success
                .value
                .set(DeclareNumberOfPackagesPage(index, index), false)
                .success
                .value

            result.get(HowManyPackagesPage(index, index)) must not be defined
            result.get(TotalPiecesPage(index, index)) must not be defined
        }
      }
    }
  }

}
