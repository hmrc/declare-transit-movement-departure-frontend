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

package models.journeyDomain

import cats.implicits._
import models.GuaranteeType
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}

sealed trait GuaranteeDetails

object GuaranteeDetails {

  implicit val parseGuaranteeDetails: UserAnswersReader[GuaranteeDetails] =
    UserAnswersReader[GuaranteeReference].widen[GuaranteeDetails] orElse
      UserAnswersReader[GuaranteeOther].widen[GuaranteeDetails]

  final case class GuaranteeReference(
    guaranteeType: GuaranteeType,
    guaranteeReferenceNumber: String,
    liabilityAmount: String,
    accessCode: String
  ) extends GuaranteeDetails

  object GuaranteeReference {

    private val defaultLiability = "10000"

    private val liabilityAmount: UserAnswersReader[String] = DefaultAmountPage.optionalReader.flatMap {
      case Some(defaultAmountPage) =>
        if (defaultAmountPage) defaultLiability.pure[UserAnswersReader] else LiabilityAmountPage.reader
      case None => LiabilityAmountPage.reader
    }

    implicit val parseGuaranteeReference: UserAnswersReader[GuaranteeReference] =
      (
        GuaranteeTypePage.reader,
        GuaranteeReferencePage.reader,
        liabilityAmount,
        AccessCodePage.reader
      ).tupled.map((GuaranteeReference.apply _).tupled)
  }

  final case class GuaranteeOther(
    guaranteeType: GuaranteeType,
    otherReference: String,
    liabilityAmount: String
  ) extends GuaranteeDetails

  object GuaranteeOther {

    implicit val parseGuaranteeOther: UserAnswersReader[GuaranteeOther] =
      (
        GuaranteeTypePage.reader,
        OtherReferencePage.reader,
        OtherReferenceLiabilityAmountPage.reader
      ).tupled.map((GuaranteeOther.apply _).tupled)
  }

}
