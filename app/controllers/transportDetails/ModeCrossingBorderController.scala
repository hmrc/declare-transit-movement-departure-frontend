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

package controllers.transportDetails

import controllers.actions._
import forms.ModeCrossingBorderFormProvider
import models.reference.TransportMode
import models.{DependentSection, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.TransportDetails
import pages.ModeCrossingBorderPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import services.TransportModesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.transportModesAsJson

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ModeCrossingBorderController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @TransportDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: ModeCrossingBorderFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  transportModesService: TransportModesService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.TransportDetails)).async {
      implicit request =>
        transportModesService.getTransportModes() flatMap {

          transportModes =>
            val form = formProvider(transportModes)

            val preparedForm = request.userAnswers
              .get(ModeCrossingBorderPage)
              .flatMap(transportModes.getTransportMode)
              .map(form.fill)
              .getOrElse(form)

            renderPage(lrn, mode, preparedForm, transportModes.transportModes, Results.Ok)
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.TransportDetails)).async {
      implicit request =>
        transportModesService.getTransportModes() flatMap {

          transportModes =>
            formProvider(transportModes)
              .bindFromRequest()
              .fold(
                formWithErrors => renderPage(lrn, mode, formWithErrors, transportModes.transportModes, Results.BadRequest),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(ModeCrossingBorderPage, value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(ModeCrossingBorderPage, mode, updatedAnswers))
              )
        }
    }

  def renderPage(lrn: LocalReferenceNumber, mode: Mode, form: Form[TransportMode], transportModes: Seq[TransportMode], status: Results.Status)(implicit
    request: Request[AnyContent]
  ): Future[Result] = {
    val json = Json.obj(
      "form"           -> form,
      "lrn"            -> lrn,
      "mode"           -> mode,
      "transportModes" -> transportModesAsJson(form.value, transportModes),
      "onSubmitUrl"    -> routes.ModeCrossingBorderController.onSubmit(lrn, mode).url
    )
    renderer.render("modeCrossingBorder.njk", json).map(status(_))
  }
}
