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
import models.{CommonAddress, EoriNumber}
import models.ProcedureType.{Normal, Simplified}
import models.journeyDomain.{UserAnswersReader, _}
import pages._

sealed trait PrincipalTraderDetails

final case class PrincipalTraderPersonalInfo(name: String, address: CommonAddress) extends PrincipalTraderDetails

final case class PrincipalTraderEoriInfo(eori: EoriNumber) extends PrincipalTraderDetails

final case class PrincipalTraderEoriPersonalInfo(eori: EoriNumber, name: String, address: CommonAddress) extends PrincipalTraderDetails

object PrincipalTraderDetails {
  def apply(eori: EoriNumber): PrincipalTraderDetails = PrincipalTraderEoriInfo(eori)

  def apply(name: String, address: CommonAddress): PrincipalTraderDetails = PrincipalTraderPersonalInfo(name, address)

  def apply(eori: EoriNumber, name: String, address: CommonAddress): PrincipalTraderDetails = PrincipalTraderEoriPersonalInfo(eori, name, address)

  implicit val principalTraderDetails: UserAnswersReader[PrincipalTraderDetails] = {

    val readEori = WhatIsPrincipalEoriPage.reader
      .map(EoriNumber(_))
      .map(PrincipalTraderDetails(_))

    val readNameAndAddress =
      (
        PrincipalNamePage.reader,
        PrincipalAddressPage.reader
      ).tupled.map {
        case (name, address) => PrincipalTraderDetails(name, address)
      }

    val readAllDetails: UserAnswersReader[PrincipalTraderDetails] =
      (
        WhatIsPrincipalEoriPage.reader,
        PrincipalNamePage.reader,
        PrincipalAddressPage.reader
      ).tupled.map {
        case (eori, name, address) => PrincipalTraderEoriPersonalInfo(EoriNumber(eori), name, address)
      }

    ProcedureTypePage.reader.flatMap {
      case Normal =>
        (IsPrincipalEoriKnownPage.reader, WhatIsPrincipalEoriPage.optionalReader).tupled.flatMap {
          case (true, Some(principleEori)) if principleEori.toUpperCase.startsWith("GB") => readEori
          case (true, _)                                                                 => readAllDetails
          case (false, _)                                                                => readNameAndAddress
        }
      case Simplified => readEori
    }
  }
}
