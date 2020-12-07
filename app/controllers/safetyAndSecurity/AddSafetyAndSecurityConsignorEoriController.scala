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

package controllers.safetyAndSecurity

import controllers.actions._
import forms.safetyAndSecurity.AddSafetyAndSecurityConsignorEoriFormProvider
import javax.inject.Inject
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurityTraderDetails
import pages.safetyAndSecurity.AddSafetyAndSecurityConsignorEoriPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class AddSafetyAndSecurityConsignorEoriController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SafetyAndSecurityTraderDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: AddSafetyAndSecurityConsignorEoriFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "safetyAndSecurity/addSafetyAndSecurityConsignorEori.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(AddSafetyAndSecurityConsignorEoriPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "lrn"    -> lrn,
        "radios" -> Radios.yesNo(preparedForm("value"))
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"   -> formWithErrors,
              "mode"   -> mode,
              "lrn"    -> lrn,
              "radios" -> Radios.yesNo(formWithErrors("value"))
            )

            renderer.render(template, json).map(BadRequest(_))
          },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AddSafetyAndSecurityConsignorEoriPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(AddSafetyAndSecurityConsignorEoriPage, mode, updatedAnswers))
        )
  }
}
