import sbt._
import play.core.PlayVersion.current

object AppDependencies {

  private val mongoVersion = "0.71.0"
  private val monocleVersion = "2.1.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo"          %% "hmrc-mongo-play-28"              % mongoVersion,
    "uk.gov.hmrc"                %% "logback-json-logger"             % "5.1.0",
    "uk.gov.hmrc"                %% "play-conditional-form-mapping"   % "1.11.0-play-28",
    "uk.gov.hmrc"                %% "bootstrap-frontend-play-28"      % "7.3.0",
    "uk.gov.hmrc"                %% "play-allowlist-filter"           % "1.0.0-play-28",
    "uk.gov.hmrc"                %% "play-nunjucks"                   % "0.40.0-play-28",
    "uk.gov.hmrc"                %% "play-nunjucks-viewmodel"         % "0.16.0-play-28",
    "org.webjars.npm"            %  "govuk-frontend"                  % "4.3.1",
    "uk.gov.hmrc.webjars"        %  "hmrc-frontend"                   % "5.7.0",
    "com.typesafe.play"          %% "play-iteratees"                  % "2.6.1",
    "com.typesafe.play"          %% "play-iteratees-reactive-streams" % "2.6.1",
    "com.lucidchart"             %% "xtract"                          % "2.2.1",
    "com.github.julien-truffaut" %% "monocle-core"                    % monocleVersion,
    "com.github.julien-truffaut" %% "monocle-macro"                   % monocleVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"          %% "hmrc-mongo-test-play-28"  % mongoVersion,
    "org.scalatest"              %% "scalatest"                % "3.2.12",
    "org.scalatestplus.play"     %% "scalatestplus-play"       % "5.1.0",
    "org.mockito"                %  "mockito-core"             % "4.8.0",
    "org.scalatestplus"          %% "mockito-4-5"              % "3.2.12.0",
    "org.scalacheck"             %% "scalacheck"               % "1.16.0",
    "org.scalatestplus"          %% "scalacheck-1-16"          % "3.2.13.0",
    "wolfendale"                 %% "scalacheck-gen-regexp"    % "0.1.2",
    "org.pegdown"                %  "pegdown"                  % "1.6.0",
    "org.jsoup"                  %  "jsoup"                    % "1.15.3",
    "com.typesafe.play"          %% "play-test"                % current,
    "com.github.tomakehurst"     %  "wiremock-standalone"      % "2.27.2",
    "org.typelevel"              %% "discipline-core"          % "1.5.1",
    "org.typelevel"              %% "discipline-scalatest"     % "2.2.0",
    "com.github.julien-truffaut" %% "monocle-law"              % monocleVersion,
    "com.vladsch.flexmark"       %  "flexmark-all"             % "0.62.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
