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
import forms.addItems.containers.AddAnotherContainerFormProvider
import models.requests.DataRequest
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsContainer
import pages.addItems.containers.AddAnotherContainerPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.ContainersCheckYourAnswersHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherContainerController @Inject() (
  override val messagesApi: MessagesApi,
  @AddItemsContainer navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: AddAnotherContainerFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "addItems/containers/addAnotherContainer.njk"

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        renderPage(itemIndex, mode, form).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => renderPage(itemIndex, mode, formWithErrors).map(BadRequest(_)),
            value => {
              val onwardRoute = if (value) {
                val containerCount = request.userAnswers.get(DeriveNumberOfContainers(itemIndex)).getOrElse(0)
                val containerIndex = Index(containerCount)
                routes.ContainerNumberController.onPageLoad(request.userAnswers.lrn, itemIndex, containerIndex, mode)
              } else {
                navigator.nextPage(AddAnotherContainerPage(itemIndex), mode, request.userAnswers)
              }

              Future.successful(Redirect(onwardRoute))
            }
          )
    }

  private def renderPage(itemIndex: Index, mode: Mode, form: Form[_])(implicit request: DataRequest[AnyContent]): Future[Html] = {
    val cyaHelper          = new ContainersCheckYourAnswersHelper(request.userAnswers)
    val numberOfContainers = request.userAnswers.get(DeriveNumberOfContainers(itemIndex)).getOrElse(0)
    val indexList          = List.range(0, numberOfContainers).map(Index(_))
    val containerRows = indexList.map {
      containerIndex =>
        cyaHelper.containerRow(itemIndex, containerIndex)
    }

    val singularOrPlural = if (numberOfContainers == 1) "singular" else "plural"
    val title            = msg"addAnotherContainer.title.$singularOrPlural".withArgs(numberOfContainers)

    val json = Json.obj(
      "form"           -> form,
      "index"          -> itemIndex.display,
      "mode"           -> mode,
      "lrn"            -> request.userAnswers.lrn,
      "pageTitle"      -> title,
      "containerCount" -> numberOfContainers,
      "containerRows"  -> containerRows,
      "radios"         -> Radios.yesNo(form("value"))
    )

    renderer.render(template, json)
  }
}
