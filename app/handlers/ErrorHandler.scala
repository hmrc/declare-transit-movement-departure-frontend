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

package handlers

import models.requests.DataRequest
import play.api.http.HeaderNames.CACHE_CONTROL
import play.api.http.HttpErrorHandler
import play.api.http.Status._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, RequestHeader, Result, Results}
import play.api.{Logger, PlayException}
import renderer.Renderer
import uk.gov.hmrc.nunjucks.NunjucksSupport
import uk.gov.hmrc.play.bootstrap.frontend.http.ApplicationException
import uk.gov.hmrc.viewmodels.MessageInterpolators

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// NOTE: There should be changes to bootstrap to make this easier, the API in bootstrap should allow a `Future[Html]` rather than just an `Html`
@Singleton
class ErrorHandler @Inject() (
  renderer: Renderer,
  val messagesApi: MessagesApi
)(implicit ec: ExecutionContext)
    extends HttpErrorHandler
    with I18nSupport
    with NunjucksSupport {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String = ""): Future[Result] = {

    implicit val rh: RequestHeader = request

    statusCode match {
      case BAD_REQUEST =>
        renderer.render("badRequest.njk").map(BadRequest(_))
      case NOT_FOUND =>
        renderer.render("notFound.njk", Json.obj()).map(NotFound(_))
      case _ =>
        renderer.render("error.njk", Json.obj()).map {
          content =>
            Results.Status(statusCode)(content)
        }
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {

    implicit val rh: RequestHeader = request

    logError(request, exception)
    exception match {
      case ApplicationException(result, _) =>
        Future.successful(result)
      case _ =>
        renderer.render("internalServerError.njk").map {
          content =>
            InternalServerError(content).withHeaders(CACHE_CONTROL -> "no-cache")
        }
    }
  }

  def onConcurrentError(message: String, redirectLink: String, journey: String)(implicit request: DataRequest[AnyContent]): Future[Result] = {
    val json = Json.obj(
      "pageTitle"    -> msg"concurrent.remove.error.title".withArgs(msg"$journey"),
      "pageHeading"  -> msg"concurrent.remove.error.heading".withArgs(msg"$journey"),
      "linkText"     -> msg"concurrent.remove.error.$message.link.text",
      "redirectLink" -> redirectLink
    )
    renderer.render("concurrentRemoveError.njk", json).map(NotFound(_))
  }

  private def logError(request: RequestHeader, ex: Throwable): Unit =
    Logger.error(
      """
        |
        |! %sInternal server error, for (%s) [%s] ->
        | """.stripMargin.format(ex match {
                                   case p: PlayException => "@" + p.id + " - "
                                   case _                => ""
                                 },
                                 request.method,
                                 request.uri
      ),
      ex
    )
}
