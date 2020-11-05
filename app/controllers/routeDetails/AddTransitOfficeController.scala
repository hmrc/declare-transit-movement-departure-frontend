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

package controllers.routeDetails

import connectors.ReferenceDataConnector
import controllers.actions._
import derivable.DeriveNumberOfOfficeOfTransits
import forms.AddTransitOfficeFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.AddTransitOfficePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.RouteDetailsCheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class AddTransitOfficeController @Inject()(
  override val messagesApi: MessagesApi,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: AddTransitOfficeFormProvider,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      renderPage(lrn, mode, form).map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => renderPage(lrn, mode, formWithErrors).map(BadRequest(_)),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AddTransitOfficePage, value))
            } yield Redirect(navigator.nextPage(AddTransitOfficePage, mode, updatedAnswers))
        )
  }

  private def renderPage(lrn: LocalReferenceNumber, mode: Mode, form: Form[Boolean])(implicit request: DataRequest[AnyContent]): Future[Html] =
    referenceDataConnector.getOfficeOfTransitList() flatMap {
      officeOfTransitList =>
        val routesCYAHelper          = new RouteDetailsCheckYourAnswersHelper(request.userAnswers)
        val numberOfTransitOffices   = request.userAnswers.get(DeriveNumberOfOfficeOfTransits).getOrElse(0)
        val index: Seq[Index]        = List.range(0, numberOfTransitOffices).map(Index(_))
        val maxLimitReached: Boolean = if (numberOfTransitOffices == 5) true else false
        val officeOfTransitRows = index.map {
          index =>
            routesCYAHelper.officeOfTransitRow(index, officeOfTransitList, mode)
        }

        val singularOrPlural = if (numberOfTransitOffices == 1) "singular" else "plural"
        val json = Json.obj(
          "form"                          -> form,
          "mode"                          -> mode,
          "pageTitle"                     -> msg"addTransitOffice.title.$singularOrPlural".withArgs(numberOfTransitOffices),
          "heading"                       -> msg"addTransitOffice.heading.$singularOrPlural".withArgs(numberOfTransitOffices),
          "lrn"                           -> lrn,
          "maxLimitReached"               -> maxLimitReached,
          "redirectUrlOnReachingMaxLimit" -> routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url,
          "officeOfTransitRows"           -> officeOfTransitRows,
          "radios"                        -> Radios.yesNo(form("value"))
        )

        renderer.render("addTransitOffice.njk", json)
    }
}
