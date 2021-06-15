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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import handlers.ErrorHandler
import models.reference.CustomsOffice
import models.{LocalReferenceNumber, NormalMode, ValidateTaskListViewLogger}
import pages.{OfficeOfDeparturePage, TechnicalDifficultiesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.DeclarationSubmissionService
import uk.gov.hmrc.http.HttpReads.{is2xx, is4xx}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewModels.DeclarationSummaryViewModel

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationSummaryController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  val renderer: Renderer,
  val appConfig: FrontendAppConfig,
  errorHandler: ErrorHandler,
  submissionService: DeclarationSubmissionService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with ValidateTaskListViewLogger
    with TechnicalDifficultiesPage {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val officeOfDepartureAnswer: Option[CustomsOffice] = request.userAnswers.get(OfficeOfDeparturePage)

      val declarationSummaryViewModel = DeclarationSummaryViewModel(appConfig.manageTransitMovementsViewDeparturesUrl, request.userAnswers)

      ValidateTaskListViewLogger(declarationSummaryViewModel.sectionErrors)

      //TODO To be removed when confident all users have completed their journey following 2309 deployment
      if (officeOfDepartureAnswer.isDefined) {
        renderer
          .render("declarationSummary.njk", declarationSummaryViewModel)
          .map(Ok(_))
      } else {
        Future.successful(Redirect(routes.OfficeOfDepartureController.onPageLoad(lrn, NormalMode)))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        submissionService.submit(request.userAnswers) flatMap {

          case Right(value) =>
            value.status match {
              case status if is2xx(status) => Future.successful(Redirect(routes.SubmissionConfirmationController.onPageLoad(lrn)))
              case status if is4xx(status) => errorHandler.onClientError(request, status)
              case _ =>
                renderTechnicalDifficultiesPage
            }
          case Left(_) => // TODO we can pass this value back to help debug
            errorHandler.onClientError(request, BAD_REQUEST)
        }
    }
}
