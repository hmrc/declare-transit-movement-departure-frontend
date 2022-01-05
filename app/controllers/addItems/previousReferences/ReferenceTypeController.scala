/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.addItems.previousReferences

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.ReferenceTypeFormProvider
import models.reference.PreviousReferencesDocumentType
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsAdminReference
import pages.addItems.ReferenceTypePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.getPreviousDocumentsAsJson

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsAdminReference navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: ReferenceTypeFormProvider,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/referenceType.njk"

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        referenceDataConnector.getPreviousReferencesDocumentTypes() flatMap {
          previousDocuments =>
            val form: Form[PreviousReferencesDocumentType] = formProvider(previousDocuments)

            val preparedForm = request.userAnswers
              .get(ReferenceTypePage(itemIndex, referenceIndex))
              .flatMap(previousDocuments.getPreviousReferencesDocumentType)
              .map(form.fill)
              .getOrElse(form)

            val json = Json.obj(
              "form"              -> preparedForm,
              "index"             -> itemIndex.display,
              "referenceIndex"    -> referenceIndex.display,
              "previousDocuments" -> getPreviousDocumentsAsJson(preparedForm.value, previousDocuments.previousReferencesDocumentTypes),
              "lrn"               -> lrn,
              "mode"              -> mode
            )

            renderer.render(template, json).map(Ok(_))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        referenceDataConnector.getPreviousReferencesDocumentTypes() flatMap {
          previousDocuments =>
            val form = formProvider(previousDocuments)
            form
              .bindFromRequest()
              .fold(
                formWithErrors => {

                  val json = Json.obj(
                    "form"              -> formWithErrors,
                    "index"             -> itemIndex.display,
                    "referenceIndex"    -> referenceIndex.display,
                    "previousDocuments" -> getPreviousDocumentsAsJson(form.value, previousDocuments.previousReferencesDocumentTypes),
                    "lrn"               -> lrn,
                    "mode"              -> mode
                  )

                  renderer.render(template, json).map(BadRequest(_))
                },
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(ReferenceTypePage(itemIndex, referenceIndex), value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(ReferenceTypePage(itemIndex, referenceIndex), mode, updatedAnswers))
              )
        }
    }
}
