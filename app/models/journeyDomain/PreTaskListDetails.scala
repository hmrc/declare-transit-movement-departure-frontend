/*
 * Copyright 2022 HM Revenue & Customs
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

import cats.data.ReaderT
import cats.implicits._
import models.reference.CustomsOffice
import models.{DeclarationType, LocalReferenceNumber, ProcedureType, UserAnswers}
import pages.{AddSecurityDetailsPage, DeclarationTypePage, OfficeOfDeparturePage, ProcedureTypePage}

case class PreTaskListDetails(
  lrn: LocalReferenceNumber,
  procedureType: ProcedureType,
  officeOfDeparture: CustomsOffice,
  declarationType: DeclarationType,
  addSecurityDetails: Boolean
)

object PreTaskListDetails {

  val localReferenceNumber: UserAnswersReader[LocalReferenceNumber] =
    ReaderT[EitherType, UserAnswers, LocalReferenceNumber](
      ua => Right(ua.lrn)
    )

  implicit val reader: UserAnswersReader[PreTaskListDetails] =
    (
      localReferenceNumber,
      ProcedureTypePage.reader,
      OfficeOfDeparturePage.reader,
      DeclarationTypePage.reader,
      AddSecurityDetailsPage.reader
    ).tupled.map((PreTaskListDetails.apply _).tupled)
}
