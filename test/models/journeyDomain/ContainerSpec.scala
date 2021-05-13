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
import generators.JourneyModelGenerators
import models.journeyDomain.ContainerSpec.setContainerUserAnswers
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.addItems.containers.ContainerNumberPage

class ContainerSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {
  "Container" - {
    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[Container], arbitrary[UserAnswers]) {
          case (container, userAnswers) =>
            val updatedUserAnswers = setContainerUserAnswers(container, index, referenceIndex)(userAnswers)
            val result             = UserAnswersReader[Container](Container.containerReader(index, referenceIndex)).run(updatedUserAnswers)

            result.right.value mustEqual container
        }
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val updatedUserAnswers = userAnswers.remove(ContainerNumberPage(index, referenceIndex)).success.value
            val result =
              UserAnswersReader[Container](Container.containerReader(index, referenceIndex)).run(updatedUserAnswers).left.value

            result.page mustBe ContainerNumberPage(index, referenceIndex)
        }
      }
    }
  }
}

object ContainerSpec extends UserAnswersSpecHelper {

  def setContainers(containers: Seq[Container], index: Index)(userAnswers: UserAnswers): UserAnswers =
    userAnswers.unsafeSetSeqIndex(ContainerNumberPage(index, _))(containers.map(_.containerNumber))

  def setContainerUserAnswers(container: Container, index: Index, referenceIndex: Index)(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .unsafeSetVal(ContainerNumberPage(index, referenceIndex))(container.containerNumber)

}
