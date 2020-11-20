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

import controllers.actions._
import javax.inject.Inject
import models.{DepartureId, LocalReferenceNumber, RejectionError}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

class DeclarationRejectionController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(departureId: DepartureId): Action[AnyContent] = (identify).async {
    implicit request =>
      val json = Json.obj(
        "rejectionReason" -> "It is rejected due to If the caption should be considered part of the page heading",
        "errors" -> Seq(
          Json.obj("errorType" -> "ABC", "pointer" -> "Sample Pointer 1", "reason" -> "Something messed up!"),
          Json.obj("errorType" -> "DEF", "pointer" -> "Sample Pointer 2", "reason" -> "Something messed up too!")
        )
      )
      renderer.render("declarationRejection.njk", json).map(Ok(_))
  }
}
