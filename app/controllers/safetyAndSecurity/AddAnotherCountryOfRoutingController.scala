/*
 * Copyright 2023 HM Revenue & Customs
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

import config.FrontendAppConfig
import controllers.actions._
import derivable.DeriveNumberOfCountryOfRouting
import forms.safetyAndSecurity.AddAnotherCountryOfRoutingFormProvider
import models.requests.DataRequest
import models.{DependentSection, Index, LocalReferenceNumber, Mode, UserAnswers}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurity
import pages.safetyAndSecurity.AddAnotherCountryOfRoutingPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.SafetyAndSecurityCheckYourAnswersHelper
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class AddAnotherCountryOfRoutingController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SafetyAndSecurity navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  countriesService: CountriesService,
  formProvider: AddAnotherCountryOfRoutingFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "safetyAndSecurity/addAnotherCountryOfRouting.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        renderPage(lrn, mode, formProvider(allowMoreCountriesOfRouting(request.userAnswers))).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        formProvider(allowMoreCountriesOfRouting(request.userAnswers))
          .bindFromRequest()
          .fold(
            formWithErrors => renderPage(lrn, mode, formWithErrors).map(BadRequest(_)),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherCountryOfRoutingPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddAnotherCountryOfRoutingPage, mode, updatedAnswers))
          )
    }

  private def renderPage(lrn: LocalReferenceNumber, mode: Mode, form: Form[Boolean])(implicit request: DataRequest[AnyContent]): Future[Html] = {

    val cyaHelper                = new SafetyAndSecurityCheckYourAnswersHelper(request.userAnswers, mode)
    val numberOfRoutingCountries = request.userAnswers.get(DeriveNumberOfCountryOfRouting).getOrElse(0)
    val indexList: Seq[Index]    = List.range(0, numberOfRoutingCountries).map(Index(_))
    val allowMoreCountries       = allowMoreCountriesOfRouting(request.userAnswers)
    countriesService.getCountries() flatMap {
      countries =>
        val countryRows = indexList.map {
          index =>
            cyaHelper.countryRow(index, countries)
        }

        val singularOrPlural = if (numberOfRoutingCountries > 1) "plural" else "singular"
        val json = Json.obj(
          "form"               -> form,
          "pageTitle"          -> msg"addAnotherCountryOfRouting.title.$singularOrPlural".withArgs(numberOfRoutingCountries),
          "heading"            -> msg"addAnotherCountryOfRouting.heading.$singularOrPlural".withArgs(numberOfRoutingCountries),
          "countryRows"        -> countryRows,
          "lrn"                -> lrn,
          "mode"               -> mode,
          "allowMoreCountries" -> allowMoreCountries,
          "radios"             -> Radios.yesNo(formProvider(allowMoreCountries)("value"))
        )

        renderer.render(template, json)

    }
  }

  private def allowMoreCountriesOfRouting(ua: UserAnswers): Boolean =
    ua.get(DeriveNumberOfCountryOfRouting).getOrElse(0) < config.maxCountriesOfRouting

}
