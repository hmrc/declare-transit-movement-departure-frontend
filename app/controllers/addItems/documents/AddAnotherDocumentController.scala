/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.addItems.documents

import config.FrontendAppConfig
import controllers.actions._
import derivable.DeriveNumberOfDocuments
import forms.addItems.AddAnotherDocumentFormProvider
import models.requests.DataRequest
import models.{DependentSection, Index, LocalReferenceNumber, Mode, UserAnswers}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsDocument
import pages.addItems.AddAnotherDocumentPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import services.DocumentTypesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.AddItemsCheckYourAnswersHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherDocumentController @Inject() (
  override val messagesApi: MessagesApi,
  @AddItemsDocument navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: AddAnotherDocumentFormProvider,
  documentTypesService: DocumentTypesService,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private def allowMoreDocuments(ua: UserAnswers, index: Index): Boolean =
    ua.get(DeriveNumberOfDocuments(index)).getOrElse(0) < config.maxDocuments

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        renderPage(lrn, index, mode, formProvider(allowMoreDocuments(request.userAnswers, index))).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        formProvider(allowMoreDocuments(request.userAnswers, index))
          .bindFromRequest()
          .fold(
            formWithErrors => renderPage(lrn, index, mode, formWithErrors).map(BadRequest(_)),
            value => {
              val onwardRoute = if (value) {
                val documentCount = request.userAnswers.get(DeriveNumberOfDocuments(index)).getOrElse(0)
                val documentIndex = Index(documentCount)
                routes.DocumentTypeController.onPageLoad(request.userAnswers.lrn, index, documentIndex, mode)
              } else {
                navigator.nextPage(AddAnotherDocumentPage(index), mode, request.userAnswers)
              }

              Future.successful(Redirect(onwardRoute))
            }
          )
    }

  private def renderPage(lrn: LocalReferenceNumber, index: Index, mode: Mode, form: Form[Boolean])(implicit
    request: DataRequest[AnyContent]
  ): Future[Html] = {

    val cyaHelper             = new AddItemsCheckYourAnswersHelper(request.userAnswers, mode)
    val numberOfDocuments     = request.userAnswers.get(DeriveNumberOfDocuments(index)).getOrElse(0)
    val indexList: Seq[Index] = List.range(0, numberOfDocuments).map(Index(_))

    documentTypesService.getDocumentTypes() flatMap {
      documents =>
        val documentRows = indexList.map {
          documentIndex =>
            cyaHelper.documentRow(index, documentIndex, documents)
        }

        val singularOrPlural = if (numberOfDocuments == 1) "singular" else "plural"
        val json = Json.obj(
          "form"               -> form,
          "lrn"                -> lrn,
          "index"              -> index.display,
          "mode"               -> mode,
          "pageTitle"          -> msg"addAnotherDocument.title.$singularOrPlural".withArgs(numberOfDocuments),
          "heading"            -> msg"addAnotherDocument.heading.$singularOrPlural".withArgs(numberOfDocuments),
          "documentRows"       -> documentRows,
          "radios"             -> Radios.yesNo(form("value")),
          "allowMoreDocuments" -> allowMoreDocuments(request.userAnswers, index)
        )

        renderer.render("addItems/addAnotherDocument.njk", json)
    }
  }
}
