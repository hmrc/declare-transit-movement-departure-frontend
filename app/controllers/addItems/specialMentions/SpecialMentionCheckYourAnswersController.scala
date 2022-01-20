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

package controllers.addItems.specialMentions

import controllers.actions._
import models.{Index, LocalReferenceNumber, Mode, ValidateReaderLogger}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.SpecialMentionTypesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.SpecialMentionsCheckYourAnswersViewModel

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class SpecialMentionCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  specialMentionTypesService: SpecialMentionTypesService,
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
        specialMentionTypesService.getSpecialMentionTypes().flatMap {
          specialMentions =>
            val json = {
              val viewModel = SpecialMentionsCheckYourAnswersViewModel(request.userAnswers, itemIndex, referenceIndex, mode, specialMentions)

              Json.obj(
                "section"     -> Json.toJson(viewModel.section),
                "nextPageUrl" -> routes.AddAnotherSpecialMentionController.onPageLoad(lrn, itemIndex, mode).url
              )
            }

            renderer.render("addItems/specialMentions/specialMentionCheckYourAnswers.njk", json).map(Ok(_))
        }
    }
}
