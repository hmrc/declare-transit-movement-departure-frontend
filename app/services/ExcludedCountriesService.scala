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

import controllers.routeDetails.{alwaysExcludedTransitCountries, gbExcludedCountries}
import models.UserAnswers
import models.reference.CountryCode
import pages.OfficeOfDeparturePage

object ExcludedCountriesService {

  def routeDetailsExcludedCountries(userAnswers: UserAnswers): Option[Seq[CountryCode]] = userAnswers.get(OfficeOfDeparturePage).map {
    _.countryId.code match {
      case "XI" => alwaysExcludedTransitCountries
      case _    => alwaysExcludedTransitCountries ++ gbExcludedCountries
    }
  }
}
