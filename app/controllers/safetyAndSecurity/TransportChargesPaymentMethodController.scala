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

package controllers.safetyAndSecurity

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.generic.PaymentMethodFormProvider
import models.reference.MethodOfPayment
import models.{DependentSection, LocalReferenceNumber, MethodOfPaymentList, Mode}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurity
import pages.safetyAndSecurity.TransportChargesPaymentMethodPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.getPaymentsAsJson

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TransportChargesPaymentMethodController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SafetyAndSecurity navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: PaymentMethodFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "safetyAndSecurity/transportChargesPaymentMethod.njk"

  private def form(methodsOfPayment: MethodOfPaymentList): Form[MethodOfPayment] =
    formProvider("transportChargesPaymentMethod", methodsOfPayment)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        referenceDataConnector.getMethodOfPaymentList() flatMap {
          payments =>
            val preparedForm = request.userAnswers
              .get(TransportChargesPaymentMethodPage)
              .flatMap(
                x => payments.getMethodOfPayment(x.code)
              )
              .map(form(payments).fill)
              .getOrElse(form(payments))

            val json = Json.obj(
              "form"     -> preparedForm,
              "payments" -> getPaymentsAsJson(preparedForm.value, payments.methodsOfPayment),
              "lrn"      -> lrn,
              "mode"     -> mode
            )

            renderer.render(template, json).map(Ok(_))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        referenceDataConnector.getMethodOfPaymentList() flatMap {
          payments =>
            form(payments)
              .bindFromRequest()
              .fold(
                formWithErrors => {

                  val json = Json.obj(
                    "form"     -> formWithErrors,
                    "payments" -> getPaymentsAsJson(formWithErrors.value, payments.methodsOfPayment),
                    "lrn"      -> lrn,
                    "mode"     -> mode
                  )

                  renderer.render(template, json).map(BadRequest(_))
                },
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(TransportChargesPaymentMethodPage, value))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(TransportChargesPaymentMethodPage, mode, updatedAnswers))
              )
        }
    }

}
