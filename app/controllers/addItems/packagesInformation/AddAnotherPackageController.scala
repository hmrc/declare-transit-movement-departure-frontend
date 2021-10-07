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

package controllers.addItems.packagesInformation

import controllers.actions._
import derivable.DeriveNumberOfPackages
import forms.addItems.AddAnotherPackageFormProvider
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsPackagesInfo
import pages.addItems.AddAnotherPackagePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}
import viewModels.PackageViewModel

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherPackageController @Inject() (
  override val messagesApi: MessagesApi,
  @AddItemsPackagesInfo navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: AddAnotherPackageFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val preparedForm = request.userAnswers.get(AddAnotherPackagePage(itemIndex)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        val totalTypes  = request.userAnswers.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
        val packageRows = PackageViewModel.packageRows(itemIndex, totalTypes, request.userAnswers)

        val singularOrPlural = if (totalTypes == 1) "singular" else "plural"

        val json = Json.obj(
          "form"        -> preparedForm,
          "mode"        -> mode,
          "lrn"         -> lrn,
          "itemIndex"   -> itemIndex.display,
          "radios"      -> Radios.yesNo(preparedForm("value")),
          "pageTitle"   -> msg"addAnotherPackage.title.$singularOrPlural".withArgs(totalTypes),
          "heading"     -> msg"addAnotherPackage.heading.$singularOrPlural".withArgs(totalTypes),
          "packageRows" -> packageRows
        )

        renderer.render("addItems/addAnotherPackage.njk", json).map(Ok(_))
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
            formWithErrors => {

              val totalTypes  = request.userAnswers.get(DeriveNumberOfPackages(itemIndex)).getOrElse(0)
              val packageRows = PackageViewModel.packageRows(itemIndex, totalTypes, request.userAnswers)

              val singularOrPlural = if (totalTypes == 1) "singular" else "plural"

              val json = Json.obj(
                "form"        -> formWithErrors,
                "mode"        -> mode,
                "lrn"         -> lrn,
                "itemIndex"   -> itemIndex.display,
                "radios"      -> Radios.yesNo(formWithErrors("value")),
                "pageTitle"   -> msg"addAnotherPackage.title.$singularOrPlural".withArgs(totalTypes),
                "heading"     -> msg"addAnotherPackage.heading.$singularOrPlural".withArgs(totalTypes),
                "packageRows" -> packageRows
              )

              renderer.render("addItems/addAnotherPackage.njk", json).map(BadRequest(_))
            },
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherPackagePage(itemIndex), value))
              } yield Redirect(navigator.nextPage(AddAnotherPackagePage(itemIndex), mode, updatedAnswers))
          )
    }
}
