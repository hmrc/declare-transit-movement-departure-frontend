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

package controllers.guaranteeDetails

import cats.data.NonEmptyList
import controllers.actions._
import models.journeyDomain.GuaranteeDetails
import models.{DependentSection, Index, LocalReferenceNumber, ValidateReaderLogger}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewModels.GuaranteeDetailsCheckYourAnswersViewModel
import viewModels.sections.Section

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GuaranteeDetailsCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with ValidateReaderLogger
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, index: Index): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.GuaranteeDetails)).async {
      implicit request =>
        val sections: Seq[Section] = GuaranteeDetailsCheckYourAnswersViewModel(request.userAnswers, index).sections
        val json = Json.obj(
          "lrn"      -> lrn,
          "sections" -> Json.toJson(sections),
          "nextPage" -> routes.AddAnotherGuaranteeController.onPageLoad(lrn).url
        )

        ValidateReaderLogger[NonEmptyList[GuaranteeDetails]](request.userAnswers)

        renderer.render("guaranteeDetailsCheckYourAnswers.njk", json).map(Ok(_))
    }
}
