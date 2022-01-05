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

package models.messages.goodsitem

import xml.XMLWrites

import scala.xml.NodeSeq

final case class SecurityDetailsSubmission(
  ComRefNumHEA: Option[String],
  MetOfPayGDI12: Option[String],
  UNDanGooCodGDI: Option[String]
)

object SecurityDetailsSubmission {

  implicit def writes: XMLWrites[SecurityDetailsSubmission] = XMLWrites[SecurityDetailsSubmission] {
    references =>
      val comRefNumHEA = references.ComRefNumHEA.fold(NodeSeq.Empty)(
        value => <ComRefNumHEA>{value}</ComRefNumHEA>
      )
      val metOfPayGDI12 = references.MetOfPayGDI12.fold(NodeSeq.Empty)(
        value => <MetOfPayGDI12>{value}</MetOfPayGDI12>
      )
      val uNDanGooCodGDI = references.UNDanGooCodGDI.fold(NodeSeq.Empty)(
        value => <UNDanGooCodGDI>{value}</UNDanGooCodGDI>
      )

      comRefNumHEA ++ metOfPayGDI12 ++ uNDanGooCodGDI

  }
}
