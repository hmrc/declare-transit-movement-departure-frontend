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
import forms.LiabilityAmountFormProvider
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.GuaranteeDetails
import pages.LiabilityAmountPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LiabilityAmountController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @GuaranteeDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  checkDependentSection: CheckDependentSectionAction,
  checkValidIndexAction: CheckValidIndexAction,
  requireData: DataRequiredAction,
  formProvider: LiabilityAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.GuaranteeDetails)).async {
      implicit request =>
        val preparedForm = request.userAnswers.get(LiabilityAmountPage(index)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        val json = Json.obj(
          "form"  -> preparedForm,
          "index" -> index.display,
          "lrn"   -> lrn,
          "mode"  -> mode
        )

        renderer.render("liabilityAmount.njk", json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
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
                "form"  -> formWithErrors,
                "index" -> index.display,
                "lrn"   -> lrn,
                "mode"  -> mode
              )

              renderer.render("liabilityAmount.njk", json).map(BadRequest(_))
            },
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(LiabilityAmountPage(index), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(LiabilityAmountPage(index), mode, updatedAnswers))
          )
    }
}
