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
import derivable.DeriveNumberOfCountryOfRouting
import forms.safetyAndSecurity.AddAnotherCountryOfRoutingFormProvider
import models.requests.DataRequest
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
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
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
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
  referenceDataConnector: ReferenceDataConnector,
  formProvider: AddAnotherCountryOfRoutingFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "safetyAndSecurity/addAnotherCountryOfRouting.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        renderPage(lrn, form).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => renderPage(lrn, formWithErrors).map(BadRequest(_)),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherCountryOfRoutingPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddAnotherCountryOfRoutingPage, mode, updatedAnswers))
          )
    }

  private def renderPage(lrn: LocalReferenceNumber, form: Form[Boolean])(implicit request: DataRequest[AnyContent]): Future[Html] = {

    val cyaHelper                = new SafetyAndSecurityCheckYourAnswersHelper(request.userAnswers)
    val numberOfRoutingCountries = request.userAnswers.get(DeriveNumberOfCountryOfRouting).getOrElse(0)
    val indexList: Seq[Index]    = List.range(0, numberOfRoutingCountries).map(Index(_))
    referenceDataConnector.getCountryList() flatMap {
      countries =>
        val countryRows = indexList.map {
          index =>
            cyaHelper.countryRow(index, countries)
        }

        val singularOrPlural = if (numberOfRoutingCountries > 1) "plural" else "singular"
        val json = Json.obj(
          "form"        -> form,
          "pageTitle"   -> msg"addAnotherCountryOfRouting.title.$singularOrPlural".withArgs(numberOfRoutingCountries),
          "heading"     -> msg"addAnotherCountryOfRouting.heading.$singularOrPlural".withArgs(numberOfRoutingCountries),
          "countryRows" -> countryRows,
          "lrn"         -> lrn,
          "radios"      -> Radios.yesNo(form("value"))
        )

        renderer.render(template, json)

    }
  }
}
