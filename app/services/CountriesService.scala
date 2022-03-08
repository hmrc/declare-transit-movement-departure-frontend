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
import models.reference.{Country, CountryCode}
import models.{CountryList, DeclarationType, UserAnswers}
import pages.DeclarationTypePage
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountriesService @Inject() (referenceDataConnector: ReferenceDataConnector)(implicit ec: ExecutionContext) {

  def getDestinationCountries(
    userAnswers: UserAnswers,
    excludedCountries: Seq[CountryCode]
  )(implicit hc: HeaderCarrier): Future[CountryList] = {
    val membership = userAnswers.get(DeclarationTypePage) match {
      case Some(DeclarationType.Option4) => "eu"
      case _                             => "ctc"
    }
    getCountriesWithCustomsOffices(excludedCountries, Seq("membership" -> membership))
  }

  def getCountriesWithCustomsOffices(
    excludedCountries: Seq[CountryCode],
    membershipQuery: Seq[(String, String)] = Nil
  )(implicit hc: HeaderCarrier): Future[CountryList] = {
    val customsOfficeQuery                     = Seq("customsOfficeRole" -> "ANY")
    val excludedCountriesQuery                 = excludedCountries.map(_.code).map("exclude" -> _)
    val queryParameters: Seq[(String, String)] = customsOfficeQuery ++ excludedCountriesQuery ++ membershipQuery
    getCountries(queryParameters)
  }

  def getCountries()(implicit hc: HeaderCarrier): Future[CountryList] =
    getCountries(Nil)

  def getTransitCountries(excludedCountries: Seq[CountryCode] = Nil)(implicit hc: HeaderCarrier): Future[CountryList] = {
    val excludedCountriesQuery                 = excludedCountries.map(_.code).map("exclude" -> _)
    val membershipQuery                        = Seq("membership" -> "ctc")
    val queryParameters: Seq[(String, String)] = excludedCountriesQuery ++ membershipQuery
    getCountries(queryParameters)
  }

  def getNonEuTransitCountries()(implicit hc: HeaderCarrier): Future[CountryList] = {
    val queryParameters = Seq("membership" -> "non_eu")
    getCountries(queryParameters)
  }

  private def getCountries(queryParameters: Seq[(String, String)])(implicit hc: HeaderCarrier): Future[CountryList] =
    referenceDataConnector
      .getCountries(queryParameters)
      .map(sort)

  private def sort(countries: Seq[Country]): CountryList =
    CountryList(countries.sortBy(_.description.toLowerCase))
}
