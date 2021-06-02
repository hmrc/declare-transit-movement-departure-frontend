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

import cats.data.Kleisli
import cats.implicits._
import models.Status.{CannotStartYet, Completed, InProgress, NotStarted}
import models.journeyDomain.{EitherType, ReaderError, UserAnswersReader}
import models.{SectionDetails, Status, UserAnswers}

private[viewModels] class TaskListDslCollectSectionName(userAnswers: UserAnswers) {

  def sectionName(sectionName: String): TaskListDslSectionNameStage =
    new TaskListDslSectionNameStage(userAnswers)(sectionName)

}

private[viewModels] class TaskListDslSectionNameStage(userAnswers: UserAnswers)(sectionName: String) {

  def ifNoDependencyOnOtherSection[A, B]: TaskListDslIfDependentSectionStage[A] =
    new TaskListDslIfDependentSectionStage[A](userAnswers)(sectionName, None)

  def ifDependentSectionCompleted[A, B](readerIfDependentSectionCompleted: UserAnswersReader[A]): TaskListDslIfDependentSectionStage[A] =
    new TaskListDslIfDependentSectionStage[A](userAnswers)(sectionName, Some(readerIfDependentSectionCompleted))

  def conditionalDependencyOnSection[A, B](readerIfDependentSectionCompleted: UserAnswersReader[A])(
    isDependent: Boolean): TaskListDslIfDependentSectionStage[A] =
    if (isDependent) {
      ifDependentSectionCompleted(readerIfDependentSectionCompleted)
    } else {
      ifNoDependencyOnOtherSection
    }

}

private[viewModels] class TaskListDslIfDependentSectionStage[A](userAnswers: UserAnswers)(sectionName: String,
                                                                                          readerIfDependentSectionCompleted: Option[UserAnswersReader[A]]) {

  def ifCompleted[B](readerIfCompleted: UserAnswersReader[B], urlIfCompleted: String): TaskListDslIfCompletedStage[A, B] =
    new TaskListDslIfCompletedStage(userAnswers)(sectionName, readerIfDependentSectionCompleted, readerIfCompleted, urlIfCompleted)

}

private[viewModels] class TaskListDslIfCompletedStage[A, B](userAnswers: UserAnswers)(sectionName: String,
                                                                                      readerIfDependentSectionCompleted: Option[UserAnswersReader[A]],
                                                                                      readerIfCompleted: UserAnswersReader[B],
                                                                                      urlIfCompleted: String) {

  def ifInProgress[C](readerIfInProgress: UserAnswersReader[C], urlIfInProgress: String): TaskListDslIfInProgressStage[A, B, C] =
    new TaskListDslIfInProgressStage(userAnswers)(sectionName,
                                                  readerIfDependentSectionCompleted,
                                                  readerIfCompleted,
                                                  urlIfCompleted,
                                                  readerIfInProgress,
                                                  urlIfInProgress)

}

private[viewModels] class TaskListDslIfInProgressStage[A, B, C](userAnswers: UserAnswers)(sectionName: String,
                                                                                          readerIfDependentSectionCompleted: Option[UserAnswersReader[A]],
                                                                                          readerIfCompleted: UserAnswersReader[B],
                                                                                          urlIfCompleted: String,
                                                                                          readerIfInProgress: UserAnswersReader[C],
                                                                                          urlIfInProgress: String) {

  def ifNotStarted(urlIfNotStarted: String): TaskListDsl[A, B, C] =
    new TaskListDsl(userAnswers)(sectionName,
                                 readerIfDependentSectionCompleted,
                                 readerIfCompleted,
                                 urlIfCompleted,
                                 readerIfInProgress,
                                 urlIfInProgress,
                                 urlIfNotStarted)
}

private[viewModels] class TaskListDsl[A, B, C](userAnswers: UserAnswers)(
  sectionName: String,
  readerIfDependentSectionCompleted: Option[UserAnswersReader[A]],
  readerIfCompleted: UserAnswersReader[B],
  urlIfCompleted: String,
  readerIfInProgress: UserAnswersReader[C],
  urlIfInProgress: String,
  urlIfNotStarted: String
) {

  val section: SectionDetails = {
    val completed = readerIfCompleted
      .map[(String, Status)](
        _ => (urlIfCompleted, Completed)
      )

    val inProgress: Kleisli[EitherType, UserAnswers, (String, Status)] = readerIfInProgress
      .map[(String, Status)](
        _ => (urlIfInProgress, InProgress)
      )

    val (onwardRoute, status) = completed
      .orElse(inProgress)
      .run(userAnswers)
      .getOrElse((urlIfNotStarted, NotStarted))

    val (updatedOnwardRoute, updatedStatus) = readerIfDependentSectionCompleted match {
      case Some(reader) =>
        reader.run(userAnswers) match {
          case Right(_) => (onwardRoute, status)
          case _        => ("", CannotStartYet)
        }
      case _ => (onwardRoute, status)
    }

    SectionDetails(sectionName, updatedOnwardRoute, updatedStatus)
  }

  val collectReaderErrors: Option[(String, ReaderError)] = {
    if (section.status == InProgress) {
      readerIfCompleted.run(userAnswers).left.toOption.map(readerError => (section.name, readerError))
    } else {
      None
    }
  }
}
