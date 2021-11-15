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

package controllers.addItems

import controllers.actions._
import derivable.DeriveNumberOfItems
import forms.generic.YesNoFormProvider
import models.requests.DataRequest
import models.{DependentSection, Index, LocalReferenceNumber, NormalMode}
import navigation.Navigator
import navigation.annotations.addItems.AddItems
import pages.addItems.AddAnotherItemPage
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

class AddAnotherItemController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItems navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider("addAnotherItem")

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        renderPage(lrn, form).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => renderPage(lrn, formWithErrors).map(BadRequest(_)),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherItemPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddAnotherItemPage, NormalMode, updatedAnswers))
          )
    }

  private def renderPage(lrn: LocalReferenceNumber, form: Form[Boolean])(implicit request: DataRequest[AnyContent]): Future[Html] = {

    val cyaHelper             = new AddItemsCheckYourAnswersHelper(request.userAnswers, NormalMode)
    val numberOfItems         = request.userAnswers.get(DeriveNumberOfItems).getOrElse(0)
    val indexList: Seq[Index] = List.range(0, numberOfItems).map(Index(_))

    val itemRows = indexList.map {
      index =>
        cyaHelper.itemRow(index)
    }

    val singularOrPlural = if (numberOfItems == 1) "singular" else "plural"
    val json = Json.obj(
      "form"      -> form,
      "lrn"       -> lrn,
      "pageTitle" -> msg"addAnotherItem.title.$singularOrPlural".withArgs(numberOfItems),
      "heading"   -> msg"addAnotherItem.heading.$singularOrPlural".withArgs(numberOfItems),
      "itemRows"  -> itemRows,
      "radios"    -> Radios.yesNo(form("value"))
    )

    renderer.render("addItems/addAnotherItem.njk", json)
  }
}
