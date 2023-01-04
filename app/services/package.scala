/*
 * Copyright 2023 HM Revenue & Customs
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

package object services {

  /**
    * Utility Function to collect when the condition is true
    * @param condition
    * @param func
    * @tparam T
    * @return Option[T]
    */
  def collectWhen[T](condition: Boolean)(func: => Option[T]): Option[T] = {
    val result = Option(condition) collect {
      case true => func
    }
    result.flatten
  }
}
