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

import cats.data.Kleisli
import cats.implicits._
import models.journeyDomain._
import models.reference.{CountryCode, CustomsOffice}
import models.{CommonAddress, EoriNumber, Index}
import pages.{AddSecurityDetailsPage, OfficeOfDeparturePage}
import pages.addItems.traderSecurityDetails._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddSafetyAndSecurityConsigneePage, AddSafetyAndSecurityConsignorPage, CircumstanceIndicatorPage}
import cats.syntax.apply._
sealed trait SecurityTraderDetails

final case class SecurityPersonalInformation(name: String, address: CommonAddress) extends SecurityTraderDetails

final case class SecurityTraderEori(eori: EoriNumber) extends SecurityTraderDetails

object SecurityTraderDetails {
  def apply(eori: EoriNumber): SecurityTraderDetails = SecurityTraderEori(eori)

  def apply(name: String, address: CommonAddress): SecurityTraderDetails = SecurityPersonalInformation(name, address)

  def consignorDetails(index: Index): UserAnswersReader[Option[SecurityTraderDetails]] = {

    val readEori = SecurityConsignorEoriPage(index).reader.map(
      eori => SecurityTraderDetails(EoriNumber(eori))
    )

    val readNameAndAddress =
      (
        SecurityConsignorNamePage(index).reader,
        SecurityConsignorAddressPage(index).reader
      ).tupled
        .map {
          case (name, address) => SecurityTraderDetails(name, address)
        }

    AddSecurityDetailsPage
      .filterOptionalDependent[Option[SecurityTraderDetails]](_ == true) {
        AddSafetyAndSecurityConsignorPage.filterOptionalDependent(_ == false) {
          (AddCircumstanceIndicatorPage.reader, CircumstanceIndicatorPage.optionalReader, OfficeOfDeparturePage.reader).tupled.flatMap {
            case (true, Some("E"), CustomsOffice(_, _, CountryCode("XI"), _)) => readEori
            case _ =>
              AddSecurityConsignorsEoriPage(index).reader.flatMap {
                case true  => readEori
                case false => readNameAndAddress
              }
          }
        }
      }
      .map(_.flatten)
  }

  def consigneeDetails(index: Index): UserAnswersReader[Option[SecurityTraderDetails]] = {

    val readEori: UserAnswersReader[SecurityTraderDetails] =
      SecurityConsigneeEoriPage(index).reader
        .map(EoriNumber(_))
        .map(SecurityTraderDetails(_))

    val readNameAndAddress: UserAnswersReader[SecurityTraderDetails] =
      (
        SecurityConsigneeNamePage(index).reader,
        SecurityConsigneeAddressPage(index).reader
      ).tupled
        .map {
          case (name, address) =>
            SecurityTraderDetails(name, address)
        }

    AddSecurityDetailsPage
      .filterOptionalDependent[Option[SecurityTraderDetails]](_ == true) {
        AddSafetyAndSecurityConsigneePage
          .filterOptionalDependent(_ == false) {
            (AddCircumstanceIndicatorPage.reader, CircumstanceIndicatorPage.optionalReader).tupled.flatMap {
              case (true, Some("E")) => readEori
              case _ =>
                AddSecurityConsigneesEoriPage(index).reader.flatMap {
                  case true  => readEori
                  case false => readNameAndAddress
                }
            }
          }
      }
      .map(_.flatten)
  }
}
