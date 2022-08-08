import sbt._
import play.core.PlayVersion.current

object AppDependencies {

  private val mongoVersion = "0.68.0"
  private val monocleVersion = "2.1.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo"          %% "hmrc-mongo-play-28"              % mongoVersion,
    "uk.gov.hmrc"                %% "logback-json-logger"             % "5.1.0",
    "uk.gov.hmrc"                %% "play-conditional-form-mapping"   % "1.10.0-play-28",
    "uk.gov.hmrc"                %% "bootstrap-frontend-play-28"      % "5.24.0",
    "uk.gov.hmrc"                %% "play-allowlist-filter"           % "1.0.0-play-28",
    "uk.gov.hmrc"                %% "play-nunjucks"                   % "0.35.0-play-28",
    "uk.gov.hmrc"                %% "play-nunjucks-viewmodel"         % "0.15.0-play-28",
    "org.webjars.npm"            % "govuk-frontend"                   % "3.14.0",
    "uk.gov.hmrc.webjars"        % "hmrc-frontend"                    % "4.0.0",
    "com.typesafe.play"          %% "play-iteratees"                  % "2.6.1",
    "com.typesafe.play"          %% "play-iteratees-reactive-streams" % "2.6.1",
    "com.lucidchart"             %% "xtract"                          % "2.2.1",
    "com.github.julien-truffaut" %% "monocle-core"                    % monocleVersion,
    "com.github.julien-truffaut" %% "monocle-macro"                   % monocleVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"          %% "hmrc-mongo-test-play-28"  % mongoVersion,
    "org.scalatest"              %% "scalatest"                % "3.2.10",
    "org.scalatestplus"          %% "mockito-3-2"              % "3.1.2.0",
    "org.scalatestplus.play"     %% "scalatestplus-play"       % "5.1.0",
    "org.scalatestplus"          %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.pegdown"                 % "pegdown"                  % "1.6.0",
    "org.jsoup"                   % "jsoup"                    % "1.14.3",
    "com.typesafe.play"          %% "play-test"                % current,
    "org.mockito"                 % "mockito-core"             % "4.1.0",
    "org.scalacheck"             %% "scalacheck"               % "1.15.4",
    "wolfendale"                 %% "scalacheck-gen-regexp"    % "0.1.1",
    "com.github.tomakehurst"      % "wiremock-standalone"      % "2.27.2",
    "com.vladsch.flexmark"        % "flexmark-all"             % "0.62.2",
    "org.typelevel"              %% "discipline-core"          % "1.1.5",
    "org.typelevel"              %% "discipline-scalatest"     % "2.1.5",
    "com.github.julien-truffaut" %% "monocle-law"              % monocleVersion
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
