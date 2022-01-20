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

package controllers

import controllers.actions._
import forms.OfficeOfDepartureFormProvider
import models.{CountryList, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import pages.OfficeOfDeparturePage
import pages.addItems.IsNonEuOfficePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import services.{CountriesService, CustomsOfficesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfDepartureController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @PreTaskListDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: OfficeOfDepartureFormProvider,
  countriesService: CountriesService,
  customsOfficesService: CustomsOfficesService,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      customsOfficesService.getCustomsOfficesOfDeparture.flatMap {
        customsOffices =>
          val form = formProvider(customsOffices)
          val preparedForm = request.userAnswers
            .get(OfficeOfDeparturePage)
            .flatMap(
              x => customsOffices.getCustomsOffice(x.id)
            )
            .map(form.fill)
            .getOrElse(form)

          val json = Json.obj(
            "form"           -> preparedForm,
            "lrn"            -> lrn,
            "customsOffices" -> getCustomsOfficesAsJson(preparedForm.value, customsOffices.getAll),
            "mode"           -> mode
          )

          renderer.render("officeOfDeparture.njk", json).map(Ok(_))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      customsOfficesService.getCustomsOfficesOfDeparture.flatMap {
        customsOffices =>
          val form = formProvider(customsOffices)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => {
                val json = Json.obj(
                  "form"           -> formWithErrors,
                  "lrn"            -> lrn,
                  "customsOffices" -> getCustomsOfficesAsJson(form.value, customsOffices.getAll),
                  "mode"           -> mode
                )

                renderer.render("officeOfDeparture.njk", json).map(BadRequest(_))
              },
              value =>
                for {
                  getNonEuCountries: CountryList <- countriesService.getNonEuTransitCountries()
                  isNotEu: Boolean = getNonEuCountries.getCountry(value.countryId).isDefined
                  ua1 <- Future.fromTry(request.userAnswers.set(OfficeOfDeparturePage, value))
                  ua2 <- Future.fromTry(ua1.set(IsNonEuOfficePage, isNotEu))
                  _   <- sessionRepository.set(ua2)
                } yield Redirect(navigator.nextPage(OfficeOfDeparturePage, mode, ua2))
            )
      }
  }

}
