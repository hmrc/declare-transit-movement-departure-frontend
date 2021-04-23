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

package controllers.actions

import derivable.DeriveNumberOfOfficeOfTransits
import models.requests.DataRequest
import models.{Index, NormalMode}
import pages.{AddAnotherTransitOfficePage, OfficeOfTransitCountryPage}
import play.api.mvc.Results._
import play.api.mvc.{ActionFilter, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TraderDetailsOfficesOfTransitProvider @Inject()()(implicit ec: ExecutionContext) {

  def apply(index: Index, pageId: Int): ActionFilter[DataRequest] = new TraderDetailsOfficesOfTransitFilter(index, pageId)

}

class TraderDetailsOfficesOfTransitFilter(index: Index, pageId: Int)(implicit protected val executionContext: ExecutionContext)
    extends ActionFilter[DataRequest] {

  // Maximumun number of offices of transit is 9, the value below is to account for zero ofset for indexes in Seq
  private val MAX_NUMBER_OF_OFFICES_OF_TRANSIT = 8

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {

    def redirectToAddTransitOfficeController =
      Future.successful(
        Option(Redirect(controllers.routeDetails.routes.AddTransitOfficeController.onPageLoad(request.userAnswers.id, NormalMode).url))
      )

    def redirectToOfficeOfTransitCountryController(index: Index) =
      Future.successful(
        Option(
          Redirect(
            controllers.routeDetails.routes.OfficeOfTransitCountryController
              .onPageLoad(request.userAnswers.id, index, NormalMode)
              .url
          )
        )
      )

    val numberOfOffices = request.userAnswers.get(DeriveNumberOfOfficeOfTransits).getOrElse(0)

    val lastloopComplete: Boolean = request.userAnswers.get(AddAnotherTransitOfficePage(Index(numberOfOffices - 1))).isDefined

    if (index.position > MAX_NUMBER_OF_OFFICES_OF_TRANSIT) {
      if (lastloopComplete) {
        redirectToAddTransitOfficeController
      } else {

        redirectToOfficeOfTransitCountryController(Index(numberOfOffices - 1))
      }
    } else {
      (lastloopComplete, index.position) match {
        case (_, 0)                                 => Future.successful(None)
        case (true, x) if x != numberOfOffices      => redirectToAddTransitOfficeController
        case (_, _) if numberOfOffices == 0         => redirectToOfficeOfTransitCountryController(Index(0))
        case (false, x) if x != numberOfOffices - 1 => redirectToOfficeOfTransitCountryController(Index(numberOfOffices - 1))
        case _                                      => Future.successful(None)
      }
    }
  }
}
