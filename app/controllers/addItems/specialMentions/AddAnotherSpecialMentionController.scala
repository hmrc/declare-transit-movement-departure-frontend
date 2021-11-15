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

package controllers.addItems.specialMentions

import connectors.ReferenceDataConnector
import controllers.actions._
import derivable.DeriveNumberOfSpecialMentions
import forms.generic.YesNoFormProvider
import models.requests.DataRequest
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsSpecialMentions
import pages.addItems.specialMentions.AddAnotherSpecialMentionPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.SpecialMentionsCheckYourAnswersHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherSpecialMentionController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsSpecialMentions navigator: Navigator,
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

  private val form     = formProvider("addAnotherSpecialMention")
  private val template = "addItems/specialMentions/addAnotherSpecialMention.njk"

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        renderPage(lrn, itemIndex, form, mode).map(Ok(_))
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
            formWithErrors => renderPage(lrn, itemIndex, formWithErrors, mode).map(BadRequest(_)),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherSpecialMentionPage(itemIndex), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddAnotherSpecialMentionPage(itemIndex), mode, updatedAnswers))
          )
    }

  private def renderPage(lrn: LocalReferenceNumber, itemIndex: Index, form: Form[Boolean], mode: Mode)(implicit
    request: DataRequest[AnyContent]
  ): Future[Html] = {

    val cya                   = new SpecialMentionsCheckYourAnswersHelper(request.userAnswers, mode)
    val numberOfReferences    = request.userAnswers.get(DeriveNumberOfSpecialMentions(itemIndex)).getOrElse(0)
    val indexList: Seq[Index] = List.range(0, numberOfReferences).map(Index(_))

    referenceDataConnector.getSpecialMention() flatMap {
      specialMentions =>
        val referenceRows = indexList.map {
          referenceIndex =>
            cya.specialMentionType(itemIndex, referenceIndex, specialMentions)
        }

        val singularOrPlural = if (numberOfReferences == 1) "singular" else "plural"

        val json = Json.obj(
          "form"          -> form,
          "lrn"           -> lrn,
          "mode"          -> mode,
          "itemIndex"     -> itemIndex.display,
          "pageTitle"     -> msg"addAnotherSpecialMention.title.$singularOrPlural".withArgs(numberOfReferences, itemIndex.display),
          "heading"       -> msg"addAnotherSpecialMention.heading.$singularOrPlural".withArgs(numberOfReferences, itemIndex.display),
          "referenceRows" -> referenceRows,
          "radios"        -> Radios.yesNo(form("value"))
        )

        renderer.render(template, json)
    }

  }
}
