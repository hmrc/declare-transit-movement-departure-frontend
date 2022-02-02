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

package controllers.routeDetails

import controllers.actions._
import forms.DestinationCountryFormProvider
import models.DeclarationType.{Option1, Option4}
import models.reference.{Country, CountryCode}
import models.{CountryList, LocalReferenceNumber, Mode, UserAnswers}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.DeclarationTypePage
import pages.routeDetails.DestinationCountryPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DestinationCountryController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  countriesService: CountriesService,
  formProvider: DestinationCountryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private def excludedCountries(userAnswers: UserAnswers): Seq[CountryCode] = userAnswers.get(DeclarationTypePage) match {
    case Some(Option1) | Some(Option4) => Seq(CountryCode("SM"))
    case _                             => Seq.empty
  }

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      countriesService.getCountries(excludedCountries(request.userAnswers)) flatMap {
        countryList =>
          val form = formProvider(countryList)

          val preparedForm = request.userAnswers
            .get(DestinationCountryPage)
            .flatMap(countryList.getCountry)
            .map(form.fill)
            .getOrElse(form)

          renderPage(lrn, mode, preparedForm, countryList.countries, Results.Ok)
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      countriesService.getCountries(excludedCountries(request.userAnswers)) flatMap {
        countryList =>
          formProvider(countryList)
            .bindFromRequest()
            .fold(
              formWithErrors => renderPage(lrn, mode, formWithErrors, countryList.countries, Results.BadRequest),
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(DestinationCountryPage, value.code))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(DestinationCountryPage, mode, updatedAnswers))
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
      "onSubmitUrl" -> routes.DestinationCountryController.onSubmit(lrn, mode).url
    )

    renderer.render("destinationCountry.njk", json).map(status(_))
  }
}
