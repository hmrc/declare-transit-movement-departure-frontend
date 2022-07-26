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

package connectors

import com.lucidchart.open.xtract.XmlReader
import config.FrontendAppConfig
import models.messages.DeclarationRequest
import models.{CancellationDecisionUpdateMessage, DeclarationRejectionMessage, DepartureId, GuaranteeNotValidMessage, MessagesSummary, ResponseMessage}
import play.api.Logging
import play.api.http.HeaderNames
import uk.gov.hmrc.http.HttpReads.is2xx
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import xml.XMLWrites._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

class DepartureMovementConnector @Inject() (val appConfig: FrontendAppConfig, http: HttpClient)(implicit ec: ExecutionContext) extends Logging {
  private val channel: String = "web"

  def submitDepartureMovement(departureMovement: DeclarationRequest)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val serviceUrl = s"${appConfig.departureHost}/movements/departures/"
    val headers    = Seq(ContentTypeHeader("application/xml"), ChannelHeader(channel))

    http.POSTString[HttpResponse](serviceUrl, departureMovement.toXml.toString, headers)
  }

  def getSummary(departureId: DepartureId)(implicit hc: HeaderCarrier): Future[Option[MessagesSummary]] = {

    val serviceUrl: String = s"${appConfig.departureHost}/movements/departures/${departureId.value}/messages/summary"
    val header             = hc.withExtraHeaders(ChannelHeader(channel))

    http.GET[HttpResponse](serviceUrl)(httpReads, header, ec) map {
      case responseMessage if is2xx(responseMessage.status) =>
        Some(responseMessage.json.as[MessagesSummary])
      case _ =>
        logger.error(s"Get Summary failed to return data")
        None
    }
  }

  def getGuaranteeNotValidMessage(location: String)(implicit hc: HeaderCarrier): Future[Option[GuaranteeNotValidMessage]] = {
    val serviceUrl = s"${appConfig.departureBaseUrl}$location"
    val header     = hc.withExtraHeaders(ChannelHeader(channel))

    http.GET[HttpResponse](serviceUrl)(httpReads, header, ec) map {
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
    val header     = hc.withExtraHeaders(ChannelHeader(channel))

    http.GET[HttpResponse](serviceUrl)(httpReads, header, ec) map {
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
    val header     = hc.withExtraHeaders(ChannelHeader(channel))

    http.GET[HttpResponse](serviceUrl)(httpReads, header, ec) map {
      case responseMessage if is2xx(responseMessage.status) =>
        val message: NodeSeq = responseMessage.json.as[ResponseMessage].message
        XmlReader.of[CancellationDecisionUpdateMessage].read(message).toOption
      case _ =>
        logger.error("getCancellationDecisionUpdateMessage failed to return data")
        None
    }
  }

  object ChannelHeader {
    def apply(value: String): (String, String) = ("Channel", value)
  }

  object ContentTypeHeader {
    def apply(value: String): (String, String) = (HeaderNames.CONTENT_TYPE, value)
  }

  object AuthorizationHeader {
    def apply(value: String): (String, String) = (HeaderNames.AUTHORIZATION, value)
  }
}
