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

import base.SpecBase
import models.reference._
import models.{DocumentTypeList, Index, PreviousReferencesDocumentTypeList, SpecialMentionList, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import pages.addItems.containers.ContainerNumberPage
import pages.addItems.specialMentions.SpecialMentionTypePage
import uk.gov.hmrc.viewmodels.MessageInterpolators

class AddItemsCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {
  // format: off

  private val documentTypeList = DocumentTypeList(Seq(DocumentType("code", "name", true)))
  private val previousReferencesDocumentTypeList = PreviousReferencesDocumentTypeList(Seq(PreviousReferencesDocumentType("code", Some("name"))))
  private val specialMentionList = SpecialMentionList(Seq(SpecialMention("code", "name")))

  private val updatedAnswers = emptyUserAnswers
    .set(ItemDescriptionPage(index), "test").success.value
    .set(ItemTotalGrossMassPage(index), 100.00).success.value
    .set(AddTotalNetMassPage(index), true).success.value
    .set(TotalNetMassPage(index), "20").success.value
    .set(IsCommodityCodeKnownPage(index), true).success.value
    .set(CommodityCodePage(index), "111111").success.value
    .set(ContainerNumberPage(itemIndex, containerIndex), arbitrary[String].sample.value).success.value
    .set(SpecialMentionTypePage(index, itemIndex), "code").success.value
    .set(PackageTypePage(index, itemIndex), PackageType("AB", "Description") ).success.value
    .set(HowManyPackagesPage(index, itemIndex), 123).success.value

  private def viewModel(userAnswers: UserAnswers) =
    AddItemsCheckYourAnswersViewModel(userAnswers, index, documentTypeList, previousReferencesDocumentTypeList, specialMentionList)

  private val data = viewModel(updatedAnswers)

  private val updatedAnswersWithUnpackedPackages = emptyUserAnswers
    .set(ItemDescriptionPage(index), "test").success.value
    .set(ItemTotalGrossMassPage(index), 100.00).success.value
    .set(AddTotalNetMassPage(index), true).success.value
    .set(TotalNetMassPage(index), "20").success.value
    .set(IsCommodityCodeKnownPage(index), true).success.value
    .set(CommodityCodePage(index), "111111").success.value
    .set(ContainerNumberPage(itemIndex, containerIndex), arbitrary[String].sample.value).success.value
    .set(SpecialMentionTypePage(index, itemIndex), "code").success.value
    .set(PackageTypePage(index, itemIndex), PackageType("NE", "Description") ).success.value
    .set(TotalPiecesPage(index, itemIndex), 123).success.value

  private val dataWithUnpackedPackages = viewModel(updatedAnswersWithUnpackedPackages)

  "AddItemsCheckYourAnswersViewModel" - {

    "display the correct number of sections" in {
      data.sections.length mustEqual 10
      data.sections.head.rows.length mustEqual 6
    }
    
    "details section have title and contain all rows" in {
      data.sections(0).sectionTitle.get mustBe msg"addItems.checkYourAnswersLabel.itemDetails"
      data.sections(0).rows.length mustEqual 6
    }

    "containers sections have title and contain all rows" in {
      data.sections(4).sectionTitle.get mustBe msg"addItems.checkYourAnswersLabel.containers"
      data.sections(4).rows.length mustEqual 1
    }

    "special mentions have title and contain all rows" in {
      data.sections(5).sectionTitle.get mustBe msg"addItems.checkYourAnswersLabel.specialMentions"
      data.sections(5).rows.length mustEqual 1
    }

    "packages section have title and contain all rows when package type is not unpacked" in {
      data.sections(3).sectionTitle.get mustBe msg"addItems.checkYourAnswersLabel.packages"
      data.sections(3).rows.length mustEqual 2
    }

    "packages section have title and contain all rows when package type is unpacked" in {
      dataWithUnpackedPackages.sections(3).sectionTitle.get mustBe msg"addItems.checkYourAnswersLabel.packages"
      dataWithUnpackedPackages.sections(3).rows.length mustEqual 2
    }

    "packages sections must have section for each package" in {
      val answers = updatedAnswers
        .set(PackageTypePage(index, Index(1)), PackageType("AB", "Description") ).success.value
        .set(HowManyPackagesPage(index, Index(1)), 123).success.value

      val result = viewModel(answers)

      result.sections(3).sectionTitle.get mustBe msg"addItems.checkYourAnswersLabel.packages"
      result.sections(3).rows.length mustEqual 2
      result.sections(3).addAnother mustNot be(defined)

      result.sections(4).sectionTitle mustNot be(defined)
      result.sections(4).rows.length mustEqual 2
      result.sections(4).addAnother.get.content mustBe msg"addItems.checkYourAnswersLabel.packages.addRemove"
    }
  }
  // format: on
}
