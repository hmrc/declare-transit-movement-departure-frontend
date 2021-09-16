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

package utils

import models.reference.CountryCode
import models.{CheckMode, CountryList, LocalReferenceNumber, Mode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.Reads
import play.api.mvc.Call
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.{Content, MessageInterpolators}

abstract private[utils] class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  lazy val lrn: LocalReferenceNumber = userAnswers.lrn

  def getAnswerAndBuildRow[T](
    page: QuestionPage[T],
    format: T => Content,
    prefix: String,
    id: Option[String],
    call: Call,
    args: Any*
  )(implicit rds: Reads[T]): Option[Row] =
    userAnswers.get(page) map {
      answer =>
        buildRow(prefix, format(answer), id, call, args: _*)
    }

  def getAnswerAndBuildValuelessRow[T](
    page: QuestionPage[T],
    format: T => Content,
    id: Option[String],
    call: Call
  )(implicit rds: Reads[T]): Option[Row] =
    userAnswers.get(page) map {
      answer =>
        buildValuelessRow(format(answer), id, call, answer)
    }

  def getAnswerAndBuildRemovableRow[T](
    page: QuestionPage[T],
    format: T => String,
    id: String,
    changeCall: Call,
    removeCall: Call
  )(implicit rds: Reads[T]): Option[Row] =
    userAnswers.get(page) map {
      answer =>
        buildRemovableRow(key = format(answer), id = id, changeCall = changeCall, removeCall = removeCall)
    }

  def buildRow(
    prefix: String,
    content: Content,
    id: Option[String],
    call: Call,
    args: Any*
  ): Row =
    Row(
      key = Key(msg"$prefix.checkYourAnswersLabel".withArgs(args: _*), classes = Seq("govuk-!-width-one-half")),
      value = Value(content),
      actions = List(
        Action(
          content = msg"site.edit",
          href = call.url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"$prefix.checkYourAnswersLabel".withArgs(args: _*))),
          attributes = id
            .map(
              x => Map("id" -> x)
            )
            .getOrElse(Map.empty)
        )
      )
    )

  def buildValuelessRow(
    key: Content,
    id: Option[String],
    call: Call,
    args: Any*
  ): Row =
    Row(
      key = Key(key),
      value = Value(lit""),
      actions = List(
        Action(
          content = msg"site.edit",
          href = call.url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(args: _*)),
          attributes = id
            .map(
              x => Map("id" -> x)
            )
            .getOrElse(Map.empty)
        )
      )
    )

  def buildRemovableRow(
    key: String,
    value: String = "",
    id: String,
    changeCall: Call,
    removeCall: Call
  ): Row =
    Row(
      key = Key(lit"$key"),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content = msg"site.edit",
          href = changeCall.url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
          attributes = Map("id" -> s"change-$id")
        ),
        Action(
          content = msg"site.delete",
          href = removeCall.url,
          visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(key)),
          attributes = Map("id" -> s"remove-$id")
        )
      )
    )

  def getAnswerAndBuildCountryRow[T](
    page: QuestionPage[T],
    f: T => CountryCode,
    countryList: CountryList,
    prefix: String,
    id: String,
    call: (LocalReferenceNumber, Mode) => Call
  )(implicit rds: Reads[T]): Option[Row] = {
    val format: T => Content = x => {
      val countryCode: CountryCode = f(x)
      lit"${countryList.getCountry(countryCode).map(_.description).getOrElse(countryCode.code)}"
    }
    getAnswerAndBuildRow[T](page, format, prefix, Some(id), call(lrn, CheckMode))
  }

}
