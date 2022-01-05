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

package controllers.safetyAndSecurity

import connectors.ReferenceDataConnector
import controllers.actions._
import controllers.{routes => mainRoutes}
import models.journeyDomain.SafetyAndSecurity
import models.{DependentSection, LocalReferenceNumber, NormalMode, ValidateReaderLogger}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.SafetyAndSecurityCheckYourAnswersViewModel
import viewModels.sections.Section

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SafetyAndSecurityCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  referenceDataConnector: ReferenceDataConnector,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with ValidateReaderLogger
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        val buildJson: Future[JsObject] =
          for {
            countries              <- referenceDataConnector.getCountryList()
            circumstanceIndicators <- referenceDataConnector.getCircumstanceIndicatorList()
          } yield {
            val sections: Seq[Section] =
              SafetyAndSecurityCheckYourAnswersViewModel(request.userAnswers, countries, circumstanceIndicators)

            Json.obj(
              "lrn"                           -> lrn,
              "sections"                      -> Json.toJson(sections),
              "addAnotherCountryOfRoutingUrl" -> routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, NormalMode).url,
              "nextPageUrl"                   -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
            )
          }

        ValidateReaderLogger[SafetyAndSecurity](request.userAnswers)

        buildJson.flatMap(renderer.render("safetyAndSecurity/SafetyAndSecurityCheckYourAnswers.njk", _).map(Ok(_)))
    }
}
