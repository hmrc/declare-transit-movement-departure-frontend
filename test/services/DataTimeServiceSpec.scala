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

package services

import java.time.{Clock, ZoneId}
import base.{MockServiceApp, SpecBase}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers

class DataTimeServiceSpec extends SpecBase with MockServiceApp with BeforeAndAfterEach with Matchers {

  val dataTimeService: DateTimeService = app.injector.instanceOf[DateTimeService]

  "currentDateTime" - {

    "must return different times based on the zone Id of the system clock" in {

      val clockOne: Clock = Clock.system(ZoneId.of("Australia/Darwin"))
      val clockTwo: Clock = Clock.system(ZoneId.of("America/New_York"))

      dataTimeService.currentDateTime(clockOne).isEqual(dataTimeService.currentDateTime(clockTwo)) mustBe false

    }
  }

}
