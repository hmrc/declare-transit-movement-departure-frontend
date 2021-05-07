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

import cats.data.NonEmptyList
import cats.implicits._
import derivable.DeriveNumberOfGuarantees
import models.{GuaranteeType, Index}
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}

sealed trait GuaranteeDetails

object GuaranteeDetails {

  implicit def parseListOfGuaranteeDetails: UserAnswersReader[NonEmptyList[GuaranteeDetails]] =
    DeriveNumberOfGuarantees.mandatoryNonEmptyListReader.flatMap {
      _.zipWithIndex.traverse[UserAnswersReader, GuaranteeDetails]({
        case (_, index) =>
          parseGuaranteeDetails(Index(index))
      })
    }

  def parseGuaranteeDetails(index: Index): UserAnswersReader[GuaranteeDetails] =
    UserAnswersReader[GuaranteeReference](GuaranteeReference.parseGuaranteeReference(index))
      .widen[GuaranteeDetails]
      .orElse(UserAnswersReader[GuaranteeOther](GuaranteeOther.parseGuaranteeOther(index)).widen[GuaranteeDetails])

  final case class GuaranteeReference(
    guaranteeType: GuaranteeType,
    guaranteeReferenceNumber: String,
    liabilityAmount: LiabilityAmount,
    accessCode: String
  ) extends GuaranteeDetails

  object GuaranteeReference {

    private val defaultLiability                = "10000"
    val currency_GB                             = "GB"
    val currency_EUR                            = "EUR"
    val defaultLiabilityAmount: LiabilityAmount = LiabilityAmount(defaultLiability, currency_EUR)

    private def liabilityAmount(index: Index): UserAnswersReader[LiabilityAmount] = DefaultAmountPage(index).optionalReader.flatMap {
      case Some(defaultAmountPage) =>
        if (defaultAmountPage) { defaultLiabilityAmount.pure[UserAnswersReader] } else {
          LiabilityAmountPage(index).reader.map(amount => LiabilityAmount(amount, currency_GB))
        }
      case None => LiabilityAmountPage(index).reader.map(amount => LiabilityAmount(amount, currency_GB))
    }

    def parseGuaranteeReference(index: Index): UserAnswersReader[GuaranteeReference] =
      (
        GuaranteeTypePage(index).reader,
        GuaranteeReferencePage(index).reader,
        liabilityAmount(index),
        AccessCodePage(index).reader
      ).tupled.map((GuaranteeReference.apply _).tupled)
  }

  final case class GuaranteeOther(
    guaranteeType: GuaranteeType,
    otherReference: String
  ) extends GuaranteeDetails

  object GuaranteeOther {

    def parseGuaranteeOther(index: Index): UserAnswersReader[GuaranteeOther] =
      (
        GuaranteeTypePage(index).reader,
        OtherReferencePage(index).reader
      ).tupled.map((GuaranteeOther.apply _).tupled)
  }

}
