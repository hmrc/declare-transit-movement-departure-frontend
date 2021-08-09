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

package controllers.addItems.itemDetails

import controllers.actions._
import forms.ConfirmStartAddItemsFormProvider
import logging.Logging
import models.{DependentSection, LocalReferenceNumber, NormalMode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsItemDetails
import pages.AddSecurityDetailsPage
import pages.addItems.ConfirmStartAddItemsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmStartAddItemsController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsItemDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  checkDependentSection: CheckDependentSectionAction,
  requireData: DataRequiredAction,
  formProvider: ConfirmStartAddItemsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with Logging {

  private val form     = formProvider()
  private val template = "addItems/confirmStartAddItems.njk"

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = (identify
    andThen getData(lrn)
    andThen requireData
    andThen checkDependentSection(DependentSection.ItemDetails)).async {
    implicit request =>
      request.userAnswers
        .get(AddSecurityDetailsPage)
        .map {
          safetyAndSecurity =>
            val json = Json.obj(
              "form"   -> form,
              "lrn"    -> lrn,
              "safety" -> safetyAndSecurity,
              "radios" -> Radios.yesNo(form("value"))
            )

            renderer.render(template, json).map(Ok(_))
        }
        .getOrElse {
          logger.warn("[onPageLoad] redirecting to session expired controller because AddSecurityDetailsPage not answered")
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
        }
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = (identify
    andThen getData(lrn)
    andThen requireData
    andThen checkDependentSection(DependentSection.ItemDetails)).async {
    implicit request =>
      request.userAnswers
        .get(AddSecurityDetailsPage)
        .map {
          safetyAndSecurity =>
            form
              .bindFromRequest()
              .fold(
                formWithErrors => {

                  val json = Json.obj(
                    "form"   -> formWithErrors,
                    "lrn"    -> lrn,
                    "safety" -> safetyAndSecurity,
                    "radios" -> Radios.yesNo(formWithErrors("value"))
                  )

                  renderer.render(template, json).map(BadRequest(_))
                },
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(ConfirmStartAddItemsPage, value))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield (Redirect(navigator.nextPage(ConfirmStartAddItemsPage, NormalMode, updatedAnswers)))
              )
        }
        .getOrElse {
          logger.warn("[onSubmit] redirecting to session expired controller because AddSecurityDetailsPage not answered")
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
        }
  }
}
