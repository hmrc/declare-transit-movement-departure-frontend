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

package forms.mappings

trait StringEquivalence[A] {
  def equivalentToString(lhs: A, formValue: String): Boolean
}

object StringEquivalence {

  def apply[A](checker: (A, String) => Boolean): StringEquivalence[A] = new StringEquivalence[A] {
    override def equivalentToString(lhs: A, formValue: String): Boolean = checker(lhs, formValue)
  }

  implicit class FormEqualityCheckOps[A: StringEquivalence](lhs: A) {
    def equalsString(formValue: String): Boolean = implicitly[StringEquivalence[A]].equivalentToString(lhs, formValue)
  }

  implicit val stringFormEquality: StringEquivalence[String] = StringEquivalence[String](_ == _)
  implicit val integerFormEquality: StringEquivalence[Int]   = StringEquivalence[Int](_.toString == _)
}
