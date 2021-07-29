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

package controllers.traderDetails

import controllers.actions._
import forms.WhatIsPrincipalEoriFormProvider
import models.ProcedureType.{Normal, Simplified}
import models.reference.{CountryCode, CustomsOffice}
import models.requests.DataRequest
import models.{LocalReferenceNumber, Mode, UserAnswers}
import navigation.Navigator
import navigation.annotations.TraderDetails
import pages.{OfficeOfDeparturePage, PrincipalNamePage, ProcedureTypePage, WhatIsPrincipalEoriPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhatIsPrincipalEoriController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @TraderDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: WhatIsPrincipalEoriFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(OfficeOfDeparturePage) match {
        case Some(officeOfDeparture) =>
          val isSimplified = request.userAnswers.get(ProcedureTypePage).contains(Simplified)
          val preparedForm = request.userAnswers.get(WhatIsPrincipalEoriPage) match {
            case None        => formProvider(isSimplified, officeOfDeparture.countryId)
            case Some(value) => formProvider(isSimplified, officeOfDeparture.countryId).fill(value)
          }
          val json = Json.obj(
            "form" -> preparedForm,
            "lrn"  -> lrn,
            "mode" -> mode
          )
          renderer.render("whatIsPrincipalEori.njk", json).map(Ok(_))
        case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(OfficeOfDeparturePage) match {
        case Some(officeOfDeparture) =>
          val isSimplified = request.userAnswers.get(ProcedureTypePage) match {
            case Some(Simplified) => true
            case _                => false
          }

          formProvider(isSimplified, officeOfDeparture.countryId)
            .bindFromRequest()
            .fold(
              formWithErrors => {
                val json = Json.obj(
                  "form" -> formWithErrors,
                  "lrn"  -> lrn,
                  "mode" -> mode
                )
                renderer.render("whatIsPrincipalEori.njk", json).map(BadRequest(_))
              },
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsPrincipalEoriPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(WhatIsPrincipalEoriPage, mode, updatedAnswers))
            )
        case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))

      }
  }

}
