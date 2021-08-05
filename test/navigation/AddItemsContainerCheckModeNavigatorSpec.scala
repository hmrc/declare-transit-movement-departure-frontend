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

package navigation

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.containers.{routes => containerRoutes}
import generators.Generators
import models.{CheckMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.containers._

class AddItemsContainerCheckModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {
  // format: off
  val navigator = new AddItemsContainerNavigator

  "Add Items section" - {

    "containerNumber" - {
      "must go from containerNumber to addAnotherContainer" in {
        forAll(arbitrary[UserAnswers], arbitrary[String]) {
          (answers, containerNumber) =>
            val updatedAnswers = answers
              .set(ContainerNumberPage(itemIndex, containerIndex), containerNumber).success.value
            navigator
              .nextPage(ContainerNumberPage(itemIndex, containerIndex), CheckMode, updatedAnswers)
              .mustBe(containerRoutes.AddAnotherContainerController.onPageLoad(updatedAnswers.lrn, itemIndex, CheckMode))
        }
      }

    }


    }
  // format: on
}
