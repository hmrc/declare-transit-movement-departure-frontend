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
import controllers.{routes => mainRoutes}
import derivable.DeriveOfficesOfTransitIds
import forms.AddAnotherTransitOfficeFormProvider
import javax.inject.Inject
import models.reference.{CountryCode, CustomsOffice}
import models.requests.DataRequest
import models.{CustomsOfficeList, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.{AddAnotherTransitOfficePage, OfficeOfTransitCountryPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.getCustomsOfficesAsJson

import scala.concurrent.{ExecutionContext, Future}

class AddAnotherTransitOfficeController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: AddAnotherTransitOfficeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  officeOfTransitFilter: TraderDetailsOfficesOfTransitProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData andThen officeOfTransitFilter(index)).async {
      implicit request =>
        request.userAnswers.get(OfficeOfTransitCountryPage(index)) match {
          case Some(countryCode) =>
            getCustomsOfficeAndCountryName(countryCode) flatMap {
              case (customsOffices, countryName) =>
                val form: Form[CustomsOffice] = formProvider(customsOffices, countryName)

                val preparedForm: Form[CustomsOffice] = request.userAnswers
                  .get(AddAnotherTransitOfficePage(index))
                  .flatMap(customsOffices.getCustomsOffice)
                  .map(form.fill)
                  .getOrElse(form)

                val selectedCustomsOfficeIds = request.userAnswers.get(DeriveOfficesOfTransitIds).getOrElse(Nil)

                val json = Json.obj(
                  "form"           -> preparedForm,
                  "lrn"            -> lrn,
                  "customsOffices" -> getCustomsOfficesAsJson(preparedForm.value, customsOffices.filterNot(selectedCustomsOfficeIds)),
                  "countryName"    -> countryName,
                  "mode"           -> mode
                )
                renderer.render("addAnotherTransitOffice.njk", json).map(Ok(_))
            }

          case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(OfficeOfTransitCountryPage(index)) match {
        case Some(countryCode) =>
          getCustomsOfficeAndCountryName(countryCode) flatMap {
            case (customsOffices, countryName) =>
              val form = formProvider(customsOffices, countryName)

              val selectedCustomsOfficeIds = request.userAnswers.get(DeriveOfficesOfTransitIds).getOrElse(Nil)

              form
                .bindFromRequest()
                .fold(
                  formWithErrors => {
                    val json = Json.obj(
                      "form"           -> formWithErrors,
                      "lrn"            -> lrn,
                      "customsOffices" -> getCustomsOfficesAsJson(formWithErrors.value, customsOffices.filterNot(selectedCustomsOfficeIds)),
                      "countryName"    -> countryName,
                      "mode"           -> mode
                    )
                    renderer.render("addAnotherTransitOffice.njk", json).map(BadRequest(_))
                  },
                  value =>
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherTransitOfficePage(index), value.id))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(AddAnotherTransitOfficePage(index), mode, updatedAnswers))
                )
          }
        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
      }
  }

  //TODO Refactor - 1) Make concurrent calls 2) Use transit country lookup by code
  private def getCustomsOfficeAndCountryName(countryCode: CountryCode)(implicit request: DataRequest[AnyContent]): Future[(CustomsOfficeList, String)] =
    referenceDataConnector.getCustomsOfficesOfTheCountry(countryCode) flatMap {
      customsOffices =>
        referenceDataConnector.getTransitCountryList() map {
          countryList =>
            val countryName = countryList.getCountry(countryCode).fold(countryCode.code)(_.description)
            (customsOffices, countryName)
        }
    }
}
