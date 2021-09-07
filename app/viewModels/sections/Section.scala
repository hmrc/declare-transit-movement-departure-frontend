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

package viewModels.sections

import play.api.i18n.Messages
import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json.{__, OWrites}
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels.Text
import viewModels.AddAnotherViewModel

case class Section(sectionTitle: Option[Text], sectionSubTitle: Option[Text], rows: Seq[Row], addAnother: Option[AddAnotherViewModel])

object Section {
  def apply(sectionTitle: Text, rows: Seq[Row]): Section = new Section(Some(sectionTitle), None, rows, None)

  def apply(rows: Seq[Row], sectionSubTitle: Text): Section = new Section(None, Some(sectionSubTitle), rows, None)

  def apply(sectionTitle: Text, sectionSubTitle: Text, rows: Seq[Row]): Section = new Section(Some(sectionTitle), Some(sectionSubTitle), rows, None)

  def apply(rows: Seq[Row]): Section = new Section(None, None, rows, None)

  def apply(sectionTitle: Text, rows: Seq[Row], addAnother: AddAnotherViewModel): Section = new Section(Some(sectionTitle), None, rows, Some(addAnother))

  implicit def sectionWrites(implicit messages: Messages): OWrites[Section] =
    (
      (__ \ "sectionTitle").write[Option[Text]] and
        (__ \ "sectionSubTitle").write[Option[Text]] and
        (__ \ "rows").write[Seq[Row]] and
        (__ \ "addAnother").write[Option[AddAnotherViewModel]]
    )(unlift(Section.unapply))

}
