import sbt._
import play.core.PlayVersion.current

object AppDependencies {

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"              % "0.20.11-play26",
    "uk.gov.hmrc"       %% "logback-json-logger"              % "4.8.0",
    "uk.gov.hmrc"       %% "play-health"                      % "3.14.0-play-26",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"    % "1.2.0-play-26",
    "uk.gov.hmrc"       %% "bootstrap-play-26"                % "1.14.0",
    "uk.gov.hmrc"       %% "play-whitelist-filter"            % "3.4.0-play-26",
    "uk.gov.hmrc"       %% "play-nunjucks"                    % "0.23.0-play-26",
    "uk.gov.hmrc"       %% "play-nunjucks-viewmodel"          % "0.9.0-play-26",
    "org.webjars.npm"   %  "govuk-frontend"                   % "3.3.0",
    "com.typesafe.play" %% "play-iteratees"                   % "2.6.1",
    "com.typesafe.play" %% "play-iteratees-reactive-streams"  % "2.6.1"
  )

  val test = Seq(
    "org.scalatest"               %% "scalatest"                % "3.2.0",
    "org.scalatestplus"           %% "mockito-3-2"              % "3.1.2.0",
    "org.scalatestplus.play"      %% "scalatestplus-play"       % "3.1.3",
    "org.scalatestplus"           %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.pegdown"                 %  "pegdown"                  % "1.6.0",
    "org.jsoup"                   %  "jsoup"                    % "1.10.3",
    "com.typesafe.play"           %% "play-test"                % current,
    "org.mockito"                 %  "mockito-core"             % "3.3.3",
    "org.scalacheck"              %% "scalacheck"               % "1.14.3",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.1",
    "com.vladsch.flexmark"        % "flexmark-all"              % "0.35.10"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.5.23"
  val akkaHttpVersion = "10.0.15"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream"    % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf"  % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"     % akkaVersion,
    "com.typesafe.akka" %% "akka-actor"     % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  )
}
