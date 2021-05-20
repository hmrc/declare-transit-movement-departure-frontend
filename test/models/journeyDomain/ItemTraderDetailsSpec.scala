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
import models.domain.Address
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.PackagesSpec.UserAnswersSpecHelperOps
import models.{CommonAddress, EoriNumber, Index, UserAnswers}
import org.scalatest.TryValues
import pages._
import pages.addItems.traderDetails._

class ItemTraderDetailsSpec extends SpecBase with GeneratorSpec with TryValues with JourneyModelGenerators {

  "ItemTraderDetail can be parsed from UserAnswers" - {

    "when there is no consignor eoriNumber but only name and address" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[CommonAddress]) {
        case (baseUserAnswers, name, address) =>
          val userAnswers = baseUserAnswers
            .unsafeSetVal(AddConsignorPage)(false)
            .unsafeSetVal(AddConsigneePage)(true)
            .unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(false)
            .unsafeSetVal(TraderDetailsConsignorNamePage(index))(name)
            .unsafeSetVal(CommonAddItemsAddressPage(index, "traderDetailsConsignorAddress"))(address)

          val expectedAddress: Address = Address.prismAddressToCommonAddress(address)
          val result                   = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          result.consignor.value mustEqual RequiredDetails(name, expectedAddress, None)
      }
    }

    "when there is consignor name, address and eori" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[CommonAddress], arb[EoriNumber]) {
        case (baseUserAnswers, name, address, eori @ EoriNumber(eoriNumber1)) =>
          val userAnswers = baseUserAnswers
            .unsafeSetVal(AddConsignorPage)(false)
            .unsafeSetVal(AddConsigneePage)(true)
            .unsafeSetVal(TraderDetailsConsignorNamePage(index))(name)
            .unsafeSetVal(CommonAddItemsAddressPage(index, "traderDetailsConsignorAddress"))(address)
            .unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(true)
            .unsafeSetVal(TraderDetailsConsignorEoriNumberPage(index))(eoriNumber1)

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          val expectedAddress: Address = Address.prismAddressToCommonAddress(address)

          result.consignor.value mustEqual RequiredDetails(name, expectedAddress, Some(eori))
      }
    }

    "when there is no consignee eori only name and address" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[CommonAddress]) {
        case (baseUserAnswers, name, address) =>
          val userAnswers = baseUserAnswers
            .unsafeSetVal(AddConsignorPage)(true)
            .unsafeSetVal(AddConsigneePage)(false)
            .unsafeSetVal(TraderDetailsConsigneeNamePage(index))(name)
            .unsafeSetVal(CommonAddItemsAddressPage(index, "traderDetailsConsigneeAddress"))(address)
            .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(false)

          val result                   = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value
          val expectedAddress: Address = Address.prismAddressToCommonAddress(address)

          result.consignee.value mustEqual RequiredDetails(name, expectedAddress, None)

      }
    }

    "when there is consignee name, address and eori" in {
      forAll(arb[UserAnswers], stringsWithMaxLength(stringMaxLength), arb[CommonAddress], arb[EoriNumber]) {
        case (baseUserAnswers, name, address, eori @ EoriNumber(eoriNumber1)) =>
          val userAnswers = baseUserAnswers
            .unsafeSetVal(AddConsignorPage)(true)
            .unsafeSetVal(AddConsigneePage)(false)
            .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(true)
            .unsafeSetVal(TraderDetailsConsigneeEoriNumberPage(index))(eoriNumber1)
            .unsafeSetVal(TraderDetailsConsigneeNamePage(index))(name)
            .unsafeSetVal(CommonAddItemsAddressPage(index, "traderDetailsConsigneeAddress"))(address)

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          val expectedAddress: Address = Address.prismAddressToCommonAddress(address)

          result.consignee.value mustEqual RequiredDetails(name, expectedAddress, Some(eori))
      }
    }

    "when header level consignor has already been answered" in {
      forAll(arb[UserAnswers], arb[EoriNumber]) {
        case (baseUserAnswers, _ @EoriNumber(eoriNumber1)) =>
          val userAnswers = baseUserAnswers
            .unsafeSetVal(AddConsignorPage)(true)
            .unsafeSetVal(AddConsigneePage)(true)
            .unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(true)
            .unsafeSetVal(TraderDetailsConsignorEoriNumberPage(index))(eoriNumber1)

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          result.consignor must be(None)

      }
    }

    "when header level consignee has already been answered" in {
      forAll(arb[UserAnswers], arb[EoriNumber]) {
        case (baseUserAnswers, eori1 @ EoriNumber(eoriNumber1)) =>
          val userAnswers = baseUserAnswers
            .unsafeSetVal(AddConsignorPage)(true)
            .unsafeSetVal(AddConsigneePage)(true)
            .unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(true)
            .unsafeSetVal(TraderDetailsConsigneeEoriNumberPage(index))(eoriNumber1)

          val result = UserAnswersReader[ItemTraderDetails](ItemTraderDetails.userAnswersParser(index)).run(userAnswers).right.value

          result.consignee must be(None)
      }
    }
  }
}

object ItemTraderDetailsSpec extends UserAnswersSpecHelper {

  def setItemTraderDetails(itemTraderDetails: ItemTraderDetails, index: Index)(startUserAnswers: UserAnswers): UserAnswers =
    startUserAnswers
    // Set Consignor
      .unsafeSetPFn(TraderDetailsConsignorEoriKnownPage(index))(itemTraderDetails.consignor)({
        case Some(RequiredDetails(_, _, Some(_))) => true
        case Some(_)                              => false
      })
      .unsafeSetPFn(TraderDetailsConsignorEoriNumberPage(index))(itemTraderDetails.consignor)({
        case Some(RequiredDetails(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(TraderDetailsConsignorNamePage(index))(itemTraderDetails.consignor)({
        case Some(RequiredDetails(name, _, _)) => name
      })
      .unsafeSetPFn(CommonAddItemsAddressPage(index, "traderDetailsConsignorAddress"))(itemTraderDetails.consignor)({
        case Some(RequiredDetails(_, address, _)) => Address.prismAddressToCommonAddress.getOption(address).get
      })
      // Set Consignee
      .unsafeSetPFn(TraderDetailsConsigneeEoriKnownPage(index))(itemTraderDetails.consignee)({
        case Some(RequiredDetails(_, _, Some(_))) => true
        case Some(_)                              => false
      })
      .unsafeSetPFn(TraderDetailsConsigneeEoriNumberPage(index))(itemTraderDetails.consignee)({
        case Some(RequiredDetails(_, _, Some(eori))) => eori.value
      })
      .unsafeSetPFn(TraderDetailsConsigneeNamePage(index))(itemTraderDetails.consignee)({
        case Some(RequiredDetails(name, _, _)) => name
      })
      .unsafeSetPFn(CommonAddItemsAddressPage(index, "traderDetailsConsigneeAddress"))(itemTraderDetails.consignee)({
        case Some(RequiredDetails(_, address, _)) => Address.prismAddressToCommonAddress.getOption(address).get
      })

}
