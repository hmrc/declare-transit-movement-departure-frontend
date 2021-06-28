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
import org.scalacheck.Gen
import pages.addItems.CommodityCodePage
import pages._

class ItemDetailsSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper {

  val itemDetailsUa = emptyUserAnswers
    .unsafeSetVal(ItemDescriptionPage(index))("itemDescription")
    .unsafeSetVal(ItemTotalGrossMassPage(index))("123")
    .unsafeSetVal(AddTotalNetMassPage(index))(true)
    .unsafeSetVal(TotalNetMassPage(index))("123")
    .unsafeSetVal(IsCommodityCodeKnownPage(index))(true)
    .unsafeSetVal(CommodityCodePage(index))("commodityCode")

  "can be parsed from UserAnswers" - {

    "when all details for section have been answered" in {

      val expectedResult = ItemDetails("itemDescription", "123", Some("123"), Some("commodityCode"))

      val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(itemDetailsUa)

      result.right.value mustEqual expectedResult
    }

    "when add total net mass is false" in {

      val userAnswers = itemDetailsUa.unsafeSetVal(AddTotalNetMassPage(index))(false)

      val expectedResult = ItemDetails("itemDescription", "123", None, Some("commodityCode"))

      val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(userAnswers)

      result.right.value mustEqual expectedResult
    }

    "when is commodity code known is false" in {

      val userAnswers = itemDetailsUa.unsafeSetVal(IsCommodityCodeKnownPage(index))(false)

      val expectedResult = ItemDetails("itemDescription", "123", Some("123"), None)

      val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(userAnswers)

      result.right.value mustEqual expectedResult
    }

  }

  "cannot be parsed from UserAnswers" - {

    val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
      ItemDescriptionPage(index),
      ItemTotalGrossMassPage(index),
      AddTotalNetMassPage(index),
      IsCommodityCodeKnownPage(index)
    )

    "when a mandatory answer is missing" in {

      forAll(mandatoryPages) {
        mandatoryPage =>
          val userAnswers = itemDetailsUa.unsafeRemove(mandatoryPage)

          val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(userAnswers)

          result.left.value.page mustBe mandatoryPage
      }
    }

    "when add total net mass is true but total net mass is missing" in {

      val userAnswers = itemDetailsUa
        .unsafeSetVal(AddTotalNetMassPage(index))(true)
        .unsafeRemove(TotalNetMassPage(index))

      val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(userAnswers)

      result.left.value.page mustEqual TotalNetMassPage(index)
    }

    "when is commodity code known is true but commodity code is missing" in {

      val userAnswers = itemDetailsUa
        .unsafeSetVal(IsCommodityCodeKnownPage(index))(true)
        .unsafeRemove(CommodityCodePage(index))

      val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(userAnswers)

      result.left.value.page mustEqual CommodityCodePage(index)
    }
  }
}
