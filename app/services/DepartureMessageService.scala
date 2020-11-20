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

package services

import connectors.DepartureMovementConnector
import javax.inject.Inject
import models.{DepartureId, GuaranteeNotValidMessage}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DepartureMessageService @Inject()(connectors: DepartureMovementConnector) {

  def guaranteeNotValidMessage(departureId: DepartureId)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[GuaranteeNotValidMessage]] =
    connectors.getSummary(departureId) flatMap {
      case Some(summary) =>
        summary.messagesLocation.guaranteeNotValid match {
          case Some(location) =>
            connectors.getGuaranteeNotValidMessage(location)
          case _ =>
            Logger.error(s"Get Summary failed to get guaranteeNotValid location")
            Future.successful(None)
        }
      case _ =>
        Logger.error(s"Get Summary failed to return data")
        Future.successful(None)
    }

  def declarationRejectionMessage(departureId: DepartureId)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Any] =
    connectors.getSummary(departureId) flatMap {
      case Some(summary) =>
        summary.messagesLocation.declarationRejection match {
          case Some(location) => {
            connectors.getGuaranteeNotValidMessage(location)
          }
          case _ => Future.successful(None)
        }
      case _ => Future.successful(None)
    }
}
