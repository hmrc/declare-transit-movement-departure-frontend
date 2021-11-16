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

package controllers.actions

import config.FrontendAppConfig
import models.requests.DataRequest
import models.{DerivableSize, Index}
import play.api.Logging
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{ActionFilter, MessagesControllerComponents, Result}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckValidIndexCompletionAction(
  val index: Index,
  val derivable: DerivableSize[JsObject],
  val controllerComponents: MessagesControllerComponents,
  val appConfig: FrontendAppConfig,
  val renderer: Renderer,
  implicit val executionContext: ExecutionContext
) extends ActionFilter[DataRequest]
    with FrontendBaseController
    with Logging {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {

    val getListLength = request.userAnswers.get(derivable)

    // format: off

    getListLength match {
      case Some(0) | None   if index.position > 0          => renderTechnicalDifficultiesPage(request)
      case Some(listLength) if index.position > listLength => renderTechnicalDifficultiesPage(request)
      case _                                               => Future.successful(None)
    }

    // format: on
  }

  private def renderTechnicalDifficultiesPage[A](request: DataRequest[A]): Future[Some[Result]] = {
    logger.info(s"[CheckValidIndexCompletionAction] Index out of bounds")

    val json = Json.obj(
      "contactUrl" -> appConfig.nctsEnquiriesUrl
    )

    renderer
      .render("technicalDifficulties.njk", json)(request)
      .map(
        x => Some(BadRequest(x))
      )
  }

}

trait CheckValidIndexAction {
  def apply(index: Index, derivableSize: DerivableSize[JsObject]): ActionFilter[DataRequest]
}

class CheckValidIndexActionImpl @Inject() (ec: ExecutionContext,
                                           controllerComponents: MessagesControllerComponents,
                                           appConfig: FrontendAppConfig,
                                           renderer: Renderer
) extends CheckValidIndexAction {

  override def apply(index: Index, derivableSize: DerivableSize[JsObject]): ActionFilter[DataRequest] =
    new CheckValidIndexCompletionAction(index, derivableSize, controllerComponents, appConfig, renderer, ec)
}