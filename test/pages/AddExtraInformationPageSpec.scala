/*
 * Copyright 2022 HM Revenue & Customs
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
import pages.addItems.{AddExtraInformationPage, ExtraInformationPage}
import pages.behaviours.PageBehaviours

class AddExtraInformationPageSpec extends PageBehaviours with ScalaCheckPropertyChecks {

  val itemIndex: Index              = Index(0)
  val referenceIndex: Index         = Index(0)
  val page: AddExtraInformationPage = AddExtraInformationPage(itemIndex, referenceIndex)

  "AddExtraInformationPage" - {

    beRetrievable[Boolean](page)

    beSettable[Boolean](page)

    beRemovable[Boolean](page)

    "cleanup" - {

      "must clean up the extra information page on selecting option 'No' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val result = answers
              .set(AddExtraInformationPage(itemIndex, referenceIndex), true)
              .success
              .value
              .set(ExtraInformationPage(itemIndex, referenceIndex), "test")
              .success
              .value
              .set(AddExtraInformationPage(itemIndex, referenceIndex), false)
              .success
              .value

            result.get(ExtraInformationPage(itemIndex, referenceIndex)) must not be defined
        }
      }

      "must keep extra information page on selecting option 'No' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val result = answers
              .set(AddExtraInformationPage(itemIndex, referenceIndex), false)
              .success
              .value
              .set(ExtraInformationPage(itemIndex, referenceIndex), "test")
              .success
              .value
              .set(AddExtraInformationPage(itemIndex, referenceIndex), true)
              .success
              .value

            result.get(ExtraInformationPage(itemIndex, referenceIndex)) mustBe defined
        }
      }

    }
  }
}
