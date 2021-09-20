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
import models.DepartureId
import pages.TechnicalDifficultiesPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.DepartureMessageService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GuaranteeNotValidController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  val renderer: Renderer,
  val appConfig: FrontendAppConfig,
  guaranteeNotValidMessageService: DepartureMessageService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with TechnicalDifficultiesPage {

  def onPageLoad(departureId: DepartureId): Action[AnyContent] = identify.async {
    implicit request =>
      guaranteeNotValidMessageService.guaranteeNotValidMessage(departureId).flatMap {
        case Some(message) =>
          val json = Json.obj(
            "guaranteeNotValidMessage" -> Json.toJson(message),
            "contactUrl"               -> appConfig.nctsEnquiriesUrl,
            "departureUrl"             -> routes.LocalReferenceNumberController.onPageLoad().url
          )

          renderer.render("guaranteeNotValid.njk", json).map(Ok(_))
        case _ =>
          renderTechnicalDifficultiesPage
      }
  }
}
