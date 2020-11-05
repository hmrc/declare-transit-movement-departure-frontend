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

package controllers.addItems

import connectors.ReferenceDataConnector
import controllers.actions._
import controllers.{routes => mainRoutes}
import javax.inject.Inject
import models.{Index, LocalReferenceNumber}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewModels.AddItemsCheckYourAnswersViewModel
import viewModels.sections.Section

import scala.concurrent.ExecutionContext

class ItemsCheckYourAnswersController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, index: Index): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getPreviousDocumentTypes() flatMap {
        documentTypes =>
          val sections: Seq[Section] = AddItemsCheckYourAnswersViewModel(request.userAnswers, index, documentTypes).sections
          val json = Json.obj(
            "lrn"         -> lrn,
            "sections"    -> Json.toJson(sections),
            "nextPageUrl" -> routes.AddAnotherItemController.onPageLoad(lrn).url
          )

          renderer.render("itemsCheckYourAnswers.njk", json).map(Ok(_))
      }
  }
}
