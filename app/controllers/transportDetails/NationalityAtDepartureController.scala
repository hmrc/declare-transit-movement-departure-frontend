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

package controllers.transportDetails

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.generic.CountryFormProvider
import models.reference.Country
import models.{CountryList, DependentSection, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.TransportDetails
import pages.NationalityAtDeparturePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NationalityAtDepartureController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @TransportDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: CountryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private def form(countries: CountryList): Form[Country] = formProvider("nationalityAtDeparture", countries)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.TransportDetails)).async {
      implicit request =>
        referenceDataConnector.getCountryList() flatMap {
          countries =>
            val preparedForm = request.userAnswers
              .get(NationalityAtDeparturePage)
              .flatMap(countries.getCountry)
              .map(form(countries).fill)
              .getOrElse(form(countries))

            renderPage(lrn, mode, preparedForm, countries.fullList, Results.Ok)
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.TransportDetails)).async {
      implicit request =>
        referenceDataConnector.getCountryList() flatMap {
          countries =>
            form(countries)
              .bindFromRequest()
              .fold(
                formWithErrors => renderPage(lrn, mode, formWithErrors, countries.fullList, Results.BadRequest),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(NationalityAtDeparturePage, value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(NationalityAtDeparturePage, mode, updatedAnswers))
              )
        }
    }

  private def renderPage(lrn: LocalReferenceNumber, mode: Mode, form: Form[Country], countries: Seq[Country], status: Results.Status)(implicit
    request: Request[AnyContent]
  ): Future[Result] = {
    val json = Json.obj(
      "form"        -> form,
      "lrn"         -> lrn,
      "mode"        -> mode,
      "countries"   -> countryJsonList(form.value, countries),
      "onSubmitUrl" -> routes.NationalityAtDepartureController.onSubmit(lrn, mode).url
    )

    renderer.render("nationalityAtDeparture.njk", json).map(status(_))
  }
}
