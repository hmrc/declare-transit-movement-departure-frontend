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
import models.{CountryList, LocalReferenceNumber, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.Reads
import play.api.mvc.Call
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.{Content, MessageInterpolators}

abstract private[utils] class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  lazy val lrn: LocalReferenceNumber = userAnswers.lrn

  def getAnswerAndBuildRow[T](
    page: QuestionPage[T],
    formatAnswer: T => Content,
    prefix: String,
    id: Option[String],
    call: Call,
    args: Any*
  )(implicit rds: Reads[T]): Option[Row] =
    userAnswers.get(page) map {
      answer =>
        buildRow(
          prefix = prefix,
          answer = formatAnswer(answer),
          id = id,
          call = call,
          args = args: _*
        )
    }

  def getAnswerAndBuildDynamicRow[T](
    page: QuestionPage[T],
    formatAnswer: T => Content,
    dynamicPrefix: T => String,
    dynamicId: T => Option[String],
    call: Call,
    args: Any*
  )(implicit rds: Reads[T]): Option[Row] =
    userAnswers.get(page) map {
      answer =>
        buildRow(
          prefix = dynamicPrefix(answer),
          answer = formatAnswer(answer),
          id = dynamicId(answer),
          call = call,
          args = args: _*
        )
    }

  def getAnswerAndBuildValuelessRow[T](
    page: QuestionPage[T],
    formatAnswer: T => String,
    id: Option[String],
    call: Call
  )(implicit rds: Reads[T]): Option[Row] =
    userAnswers.get(page) map {
      answer =>
        buildValuelessRow(
          key = formatAnswer(answer),
          id = id,
          call = call
        )
    }

  def getAnswerAndBuildRemovableRow[T](
    page: QuestionPage[T],
    formatAnswer: T => String,
    id: String,
    changeCall: Call,
    removeCall: Call
  )(implicit rds: Reads[T]): Option[Row] =
    userAnswers.get(page) map {
      answer =>
        buildRemovableRow(
          key = formatAnswer(answer),
          id = id,
          changeCall = changeCall,
          removeCall = removeCall
        )
    }

  def buildRow(
    prefix: String,
    answer: Content,
    id: Option[String],
    call: Call,
    args: Any*
  ): Row =
    Row(
      key = Key(msg"$prefix.checkYourAnswersLabel".withArgs(args: _*), classes = Seq("govuk-!-width-one-half")),
      value = Value(answer),
      actions = List(
        Action(
          content = msg"site.edit",
          href = call.url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"$prefix.checkYourAnswersLabel".withArgs(args: _*))),
          attributes = id.fold[Map[String, String]](Map.empty)(
            id => Map("id" -> id)
          )
        )
      )
    )

  def buildValuelessRow(
    key: String,
    id: Option[String],
    call: Call
  ): Row =
    Row(
      key = Key(lit"$key"),
      value = Value(lit""),
      actions = List(
        Action(
          content = msg"site.edit",
          href = call.url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
          attributes = id.fold[Map[String, String]](Map.empty)(
            id => Map("id" -> id)
          )
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

  def getAnswerAndBuildSimpleCountryRow[T](
    page: QuestionPage[T],
    getCountryCode: T => CountryCode,
    countryList: CountryList,
    prefix: String,
    id: Option[String],
    call: Call
  )(implicit rds: Reads[T]): Option[Row] =
    getAnswerAndBuildCountryRow[T](
      getCountryCode = getCountryCode,
      countryList = countryList,
      getAnswerAndBuildRow = formatAnswer =>
        getAnswerAndBuildRow[T](
          page = page,
          formatAnswer = answer => lit"${formatAnswer(answer)}",
          prefix = prefix,
          id = id,
          call = call
        )
    )

  def getAnswerAndBuildCountryRow[T](
    getCountryCode: T => CountryCode,
    countryList: CountryList,
    getAnswerAndBuildRow: (T => String) => Option[Row]
  ): Option[Row] = {
    val formatAnswer: T => String = x => {
      val countryCode: CountryCode = getCountryCode(x)
      countryList.getCountry(countryCode).map(_.description).getOrElse(countryCode.code)
    }
    getAnswerAndBuildRow(formatAnswer)
  }

}
