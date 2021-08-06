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
import forms.addItems.RemovePackageFormProvider
import models.{DependentSection, Index, LocalReferenceNumber, Mode, UserAnswers}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsPackagesInfo
import pages.addItems.RemovePackagePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.PackagesQuery
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemovePackageController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsPackagesInfo navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: RemovePackageFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "addItems/removePackage.njk"

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, packageIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val form = formProvider(packageIndex.display)

        val preparedForm = request.userAnswers.get(RemovePackagePage(itemIndex)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        val json = Json.obj(
          "form"         -> preparedForm,
          "itemIndex"    -> itemIndex.display,
          "packageIndex" -> packageIndex.display,
          "mode"         -> mode,
          "lrn"          -> lrn,
          "radios"       -> Radios.yesNo(preparedForm("value"))
        )

        renderer.render(template, json).map(Ok(_))
    }

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, packageIndex: Index, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val form = formProvider(packageIndex.display)

        form
          .bindFromRequest()
          .fold(
            formWithErrors => {

              val json = Json.obj(
                "form"         -> formWithErrors,
                "itemIndex"    -> itemIndex.display,
                "packageIndex" -> packageIndex.display,
                "mode"         -> mode,
                "lrn"          -> lrn,
                "radios"       -> Radios.yesNo(formWithErrors("value"))
              )

              renderer.render(template, json).map(BadRequest(_))
            },
            value => {
              val updatedAnswers: Future[UserAnswers] =
                if (value) {
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.remove(PackagesQuery(itemIndex, packageIndex)))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield updatedAnswers
                } else {
                  Future.successful(request.userAnswers)
                }
              updatedAnswers.map(
                userAnswers => Redirect(navigator.nextPage(RemovePackagePage(itemIndex), mode, userAnswers))
              )
            }
          )
    }
}
