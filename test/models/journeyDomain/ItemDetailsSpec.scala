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
import generators.JourneyModelGenerators
import models.journeyDomain.ItemDetailsSpec.setItemDetailsUserAnswers
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.{Index, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.addItems.CommodityCodePage
import pages._

class ItemDetailsSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "ItemDetails" - {

    val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
      ItemDescriptionPage(index),
      ItemTotalGrossMassPage(index)
    )

    "can be parsed from UserAnswers" - {
      "when all details for section have been answered" in {
        forAll(arbitrary[ItemDetails], arbitrary[UserAnswers]) {
          case (itemDetails, userAnswers) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
            val result             = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.right.value mustEqual itemDetails
        }
      }

      "when addTotalNetMass is false and totalNetMass has been answered, totNetMass should be none" in {
        val genItemDetailsNetMassSet =
          arbitrary[ItemDetails].map(_.copy(totalNetMass = Some("totalNetMassValue")))

        forAll(arbitrary[UserAnswers], genItemDetailsNetMassSet) {
          case (userAnswers, itemDetails) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
              .unsafeSetVal(AddTotalNetMassPage(index))(false)
              .unsafeSetVal(TotalNetMassPage(index))(itemDetails.totalNetMass.value)

            val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.right.value.totalNetMass mustEqual None
        }
      }

      "when IsCommodityCodeKnownPage is false and CommodityCodePage has been answered, CommodityCodePage should be none" in {
        val genItemDetailsNetMassSet =
          arbitrary[ItemDetails].map(_.copy(commodityCode = Some("totalNetMassValue")))

        forAll(arbitrary[UserAnswers], genItemDetailsNetMassSet) {
          case (userAnswers, itemDetails) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
              .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
              .unsafeSetVal(CommodityCodePage(index))(itemDetails.commodityCode.value)

            val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.right.value.commodityCode mustEqual None
        }
      }

    }

    "cannot be parsed from UserAnswers" - {
      "when a mandatory answer is missing" in {
        forAll(arbitrary[UserAnswers], mandatoryPages) {
          case (userAnswers, mandatoryPage) =>
            val updatedUserAnswers = userAnswers.remove(mandatoryPage).success.value
            val result             = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.isLeft mustEqual true
        }
      }

      "when addTotalNetMass is true but totalNetMass is missing " in {
        val genItemDetailsNetMassSet =
          arbitrary[ItemDetails].map(_.copy(totalNetMass = None))

        forAll(arbitrary[UserAnswers], genItemDetailsNetMassSet) {
          case (userAnswers, itemDetails) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
              .unsafeSetVal(AddTotalNetMassPage(index))(true)
              .unsafeRemove(TotalNetMassPage(index))

            val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.left.value.page mustEqual TotalNetMassPage(index)
        }
      }

      "when IsCommodityCodeKnownPage is true but CommodityCodePage is missing " in {
        val genItemDetailsNetMassSet =
          arbitrary[ItemDetails].map(_.copy(commodityCode = None))

        forAll(arbitrary[UserAnswers], genItemDetailsNetMassSet) {
          case (userAnswers, itemDetails) =>
            val updatedUserAnswers = setItemDetailsUserAnswers(itemDetails, index)(userAnswers)
              .unsafeSetVal(IsCommodityCodeKnownPage(index))(true)
              .unsafeRemove(CommodityCodePage(index))

            val result = UserAnswersReader[ItemDetails](ItemDetails.itemDetailsReader(index)).run(updatedUserAnswers)

            result.left.value.page mustEqual CommodityCodePage(index)
        }
      }
    }
  }

}

object ItemDetailsSpec {
  //format off

  def setItemDetailsUserAnswers(itemDetails: ItemDetails, index: Index)(startUserAnswers: UserAnswers): UserAnswers = {
    val userAnswers =
      startUserAnswers
        .unsafeSetVal(ItemTotalGrossMassPage(index))(itemDetails.totalGrossMass)
        .unsafeSetVal(ItemDescriptionPage(index))(itemDetails.itemDescription)

    val totalNetMass = itemDetails.totalNetMass match {
      case Some(value) =>
        userAnswers
          .unsafeSetVal(AddTotalNetMassPage(index))(true)
          .unsafeSetVal(TotalNetMassPage(index))(value)

      case _ =>
        userAnswers
          .unsafeSetVal(AddTotalNetMassPage(index))(false)
    }

    val commodityCode = itemDetails.commodityCode match {
      case Some(value) =>
        totalNetMass
          .unsafeSetVal(IsCommodityCodeKnownPage(index))(true)
          .unsafeSetVal(CommodityCodePage(index))(value)
      case _ =>
        totalNetMass
          .unsafeSetVal(IsCommodityCodeKnownPage(index))(false)
    }

    commodityCode

    //format off
  }
}
