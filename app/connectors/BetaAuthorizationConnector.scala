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

package connectors

import config.FrontendAppConfig
import controllers.Assets.NO_CONTENT
import javax.inject.Inject
import models.BetaEoriNumber
import play.api.http.HeaderNames
import play.api.libs.json.Json
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class BetaAuthorizationConnector @Inject() (val appConfig: FrontendAppConfig, http: HttpClient)(implicit ec: ExecutionContext) {

  def getBetaUser(eori: BetaEoriNumber)(implicit hc: HeaderCarrier): Future[Boolean] = {

    val serviceUrl: String = s"${appConfig.betaAuthorizationUrl}/features/private-beta"
    val headers            = Seq(ContentTypeHeader("application/json"))

    http
      .POSTString[HttpResponse](serviceUrl, Json.toJsObject(eori).toString, headers)
      .map {
        response =>
          response.status match {
            case NO_CONTENT => true
            case _          => false
          }
      }
      .recover {
        case _ => false
      }
  }

  object ContentTypeHeader {
    def apply(value: String): (String, String) = (HeaderNames.CONTENT_TYPE, value)
  }
}
