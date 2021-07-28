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

package services

import config.FrontendAppConfig
import connectors.BetaAuthorizationConnector
import models.BetaEoriNumber
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.Future

class IsDeparturesEnabledService @Inject() (
  betaAuthorizationConnector: BetaAuthorizationConnector,
  appConfig: FrontendAppConfig
) {

  def isDeparturesEnabled(eori: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    if (appConfig.isDeparturesEnabled) {
      betaAuthorizationConnector.getBetaUser(BetaEoriNumber(eori))
    } else {
      Future.successful(appConfig.isDeparturesEnabled)
    }
}
