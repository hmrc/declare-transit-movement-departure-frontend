/*
 * Copyright 2020 HM Revenue & Customs
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

package models

import play.api.data.Form
import uk.gov.hmrc.viewmodels._

sealed trait DeclarationType

object DeclarationType extends Enumerable.Implicits {

  case object Option1 extends WithName("option1") with DeclarationType
  case object Option2 extends WithName("option2") with DeclarationType
  case object Option3 extends WithName("option3") with DeclarationType
  case object Option4 extends WithName("option4") with DeclarationType

  val values: Seq[DeclarationType] = Seq(
    Option1,
    Option2,
    Option3,
    Option4
  )

  def radios(form: Form[_])(): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"declarationType.option1", Option1.toString),
      Radios.Radio(msg"declarationType.option2", Option2.toString),
      Radios.Radio(msg"declarationType.option3", Option3.toString),
      Radios.Radio(msg"declarationType.option4", Option4.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[DeclarationType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
