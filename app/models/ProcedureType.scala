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

sealed trait ProcedureType

object ProcedureType extends Enumerable.Implicits {

  case object Normal extends WithName("normal") with ProcedureType
  case object Simplified extends WithName("simplified") with ProcedureType

  val values: Seq[ProcedureType] = Seq(
    Normal,
    Simplified
  )

  def radios(form: Form[_]): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"procedureType.normal", Normal.toString),
      Radios.Radio(msg"procedureType.simplified", Simplified.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[ProcedureType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
