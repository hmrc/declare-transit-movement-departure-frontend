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

package controllers.traderDetails

import controllers.actions._
import controllers.{routes => mainRoutes}
import javax.inject.Inject
import models.{LocalReferenceNumber, UserAnswers}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.TraderDetailsCheckYourAnswersHelper
import viewModels.sections.Section

import scala.concurrent.ExecutionContext

class TraderDetailsCheckYourAnswersController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val sections: Seq[Section] = createSections(request.userAnswers)
      val json = Json.obj(
        "lrn"         -> lrn,
        "sections"    -> Json.toJson(sections),
        "nextPageUrl" -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
      )

      renderer.render("traderDetailsCheckYourAnswers.njk", json).map(Ok(_))
  }

  private def createSections(userAnswers: UserAnswers): Seq[Section] = {
    val checkYourAnswersHelper = new TraderDetailsCheckYourAnswersHelper(userAnswers)

    Seq(
      Section(
        Seq(
          checkYourAnswersHelper.isPrincipalEoriKnown,
          checkYourAnswersHelper.whatIsPrincipalEori,
          checkYourAnswersHelper.principalName,
          checkYourAnswersHelper.principalAddress,
          checkYourAnswersHelper.consignorForAllItems,
          checkYourAnswersHelper.addConsignor,
          checkYourAnswersHelper.isConsignorEoriKnown,
          checkYourAnswersHelper.consignorName,
          checkYourAnswersHelper.consignorAddress,
          checkYourAnswersHelper.consignorEori,
          checkYourAnswersHelper.consigneeForAllItems,
          checkYourAnswersHelper.addConsignee,
          checkYourAnswersHelper.isConsigneeEoriKnown,
          checkYourAnswersHelper.consigneeName,
          checkYourAnswersHelper.consigneeAddress,
          checkYourAnswersHelper.whatIsConsigneeEori,
        ).flatten
      ))
  }
}
