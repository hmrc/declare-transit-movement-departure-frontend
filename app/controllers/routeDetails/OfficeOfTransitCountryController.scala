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

import cats.data.OptionT
import cats.implicits._
import connectors.ReferenceDataConnector
import controllers.actions._
import forms.OfficeOfTransitCountryFormProvider
import logging.Logging
import models.reference.Country
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.OfficeOfTransitCountryPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import services.ExcludedCountriesService.routeDetailsExcludedCountries
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.countryJsonList

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfTransitCountryController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: OfficeOfTransitCountryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  officeOfTransitFilter: TraderDetailsOfficesOfTransitProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with Logging {

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData andThen officeOfTransitFilter(index)).async {
      implicit request =>
        (
          for {
            excludedCountries  <- OptionT.fromOption[Future](routeDetailsExcludedCountries(request.userAnswers))
            transitCountryList <- OptionT.liftF(referenceDataConnector.getTransitCountryList(excludedCountries))
            form = formProvider(transitCountryList)
            preparedForm = request.userAnswers
              .get(OfficeOfTransitCountryPage(index))
              .flatMap(transitCountryList.getCountry)
              .map(form.fill)
              .getOrElse(form)
            page <- OptionT.liftF(renderPage(lrn, index, mode, preparedForm, transitCountryList.fullList, Results.Ok))
          } yield page
        ).getOrElseF {
          logger.warn(s"[Controller][OfficeOfTransitCountry][onPageLoad] OfficeOfDeparturePage is missing")
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      (
        for {
          excludedCountries  <- OptionT.fromOption[Future](routeDetailsExcludedCountries(request.userAnswers))
          transitCountryList <- OptionT.liftF(referenceDataConnector.getTransitCountryList(excludedCountries))
          page <- OptionT.liftF(
            formProvider(transitCountryList)
              .bindFromRequest()
              .fold(
                formWithErrors => renderPage(lrn, index, mode, formWithErrors, transitCountryList.fullList, Results.BadRequest),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(OfficeOfTransitCountryPage(index), value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(OfficeOfTransitCountryPage(index), mode, updatedAnswers))
              )
          )
        } yield page
      ).getOrElseF {
        logger.warn(s"[Controller][OfficeOfTransitCountry][onPageLoad] OfficeOfDeparturePage is missing")
        Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }

  private def renderPage(lrn: LocalReferenceNumber, index: Index, mode: Mode, form: Form[Country], countries: Seq[Country], status: Results.Status)(implicit
    request: Request[AnyContent]
  ): Future[Result] = {
    val json = Json.obj(
      "form"        -> form,
      "lrn"         -> lrn,
      "mode"        -> mode,
      "countries"   -> countryJsonList(form.value, countries),
      "onSubmitUrl" -> routes.OfficeOfTransitCountryController.onSubmit(lrn, index, mode).url
    )

    renderer.render("officeOfTransitCountry.njk", json).map(status(_))
  }
}
