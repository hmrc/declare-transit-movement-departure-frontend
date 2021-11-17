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
import forms.SealIdDetailsFormProvider
import models.domain.SealDomain
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.GoodsSummary
import pages.SealIdDetailsPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.SealsQuery
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SealIdDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @GoodsSummary navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  checkValidIndexAction: CheckValidIndexAction,
  requireData: DataRequiredAction,
  formProvider: SealIdDetailsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider

  def onPageLoad(lrn: LocalReferenceNumber, sealIndex: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(SealIdDetailsPage(sealIndex)) match {
        case Some(value) => form(sealIndex).fill(value.numberOrMark)
        case _           => form(sealIndex)
      }

      renderView(lrn, sealIndex, mode, preparedForm).map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, sealIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkValidIndexAction(sealIndex, DeriveNumberOfSeals)
      ).async {
      implicit request =>
        val seals = request.userAnswers.get(SealsQuery()).getOrElse(Seq.empty)

        form(sealIndex, seals)
          .bindFromRequest()
          .fold(
            formWithErrors => renderView(lrn, sealIndex, mode, formWithErrors).map(BadRequest(_)),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(SealIdDetailsPage(sealIndex), SealDomain(value)))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(SealIdDetailsPage(sealIndex), mode, updatedAnswers))
          )
    }

  private def renderView(lrn: LocalReferenceNumber, sealIndex: Index, mode: Mode, preparedForm: Form[String])(implicit request: DataRequest[AnyContent]) = {
    val json = Json.obj(
      "form"        -> preparedForm,
      "lrn"         -> lrn,
      "mode"        -> mode,
      "sealIndex"   -> sealIndex.display,
      "onSubmitUrl" -> routes.SealIdDetailsController.onSubmit(lrn, sealIndex, mode).url
    )

    renderer.render("sealIdDetails.njk", json)
  }
}
