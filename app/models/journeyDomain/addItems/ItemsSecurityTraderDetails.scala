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

package models.journeyDomain.addItems

import cats.implicits._
import models.Index
import models.journeyDomain.addItems.SecurityTraderDetails._
import models.journeyDomain.{UserAnswersReader, _}
import pages.AddSecurityDetailsPage
import pages.addItems.securityDetails.{AddDangerousGoodsCodePage, CommercialReferenceNumberPage, DangerousGoodsCodePage, TransportChargesPage}
import pages.safetyAndSecurity._

final case class ItemsSecurityTraderDetails(
                                             methodOfPayment: Option[String],
                                             commercialReferenceNumber: Option[String],
                                             dangerousGoodsCode: Option[String],
                                             consignor: Option[SecurityTraderDetails],
                                             consignee: Option[SecurityTraderDetails]
                                           )

object ItemsSecurityTraderDetails {

  def parser(index: Index): UserAnswersReader[Option[ItemsSecurityTraderDetails]] =
    AddSecurityDetailsPage.filterOptionalDependent(identity) {
      (
        methodOfPaymentPage(index),
        commercialReferenceNumberPage(index),
        dangerousGoodsCodePage(index),
        consignorDetails(index),
        consigneeDetails(index)
        ).tupled.map((ItemsSecurityTraderDetails.apply _).tupled)
    }

  private def methodOfPaymentPage(index: Index): UserAnswersReader[Option[String]] =
    AddTransportChargesPaymentMethodPage.filterOptionalDependent(_ == false) {
      TransportChargesPage(index).reader
    }

  private def commercialReferenceNumberPage(index: Index): UserAnswersReader[Option[String]] =
    AddCommercialReferenceNumberAllItemsPage.optionalReader
      .flatMap {
        case Some(true) => none[String].pure[UserAnswersReader]
        case _ => CommercialReferenceNumberPage(index).reader.map(Some(_))
      }

  private def dangerousGoodsCodePage(index: Index): UserAnswersReader[Option[String]] =
    AddDangerousGoodsCodePage(index).filterOptionalDependent(identity) {
      DangerousGoodsCodePage(index).reader
    }
}
