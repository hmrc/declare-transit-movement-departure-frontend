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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class FrontendAppConfig @Inject()(configuration: Configuration) {

  private val contactHost = configuration.get[String]("contact-frontend.host")

  val analyticsToken: String   = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String    = configuration.get[String](s"google-analytics.host")
  val betaFeedbackUrl          = s"$contactHost/contact/beta-feedback"
  val nctsEnquiriesUrl: String = configuration.get[String]("urls.nctsEnquiries")

  val trackingConsentUrl: String = configuration.get[String]("microservice.services.tracking-consent-frontend.url")
  val gtmContainer: String       = configuration.get[String]("microservice.services.tracking-consent-frontend.gtm.container")

  val betaAuthorizationUrl: String = configuration.get[Service]("microservice.services.transit-movements-trader-authorization").fullServiceUrl

  //TODO: Move out into it's own config object like `ManageTransitMovementsService`
  lazy val referenceDataUrl: String = configuration.get[Service]("microservice.services.referenceData").fullServiceUrl

  //TODO: Move out into it's own config object like `ManageTransitMovementsService`
  val departureHost    = configuration.get[Service]("microservice.services.departures").fullServiceUrl
  val departureBaseUrl = configuration.get[Service]("microservice.services.departures").baseUrl

  // TODO: Move config values for IdentifierAction to it's own config class
  // TODO: Make these values eagerly evaluated. I.e. non lazy
  lazy val authUrl: String          = configuration.get[Service]("auth").baseUrl
  lazy val loginUrl: String         = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val enrolmentKey: String     = configuration.get[String]("keys.enrolmentKey")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val manageTransitMovementsUrl: String               = configuration.get[String]("urls.manageTransitMovementsFrontend")
  lazy val serviceUrl: String                              = s"$manageTransitMovementsUrl/index"
  lazy val manageTransitMovementsViewDeparturesUrl: String = s"$manageTransitMovementsUrl/test-only/view-departures"

  lazy val enrolmentProxyUrl: String                   = configuration.get[Service]("microservice.services.enrolment-store-proxy").fullServiceUrl
  lazy val enrolmentManagementFrontendEnrolUrl: String = configuration.get[String]("urls.enrolmentManagementFrontendEnrolUrl")
  lazy val enrolmentIdentifierKey: String              = configuration.get[String]("keys.enrolmentIdentifierKey")
}
