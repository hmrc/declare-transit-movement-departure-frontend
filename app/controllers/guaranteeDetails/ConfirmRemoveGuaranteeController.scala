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

import controllers.actions._
import derivable.DeriveNumberOfGuarantees
import forms.ConfirmRemoveGuaranteeFormProvider
import models.{DependentSection, Index, LocalReferenceNumber, NormalMode}
import navigation.Navigator
import navigation.annotations.GuaranteeDetails
import pages.ConfirmRemoveGuaranteePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.GuaranteesQuery
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveGuaranteeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @GuaranteeDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  checkValidIndexAction: CheckValidIndexAction,
  formProvider: ConfirmRemoveGuaranteeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "guaranteeDetails/confirmRemoveGuarantee.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.GuaranteeDetails)).async {
      implicit request =>
        val json = Json.obj(
          "form"   -> form,
          "index"  -> index.display,
          "lrn"    -> lrn,
          "radios" -> Radios.yesNo(form("value"))
        )

        renderer.render(template, json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.GuaranteeDetails)
      andThen checkValidIndexAction(index, DeriveNumberOfGuarantees)).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => {

              val json = Json.obj(
                "form"   -> formWithErrors,
                "index"  -> index.display,
                "lrn"    -> lrn,
                "radios" -> Radios.yesNo(formWithErrors("value"))
              )

              renderer.render(template, json).map(BadRequest(_))
            },
            value =>
              Future.fromTry(request.userAnswers.set(ConfirmRemoveGuaranteePage, value)).flatMap {
                updatedAnswers =>
                  if (value) {
                    for {
                      deletedAnswers <- Future.fromTry(updatedAnswers.remove(GuaranteesQuery(index)))
                      _              <- sessionRepository.set(deletedAnswers)
                    } yield Redirect(navigator.nextPage(ConfirmRemoveGuaranteePage, NormalMode, deletedAnswers))
                  } else {
                    Future.successful(Redirect(navigator.nextPage(ConfirmRemoveGuaranteePage, NormalMode, updatedAnswers)))
                  }
              }
          )
    }
}
