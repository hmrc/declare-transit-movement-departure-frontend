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

import cats.data.ReaderT
import cats.implicits._
import models.ProcedureType.{Normal, Simplified}
import models.domain.Address
import models.journeyDomain.{UserAnswersReader, _}
import models.{EoriNumber, UserAnswers}
import pages._

sealed trait PrincipalTraderDetails

final case class PrincipalTraderPersonalInfo(name: String, address: Address) extends PrincipalTraderDetails

final case class PrincipalTraderEoriInfo(eori: EoriNumber) extends PrincipalTraderDetails

final case class PrincipalTraderEoriPersonalInfo(eori: EoriNumber, name: String, address: Address) extends PrincipalTraderDetails

object PrincipalTraderDetails {
  def apply(eori: EoriNumber): PrincipalTraderDetails = PrincipalTraderEoriInfo(eori)

  def apply(name: String, address: Address): PrincipalTraderDetails = PrincipalTraderPersonalInfo(name, address)

  def apply(eori: EoriNumber, name: String, address: Address): PrincipalTraderDetails = PrincipalTraderEoriPersonalInfo(eori, name, address)

  implicit val principalTraderDetails: UserAnswersReader[PrincipalTraderDetails] = {

    val simplified: UserAnswersReader[PrincipalTraderDetails] = {
      ProcedureTypePage.filterMandatoryDependent(_ == Simplified) {
        WhatIsPrincipalEoriPage.reader
          .map(EoriNumber(_))
          .map(PrincipalTraderDetails(_))
      }
    }

    val normalEori: ReaderT[EitherType, UserAnswers, PrincipalTraderDetails] = {
      ProcedureTypePage.filterMandatoryDependent(_ == Normal) {
        IsPrincipalEoriKnownPage.filterMandatoryDependent(identity) {
          WhatIsPrincipalEoriPage.filterMandatoryDependent(_.startsWith("GB")) {
            WhatIsPrincipalEoriPage.reader
              .map(EoriNumber(_))
              .map(PrincipalTraderDetails(_))
          }
        }
      }
    }

    val normalEoriNameAndAddress: UserAnswersReader[PrincipalTraderDetails] = {
      ProcedureTypePage.filterMandatoryDependent(_ == Normal) {
        IsPrincipalEoriKnownPage.filterMandatoryDependent(identity) {
          WhatIsPrincipalEoriPage.filterMandatoryDependent(!_.startsWith("GB")) {
            (
              WhatIsPrincipalEoriPage.reader,
              PrincipalNamePage.reader,
              PrincipalAddressPage.reader
            ).tupled.map {
              case (eori, name, principalAddress) =>
                val address = Address.prismAddressToPrincipalAddress(principalAddress)
                PrincipalTraderEoriPersonalInfo(EoriNumber(eori), name, address)
            }
          }
        }
      }
    }

    val normalNameAddress: UserAnswersReader[PrincipalTraderDetails] = {
      ProcedureTypePage.filterMandatoryDependent(_ == Normal) {
        IsPrincipalEoriKnownPage.filterMandatoryDependent(_ == false) {
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
    }

    // TODO need to investigate the error handling here as it will only retrieve the last left (due to orElse)
    normalEori orElse normalNameAddress orElse simplified orElse normalEoriNameAndAddress
  }
}
