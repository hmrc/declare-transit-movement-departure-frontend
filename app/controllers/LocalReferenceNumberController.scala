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

package controllers

import controllers.actions._
import forms.LocalReferenceNumberFormProvider
import models.{EoriNumber, LocalReferenceNumber, NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import pages.LocalReferenceNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LocalReferenceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @PreTaskListDetails navigator: Navigator,
  identify: IdentifierAction,
  formProvider: LocalReferenceNumberFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad: Action[AnyContent] = identify.async {
    implicit request =>
      val json = Json.obj("form" -> form)

      renderer.render("localReferenceNumber.njk", json).map(Ok(_))
  }

  def onSubmit: Action[AnyContent] = identify.async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form" -> formWithErrors
            )

            renderer.render("localReferenceNumber.njk", json).map(BadRequest(_))
          },
          value =>
            for {
              userAnswers <- getOrCreateUserAnswers(request.eoriNumber, value)
              _           <- sessionRepository.set(userAnswers)
            } yield Redirect(navigator.nextPage(LocalReferenceNumberPage, NormalMode, userAnswers))
        )
  }

  def getOrCreateUserAnswers(eoriNumber: EoriNumber, value: LocalReferenceNumber): Future[UserAnswers] = {
    val initialUserAnswers = UserAnswers(lrn = value, eoriNumber = eoriNumber)

    sessionRepository.get(id = value, eoriNumber = eoriNumber) map {
      userAnswers =>
        userAnswers getOrElse initialUserAnswers
    }
  }
}
