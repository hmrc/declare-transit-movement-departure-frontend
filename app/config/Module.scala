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

package config

import com.google.inject.AbstractModule
import controllers.actions._
import navigation._
import navigation.annotations._
import services.{DateTimeService, DateTimeServiceImpl}

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[Navigator]).annotatedWith(classOf[PreTaskListDetails]).to(classOf[PreTaskListNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[MovementDetails]).to(classOf[MovementDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[RouteDetails]).to(classOf[RouteDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[TransportDetails]).to(classOf[TransportDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[TraderDetails]).to(classOf[TraderDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[GoodsSummary]).to(classOf[GoodsSummaryNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[GuaranteeDetails]).to(classOf[GuaranteeDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[SpecialMentions]).to(classOf[SpecialMentionsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[AddItems]).to(classOf[AddItemsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[Document]).to(classOf[DocumentNavigator])

    bind(classOf[DataRetrievalActionProvider]).to(classOf[DataRetrievalActionProviderImpl])
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl])

    bind(classOf[IdentifierAction]).to(classOf[AuthenticatedIdentifierAction])
    bind(classOf[DateTimeService]).to(classOf[DateTimeServiceImpl]).asEagerSingleton()

    bind(classOf[RenderConfig]).to(classOf[RenderConfigImpl]).asEagerSingleton()
  }
}
