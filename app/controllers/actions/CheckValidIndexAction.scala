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
import pages.TechnicalDifficultiesPage
import play.api.Logging
import play.api.mvc.{ActionFilter, MessagesControllerComponents, Result}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckValidIndexCompletionAction(
  val index: Index,
  val derivable: DerivableSize,
  val controllerComponents: MessagesControllerComponents,
  val appConfig: FrontendAppConfig,
  val renderer: Renderer,
  implicit val executionContext: ExecutionContext
) extends ActionFilter[DataRequest]
    with FrontendBaseController
    with TechnicalDifficultiesPage
    with Logging {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {

    val getListLength = request.userAnswers.get(derivable).getOrElse(0)
    val listToIndex   = if (getListLength > 0) getListLength else 1

    if (index.position <= listToIndex) {
      Future.successful(None)
    } else {
      logger.info(s"[CheckValidIndexCompletionAction] Index out of bounds")
      renderTechnicalDifficultiesPage(request, executionContext).map(Some(_))
    }
  }

}

trait CheckValidIndexAction {
  def apply(index: Index, derivableSize: DerivableSize): ActionFilter[DataRequest]
}

class CheckValidIndexActionImpl @Inject() (ec: ExecutionContext,
                                           controllerComponents: MessagesControllerComponents,
                                           appConfig: FrontendAppConfig,
                                           renderer: Renderer
) extends CheckValidIndexAction {

  override def apply(index: Index, derivableSize: DerivableSize): ActionFilter[DataRequest] =
    new CheckValidIndexCompletionAction(index, derivableSize, controllerComponents, appConfig, renderer, ec)
}
