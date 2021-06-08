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

package controllers.traderDetails

import connectors.ReferenceDataConnector
import controllers.actions._
import controllers.{routes => mainRoutes}
import forms.{CommonAddressFormProvider, PrincipalAddressFormProvider}
import models.reference.{Country, CountryCode}
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.TraderDetails
import pages.{PrincipalAddressPage, PrincipalNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PrincipalAddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @TraderDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: CommonAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getCountryList() flatMap {
        countries =>
          request.userAnswers.get(PrincipalNamePage) match {
            case Some(principalName) =>
              val preparedForm = request.userAnswers.get(PrincipalAddressPage) match {
                case Some(value) => formProvider(countries, principalName).fill(value)
                case None        => formProvider(countries, principalName)
              }

              val json = Json.obj(
                "form"          -> preparedForm,
                "lrn"           -> lrn,
                "mode"          -> mode,
                "principalName" -> principalName,
                "countries"     -> countryJsonList(preparedForm.value.map(_.country), countries.fullList)
              )

              renderer.render("principalAddress.njk", json).map(Ok(_))
            case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
          }
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(PrincipalNamePage) match {
        case Some(principalName) =>
          referenceDataConnector.getCountryList() flatMap {
            countries =>
              formProvider(countries, principalName)
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
                      "principalName" -> principalName,
                      "countries"     -> countryJsonList(countryValue, countries.fullList)
                    )

                    renderer.render("principalAddress.njk", json).map(BadRequest(_))
                  },
                  value =>
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(PrincipalAddressPage, value))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(PrincipalAddressPage, mode, updatedAnswers))
                )
          }
        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
      }

  }

  private def countryJsonList(value: Option[Country], countries: Seq[Country]): Seq[JsObject] = {
    val countryJsonList = countries.map {
      country =>
        Json.obj("text" -> country.description, "value" -> country.code, "selected" -> value.contains(country))
    }

    Json.obj("value" -> "", "text" -> "") +: countryJsonList
  }
}
