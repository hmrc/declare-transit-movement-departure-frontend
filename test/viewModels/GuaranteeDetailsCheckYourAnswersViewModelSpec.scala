/*
 * Copyright 2020 HM Revenue & Customs
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

package viewModels

import base.SpecBase
import generators.Generators
import models.GuaranteeType.GuaranteeWaiver
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import pages.{AccessCodePage, DefaultAmountPage, LiabilityAmountPage, OtherReferencePage}
import uk.gov.hmrc.viewmodels.Text.{Literal, Message}

class GuaranteeDetailsCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "GuaranteeDetailsCheckYourAnswersViewModel" - {

    "display Guarantee Type when selected" in {
      val updatedAnswers = emptyUserAnswers.set(GuaranteeTypePage, GuaranteeWaiver).success.value
      val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      val message = data.sections.head.rows.head.value.content.asInstanceOf[Message]
      message.key mustBe "guaranteeType.GuaranteeWaiver"
    }

    "display Guarantee Reference number when selected" in {

      val updatedAnswers = emptyUserAnswers.set(GuaranteeReferencePage, "test").success.value
      val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      data.sections.head.rows.head.value.content mustEqual Literal("test")
    }

    "display Other Reference when selected" in {

      val updatedAnswers = emptyUserAnswers.set(OtherReferencePage, "test").success.value
      val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      data.sections.head.rows.head.value.content mustEqual Literal("test")
    }

    "display Liability Amount" - {
      "and amount as 10 when selected" in {
        val updatedAnswers = emptyUserAnswers.set(LiabilityAmountPage, "10.00").success.value
        val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers)

        data.sections.head.sectionTitle must not be defined
        data.sections.length mustEqual 1
        data.sections.head.rows.length mustEqual 1
        data.sections.head.rows.head.value.content mustEqual Literal("10.00")
      }
    }
    "display Default Liability Amount when selected" in {

      val updatedAnswers = emptyUserAnswers
        .remove(LiabilityAmountPage)
        .success
        .value
        .set(DefaultAmountPage, true)
        .success
        .value
      val data = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      data.sections.head.rows.head.value.content mustEqual Literal("Yes")

    }
    "display Default Liability Amount when no is selected" in {

      val updatedAnswers = emptyUserAnswers
        .remove(LiabilityAmountPage)
        .success
        .value
        .set(DefaultAmountPage, false)
        .success
        .value
      val data = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      data.sections.head.rows.head.value.content mustEqual Literal("No")

    }
    "display Access Code when selected" in {

      val updatedAnswers = emptyUserAnswers.set(AccessCodePage, "a1b2").success.value
      val data           = GuaranteeDetailsCheckYourAnswersViewModel(updatedAnswers)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 1
      data.sections.head.rows.head.value.content mustEqual Literal("••••")
    }
  }
}
