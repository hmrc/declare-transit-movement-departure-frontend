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

package controllers.addItems.traderDetails

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.addItems.traderDetails.TraderDetailsConsignorAddressFormProvider
import javax.inject.Inject
import models.reference.{Country, CountryCode}
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.AddItems
import pages.addItems.traderDetails.{TraderDetailsConsignorAddressPage, TraderDetailsConsignorNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.countryJsonList

import scala.concurrent.{ExecutionContext, Future}

class TraderDetailsConsignorAddressController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItems navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: TraderDetailsConsignorAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/traderDetails/traderDetailsConsignorAddress.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getCountryList() flatMap {
        countries =>
          request.userAnswers.get(TraderDetailsConsignorNamePage(index)) match {
            case Some(consignorName) =>
              val preparedForm = request.userAnswers.get(TraderDetailsConsignorAddressPage(index)) match {
                case Some(value) => formProvider(countries).fill(value)
                case None        => formProvider(countries)
              }

              val json = Json.obj(
                "form"          -> preparedForm,
                "lrn"           -> lrn,
                "mode"          -> mode,
                "consignorName" -> consignorName,
                "countries"     -> countryJsonList(preparedForm.value.map(_.country), countries.fullList)
              )

              renderer.render("consignorAddress.njk", json).map(Ok(_))
            case _ => ???
//            case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))

          }
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(TraderDetailsConsignorNamePage(index)) match {
        case Some(consignorName) =>
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
                      "form"          -> formWithErrors,
                      "lrn"           -> lrn,
                      "mode"          -> mode,
                      "consignorName" -> consignorName,
                      "countries"     -> countryJsonList(countryValue, countries.fullList)
                    )

                    renderer.render("consignorAddress.njk", json).map(BadRequest(_))
                  },
                  value =>
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(TraderDetailsConsignorAddressPage(index), value))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(TraderDetailsConsignorAddressPage(index), mode, updatedAnswers))
                )
          }
        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))

      }
  }
}
