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

import generators.Generators
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class LiabilityAmountPageSpec extends PageBehaviours with Generators {

  implicit lazy val arbitraryNonEmptyString: Arbitrary[String] = Arbitrary(nonEmptyString)

  "LiabilityAmountPage" - {

    val index = Index(0)

    beRetrievable[String](LiabilityAmountPage(index))

    beSettable[String](LiabilityAmountPage(index))

    beRemovable[String](LiabilityAmountPage(index))

    "cleanup" - {

      "must remove DefaultAmountPage when answer is non empty" in {

        forAll(arbitrary[UserAnswers], nonEmptyString, arbitrary[Boolean]) {
          (userAnswers, liabilityAmount, useDefaultAmount) =>
            val result = userAnswers
              .set(DefaultAmountPage(index), useDefaultAmount)
              .success
              .value
              .set(LiabilityAmountPage(index), liabilityAmount)
              .success
              .value

            result.get(DefaultAmountPage(index)) must not be defined
        }
      }

      "must remove LiabilityAmountPage when answer is empty" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result = userAnswers
              .set(LiabilityAmountPage(index), "")
              .success
              .value

            result.get(DefaultAmountPage(index)) must not be defined
        }
      }
    }
  }

}
