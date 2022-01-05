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

package controllers.guaranteeDetails

import controllers.actions._
import derivable.DeriveNumberOfGuarantees
import forms.AddAnotherGuaranteeFormProvider
import models.DeclarationType.Option4
import models.requests.DataRequest
import models.{DependentSection, Index, LocalReferenceNumber, NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.GuaranteeDetails
import pages.{AddAnotherGuaranteePage, DeclarationTypePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import utils.GuaranteeDetailsCheckYourAnswersHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherGuaranteeController @Inject() (
  override val messagesApi: MessagesApi,
  @GuaranteeDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: AddAnotherGuaranteeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def allowMoreGuarantees(ua: UserAnswers): Boolean =
    ua.get(DeriveNumberOfGuarantees).getOrElse(0) < AddAnotherGuaranteePage.maxAllowedGuarantees

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.GuaranteeDetails)).async {
      implicit request =>
        val tirDeclarationType = request.userAnswers.get(DeclarationTypePage) match {
          case Some(Option4) => true
          case _             => false
        }

        renderPage(lrn, formProvider(allowMoreGuarantees(request.userAnswers), tirDeclarationType), tirDeclarationType).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.GuaranteeDetails)).async {
      implicit request =>
        val tirDeclarationType = request.userAnswers.get(DeclarationTypePage) match {
          case Some(Option4) => true
          case _             => false
        }

        formProvider(allowMoreGuarantees(request.userAnswers), tirDeclarationType)
          .bindFromRequest()
          .fold(
            formWithErrors => renderPage(lrn, formWithErrors, tirDeclarationType).map(BadRequest(_)),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherGuaranteePage, value))
              } yield Redirect(navigator.nextPage(AddAnotherGuaranteePage, NormalMode, updatedAnswers))
          )
    }

  private def renderPage(lrn: LocalReferenceNumber, form: Form[Boolean], isTir: Boolean)(implicit request: DataRequest[AnyContent]): Future[Html] = {

    val cyaHelper             = new GuaranteeDetailsCheckYourAnswersHelper(request.userAnswers, NormalMode)
    val numberOfItems         = request.userAnswers.get(DeriveNumberOfGuarantees).getOrElse(0)
    val indexList: Seq[Index] = List.range(0, numberOfItems).map(Index(_))

    val guaranteeRows = indexList.map {
      index =>
        cyaHelper.guaranteeRow(index, isTir)
    }

    val singularOrPlural = if (numberOfItems == 1) "singular" else "plural"

    val tirOrRegularGuarantee = if (isTir) "addAnotherGuarantee.tir" else "addAnotherGuarantee"

    val json = Json.obj(
      "form"                -> form,
      "lrn"                 -> lrn,
      "pageTitle"           -> msg"$tirOrRegularGuarantee.title.$singularOrPlural".withArgs(numberOfItems),
      "heading"             -> msg"$tirOrRegularGuarantee.heading.$singularOrPlural".withArgs(numberOfItems),
      "radioHeading"        -> msg"$tirOrRegularGuarantee.radio.heading",
      "guaranteeRows"       -> guaranteeRows,
      "allowMoreGuarantees" -> allowMoreGuarantees(request.userAnswers),
      "radios"              -> Radios.yesNo(form("value"))
    )

    renderer.render("guaranteeDetails/addAnotherGuarantee.njk", json)
  }
}
