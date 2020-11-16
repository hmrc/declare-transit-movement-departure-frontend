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

package controllers.addItems

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.addItems.DocumentTypeFormProvider
import javax.inject.Inject
import models.reference.DocumentType
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.{AddItems, Document}
import pages.addItems
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.getDocumentsAsJson

import scala.concurrent.{ExecutionContext, Future}

class DocumentTypeController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @Document navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: DocumentTypeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/documentType.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, documentIndex: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        referenceDataConnector.getDocumentTypes() flatMap {
          documents =>
            val form: Form[DocumentType] = formProvider(documents)

            val preparedForm = request.userAnswers
              .get(addItems.DocumentTypePage(index, documentIndex))
              .flatMap(documents.getDocumentType)
              .map(form.fill)
              .getOrElse(form)

            val json = Json.obj(
              "form"          -> preparedForm,
              "index"         -> index.display,
              "documentIndex" -> documentIndex.display,
              "documents"     -> getDocumentsAsJson(preparedForm.value, documents.documentTypes),
              "lrn"           -> lrn,
              "mode"          -> mode
            )
            renderer.render(template, json).map(Ok(_))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, documentIndex: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        referenceDataConnector.getDocumentTypes() flatMap {
          documents =>
            val form = formProvider(documents)
            form
              .bindFromRequest()
              .fold(
                formWithErrors => {

                  val json = Json.obj(
                    "form"          -> formWithErrors,
                    "index"         -> index.display,
                    "documentIndex" -> documentIndex.display,
                    "documents"     -> getDocumentsAsJson(form.value, documents.documentTypes),
                    "lrn"           -> lrn,
                    "mode"          -> mode
                  )

                  renderer.render(template, json).map(BadRequest(_))
                },
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(addItems.DocumentTypePage(index, documentIndex), value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(addItems.DocumentTypePage(index, documentIndex), mode, updatedAnswers))
              )
        }
    }
}
