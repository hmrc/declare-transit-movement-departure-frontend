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
import models.ProcedureType.{Normal, Simplified}
import models.domain.Address
import models.journeyDomain.{UserAnswersReader, _}
import pages._

sealed trait PrincipalTraderDetails
final case class PrincipalTraderPersonalInfo(name: String, address: Address) extends PrincipalTraderDetails
final case class PrincipalTraderEoriInfo(eori: EoriNumber) extends PrincipalTraderDetails

object PrincipalTraderDetails {
  def apply(eori: EoriNumber): PrincipalTraderDetails               = PrincipalTraderEoriInfo(eori)
  def apply(name: String, address: Address): PrincipalTraderDetails = PrincipalTraderPersonalInfo(name, address)

  implicit val principalTraderDetails: UserAnswersReader[PrincipalTraderDetails] = {
    val simplified = ProcedureTypePage.reader
      .filter(_ == Simplified)
      .productR(
        WhatIsPrincipalEoriPage.reader
          .map(EoriNumber(_))
          .map(PrincipalTraderDetails(_))
      )

    val normal = ProcedureTypePage.reader
      .filter(_ == Normal)
      .productR {
        IsPrincipalEoriKnownPage.reader
          .flatMap {
            case true =>
              WhatIsPrincipalEoriPage.reader
                .map(EoriNumber(_))
                .map(PrincipalTraderDetails(_))
            case false =>
              (
                PrincipalNamePage.reader,
                PrincipalAddressPage.reader
              ).tupled.map {
                case (name, principalAddress) =>
                  val address = Address.prismAddressToPrincipalAddress(principalAddress)
                  PrincipalTraderDetails(name, address)
              }
          }
      }

    normal orElse simplified
  }

}
