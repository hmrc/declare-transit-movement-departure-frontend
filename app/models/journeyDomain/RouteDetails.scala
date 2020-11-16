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

import java.time.LocalDateTime

import cats.data._
import cats.implicits._
import derivable.DeriveNumberOfOfficeOfTransits
import models.reference.CountryCode
import pages._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import RouteDetails.TransitInformation
import models.{Index, UserAnswers}

final case class RouteDetails(
  countryOfDispatch: CountryCode,
  officeOfDeparture: String,
  destinationCountry: CountryCode,
  destinationOffice: String,
  transitInformation: NonEmptyList[TransitInformation]
)

object RouteDetails {

  final case class TransitInformation(
    transitOffice: String,
    arrivalTime: Option[LocalDateTime]
  )

  object TransitInformation {

    implicit val readSeqTransitInformation: UserAnswersReader[NonEmptyList[TransitInformation]] =
      AddSecurityDetailsPage.reader
        .flatMap {
          addSecurityDetailsFlag =>
            if (addSecurityDetailsFlag) {
              DeriveNumberOfOfficeOfTransits.reader.flatMap {
                offices =>
                  offices.zipWithIndex.traverse({
                    case (_, index) =>
                      (
                        AddAnotherTransitOfficePage(Index(index)).reader,
                        ArrivalTimesAtOfficePage(Index(index)).reader
                      ).tupled.map {
                        case (office, time) => TransitInformation(office, Some(time.dateTime))
                      }
                  })
              }
            } else {
              DeriveNumberOfOfficeOfTransits.reader.flatMap {
                offices =>
                  offices.zipWithIndex.traverse({
                    case (_, index) =>
                      AddAnotherTransitOfficePage(Index(index)).reader.map(TransitInformation(_, None))
                  })

              }

            }
        }
        .flatMapF(NonEmptyList.fromList)
  }

  implicit val makeSimplifiedMovementDetails: UserAnswersParser[Option, RouteDetails] =
    UserAnswersOptionalParser(
      (
        CountryOfDispatchPage.reader,
        OfficeOfDeparturePage.reader,
        DestinationCountryPage.reader,
        DestinationOfficePage.reader,
        UserAnswersReader[NonEmptyList[TransitInformation]]
      ).tupled
    )((RouteDetails.apply _).tupled)
}
