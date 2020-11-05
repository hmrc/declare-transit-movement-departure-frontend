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
import forms.ConfirmRemoveOfficeOfTransitFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.{AddAnotherTransitOfficePage, ConfirmRemoveOfficeOfTransitPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import play.twirl.api.Html
import queries.OfficeOfTransitQuery
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveOfficeOfTransitController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  errorHandler: ErrorHandler,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: ConfirmRemoveOfficeOfTransitFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(AddAnotherTransitOfficePage(index)) match {
        case Some(officeOfTransitId) =>
          val preparedForm = request.userAnswers.get(ConfirmRemoveOfficeOfTransitPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }
          renderPage(lrn, officeOfTransitId, mode, preparedForm).map(Ok(_))
        case _ => renderErrorPage(mode)
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(AddAnotherTransitOfficePage(index)) match {
        case Some(officeOfTransitId) =>
          form
            .bindFromRequest()
            .fold(
              formWithErrors => renderPage(lrn, officeOfTransitId, mode, formWithErrors).map(BadRequest(_)),
              value =>
                if (value) {
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.remove(OfficeOfTransitQuery(index)))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(ConfirmRemoveOfficeOfTransitPage, mode, updatedAnswers))
                } else {
                  Future.successful(Redirect(navigator.nextPage(ConfirmRemoveOfficeOfTransitPage, mode, request.userAnswers)))
              }
            )
        case _ => renderErrorPage(mode)
      }
  }

  private def renderPage(lrn: LocalReferenceNumber, officeOfTransitId: String, mode: Mode, form: Form[Boolean])(
    implicit
    request: DataRequest[AnyContent]): Future[Html] =
    referenceDataConnector.getOfficeOfTransit(officeOfTransitId) flatMap {
      officeOfTransit =>
        val json = Json.obj(
          "form"            -> form,
          "mode"            -> mode,
          "officeOfTransit" -> s"${officeOfTransit.name} (${officeOfTransit.id})",
          "lrn"             -> lrn,
          "radios"          -> Radios.yesNo(form("value"))
        )
        renderer.render("confirmRemoveOfficeOfTransit.njk", json)
    }

  private def renderErrorPage(mode: Mode)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    val redirectLinkText = if (request.userAnswers.get(DeriveNumberOfOfficeOfTransits).contains(0)) "noOfficeOfTransit" else "multipleOfficeOfTransit"
    val redirectLink     = "" //navigator.nextPage(ConfirmRemoveOfficeOfTransitPage, mode, request.userAnswers).url

    errorHandler.onConcurrentError(redirectLinkText, redirectLink, "concurrent.officeOfTransit")
  }

}
