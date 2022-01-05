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

package models

import models.reference.PackageType
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class PackageTypeListSpec extends AnyFreeSpec with Matchers with OptionValues {

  "PackageTypeList" - {

    "must return full list" in {

      val packageTypeListData = Seq(PackageType("AB", "Description"))

      val packageTypeList = PackageTypeList(packageTypeListData)

      packageTypeList.fullList mustBe packageTypeListData
    }

    "must return specific PackageType" in {

      val packageType1 = PackageType("AB", "Description 1")
      val packageType2 = PackageType("CD", "Description 2")

      val packageTypeListData = Seq(packageType1, packageType2)

      val packageTypeList = PackageTypeList(packageTypeListData)

      packageTypeList.getPackageType(packageType1.code).value mustBe packageType1
    }
  }

}
