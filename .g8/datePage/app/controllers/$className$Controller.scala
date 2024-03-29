package controllers

import controllers.actions._
import forms.$className$FormProvider
import javax.inject.Inject
import models.{Mode, LocalReferenceNumber}
import pages.$className$Page
import navigation.Navigator
import navigation.annotations.$navRoute$
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{DateInput, NunjucksSupport}

import scala.concurrent.{ExecutionContext, Future}

class $className$Controller @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       @$navRoute$ navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       formProvider: $className$FormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()
  private val template = "$className;format="decap"$.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get($className$Page) match {
        case Some(value) => form.fill(value)
        case None        => form
      }

      val viewModel = DateInput.localDate(preparedForm("value"))

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode,
        "lrn"  -> lrn,
        "date" -> viewModel
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>  {

          val viewModel = DateInput.localDate(formWithErrors("value"))

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "lrn"  -> lrn,
            "date" -> viewModel
          )

          renderer.render(template, json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set($className$Page, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage($className$Page, mode, updatedAnswers))
      )
  }
}
