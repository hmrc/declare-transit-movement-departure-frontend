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

package controllers.addItems.previousReferences

import connectors.ReferenceDataConnector
import controllers.actions._
import derivable.DeriveNumberOfPreviousAdministrativeReferences
import forms.addItems.AddAnotherPreviousAdministrativeReferenceFormProvider
import models.requests.DataRequest
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsAdminReference
import pages.addItems.AddAnotherPreviousAdministrativeReferencePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.AddItemsCheckYourAnswersHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherPreviousAdministrativeReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsAdminReference navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: AddAnotherPreviousAdministrativeReferenceFormProvider,
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
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        renderPage(lrn, index, mode, form).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => renderPage(lrn, index, mode, formWithErrors).map(BadRequest(_)),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherPreviousAdministrativeReferencePage(index), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddAnotherPreviousAdministrativeReferencePage(index), mode, updatedAnswers))
          )
    }

  private def renderPage(lrn: LocalReferenceNumber, index: Index, mode: Mode, form: Form[Boolean])(implicit request: DataRequest[AnyContent]): Future[Html] = {

    val cyaHelper             = new AddItemsCheckYourAnswersHelper(request.userAnswers)
    val numberOfReferences    = request.userAnswers.get(DeriveNumberOfPreviousAdministrativeReferences(index)).getOrElse(0)
    val indexList: Seq[Index] = List.range(0, numberOfReferences).map(Index(_))

    referenceDataConnector.getPreviousReferencesDocumentTypes() flatMap {
      previousDocuments =>
        val referenceRows = indexList.map {
          referenceIndex =>
            cyaHelper.previousAdministrativeReferenceRow(index, referenceIndex, previousDocuments)
        }

        val singularOrPlural = if (numberOfReferences == 1) "singular" else "plural"
        val json = Json.obj(
          "form"          -> form,
          "index"         -> index.display,
          "lrn"           -> lrn,
          "mode"          -> mode,
          "pageTitle"     -> msg"addAnotherPreviousAdministrativeReference.title.$singularOrPlural".withArgs(numberOfReferences),
          "heading"       -> msg"addAnotherPreviousAdministrativeReference.heading.$singularOrPlural".withArgs(numberOfReferences),
          "referenceRows" -> referenceRows,
          "radios"        -> Radios.yesNo(form("value"))
        )

        renderer.render("addItems/addAnotherPreviousAdministrativeReference.njk", json)
    }
  }
}
