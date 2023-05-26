/*
 * Copyright 2023 HM Revenue & Customs
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

package renderer

import config.{FrontendAppConfig, RenderConfig}
import play.api.libs.json.{JsObject, Json, OWrites}
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.nunjucks.NunjucksRenderer

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class RendererImpl @Inject() (
  frontendAppConfig: FrontendAppConfig,
  renderConfig: RenderConfig,
  renderer: NunjucksRenderer
) extends Renderer {

  def render(template: String)(implicit request: RequestHeader): Future[Html] =
    renderTemplate(template, Json.obj())

  def render[A](template: String, ctx: A)(implicit request: RequestHeader, writes: OWrites[A]): Future[Html] =
    renderTemplate(template, Json.toJsObject(ctx))

  def render(template: String, ctx: JsObject)(implicit request: RequestHeader): Future[Html] =
    renderTemplate(template, ctx)

  private def renderTemplate(template: String, ctx: JsObject)(implicit request: RequestHeader): Future[Html] =
    renderer.render(template, ctx ++ Json.obj("config" -> (config ++ Json.obj("feedbackUrl" -> renderConfig.feedbackUrl))))

  private lazy val config: JsObject = Json.obj(
    "serviceIdentifier"      -> renderConfig.contactFormServiceIdentifier,
    "contactHost"            -> renderConfig.contactHost,
    "signOutUrl"             -> renderConfig.signOutUrl,
    "timeout"                -> renderConfig.timeoutSeconds,
    "countdown"              -> renderConfig.countdownSeconds,
    "trackingConsentUrl"     -> frontendAppConfig.trackingConsentUrl,
    "gtmContainer"           -> frontendAppConfig.gtmContainer,
    "serviceUrl"             -> frontendAppConfig.serviceUrl,
    "contactUrl"             -> frontendAppConfig.nctsEnquiriesUrl,
    "guidanceUrl"            -> frontendAppConfig.nctsGuidanceUrl,
    "userResearchUrl"        -> frontendAppConfig.userResearchUrl,
    "showPhaseBanner"        -> frontendAppConfig.showPhaseBanner,
    "showUserResearchBanner" -> frontendAppConfig.showUserResearchBanner
  )
}

trait Renderer {
  def render(template: String)(implicit request: RequestHeader): Future[Html]

  def render[A](template: String, ctx: A)(implicit request: RequestHeader, writes: OWrites[A]): Future[Html]

  def render(template: String, ctx: JsObject)(implicit request: RequestHeader): Future[Html]
}
