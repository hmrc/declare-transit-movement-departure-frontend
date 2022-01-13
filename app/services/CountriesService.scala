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

package services

import connectors.ReferenceDataConnector
import models.reference.CountryCode
import models.{CountryList, DeclarationType, UserAnswers}
import pages.DeclarationTypePage
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountriesService @Inject() (referenceDataConnector: ReferenceDataConnector)(implicit ec: ExecutionContext) {

  def getDestinationCountries(userAnswers: UserAnswers, excludedCountries: Seq[CountryCode])(implicit hc: HeaderCarrier): Future[CountryList] =
    userAnswers.get(DeclarationTypePage) match {
      case Some(DeclarationType.Option4) => getCountriesWithCustomsOfficesAndMembership(excludedCountries, "eu")
      case _                             => getCountriesWithCustomsOfficesAndMembership(excludedCountries, "ctc")
    }

  private def getCountriesWithCustomsOfficesAndMembership(
    excludedCountries: Seq[CountryCode],
    membership: String
  )(implicit hc: HeaderCarrier): Future[CountryList] =
    getCountriesWithCustomsOffices(excludedCountries, Some(Seq("membership" -> membership)))

  def getCountriesWithCustomsOffices(
    excludedCountries: Seq[CountryCode],
    membershipQuery: Option[Seq[(String, String)]] = None
  )(implicit hc: HeaderCarrier): Future[CountryList] = {
    val customsOfficeQuery                     = Seq("customsOfficeRole" -> "ANY")
    val excludedCountriesQuery                 = excludedCountries.map(_.code).map("exclude" -> _)
    val queryParameters: Seq[(String, String)] = customsOfficeQuery ++ excludedCountriesQuery ++ membershipQuery.getOrElse(Nil)

    referenceDataConnector
      .getCountries(queryParameters)
      .map(
        countries => CountryList(countries.sortBy(_.description))
      )
  }

  def getCountries()(implicit hc: HeaderCarrier): Future[CountryList] =
    referenceDataConnector
      .getCountries()
      .map(
        countries => CountryList(countries.sortBy(_.description))
      )

  def getTransitCountries(excludedCountries: Seq[CountryCode] = Nil)(implicit hc: HeaderCarrier): Future[CountryList] = {
    val queryParameters = excludedCountries.map(
      countryCode => "excludeCountries" -> countryCode.code
    )

    referenceDataConnector
      .getTransitCountries(queryParameters)
      .map(
        countries => CountryList(countries.sortBy(_.description))
      )
  }

  def getNonEuTransitCountries()(implicit hc: HeaderCarrier): Future[CountryList] =
    referenceDataConnector
      .getNonEuTransitCountries()
      .map(
        countries => CountryList(countries.sortBy(_.description))
      )
}
