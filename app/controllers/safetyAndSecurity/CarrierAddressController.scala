/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.safetyAndSecurity

import connectors.ReferenceDataConnector
import controllers.actions._
import controllers.{routes => mainRoutes}
import forms.safetyAndSecurity.CarrierAddressFormProvider
import javax.inject.Inject
import models.reference.{Country, CountryCode}
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurityTraderDetails
import pages.safetyAndSecurity.{CarrierAddressPage, CarrierNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.countryJsonList

import scala.concurrent.{ExecutionContext, Future}

class CarrierAddressController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SafetyAndSecurityTraderDetails navigator: Navigator,
  identify: IdentifierAction,
  referenceDataConnector: ReferenceDataConnector,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: CarrierAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "safetyAndSecurity/carrierAddress.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getCountryList() flatMap {
        countries =>
          request.userAnswers.get(CarrierNamePage) match {
            case Some(carrierName) =>
              val preparedForm = request.userAnswers.get(CarrierAddressPage) match {
                case Some(value) => formProvider(countries).fill(value)
                case None        => formProvider(countries)
              }

              val json = Json.obj(
                "form"        -> preparedForm,
                "carrierName" -> carrierName,
                "lrn"         -> lrn,
                "mode"        -> mode,
                "countries"   -> countryJsonList(preparedForm.value.map(_.country), countries.fullList)
              )

              renderer.render(template, json).map(Ok(_))
            case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))

          }
      }

  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(CarrierNamePage) match {
        case Some(carrierName) =>
          referenceDataConnector.getCountryList() flatMap {
            countries =>
              formProvider(countries)
                .bindFromRequest()
                .fold(
                  formWithErrors => {
                    val countryValue: Option[Country] = formWithErrors.data.get("country").flatMap {
                      country =>
                        countries.getCountry(CountryCode(country))
                    }
                    val json = Json.obj(
                      "form"        -> formWithErrors,
                      "lrn"         -> lrn,
                      "mode"        -> mode,
                      "carrierName" -> carrierName,
                      "countries"   -> countryJsonList(countryValue, countries.fullList)
                    )

                    renderer.render(template, json).map(BadRequest(_))
                  },
                  value =>
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(CarrierAddressPage, value))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(CarrierAddressPage, mode, updatedAnswers))
                )
          }
        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))

      }
  }
}
