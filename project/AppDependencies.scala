import sbt._
import play.core.PlayVersion.current

object AppDependencies {

  val disciplineVersion = "1.0.0"
  val monocleVersion    = "2.0.3"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo"          %% "play2-reactivemongo"             % "0.20.13-play26",
    "org.reactivemongo"          %% "reactivemongo-play-json-compat"  % "0.20.13-play26",
    "uk.gov.hmrc"                %% "logback-json-logger"             % "5.1.0",
    "uk.gov.hmrc"                %% "play-conditional-form-mapping"   % "1.9.0-play-26",
    "uk.gov.hmrc"                %% "play-ui"                         % "9.6.0-play-26",
    "uk.gov.hmrc"                %% "bootstrap-frontend-play-26"      % "4.0.0",
    "uk.gov.hmrc"                %% "play-allowlist-filter"           % "1.0.0-play-26",
    "uk.gov.hmrc"                %% "play-nunjucks"                   % "0.29.0-play-26",
    "uk.gov.hmrc"                %% "play-nunjucks-viewmodel"         % "0.14.0-play-26",
    "org.webjars.npm"             % "govuk-frontend"                  % "3.13.0",
    "org.webjars.npm"             % "hmrc-frontend"                   % "1.35.2",
    "com.typesafe.play"          %% "play-iteratees"                  % "2.6.1",
    "com.typesafe.play"          %% "play-iteratees-reactive-streams" % "2.6.1",
    "com.lucidchart"             %% "xtract"                          % "2.2.1",
    "com.github.julien-truffaut" %% "monocle-core"                    % monocleVersion,
    "com.github.julien-truffaut" %% "monocle-macro"                   % monocleVersion
  )

  val test = Seq(
    "org.scalatest"              %% "scalatest"                % "3.2.9",
    "org.scalatestplus"          %% "mockito-3-2"              % "3.1.2.0",
    "org.scalatestplus.play"     %% "scalatestplus-play"       % "3.1.3",
    "org.scalatestplus"          %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.pegdown"                 % "pegdown"                  % "1.6.0",
    "org.jsoup"                   % "jsoup"                    % "1.10.3",
    "com.typesafe.play"          %% "play-test"                % current,
    "org.mockito"                 % "mockito-core"             % "3.3.3",
    "org.scalacheck"             %% "scalacheck"               % "1.14.3",
    "wolfendale"                 %% "scalacheck-gen-regexp"    % "0.1.1",
    "com.github.tomakehurst"      % "wiremock-standalone"      % "2.27.0",
    "com.vladsch.flexmark"        % "flexmark-all"             % "0.35.10",
    "org.typelevel"              %% "discipline-core"          % "1.0.0",
    "org.typelevel"              %% "discipline-scalatest"     % "1.0.0",
    "com.github.julien-truffaut" %% "monocle-law"              % monocleVersion
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion     = "2.5.23"
  val akkaHttpVersion = "10.0.15"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream"    % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf"  % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"     % akkaVersion,
    "com.typesafe.akka" %% "akka-actor"     % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  )
}
