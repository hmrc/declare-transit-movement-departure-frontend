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

package controllers.safetyAndSecurity

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.safetyAndSecurity.CountryOfRoutingFormProvider
import models.reference.Country
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurity
import pages.safetyAndSecurity.CountryOfRoutingPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.countryJsonList

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfRoutingController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SafetyAndSecurity navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: CountryOfRoutingFormProvider,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "safetyAndSecurity/countryOfRouting.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        referenceDataConnector.getCountryList() flatMap {
          countries =>
            val form = formProvider(countries)

            val preparedForm = request.userAnswers
              .get(CountryOfRoutingPage(index))
              .flatMap(countries.getCountry)
              .map(form.fill)
              .getOrElse(form)

            renderPage(lrn, index, mode, preparedForm, countries.fullList) map (Ok(_))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        referenceDataConnector.getCountryList() flatMap {
          countries =>
            formProvider(countries)
              .bindFromRequest()
              .fold(
                formWithErrors => renderPage(lrn, index, mode, formWithErrors, countries.fullList) map (BadRequest(_)),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfRoutingPage(index), value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(CountryOfRoutingPage(index), mode, updatedAnswers))
              )
        }
    }

  private def renderPage(lrn: LocalReferenceNumber, index: Index, mode: Mode, form: Form[Country], countries: Seq[Country])(implicit
    request: Request[AnyContent]
  ): Future[Html] = {
    val json = Json.obj(
      "form"      -> form,
      "lrn"       -> lrn,
      "index"     -> index.display,
      "mode"      -> mode,
      "countries" -> countryJsonList(form.value, countries)
    )

    renderer.render(template, json)
  }
}
