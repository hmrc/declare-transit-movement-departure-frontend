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

package controllers.addItems.traderSecurityDetails

import controllers.actions._
import forms.addItems.traderSecurityDetails.SecurityConsigneeEoriFormProvider
import javax.inject.Inject
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.AddItems
import pages.addItems.traderSecurityDetails.SecurityConsigneeEoriPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class SecurityConsigneeEoriController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItems navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: SecurityConsigneeEoriFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "addItems/traderSecurityDetails/securityConsigneeEori.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(SecurityConsigneeEoriPage(index)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "lrn"  -> lrn,
        "mode" -> mode
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form" -> formWithErrors,
              "lrn"  -> lrn,
              "mode" -> mode
            )

            renderer.render(template, json).map(BadRequest(_))
          },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(SecurityConsigneeEoriPage(index), value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(SecurityConsigneeEoriPage(index), mode, updatedAnswers))
        )
  }
}