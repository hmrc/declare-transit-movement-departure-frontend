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

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.addItems.PackageTypeFormProvider
import models.reference.PackageType
import models.{DependentSection, Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsPackagesInfo
import pages.PackageTypePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.packageTypeList

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PackageTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  @AddItemsPackagesInfo navigator: Navigator,
  referenceDataConnector: ReferenceDataConnector,
  formProvider: PackageTypeFormProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with NunjucksSupport
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, packageIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        referenceDataConnector.getPackageTypes().flatMap {
          packageTypes =>
            val form = formProvider(packageTypes)

            val preparedForm: Form[PackageType] = request.userAnswers
              .get(PackageTypePage(itemIndex, packageIndex))
              .flatMap(
                packageType => packageTypes.getPackageType(packageType.code)
              )
              .map(form.fill)
              .getOrElse(form)

            val json = Json.obj(
              "form"         -> preparedForm,
              "lrn"          -> lrn,
              "mode"         -> mode,
              "itemIndex"    -> itemIndex.display,
              "packageIndex" -> packageIndex.display,
              "packageTypes" -> packageTypeList(preparedForm.value, packageTypes.packageTypeList)
            )

            renderer.render("addItems/packageType.njk", json).map(Ok(_))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, packageIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        referenceDataConnector.getPackageTypes().flatMap {
          packageTypes =>
            val form = formProvider(packageTypes)
            form
              .bindFromRequest()
              .fold(
                formWithErrors => {

                  val json = Json.obj(
                    "form"         -> formWithErrors,
                    "lrn"          -> lrn,
                    "mode"         -> mode,
                    "itemIndex"    -> itemIndex.display,
                    "packageIndex" -> packageIndex.display,
                    "packageTypes" -> packageTypeList(form.value, packageTypes.packageTypeList)
                  )

                  renderer.render("addItems/packageType.njk", json).map(BadRequest(_))
                },
                value => {
                  val userAnswers = request.userAnswers.get(PackageTypePage(itemIndex, packageIndex)).map(_ == value) match {
                    case Some(true) => Future.successful(request.userAnswers)
                    case _ =>
                      for {
                        updatedAnswers <- Future.fromTry(request.userAnswers.set(PackageTypePage(itemIndex, packageIndex), value))
                        _              <- sessionRepository.set(updatedAnswers)
                      } yield updatedAnswers
                  }

                  userAnswers.map {
                    ua =>
                      Redirect(navigator.nextPage(PackageTypePage(itemIndex, packageIndex), mode, ua))
                  }
                }
              )
        }
    }

}
