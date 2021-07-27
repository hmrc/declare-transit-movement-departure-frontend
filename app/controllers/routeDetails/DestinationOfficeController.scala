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
import forms.DestinationOfficeFormProvider
import logging.Logging
import models.reference.CustomsOffice
import models.{CustomsOfficeList, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.routeDetails.{DestinationOfficePage, MovementDestinationCountryPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import services.ExcludedCountriesService._
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DestinationOfficeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: DestinationOfficeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with Logging {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      (
        for {
          movementDestination <- OptionT.fromOption[Future](request.userAnswers.get(MovementDestinationCountryPage))
          excludedCountries   <- OptionT.fromOption[Future](routeDetailsExcludedCountries(request.userAnswers))
          customsOffices      <- OptionT.liftF(referenceDataConnector.getCustomsOfficesOfTheCountry(movementDestination, destinationOfficeRoles))
          countryList         <- OptionT.liftF(referenceDataConnector.getTransitCountryList(excludedCountries))
          countryName = countryList.getCountry(movementDestination).fold(movementDestination.code)(_.description)
          preparedForm = request.userAnswers
            .get(DestinationOfficePage)
            .flatMap(
              x => customsOffices.getCustomsOffice(x.id)
            )
            .map(formProvider(customsOffices, countryName).fill)
            .getOrElse(formProvider(customsOffices, countryName))
          page <- OptionT.liftF(renderPage(lrn, mode, preparedForm, customsOffices, countryName, Results.Ok))
        } yield page
      ).getOrElseF {
        logger.warn(s"[Controller][DestinationOffice][onPageLoad] OfficeOfDeparturePage or MovementDestinationCountryPage is missing")
        Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      (
        for {
          movementDestination <- OptionT.fromOption[Future](request.userAnswers.get(MovementDestinationCountryPage))
          excludedCountries   <- OptionT.fromOption[Future](routeDetailsExcludedCountries(request.userAnswers))
          customsOffices      <- OptionT.liftF(referenceDataConnector.getCustomsOfficesOfTheCountry(movementDestination, destinationOfficeRoles))
          countryList         <- OptionT.liftF(referenceDataConnector.getTransitCountryList(excludedCountries))
          countryName = countryList.getCountry(movementDestination).fold(movementDestination.code)(_.description)
          page <- OptionT.liftF(
            formProvider(customsOffices, countryName)
              .bindFromRequest()
              .fold(
                formWithErrors => renderPage(lrn, mode, formWithErrors, customsOffices, countryName, Results.BadRequest),
                success = (value: CustomsOffice) =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(DestinationOfficePage, value))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(DestinationOfficePage, mode, updatedAnswers))
              )
          )
        } yield page
      ).getOrElseF {
        logger.warn(s"[Controller][DestinationOffice][onSubmit] OfficeOfDeparturePage or MovementDestinationCountryPage is missing")
        Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }

  private def renderPage(lrn: LocalReferenceNumber,
                         mode: Mode,
                         form: Form[CustomsOffice],
                         customsOffices: CustomsOfficeList,
                         countryName: String,
                         status: Results.Status
  )(implicit request: Request[AnyContent]): Future[Result] = {

    val json = Json.obj(
      "form"           -> form,
      "lrn"            -> lrn,
      "customsOffices" -> getCustomsOfficesAsJson(form.value, customsOffices.getAll),
      "countryName"    -> countryName,
      "mode"           -> mode
    )
    renderer.render("destinationOffice.njk", json).map(status(_))
  }

}
