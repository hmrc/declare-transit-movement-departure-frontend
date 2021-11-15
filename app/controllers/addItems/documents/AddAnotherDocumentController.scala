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

package controllers.addItems.documents

import connectors.ReferenceDataConnector
import controllers.actions._
import derivable.DeriveNumberOfDocuments
import forms.generic.YesNoFormProvider
import models.requests.DataRequest
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsDocument
import pages.addItems.AddAnotherDocumentPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.AddItemsCheckYourAnswersHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherDocumentController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsDocument navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: YesNoFormProvider,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private def form(index: Index): Form[Boolean] = formProvider("addAnotherDocument", index)

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        renderPage(lrn, index, mode, form(index)).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        form(index)
          .bindFromRequest()
          .fold(
            formWithErrors => renderPage(lrn, index, mode, formWithErrors).map(BadRequest(_)),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherDocumentPage(index), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddAnotherDocumentPage(index), mode, updatedAnswers))
          )
    }

  private def renderPage(lrn: LocalReferenceNumber, index: Index, mode: Mode, form: Form[Boolean])(implicit request: DataRequest[AnyContent]): Future[Html] = {

    val cyaHelper             = new AddItemsCheckYourAnswersHelper(request.userAnswers, mode)
    val numberOfDocuments     = request.userAnswers.get(DeriveNumberOfDocuments(index)).getOrElse(0)
    val indexList: Seq[Index] = List.range(0, numberOfDocuments).map(Index(_))

    referenceDataConnector.getDocumentTypes() flatMap {
      documents =>
        val documentRows = indexList.map {
          documentIndex =>
            cyaHelper.documentRow(index, documentIndex, documents, removable = true)
        }

        val singularOrPlural = if (numberOfDocuments == 1) "singular" else "plural"
        val json = Json.obj(
          "form"         -> form,
          "lrn"          -> lrn,
          "index"        -> index.display,
          "mode"         -> mode,
          "pageTitle"    -> msg"addAnotherDocument.title.$singularOrPlural".withArgs(numberOfDocuments),
          "heading"      -> msg"addAnotherDocument.heading.$singularOrPlural".withArgs(numberOfDocuments),
          "documentRows" -> documentRows,
          "radios"       -> Radios.yesNo(form("value"))
        )

        renderer.render("addItems/addAnotherDocument.njk", json)
    }
  }
}
