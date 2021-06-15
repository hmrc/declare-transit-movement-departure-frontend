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
import connectors.ReferenceDataConnector
import models.CustomsOfficeList
import models.reference.CountryCode
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomsOfficesService @Inject() (
  frontendAppConfig: FrontendAppConfig,
  referenceDataConnector: ReferenceDataConnector
)(implicit ec: ExecutionContext) {

  private def getNICustomsOffices(implicit hc: HeaderCarrier): Future[CustomsOfficeList] = if (frontendAppConfig.isNIJourneyEnabled) {
    referenceDataConnector.getCustomsOfficesOfTheCountry(CountryCode("XI"))
  } else {
    Future.successful(CustomsOfficeList(Nil))
  }

  def getCustomsOfficesOfDeparture(implicit hc: HeaderCarrier): Future[CustomsOfficeList] =
    for {
      gbOffices <- referenceDataConnector.getCustomsOfficesOfTheCountry(CountryCode("GB"))
      niOffices <- getNICustomsOffices
    } yield CustomsOfficeList(gbOffices.getAll ++ niOffices.getAll)
}
