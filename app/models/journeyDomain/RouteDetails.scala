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

import cats.data._
import cats.implicits._
import derivable.DeriveNumberOfOfficeOfTransits
import models.Index
import models.journeyDomain.RouteDetails.TransitInformation
import models.reference.{CountryCode, CountryOfDispatch, CustomsOffice}
import pages._
import pages.routeDetails.{
  AddAnotherTransitOfficePage,
  AddOfficeOfTransitPage,
  ArrivalTimesAtOfficePage,
  CountryOfDispatchPage,
  DestinationCountryPage,
  DestinationOfficePage
}

import java.time.LocalDateTime

final case class RouteDetails(
  countryOfDispatch: CountryOfDispatch,
  destinationCountry: CountryCode,
  destinationOffice: CustomsOffice,
  transitInformation: Option[NonEmptyList[TransitInformation]]
)

object RouteDetails {

  final case class TransitInformation(
    transitOffice: String,
    arrivalTime: Option[LocalDateTime]
  )

  object TransitInformation {

    private def addOfficeOfTransit = AddSecurityDetailsPage.reader
      .flatMap {
        addSecurityDetailsFlag =>
          if (addSecurityDetailsFlag) {
            DeriveNumberOfOfficeOfTransits.mandatoryNonEmptyListReader.flatMap {
              _.zipWithIndex.traverse({
                case (_, index) =>
                  (
                    AddAnotherTransitOfficePage(Index(index)).reader,
                    ArrivalTimesAtOfficePage(Index(index)).reader
                  ).tupled.map {
                    case (office, time) => TransitInformation(office, Some(time))
                  }
              })
            }
          } else {
            DeriveNumberOfOfficeOfTransits.mandatoryNonEmptyListReader.flatMap {
              _.zipWithIndex.traverse({
                case (_, index) =>
                  AddAnotherTransitOfficePage(Index(index)).reader.map(TransitInformation(_, None))
              })
            }
          }
      }

    implicit val readSeqTransitInformation: UserAnswersReader[Option[NonEmptyList[TransitInformation]]] =
      OfficeOfDeparturePage.reader.flatMap {
        case c if c.countryId.code.toUpperCase == "XI" =>
          AddOfficeOfTransitPage.filterOptionalDependent(identity) {
            addOfficeOfTransit
          }
        case _ => addOfficeOfTransit.map(Some.apply)
      }
  }

  implicit val makeSimplifiedMovementDetails: UserAnswersReader[RouteDetails] =
    (
      CountryOfDispatchPage.reader,
      DestinationCountryPage.reader,
      DestinationOfficePage.reader,
      UserAnswersReader[Option[NonEmptyList[TransitInformation]]]
    ).tupled.map((RouteDetails.apply _).tupled)
}
