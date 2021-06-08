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

package repositories

import org.scalatestplus.mockito.MockitoSugar
import play.api.inject._

import scala.concurrent.Future

object FakeSessionCollectionIndexManager extends SessionCollectionIndexManager {
  override def started: Future[Unit] = Future.successful(())
}

class TestRepositoryModule
    extends SimpleModule(
      (_, _) =>
        Seq(
          bind[SessionCollection].toInstance(TestRepositoryModule.mockSessionCollection),
          bind(classOf[SessionRepository]).to(classOf[DefaultSessionRepository]),
          bind[InterchangeControlReferenceCollection].toInstance(TestRepositoryModule.mockInterchangeControlReferenceCollection),
          bind[SessionCollectionIndexManager].toInstance(FakeSessionCollectionIndexManager)
        )
    )

object TestRepositoryModule extends MockitoSugar {
  private val mockSessionCollection                     = mock[SessionCollection]
  private val mockInterchangeControlReferenceCollection = mock[InterchangeControlReferenceCollection]
}
