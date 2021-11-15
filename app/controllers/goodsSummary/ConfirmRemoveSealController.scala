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

package controllers.goodsSummary

import controllers.actions._
import derivable.DeriveNumberOfSeals
import forms.generic.YesNoFormProvider
import handlers.ErrorHandler
import models.domain.SealDomain
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.GoodsSummary
import pages.{ConfirmRemoveSealPage, SealIdDetailsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveSealController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @GoodsSummary navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: YesNoFormProvider,
  errorHandler: ErrorHandler,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val confirmRemoveSealTemplate = "/confirmRemoveSeal.njk"

  private def form(sealDomain: SealDomain): Form[Boolean] = formProvider("confirmRemoveSeal", Seq(sealDomain.numberOrMark))

  def onPageLoad(lrn: LocalReferenceNumber, sealIndex: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        request.userAnswers.get(SealIdDetailsPage(sealIndex)) match {
          case Some(seal) =>
            renderPage(lrn, sealIndex, mode, form(seal), seal).map(Ok(_))
          case _ =>
            renderErrorPage(mode)
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, sealIndex: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        request.userAnswers.get(SealIdDetailsPage(sealIndex)) match {
          case Some(seal) =>
            form(seal)
              .bindFromRequest()
              .fold(
                formWithErrors => renderPage(lrn, sealIndex, mode, formWithErrors, seal).map(BadRequest(_)),
                value =>
                  if (value) {
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.remove(SealIdDetailsPage(sealIndex)))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(ConfirmRemoveSealPage(), mode, updatedAnswers))
                  } else {
                    Future.successful(Redirect(navigator.nextPage(ConfirmRemoveSealPage(), mode, request.userAnswers)))
                  }
              )
          case _ =>
            renderErrorPage(mode)
        }
    }

  private def renderPage(lrn: LocalReferenceNumber, sealIndex: Index, mode: Mode, form: Form[Boolean], seal: SealDomain)(implicit
    request: DataRequest[AnyContent]
  ): Future[Html] = {
    val json = Json.obj(
      "form"        -> form,
      "mode"        -> mode,
      "lrn"         -> lrn,
      "sealNumber"  -> seal.numberOrMark,
      "radios"      -> Radios.yesNo(form("value")),
      "onSubmitUrl" -> routes.ConfirmRemoveSealController.onSubmit(lrn, sealIndex, mode).url
    )
    renderer.render(confirmRemoveSealTemplate, json)

  }

  private def renderErrorPage(mode: Mode)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    val redirectLinkText = if (request.userAnswers.get(DeriveNumberOfSeals).contains(0)) "noSeal" else "multipleSeal"
    val redirectLink     = navigator.nextPage(ConfirmRemoveSealPage(), mode, request.userAnswers).url

    errorHandler.onConcurrentError(redirectLinkText, redirectLink, "concurrent.seal")
  }
}
