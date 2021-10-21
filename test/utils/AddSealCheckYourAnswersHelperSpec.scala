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
import controllers.goodsSummary.routes._
import models.domain.SealDomain
import models.{CheckMode, Mode}
import pages.SealIdDetailsPage
import queries.SealsQuery
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.{Html, MessageInterpolators}

class AddSealCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper {

  val mode: Mode = CheckMode

  "AddSealCheckYourAnswersHelper" - {

    "sealRow" - {

      val sealDomain: SealDomain = SealDomain("1")

      "return None" - {
        "SealIdDetailsPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddSealCheckYourAnswersHelper(answers, mode)
          val result = helper.sealRow(sealIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SealIdDetailsPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SealIdDetailsPage(sealIndex))(sealDomain)

          val helper = new AddSealCheckYourAnswersHelper(answers, mode)
          val result = helper.sealRow(sealIndex)

          val label = lit"${sealDomain.numberOrMark}"

          result mustBe Some(
            Row(
              key = Key(label),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SealIdDetailsController.onPageLoad(lrn, sealIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-seal-${sealIndex.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = ConfirmRemoveSealController.onPageLoad(lrn, sealIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"remove-seal-${sealIndex.display}")
                )
              )
            )
          )
        }
      }
    }

    "sealsRow" - {

      "return None" - {
        "SealsQuery undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddSealCheckYourAnswersHelper(answers, mode)
          val result = helper.sealsRow()
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SealsQuery defined at index" - {

          "1 seal" in {

            val sealDomain: SealDomain = SealDomain("1")

            val answers = emptyUserAnswers.unsafeSetVal(SealsQuery())(Seq(sealDomain))

            val helper = new AddSealCheckYourAnswersHelper(answers, mode)
            val result = helper.sealsRow()

            val label = msg"sealIdDetails.singular.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(sealDomain.numberOrMark)),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = SealsInformationController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-seal")
                  )
                )
              )
            )
          }

          "multiple seals" in {

            val sealDomain1: SealDomain = SealDomain("1")
            val sealDomain2: SealDomain = SealDomain("2")

            val answers = emptyUserAnswers.unsafeSetVal(SealsQuery())(Seq(sealDomain1, sealDomain2))

            val helper = new AddSealCheckYourAnswersHelper(answers, mode)
            val result = helper.sealsRow()

            val label = msg"sealIdDetails.plural.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(s"${sealDomain1.numberOrMark}<br>${sealDomain2.numberOrMark}")),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = SealsInformationController.onPageLoad(lrn, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-seals")
                  )
                )
              )
            )
          }
        }
      }
    }
  }

}
