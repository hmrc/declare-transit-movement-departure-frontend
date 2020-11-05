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

package controllers.guaranteeDetails

import controllers.actions._
import forms.guaranteeDetails.GuaranteeReferenceFormProvider
import javax.inject.Inject
import models.GuaranteeType.FlatRateVoucher
import models.messages.guarantee.GuaranteeReferenceWithGrn
import models.{LocalReferenceNumber, Mode, UserAnswers}
import navigation.Navigator
import navigation.annotations.GuaranteeDetails
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class GuaranteeReferenceController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @GuaranteeDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: GuaranteeReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        val lengthGRN: Int = grnMaxLengthValue(request.userAnswers)
        val preparedForm = request.userAnswers.get(GuaranteeReferencePage) match {

          case None        => formProvider(lengthGRN)
          case Some(value) => formProvider(lengthGRN).fill(value)
        }

        val json = Json.obj(
          "form" -> preparedForm,
          "lrn"  -> lrn,
          "mode" -> mode
        )

        renderer.render("guaranteeDetails/guaranteeReference.njk", json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify andThen getData(lrn) andThen requireData).async {
      implicit request =>
        val grnMaxLength: Int = grnMaxLengthValue(request.userAnswers)
        formProvider(grnMaxLength)
          .bindFromRequest()
          .fold(
            formWithErrors => {

              val json = Json.obj(
                "form" -> formWithErrors,
                "lrn"  -> lrn,
                "mode" -> mode
              )

              renderer.render("guaranteeDetails/guaranteeReference.njk", json).map(BadRequest(_))
            },
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(GuaranteeReferencePage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(GuaranteeReferencePage, mode, updatedAnswers))
          )
    }

  private def grnMaxLengthValue(userAnswers: UserAnswers) = userAnswers.get(GuaranteeTypePage) match {
    case Some(FlatRateVoucher) => GuaranteeReferenceWithGrn.Constants.guaranteeReferenceNumberLength
    case _                     => GuaranteeReferenceWithGrn.Constants.grnOtherTypeLength
  }

}
