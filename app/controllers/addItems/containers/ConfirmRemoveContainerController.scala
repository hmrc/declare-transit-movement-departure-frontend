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

package controllers.addItems.containers

import controllers.actions._
import derivable.DeriveNumberOfContainers
import forms.addItems.containers.ConfirmRemoveContainerFormProvider
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsContainer
import pages.addItems.containers.{ConfirmRemoveContainerPage, ContainerNumberPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.ContainersQuery
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveContainerController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsContainer navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  checkDependentSection: CheckDependentSectionAction,
  requireData: DataRequiredAction,
  formProvider: ConfirmRemoveContainerFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "addItems/containers/confirmRemoveContainer.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, containerIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val json = Json.obj(
          "form"           -> form,
          "mode"           -> mode,
          "lrn"            -> lrn,
          "index"          -> index.display,
          "containerIndex" -> containerIndex.display,
          "radios"         -> Radios.yesNo(form("value")),
          "onSubmitUrl"    -> routes.ConfirmRemoveContainerController.onSubmit(lrn, index, containerIndex, mode).url
        )

        renderer.render(template, json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, containerIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
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
                "containerIndex" -> containerIndex.display,
                "radios"         -> Radios.yesNo(formWithErrors("value"))
              )

              renderer.render(template, json).map(BadRequest(_))
            },
            value =>
              if (value) {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.remove(ContainersQuery(index, containerIndex)))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield {
                  val numberOfContainers = request.userAnswers.get(DeriveNumberOfContainers(index)).getOrElse(0)
                  numberOfContainers match {
                    case 0 => Redirect(navigator.nextPage(ContainerNumberPage(index, containerIndex), mode, updatedAnswers))
                    case _ => Redirect(navigator.nextPage(ConfirmRemoveContainerPage(index, containerIndex), mode, updatedAnswers))
                  }
                }
              } else {
                Future.successful(Redirect((navigator.nextPage(ConfirmRemoveContainerPage(index, containerIndex), mode, request.userAnswers))))
              }
          )
    }
}
