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

import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.{AddAdministrativeReferencePage, AddExtraInformationPage, PreviousReferencePage, ReferenceTypePage}
import pages.behaviours.PageBehaviours

class AddAdministrativeReferencePageSpec extends PageBehaviours with ScalaCheckPropertyChecks {

  private val index          = Index(0)
  private val referenceIndex = Index(0)

  "AddAdministrativeReferencePage" - {

    beRetrievable[Boolean](AddAdministrativeReferencePage(index))

    beSettable[Boolean](addItems.AddAdministrativeReferencePage(index))

    beRemovable[Boolean](addItems.AddAdministrativeReferencePage(index))

    "cleanup" - {

      "must clean up the previous references pages on selecting option 'No' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val result = answers
              .set(AddAdministrativeReferencePage(index), true)
              .success
              .value
              .set(ReferenceTypePage(index, referenceIndex), "test")
              .success
              .value
              .set(PreviousReferencePage(index, referenceIndex), "test")
              .success
              .value
              .set(AddExtraInformationPage(index, referenceIndex), false)
              .success
              .value
              .set(AddAdministrativeReferencePage(index), false)
              .success
              .value

            result.get(ReferenceTypePage(index, index)) must not be defined
            result.get(AddExtraInformationPage(index, index)) must not be defined
            result.get(PreviousReferencePage(index, index)) must not be defined
        }
      }

      "must keep the previous references pages on selecting option 'Yes' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val result = answers
              .set(PreviousReferencePage(index, referenceIndex), "test")
              .success
              .value
              .set(AddAdministrativeReferencePage(index), true)
              .success
              .value

            result.get(PreviousReferencePage(index, index)) mustBe defined
        }
      }

    }
  }
}
