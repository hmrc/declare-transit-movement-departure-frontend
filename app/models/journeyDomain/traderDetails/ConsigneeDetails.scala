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

package models.journeyDomain.traderDetails

import cats.implicits._
import models.EoriNumber
import models.domain.Address
import models.journeyDomain.{UserAnswersReader, _}
import pages._

case class ConsigneeDetails(name: String, address: Address, eori: Option[EoriNumber])

object ConsigneeDetails {

  implicit val consigneeDetails: UserAnswersReader[ConsigneeDetails] = {

    val readConsigneeEoriPage =
      IsConsigneeEoriKnownPage
        .filterOptionalDependent(identity)(WhatIsConsigneeEoriPage.reader.map(EoriNumber(_)))

    (
      readConsigneeEoriPage,
      ConsigneeNamePage.reader,
      CommonAddressPage("consigneeAddress").reader
    ).tupled
      .map {
        case (eori, name, consigneeAddress) =>
          val address = Address.prismAddressToCommonAddress(consigneeAddress)
          ConsigneeDetails(name, address, eori)
      }
  }
}
