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

package viewModels

import controllers.addItems.routes
import models.{Index, Mode, UserAnswers}
import pages.PackageTypePage
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

object PackageViewModel {

  def packageRows(itemIndex: Index, packageRange: Int, userAnswers: UserAnswers, mode: Mode): Seq[Option[Row]] =
    List.range(0, packageRange).map {
      packagePosition =>
        val packageIndex = Index(packagePosition)

        userAnswers.get(PackageTypePage(itemIndex, packageIndex)).map {
          answer =>
            Row(
              key = Key(lit"$answer"),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.change",
                  href = routes.PackageTypeController.onPageLoad(userAnswers.id, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer.toString)),
                  attributes = Map("id" -> s"""change-package-${packageIndex.display}""")
                ),
                Action(
                  content = msg"site.delete",
                  href = routes.RemovePackageController.onPageLoad(userAnswers.id, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(answer.toString)),
                  attributes = Map("id" -> s"""remove-package-${packageIndex.display}""")
                )
              )
            )
        }
    }
}
