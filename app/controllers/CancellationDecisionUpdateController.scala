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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import javax.inject.Inject
import models.{CancellationDecisionUpdateMessage, DepartureId, LocalReferenceNumber}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.DepartureMessageService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Row, Value}
import utils.yesOrNo

import scala.concurrent.{ExecutionContext, Future}

class CancellationDecisionUpdateController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  appConfig: FrontendAppConfig,
  departureMessageService: DepartureMessageService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(departureId: DepartureId): Action[AnyContent] = (identify).async {
    implicit request =>
      departureMessageService.cancellationDecisionUpdateMessage(departureId).flatMap {
        case Some(message) =>
          val json = Json.obj("cancellationDecisionUpdateMessage" -> cancellationDecisionUpdateContent(message), "contactUrl" -> appConfig.nctsEnquiriesUrl)
          renderer.render("cancellationDecisionUpdate.njk", json).map(Ok(_))
        case _ =>
          Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
      }
  }

  def cancellationDecisionUpdateContent(message: CancellationDecisionUpdateMessage): Seq[Row] =
    Seq(
      Row(Key(msg"cancellationDecisionUpdate.mrn"), Value(lit"${message.mrn}"), Seq.empty),
      Row(Key(msg"cancellationDecisionUpdate.cancellationRequestDate"), Value(lit"${message.cancellationRequestDate}"), Seq.empty),
      Row(Key(msg"cancellationDecisionUpdate.initiatedByCustoms"), Value(yesOrNo(message.cancellationInitiatedBy)), Seq.empty),
      Row(Key(msg"cancellationDecisionUpdate.cancellationDecision"), Value(yesOrNo(message.cancellationDecision)), Seq.empty),
      Row(Key(msg"cancellationDecisionUpdate.cancellationDecisionDate"), Value(lit"${message.cancellationDecisionDate}"), Seq.empty),
      Row(Key(msg"cancellationDecisionUpdate.cancellationJustification"), Value(lit"${message.cancellationJustification}"), Seq.empty)
    )

}
