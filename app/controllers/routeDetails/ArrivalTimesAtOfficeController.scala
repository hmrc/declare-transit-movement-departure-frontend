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
import forms.ArrivalTimesAtOfficeFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.routeDetails.{AddAnotherTransitOfficePage, ArrivalTimesAtOfficePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import viewModels.DateTimeInput

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ArrivalTimesAtOfficeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: ArrivalTimesAtOfficeFormProvider,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(AddAnotherTransitOfficePage(index)) match {
        case Some(officeOfTransitId) =>
          referenceDataConnector.getCustomsOffice(officeOfTransitId) flatMap {
            office =>
              val form: Form[LocalDate] = formProvider(office.name)

              val preparedForm = request.userAnswers.get(ArrivalTimesAtOfficePage(index)) match {
                case Some(value) => form.fill(LocalDate.of(value.getYear, value.getMonth, value.getDayOfMonth))
                case None        => form
              }

              loadPage(lrn, index, mode, preparedForm).map(Ok(_))
          }
        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
      }
  }

  private def loadPage(lrn: LocalReferenceNumber, index: Index, mode: Mode, form: Form[LocalDate])(implicit
    request: Request[AnyContent]
  ): Future[Html] = {
    val viewModel = DateTimeInput.localDateTime(form("value"))

    val json = Json.obj(
      "form"     -> form,
      "index"    -> index.display,
      "mode"     -> mode,
      "lrn"      -> lrn,
      "dateTime" -> viewModel
    )

    renderer.render("arrivalTimesAtOffice.njk", json)
  }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(AddAnotherTransitOfficePage(index)) match {
        case Some(officeOfTransitId) =>
          referenceDataConnector.getCustomsOffice(officeOfTransitId) flatMap {
            office =>
              val form: Form[LocalDate] = formProvider(office.name)

              form
                .bindFromRequest()
                .fold(
                  formWithErrors => loadPage(lrn, index, mode, formWithErrors).map(BadRequest(_)),
                  value =>
                    for {
                      updatedAnswers <- Future
                        .fromTry(request.userAnswers.set(ArrivalTimesAtOfficePage(index), LocalDate.of(value.getYear, value.getMonth, value.getDayOfMonth)))
                      _ <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(ArrivalTimesAtOfficePage(index), mode, updatedAnswers))
                )
          }
        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
      }
  }
}
