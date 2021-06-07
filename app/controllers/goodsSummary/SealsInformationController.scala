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
import forms.SealsInformationFormProvider
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.GoodsSummary
import pages.SealsInformationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.AddSealHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SealsInformationController @Inject() (
  override val messagesApi: MessagesApi,
  @GoodsSummary navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: SealsInformationFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      renderPage(lrn, mode, form)
        .map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            renderPage(lrn, mode, formWithErrors)
              .map(BadRequest(_)),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(SealsInformationPage, value))
            } yield Redirect(navigator.nextPage(SealsInformationPage, mode, updatedAnswers))
        )
  }

  private def renderPage(lrn: LocalReferenceNumber, mode: Mode, form: Form[Boolean])(implicit request: DataRequest[AnyContent]): Future[Html] = {

    val numberOfSeals    = request.userAnswers.get(DeriveNumberOfSeals).getOrElse(0)
    val listOfSealsIndex = List.range(0, numberOfSeals).map(Index(_))
    val sealsRows = listOfSealsIndex.flatMap {
      index =>
        AddSealHelper.apply(request.userAnswers).sealRow(lrn, index, mode)

    }

    val singularOrPlural = if (numberOfSeals == 1) "singular" else "plural"
    val onSubmit = if (numberOfSeals < 10) {
      routes.SealsInformationController.onSubmit(lrn, mode).url
    } else { routes.GoodsSummaryCheckYourAnswersController.onPageLoad(lrn).url }

    val json = Json.obj(
      "form"        -> form,
      "mode"        -> mode,
      "lrn"         -> lrn,
      "pageTitle"   -> msg"sealsInformation.title.$singularOrPlural".withArgs(numberOfSeals),
      "heading"     -> msg"sealsInformation.heading.$singularOrPlural".withArgs(numberOfSeals),
      "seals"       -> sealsRows,
      "radios"      -> Radios.yesNo(form("value")),
      "onSubmitUrl" -> onSubmit
    )

    if (numberOfSeals < 10) {
      renderer.render("sealsInformation.njk", json)
    } else {
      renderer.render("maximumSealsInformation.njk", json)

    }
  }
}
