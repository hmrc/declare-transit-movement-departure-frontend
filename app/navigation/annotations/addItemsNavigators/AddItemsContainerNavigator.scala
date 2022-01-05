/*
 * Copyright 2022 HM Revenue & Customs
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

package navigation.annotations.addItemsNavigators

import controllers.addItems.containers.{routes => containerRoutes}
import controllers.addItems.specialMentions.{routes => specialMentionsRoutes}
import controllers.addItems.{routes => addItemsRoutes}
import derivable._
import models._
import navigation.Navigator
import pages._
import pages.addItems.containers._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsContainerNavigator @Inject() () extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case ContainerNumberPage(itemIndex, _)    => ua => Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.lrn, itemIndex, NormalMode))
    case AddAnotherContainerPage(itemIndex)   => ua => Some(specialMentionsRoutes.AddSpecialMentionController.onPageLoad(ua.lrn, itemIndex, NormalMode))
    case ConfirmRemoveContainerPage(index, _) => ua => Some(confirmRemoveContainerRoute(ua, index, NormalMode))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case ContainerNumberPage(itemIndex, _)    => ua => Some(containerRoutes.AddAnotherContainerController.onPageLoad(ua.lrn, itemIndex, CheckMode))
    case AddAnotherContainerPage(itemIndex)   => ua => Some(addItemsRoutes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, itemIndex))
    case ConfirmRemoveContainerPage(index, _) => ua => Some(confirmRemoveContainerRoute(ua, index, CheckMode))
  }

  private def confirmRemoveContainerRoute(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(DeriveNumberOfContainers(index)).getOrElse(0) match {
      case 0 => containerRoutes.ContainerNumberController.onPageLoad(ua.lrn, index, Index(0), mode)
      case _ => containerRoutes.AddAnotherContainerController.onPageLoad(ua.lrn, index, mode)
    }

}
