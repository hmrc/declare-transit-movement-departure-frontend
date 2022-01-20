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

package controllers.addItems.specialMentions

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.addItems.specialMentions.SpecialMentionTypeFormProvider
import models.reference.SpecialMention
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsSpecialMentions
import pages.addItems.specialMentions.SpecialMentionTypePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.getSpecialMentionAsJson

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SpecialMentionTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsSpecialMentions navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: SpecialMentionTypeFormProvider,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/specialMentions/specialMentionType.njk"

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        referenceDataConnector.getSpecialMentionTypes() flatMap {
          specialMention =>
            val form: Form[SpecialMention] = formProvider(specialMention, itemIndex)

            val preparedForm = request.userAnswers
              .get(SpecialMentionTypePage(itemIndex, referenceIndex))
              .flatMap(specialMention.getSpecialMention)
              .map(form.fill)
              .getOrElse(form)

            val json = Json.obj(
              "form"           -> preparedForm,
              "index"          -> itemIndex.display,
              "referenceIndex" -> referenceIndex.display,
              "specialMention" -> getSpecialMentionAsJson(preparedForm.value, specialMention.list),
              "lrn"            -> lrn,
              "mode"           -> mode
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
        referenceDataConnector.getSpecialMentionTypes() flatMap {
          specialMention =>
            val form = formProvider(specialMention, itemIndex)
            form
              .bindFromRequest()
              .fold(
                formWithErrors => {

                  val json = Json.obj(
                    "form"           -> formWithErrors,
                    "index"          -> itemIndex.display,
                    "referenceIndex" -> referenceIndex.display,
                    "specialMention" -> getSpecialMentionAsJson(form.value, specialMention.list),
                    "lrn"            -> lrn,
                    "mode"           -> mode
                  )

                  renderer.render(template, json).map(BadRequest(_))
                },
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(SpecialMentionTypePage(itemIndex, referenceIndex), value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(SpecialMentionTypePage(itemIndex, referenceIndex), mode, updatedAnswers))
              )
        }
    }
  //  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, referenceIndex: Index, mode: Mode): Action[AnyContent] =
//    (identify andThen getData(lrn) andThen requireData).async {
//      implicit request =>
//        form
//          .bindFromRequest()
//          .fold(
//            formWithErrors => {
//
//              val json = Json.obj(
//                "form" -> formWithErrors,
//                "lrn"  -> lrn,
//                "mode" -> mode
//              )
//
//              renderer.render(template, json).map(BadRequest(_))
//            },
//            value =>
//              for {
//                updatedAnswers <- Future.fromTry(request.userAnswers.set(SpecialMentionTypePage(itemIndex, referenceIndex), value))
//                _              <- sessionRepository.set(updatedAnswers)
//              } yield Redirect(navigator.nextPage(SpecialMentionTypePage(itemIndex, referenceIndex), mode, updatedAnswers))
//          )
//    }
}
