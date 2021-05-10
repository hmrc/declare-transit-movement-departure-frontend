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

package services

import base.{GeneratorSpec, SpecBase}
import cats.data.NonEmptyList
import generators.{JourneyModelGenerators, ModelGenerators}
import models.GuaranteeType.{nonGuaranteeReferenceRoute, GuaranteeNotRequired, GuaranteeWaiver}
import models.journeyDomain.GuaranteeDetails.GuaranteeReference
import models.journeyDomain.{CurrencyCode, DefaultLiabilityAmount, OtherLiabilityAmount}
import models.messages.goodsitem.SpecialMentionGuaranteeLiabilityAmount
import org.scalacheck.Gen

class SpecialMentionGuaranteeLiabilityConversionSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with ModelGenerators {

  "SpecialMentionGuaranteeLiabilityConversion" - {

    "must return SpecialMentionGuaranteeLiabilityAmount with EUR formatting " +
      "when given a GuaranteeReference " +
      "with a GuaranteeType of 0, 1, 2, 4 or 9 " +
      "and a default liability amount" in {

      val guaranteeReference1 = GuaranteeReference(GuaranteeWaiver, "AB123", DefaultLiabilityAmount, "****")

      val guaranteeReferenceNonEmptyList = NonEmptyList(guaranteeReference1, List.empty)

      val expectedAdditionalInformationFormat =
        s"10000EURAB123"

      val expectedResult = SpecialMentionGuaranteeLiabilityAmount("CAL", expectedAdditionalInformationFormat)

      SpecialMentionGuaranteeLiabilityConversion(guaranteeReferenceNonEmptyList) mustBe Seq(expectedResult)
    }

    "must return SpecialMentionGuaranteeLiabilityAmount with GBP formatting " +
      "when given a GuaranteeReference " +
      "with a GuaranteeType of 0, 1, 2, 4 or 9 " +
      "and liability amount is not the default liability" in {

      val guaranteeReference1 = GuaranteeReference(GuaranteeWaiver, "AB123", OtherLiabilityAmount("1234", CurrencyCode.GBP), "****")

      val guaranteeReferenceNonEmptyList = NonEmptyList(guaranteeReference1, List.empty)

      val expectedAdditionalInformationFormat = "1234GBPAB123"

      val expectedResult = SpecialMentionGuaranteeLiabilityAmount("CAL", expectedAdditionalInformationFormat)

      SpecialMentionGuaranteeLiabilityConversion(guaranteeReferenceNonEmptyList) mustBe Seq(expectedResult)
    }

    "must return multiple SpecialMentionGuaranteeLiabilityAmount if there are multiple valid GuaranteeReference" in {
      val liabilityAmount           = OtherLiabilityAmount("1234", CurrencyCode.GBP)
      val guaranteeReference1       = GuaranteeReference(GuaranteeWaiver, "AB123", liabilityAmount, "****")
      val guaranteeReference2       = GuaranteeReference(GuaranteeWaiver, "AB123", DefaultLiabilityAmount, "****")
      val invalidGuaranteeReference = GuaranteeReference(GuaranteeNotRequired, "AB123", liabilityAmount, "****")

      val guaranteeReferenceNonEmptyList = NonEmptyList(guaranteeReference1, List(guaranteeReference2, invalidGuaranteeReference))

      val expectedAdditionalInformationFormat1 = "1234GBPAB123"
      val expectedAdditionalInformationFormat2 = s"10000EURAB123"

      val expectedResult1 = SpecialMentionGuaranteeLiabilityAmount("CAL", expectedAdditionalInformationFormat1)
      val expectedResult2 = SpecialMentionGuaranteeLiabilityAmount("CAL", expectedAdditionalInformationFormat2)

      SpecialMentionGuaranteeLiabilityConversion(guaranteeReferenceNonEmptyList) mustBe Seq(expectedResult1, expectedResult2)
    }

    "must return empty list when all GuaranteeReferences dont have a GuaranteeType of 0, 1, 2, 4 or 9" in {

      val genGuaranteeType = Gen.oneOf(nonGuaranteeReferenceRoute)

      forAll(nonEmptyListOf[GuaranteeReference](2), genGuaranteeType) {
        (guaranteeDetails, guaranteeType) =>
          val updatedGuaranteeReferenceHead: NonEmptyList[GuaranteeReference] = guaranteeDetails.map {
            _.copy(guaranteeType = guaranteeType)
          }

          SpecialMentionGuaranteeLiabilityConversion(updatedGuaranteeReferenceHead) mustBe Seq.empty
      }
    }
  }

}
