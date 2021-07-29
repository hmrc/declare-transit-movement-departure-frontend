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
import pages.traderDetails._

sealed trait PrincipalTraderDetails

final case class PrincipalTraderPersonalInfo(name: String, address: CommonAddress, principalTirHolderId: Option[String]) extends PrincipalTraderDetails

final case class PrincipalTraderEoriInfo(eori: EoriNumber, principalTirHolderId: Option[String]) extends PrincipalTraderDetails

final case class PrincipalTraderEoriPersonalInfo(eori: EoriNumber, name: String, address: CommonAddress, principalTirHolderId: Option[String])
    extends PrincipalTraderDetails

object PrincipalTraderDetails {
  def apply(eori: EoriNumber, principalTirHolderId: Option[String]): PrincipalTraderDetails = PrincipalTraderEoriInfo(eori, principalTirHolderId)

  def apply(name: String, address: CommonAddress, principalTirHolderId: Option[String]): PrincipalTraderDetails =
    PrincipalTraderPersonalInfo(name, address, principalTirHolderId)

  def apply(eori: EoriNumber, name: String, address: CommonAddress, principalTirHolderId: Option[String]): PrincipalTraderDetails =
    PrincipalTraderEoriPersonalInfo(eori, name, address, principalTirHolderId)

  implicit val principalTraderDetails: UserAnswersReader[PrincipalTraderDetails] = {

    val readEoriWithTri = (WhatIsPrincipalEoriPage.reader, PrincipalTirHolderIdPage.optionalReader).tupled.map {
      case (eori, optionalPrincipalTirHolderId) => PrincipalTraderDetails(EoriNumber(eori), optionalPrincipalTirHolderId)
    }

    val readNameAndAddress =
      (
        PrincipalNamePage.reader,
        PrincipalAddressPage.reader,
        PrincipalTirHolderIdPage.optionalReader
      ).tupled.map {
        case (name, address, optionalPrincipalTirHolderId) =>
          PrincipalTraderDetails(name, address, optionalPrincipalTirHolderId)
      }

    val readAllDetails: UserAnswersReader[PrincipalTraderDetails] =
      (
        WhatIsPrincipalEoriPage.reader,
        PrincipalNamePage.reader,
        PrincipalAddressPage.reader,
        PrincipalTirHolderIdPage.optionalReader
      ).tupled.map {
        case (eori, name, address, optionalPrincipalTirHolderId) =>
          PrincipalTraderEoriPersonalInfo(EoriNumber(eori), name, address, optionalPrincipalTirHolderId)
      }

    ProcedureTypePage.reader.flatMap {
      case Normal =>
        (IsPrincipalEoriKnownPage.reader, WhatIsPrincipalEoriPage.optionalReader).tupled.flatMap {
          case (true, Some(principleEori)) if principleEori.toUpperCase.startsWith("GB") || principleEori.toUpperCase.startsWith("XI") => readEoriWithTri
          case (true, _)                                                                                                               => readAllDetails
          case (false, _)                                                                                                              => readNameAndAddress
        }
      case Simplified => readEoriWithTri
    }
  }
}
