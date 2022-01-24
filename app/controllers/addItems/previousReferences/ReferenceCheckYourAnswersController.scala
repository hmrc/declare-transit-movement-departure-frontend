/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.addItems.previousReferences

import controllers.actions._
import models.{Index, LocalReferenceNumber, Mode, ValidateReaderLogger}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.PreviousDocumentTypesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ReferencesCheckYourAnswersViewModel

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ReferenceCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  previousDocumentTypesService: PreviousDocumentTypesService,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with ValidateReaderLogger
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData).async {
      implicit request =>
        previousDocumentTypesService.getPreviousDocumentTypes().flatMap {
          previousReferencesDocumentTypes =>
            val json = {
              val viewModel = ReferencesCheckYourAnswersViewModel(request.userAnswers, itemIndex, referenceIndex, mode, previousReferencesDocumentTypes)

              Json.obj(
                "section"     -> Json.toJson(viewModel.section),
                "nextPageUrl" -> routes.AddAnotherPreviousAdministrativeReferenceController.onPageLoad(lrn, itemIndex, mode).url
              )
            }

            renderer.render("addItems/referenceCheckYourAnswers.njk", json).map(Ok(_))
        }
    }
}
