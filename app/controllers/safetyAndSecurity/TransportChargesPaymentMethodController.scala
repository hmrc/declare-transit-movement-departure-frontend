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

package controllers.safetyAndSecurity

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.safetyAndSecurity.TransportChargesPaymentMethodFormProvider
import javax.inject.Inject
import models.reference.MethodOfPayment
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurity
import pages.addItems.securityDetails.TransportChargesPage
import pages.safetyAndSecurity.TransportChargesPaymentMethodPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.getPaymentsAsJson

import scala.concurrent.{ExecutionContext, Future}

class TransportChargesPaymentMethodController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SafetyAndSecurity navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: TransportChargesPaymentMethodFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "safetyAndSecurity/transportChargesPaymentMethod.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getMethodOfPaymentList() flatMap {
        payments =>
          val form: Form[MethodOfPayment] = formProvider(payments)

          val preparedForm = request.userAnswers
            .get(TransportChargesPaymentMethodPage)
            .flatMap(payments.getMethodOfPayment)
            .map(form.fill)
            .getOrElse(form)

          val json = Json.obj(
            "form"     -> preparedForm,
            "payments" -> getPaymentsAsJson(preparedForm.value, payments.methodsOfPayment),
            "lrn"      -> lrn,
            "mode"     -> mode
          )

          renderer.render(template, json).map(Ok(_))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getMethodOfPaymentList() flatMap {
        payments =>
          val form = formProvider(payments)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => {

                val json = Json.obj(
                  "form"     -> formWithErrors,
                  "payments" -> getPaymentsAsJson(form.value, payments.methodsOfPayment),
                  "lrn"      -> lrn,
                  "mode"     -> mode
                )

                renderer.render(template, json).map(BadRequest(_))
              },
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(TransportChargesPaymentMethodPage, value.code))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(TransportChargesPaymentMethodPage, mode, updatedAnswers))
            )
      }
  }

}
