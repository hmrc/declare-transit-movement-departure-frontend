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

package connectors

import com.lucidchart.open.xtract.XmlReader
import config.FrontendAppConfig
import javax.inject.Inject
import xml.XMLWrites._
import models.messages.DeclarationRequest
import models.{CancellationDecisionUpdateMessage, DeclarationRejectionMessage, DepartureId, GuaranteeNotValidMessage, MessagesSummary, ResponseMessage}
import play.api.Logger
import uk.gov.hmrc.http.RawReads.is2xx
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

class DepartureMovementConnector @Inject()(val appConfig: FrontendAppConfig, http: HttpClient)(implicit ec: ExecutionContext) {
  val logger: Logger = Logger(getClass)

  def submitDepartureMovement(departureMovement: DeclarationRequest)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val serviceUrl = s"${appConfig.departureHost}/movements/departures/"
    val headers    = Seq(("Content-Type", "application/xml"))

    http.POSTString[HttpResponse](serviceUrl, departureMovement.toXml.toString, headers)
  }

  def getSummary(departureId: DepartureId)(implicit hc: HeaderCarrier): Future[Option[MessagesSummary]] = {

    val serviceUrl: String = s"${appConfig.departureHost}/movements/departures/${departureId.value}/messages/summary"
    http.GET[HttpResponse](serviceUrl) map {
      case responseMessage if is2xx(responseMessage.status) =>
        Some(responseMessage.json.as[MessagesSummary])
      case _ =>
        logger.error(s"Get Summary failed to return data")
        None
    }
  }

  def getGuaranteeNotValidMessage(location: String)(implicit hc: HeaderCarrier): Future[Option[GuaranteeNotValidMessage]] = {
    val serviceUrl = s"${appConfig.departureBaseUrl}$location"
    http.GET[HttpResponse](serviceUrl) map {
      case responseMessage if is2xx(responseMessage.status) =>
        val message: NodeSeq = responseMessage.json.as[ResponseMessage].message
        XmlReader.of[GuaranteeNotValidMessage].read(message).toOption
      case _ =>
        logger.error(s"GetGuaranteeNotValidMessage failed to return data")
        None
    }
  }

  def getDeclarationRejectionMessage(location: String)(implicit hc: HeaderCarrier): Future[Option[DeclarationRejectionMessage]] = {
    val serviceUrl = s"${appConfig.departureBaseUrl}$location"
    http.GET[HttpResponse](serviceUrl) map {
      case responseMessage if is2xx(responseMessage.status) =>
        val message: NodeSeq = responseMessage.json.as[ResponseMessage].message
        XmlReader.of[DeclarationRejectionMessage].read(message).toOption
      case _ =>
        logger.error("getDeclarationRejectionMessage failed to return data")
        None
    }
  }

  def getCancellationDecisionUpdateMessage(location: String)(implicit hc: HeaderCarrier): Future[Option[CancellationDecisionUpdateMessage]] = {
    val serviceUrl = s"${appConfig.departureBaseUrl}$location"
    http.GET[HttpResponse](serviceUrl) map {
      case responseMessage if is2xx(responseMessage.status) =>
        val message: NodeSeq = responseMessage.json.as[ResponseMessage].message
        XmlReader.of[CancellationDecisionUpdateMessage].read(message).toOption
      case _ =>
        logger.error("getCancellationDecisionUpdateMessage failed to return data")
        None
    }
  }
}
