/*
 * Copyright 2020 HM Revenue & Customs
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

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.addItems.securityDetails.DangerousGoodsCodeFormProvider
import javax.inject.Inject
import models.reference.DangerousGoodsCode
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SecurityDetails
import pages.addItems.securityDetails.DangerousGoodsCodePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.getDangerousGoodsCodeAsJson

import scala.concurrent.{ExecutionContext, Future}

class DangerousGoodsCodeController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SecurityDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: DangerousGoodsCodeFormProvider,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/securityDetails/dangerousGoodsCode.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        referenceDataConnector.getDangerousGoodsCodeList() flatMap {
          dangerousGoodsCodes =>
            val form: Form[DangerousGoodsCode] = formProvider(dangerousGoodsCodes)

            val preparedForm = request.userAnswers
              .get(DangerousGoodsCodePage(index))
              .flatMap(dangerousGoodsCodes.getDangerousGoodsCode)
              .map(form.fill)
              .getOrElse(form)

            val json = Json.obj(
              "form"                -> preparedForm,
              "index"               -> index.display,
              "dangerousGoodsCodes" -> getDangerousGoodsCodeAsJson(preparedForm.value, dangerousGoodsCodes.dangerousGoodsCodes),
              "lrn"                 -> lrn,
              "mode"                -> mode
            )

            renderer.render(template, json).map(Ok(_))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        referenceDataConnector.getDangerousGoodsCodeList() flatMap {
          dangerousGoodsCodes =>
            val form: Form[DangerousGoodsCode] = formProvider(dangerousGoodsCodes)
            form
              .bindFromRequest()
              .fold(
                formWithErrors => {

                  val json = Json.obj(
                    "form"                -> formWithErrors,
                    "index"               -> index.display,
                    "dangerousGoodsCodes" -> getDangerousGoodsCodeAsJson(form.value, dangerousGoodsCodes.dangerousGoodsCodes),
                    "lrn"                 -> lrn,
                    "mode"                -> mode
                  )

                  renderer.render(template, json).map(BadRequest(_))
                },
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(DangerousGoodsCodePage(index), value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(DangerousGoodsCodePage(index), mode, updatedAnswers))
              )
        }
    }
}
