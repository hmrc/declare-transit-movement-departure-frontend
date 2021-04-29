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

import controllers.actions._
import forms.addItems.specialMentions.SpecialMentionAdditionalInfoFormProvider

import javax.inject.Inject
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SpecialMentions
import pages.addItems.specialMentions.SpecialMentionAdditionalInfoPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class SpecialMentionAdditionalInfoController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SpecialMentions navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: SpecialMentionAdditionalInfoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/specialMentions/specialMentionAdditionalInfo.njk"

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val preparedForm = request.userAnswers.get(SpecialMentionAdditionalInfoPage(itemIndex, referenceIndex)) match {
          case None        => formProvider(itemIndex, referenceIndex)
          case Some(value) => formProvider(itemIndex, referenceIndex).fill(value)
        }

        val json = Json.obj(
          "form"           -> preparedForm,
          "lrn"            -> lrn,
          "mode"           -> mode,
          "index"          -> itemIndex.display,
          "referenceIndex" -> referenceIndex.display
        )

        renderer.render(template, json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        formProvider(itemIndex, referenceIndex)
          .bindFromRequest()
          .fold(
            formWithErrors => {

              val json = Json.obj(
                "form"           -> formWithErrors,
                "lrn"            -> lrn,
                "mode"           -> mode,
                "index"          -> itemIndex.display,
                "referenceIndex" -> referenceIndex.display
              )

              renderer.render(template, json).map(BadRequest(_))
            },
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(SpecialMentionAdditionalInfoPage(itemIndex, referenceIndex), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(SpecialMentionAdditionalInfoPage(itemIndex, referenceIndex), mode, updatedAnswers))
          )
    }
}
