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
import models.{DeclarationRejectionMessage, DepartureId}
import pages.TechnicalDifficultiesPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.DepartureMessageService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Row, Value}
import viewModels.sections.Section

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DeclarationRejectionController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  val renderer: Renderer,
  val appConfig: FrontendAppConfig,
  departureMessageService: DepartureMessageService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with TechnicalDifficultiesPage {

  private def errorSections(message: DeclarationRejectionMessage): Seq[Section] = message.errors map {
    error =>
      val rows = Seq(
        Row(Key(msg"declarationRejection.code"), Value(lit"${error.errorCode}")),
        Row(Key(msg"declarationRejection.pointer"), Value(lit"${error.pointer}"))
      )

      Section(rows)
  }

  def onPageLoad(departureId: DepartureId): Action[AnyContent] = identify.async {
    implicit request =>
      departureMessageService.declarationRejectionMessage(departureId).flatMap {
        case Some(message) =>
          val json = Json.obj(
            "errorsSection" -> errorSections(message),
            "contactUrl"    -> appConfig.nctsEnquiriesUrl,
            "departureUrl"  -> routes.LocalReferenceNumberController.onPageLoad().url
          )
          renderer.render("declarationRejection.njk", json).map(Ok(_))
        case _ =>
          renderTechnicalDifficultiesPage
      }
  }
}
