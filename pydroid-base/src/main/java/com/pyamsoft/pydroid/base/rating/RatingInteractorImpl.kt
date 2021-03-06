/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.base.rating

import io.reactivex.Completable
import io.reactivex.Single

internal class RatingInteractorImpl internal constructor(
  private val preferences: RatingPreferences
) : RatingInteractor {

  override fun needsToViewRating(
    force: Boolean,
    versionCode: Int
  ): Single<Boolean> {
    return Single.fromCallable {
      if (force) {
        return@fromCallable true
      } else {
        // If the version code is 1, it's the first app version, don't show a changelog
        if (versionCode <= 1) {
          return@fromCallable false
        } else {
          // If the preference is default, the app may be installed for the first time
          // regardless of the current version. Don't show change log, else show it
          val lastSeenVersion: Int = preferences.ratingAcceptedVersion
          return@fromCallable lastSeenVersion < RatingPreferences.DEFAULT_RATING_ACCEPTED_VERSION
        }
      }
    }
  }

  override fun saveRating(versionCode: Int): Completable =
    Completable.fromAction { preferences.ratingAcceptedVersion = versionCode }
}
