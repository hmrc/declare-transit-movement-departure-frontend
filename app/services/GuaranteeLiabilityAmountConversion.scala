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

import cats.data.NonEmptyList
import models.Convert
import models.GuaranteeType.guaranteeReferenceRoute
import models.journeyDomain.GuaranteeDetails
import models.journeyDomain.GuaranteeDetails.GuaranteeReference
import models.messages.goodsitem.SpecialMentionGuaranteeLiabilityAmount

private[services] object GuaranteeLiabilityAmountConversion extends Convert[NonEmptyList, Seq, GuaranteeDetails, SpecialMentionGuaranteeLiabilityAmount] {

  override def apply(guaranteeDetails: NonEmptyList[GuaranteeDetails]): Seq[SpecialMentionGuaranteeLiabilityAmount] =
    guaranteeDetails collect {
      case GuaranteeDetails.GuaranteeReference(guaranteeType, guaranteeReferenceNumber, liabilityAmount, _)
          if guaranteeReferenceRoute.contains(guaranteeType) =>
        val additionalInformationFormat = if (liabilityAmount == GuaranteeReference.defaultLiability) {
          s"${liabilityAmount}EUR$guaranteeReferenceNumber"
        } else {
          s"${liabilityAmount}GBP$guaranteeReferenceNumber"
        }

        SpecialMentionGuaranteeLiabilityAmount("CAL", additionalInformationFormat)
    }
}
