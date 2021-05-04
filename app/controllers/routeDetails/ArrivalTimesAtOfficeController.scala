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
import pages.{AddAnotherTransitOfficePage, ArrivalTimesAtOfficePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils._
import viewModels.DateTimeInput

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ArrivalTimesAtOfficeController @Inject()(
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
              val form: Form[LocalDateTime] = formProvider(office.name)

              val preparedForm = request.userAnswers.get(ArrivalTimesAtOfficePage(index)) match {
                case Some(value) => form.fill(value)
                case None        => form
              }

              val amOrPm: Option[String] = preparedForm.value.map {
                dateTime =>
                  if (dateTime.getHour > 12) "pm" else "am"
              }

              loadPage(lrn, mode, amOrPm, preparedForm).map(Ok(_))
          }
        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
      }
  }

  private def loadPage(lrn: LocalReferenceNumber, mode: Mode, selectAMPMValue: Option[String], form: Form[LocalDateTime],
  )(implicit request: Request[AnyContent]): Future[Html] = {
    val viewModel = DateTimeInput.localDateTime(form("value"))

    val json = Json.obj(
      "form"     -> form,
      "mode"     -> mode,
      "lrn"      -> lrn,
      "amPmList" -> amPmAsJson(selectAMPMValue),
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
              val form: Form[LocalDateTime] = formProvider(office.name)

              form
                .bindFromRequest()
                .fold(
                  formWithErrors => {
                    loadPage(lrn, mode, formWithErrors.data.get("value.amOrPm"), formWithErrors).map(BadRequest(_))
                  },
                  value =>
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(ArrivalTimesAtOfficePage(index), value))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(ArrivalTimesAtOfficePage(index), mode, updatedAnswers))
                )
          }
        case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))
      }
  }
}
