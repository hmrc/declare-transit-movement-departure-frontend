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
import models.{LocalReferenceNumber, ValidateTaskListViewLogger}
import pages.TechnicalDifficultiesPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.DeclarationSubmissionService
import uk.gov.hmrc.http.HttpReads.{is2xx, is4xx}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.DeclarationSummaryViewModel
import uk.gov.hmrc.mongo.lock.MongoLockRepository
import javax.inject.Inject
import play.api.libs.json.{Format, JsObject, Json, Reads}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}

class DeclarationSummaryController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  val renderer: Renderer,
  val appConfig: FrontendAppConfig,
  errorHandler: ErrorHandler,
  submissionService: DeclarationSubmissionService,
  mongoLockRepository: MongoLockRepository
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with ValidateTaskListViewLogger
    with TechnicalDifficultiesPage {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val declarationSummaryViewModel = DeclarationSummaryViewModel(appConfig.manageTransitMovementsViewDeparturesUrl, request.userAnswers)

      ValidateTaskListViewLogger(declarationSummaryViewModel.sectionErrors)
      renderer
        .render("declarationSummary.njk", declarationSummaryViewModel)
        .map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        val jsonGuarantees = Json.parse(request.userAnswers.data.value.get("guarantees").getOrElse("").toString)

        implicit val a: Format[guarantees] = Json.format[guarantees]
        implicit val b: Reads[guarantees]  = Json.reads[guarantees]

        val listOfGuarantees = jsonGuarantees.as[List[guarantees]]
        val owner            = java.util.UUID.randomUUID().toString
        val duration         = 3600.seconds

     //   try {

          mongoLockRepository
            .takeLock((request.userAnswers.eoriNumber.toString + listOfGuarantees.head.guaranteeReference.trim.toLowerCase).hashCode.toString, owner, duration)
            .flatMap{
              lockTaken => if(true) {
                submissionService.submit(request.userAnswers) flatMap {

                  case Right(value) =>
                    println("\n\n\n\n\n\n\n\nsubmissionService.submit called\n\n\n\n\n\n")
                    value.status match {
                      case status if is2xx(status) => Future.successful(Redirect(routes.SubmissionConfirmationController.onPageLoad(lrn)))
                      case status if is4xx(status) => errorHandler.onClientError(request, status)
                      case _                       => renderTechnicalDifficultiesPage
                    }

                  case Left(_) => // TODO we can pass this value back to help debug
                    println("\n\n\n\n\n\n\n\nsubmissionService.submit called\n\n\n\n\n\n")
                    errorHandler.onClientError(request, BAD_REQUEST)
                }
              }else{
                Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
              }
            }


     /*   val afterForeach: Unit =   listOfGuarantees.foreach {
            guarantee =>
              println(s"***X*****\n\n\n\n\n\n\n\n$guarantee\n\n\n\n\n\n")
              mongoLockRepository
                .takeLock((request.userAnswers.eoriNumber.toString + guarantee.guaranteeReference.trim.toLowerCase).hashCode.toString, owner, duration)
                .onComplete {
                  taken =>
                    if (taken.get) {
                      println(s"**true\n\n\n\n\n\n\n\ntaken!!!!!$taken\n\n\n\n\n\n")
                      Future.successful(true)
                    } else {
                      println(s"**false\n\n\n\n\n\n\n\ntaken!!!!!$taken\n\n\n\n\n\n")
                      throw new Exception("Rate Limits have been surpassed ")
                    }
                }
          }*/

        /*  submissionService.submit(request.userAnswers) flatMap {

            case Right(value) =>
              println("\n\n\n\n\n\n\n\nsubmissionService.submit called\n\n\n\n\n\n")
              value.status match {
                case status if is2xx(status) => Future.successful(Redirect(routes.SubmissionConfirmationController.onPageLoad(lrn)))
                case status if is4xx(status) => errorHandler.onClientError(request, status)
                case _                       => renderTechnicalDifficultiesPage
              }

            case Left(_) => // TODO we can pass this value back to help debug
              println("\n\n\n\n\n\n\n\nsubmissionService.submit called\n\n\n\n\n\n")
              errorHandler.onClientError(request, BAD_REQUEST)
          }

        } catch {
          case e: Exception =>
            println(s"**Exception***\n\n\n\n\n\n\n${e.getMessage}\n\n\n\n\n\n\n")
            Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
        }*/

    }

  case class guarantees(
    guaranteeType: String,
    guaranteeReference: String,
    liabilityAmount: String,
    accessCode: String
  )

}
