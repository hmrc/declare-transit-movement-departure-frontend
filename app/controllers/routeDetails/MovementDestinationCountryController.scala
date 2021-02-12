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

package controllers.routeDetails

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.MovementDestinationCountryFormProvider
import javax.inject.Inject
import models.reference.Country
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.MovementDestinationCountryPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result, Results}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.countryJsonList

import scala.concurrent.{ExecutionContext, Future}

class MovementDestinationCountryController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: MovementDestinationCountryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "movementDestinationCountry.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getTransitCountryList() flatMap {
        countries =>
          val form = formProvider(countries)

          val preparedForm = request.userAnswers
            .get(MovementDestinationCountryPage)
            .flatMap(countries.getCountry)
            .map(form.fill)
            .getOrElse(form)

          renderPage(lrn, mode, preparedForm, countries.fullList, Results.Ok)
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getTransitCountryList() flatMap {
        countries =>
          formProvider(countries)
            .bindFromRequest()
            .fold(
              formWithErrors => renderPage(lrn, mode, formWithErrors, countries.fullList, Results.BadRequest),
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(MovementDestinationCountryPage, value.code))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(MovementDestinationCountryPage, mode, updatedAnswers))
            )
      }
  }

  def renderPage(lrn: LocalReferenceNumber, mode: Mode, form: Form[Country], countries: Seq[Country], status: Results.Status)(
    implicit request: Request[AnyContent]): Future[Result] = {
    val json = Json.obj(
      "form"        -> form,
      "lrn"         -> lrn,
      "mode"        -> mode,
      "countries"   -> countryJsonList(form.value, countries),
      "onSubmitUrl" -> routes.MovementDestinationCountryController.onSubmit(lrn, mode).url
    )

    renderer.render("movementDestinationCountry.njk", json).map(status(_))
  }
}
