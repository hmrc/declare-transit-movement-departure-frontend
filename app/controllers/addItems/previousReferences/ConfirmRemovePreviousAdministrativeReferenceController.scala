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

import controllers.actions._
import derivable.{DeriveNumberOfItems, DeriveNumberOfPreviousAdministrativeReferences}
import forms.ConfirmRemovePreviousAdministrativeReferenceFormProvider
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsAdminReference
import pages.addItems.ConfirmRemovePreviousAdministrativeReferencePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.PreviousReferenceQuery
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemovePreviousAdministrativeReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsAdminReference navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  checkValidIndexAction: CheckValidIndexAction,
  formProvider: ConfirmRemovePreviousAdministrativeReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "addItems/confirmRemovePreviousAdministrativeReference.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val preparedForm = request.userAnswers.get(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        val json = Json.obj(
          "form"           -> preparedForm,
          "mode"           -> mode,
          "lrn"            -> lrn,
          "index"          -> index.display,
          "referenceIndex" -> referenceIndex.display,
          "radios"         -> Radios.yesNo(preparedForm("value"))
        )

        renderer.render(template, json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)
      andThen checkValidIndexAction(index, DeriveNumberOfItems)
      andThen checkValidIndexAction(referenceIndex, DeriveNumberOfPreviousAdministrativeReferences(index))).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => {

              val json = Json.obj(
                "form"           -> formWithErrors,
                "mode"           -> mode,
                "lrn"            -> lrn,
                "index"          -> index.display,
                "referenceIndex" -> referenceIndex.display,
                "radios"         -> Radios.yesNo(formWithErrors("value"))
              )

              renderer.render(template, json).map(BadRequest(_))
            },
            value =>
              if (value) {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.remove(PreviousReferenceQuery(index, referenceIndex)))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), mode, updatedAnswers))
              } else {
                Future.successful(
                  Redirect((navigator.nextPage(ConfirmRemovePreviousAdministrativeReferencePage(index, referenceIndex), mode, request.userAnswers)))
                )
              }
          )
    }
}
