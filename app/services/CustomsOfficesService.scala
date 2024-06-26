/*
 * Copyright 2023 HM Revenue & Customs
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
import models.reference.{CountryCode, CustomsOffice}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomsOfficesService @Inject() (
  referenceDataConnector: ReferenceDataConnector,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext) {

  def getCustomsOffices(roles: Seq[String] = Nil)(implicit hc: HeaderCarrier): Future[CustomsOfficeList] =
    referenceDataConnector
      .getCustomsOffices(roles)
      .map(sort)

  def getCustomsOfficesOfDeparture(implicit hc: HeaderCarrier): Future[CustomsOfficeList] = {

    def getCustomsOffices(countryCode: String): Future[CustomsOfficeList] = {
      val departureOfficeRoles: Seq[String] = Seq("DEP")
      referenceDataConnector.getCustomsOfficesForCountry(CountryCode(countryCode), departureOfficeRoles)
    }

    Future
      .sequence(config.countriesOfDeparture.map(getCustomsOffices(_).map(_.customsOffices)))
      .map(_.flatten)
      .map(sort)
  }

  def getCustomsOfficesForCountry(
    countryCode: CountryCode,
    roles: Seq[String] = Nil
  )(implicit hc: HeaderCarrier): Future[CustomsOfficeList] =
    referenceDataConnector
      .getCustomsOfficesForCountry(countryCode, roles)
      .map(
        customsOfficeList => sort(customsOfficeList.customsOffices)
      )

  private def sort(customsOffices: Seq[CustomsOffice]): CustomsOfficeList =
    CustomsOfficeList(customsOffices.sortBy(_.name.toLowerCase))

}
