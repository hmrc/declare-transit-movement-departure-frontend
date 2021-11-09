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

import com.google.inject.Singleton
import utils.Format
import java.time.{Clock, LocalDateTime}

@Singleton
class DateTimeServiceImpl extends DateTimeService {

  def currentDateTime(clock: Clock = Clock.systemDefaultZone()): LocalDateTime = LocalDateTime.now(clock)

  def dateFormatted(clock: Clock = Clock.systemDefaultZone()): String = currentDateTime(clock).format(Format.dateFormatter)
}

trait DateTimeService {

  def currentDateTime(clock: Clock = Clock.systemDefaultZone()): LocalDateTime

  def dateFormatted(clock: Clock = Clock.systemDefaultZone()): String

}
