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

package navigation

import controllers.routes
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class PreTaskListNavigator @Inject() () extends Navigator {

  // format: off
  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case LocalReferenceNumberPage => ua => Some(routes.OfficeOfDepartureController.onPageLoad(ua.id, NormalMode))
    case OfficeOfDeparturePage => ua =>
      ua.get(CountryOfDispatchPage) match {
        case Some(_) => Some(controllers.routeDetails.routes.DestinationCountryController.onPageLoad(ua.id, NormalMode))
        case _ => Some(routes.ProcedureTypeController.onPageLoad(ua.id, NormalMode))
      }
    case ProcedureTypePage        => ua => Some(routes.AddSecurityDetailsController.onPageLoad(ua.id, NormalMode))
    case AddSecurityDetailsPage   => ua => Some(routes.DeclarationSummaryController.onPageLoad(ua.id))
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case _ => _ => None
  }
  // format: on
}
