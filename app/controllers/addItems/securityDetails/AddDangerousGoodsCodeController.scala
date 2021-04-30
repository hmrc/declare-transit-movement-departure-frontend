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

package controllers.addItems.securityDetails

import controllers.actions._
import forms.addItems.securityDetails.AddDangerousGoodsCodeFormProvider
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SecurityDetails
import pages.addItems.securityDetails.AddDangerousGoodsCodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddDangerousGoodsCodeController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SecurityDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: AddDangerousGoodsCodeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/securityDetails/addDangerousGoodsCode.njk"

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val preparedForm = request.userAnswers.get(AddDangerousGoodsCodePage(itemIndex)) match {
          case None        => formProvider()
          case Some(value) => formProvider().fill(value)
        }

        val json = Json.obj(
          "form"   -> preparedForm,
          "mode"   -> mode,
          "lrn"    -> lrn,
          "radios" -> Radios.yesNo(preparedForm("value"))
        )

        renderer.render(template, json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        formProvider()
          .bindFromRequest()
          .fold(
            formWithErrors => {

              val json = Json.obj(
                "form"   -> formWithErrors,
                "mode"   -> mode,
                "lrn"    -> lrn,
                "radios" -> Radios.yesNo(formWithErrors("value"))
              )

              renderer.render(template, json).map(BadRequest(_))
            },
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddDangerousGoodsCodePage(itemIndex), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddDangerousGoodsCodePage(itemIndex), mode, updatedAnswers))
          )
    }
}
