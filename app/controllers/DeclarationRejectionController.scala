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
import models.{DeclarationRejectionMessage, DepartureId}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.DepartureMessageService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Row, Value}
import utils.Format.dateFormatterMonthName
import viewModels.sections.Section
import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationRejectionController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  appConfig: FrontendAppConfig,
  departureMessageService: DepartureMessageService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def errorSections(message: DeclarationRejectionMessage): Seq[Section] = message.errors map {
    error =>
      val rows = Seq(
        Row(Key(msg"declarationRejection.errorType"), Value(lit"${error.errorType}"), Seq.empty),
        Row(Key(msg"declarationRejection.pointer"), Value(lit"${error.pointer}"), Seq.empty)
      )

      val completed = if (error.reason.nonEmpty) {
        rows :+ Row(Key(msg"declarationRejection.reason"), Value(lit"${error.reason.get}"), Seq.empty)
      } else {
        rows
      }

      Section(completed)
  }

  private def detailsSection(message: DeclarationRejectionMessage): Seq[Section] = {
    val rejectionDate = LocalDate.parse(message.rejectionDate.toString)
    val displayDate   = dateFormatterMonthName.format(rejectionDate)

    val rows = Seq(
      Row(Key(msg"declarationRejection.localReferenceNumber"), Value(lit"${message.reference}"), Seq.empty),
      Row(Key(msg"declarationRejection.date"), Value(lit"$displayDate"), Seq.empty)
    )

    val completedRows = if (message.reason.nonEmpty) {
      rows :+ Row(Key(msg"declarationRejection.rejectionReason"), Value(lit"${message.reason.get}"))
    } else {
      rows
    }

    Seq(Section(msg"declarationRejection.declarationDetails", completedRows))
  }

  def onPageLoad(departureId: DepartureId): Action[AnyContent] = (identify).async {
    implicit request =>
      departureMessageService.declarationRejectionMessage(departureId).flatMap {
        case Some(message) =>
          val json = Json.obj(
            "detailsSection" -> detailsSection(message),
            "errorsSection"  -> errorSections(message),
            "contactUrl"     -> appConfig.nctsEnquiriesUrl
          )
          renderer.render("declarationRejection.njk", json).map(Ok(_))
        case _ =>
          Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
      }
  }
}
