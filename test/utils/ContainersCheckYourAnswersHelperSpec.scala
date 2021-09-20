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

package utils

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.containers.routes._
import models.CheckMode
import pages.addItems.containers.ContainerNumberPage
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

class ContainersCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper {

  "ContainersCheckYourAnswersHelper" - {

    "containerRow" - {

      val containerNumber: String = "CONTAINER NUMBER"

      "return None" - {
        "ContainerNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new ContainersCheckYourAnswersHelper(answers)
          val result = helper.containerRow(itemIndex, containerIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ContainerNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ContainerNumberPage(itemIndex, containerIndex))(containerNumber)

          val helper = new ContainersCheckYourAnswersHelper(answers)
          val result = helper.containerRow(itemIndex, containerIndex)

          val label = lit"$containerNumber"

          result mustBe Some(
            Row(
              key = Key(label),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = ContainerNumberController.onPageLoad(lrn, itemIndex, containerIndex, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-container-number-${itemIndex.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = ConfirmRemoveContainerController.onPageLoad(lrn, itemIndex, containerIndex, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"remove-container-number-${itemIndex.display}")
                )
              )
            )
          )
        }
      }
    }
  }
}
