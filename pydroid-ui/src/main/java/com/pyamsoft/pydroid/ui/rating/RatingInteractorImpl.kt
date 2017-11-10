/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui.rating

import com.pyamsoft.pydroid.ui.RatingPreferences
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

internal class RatingInteractorImpl internal constructor(
    private val preferences: RatingPreferences) : RatingInteractor {

  override fun needsToViewRating(force: Boolean, versionCode: Int): Single<Boolean> {
    return Single.fromCallable {
      if (force) {
        return@fromCallable true
      } else {
        val code: Int = preferences.getRatingAcceptedVersion()
        val result = (code < versionCode)
        Timber.d("Version check: $code is lower than $versionCode ?  $result")
        return@fromCallable result
      }
    }
  }

  override fun saveRating(versionCode: Int): Completable =
      Completable.fromAction { preferences.setRatingAcceptedVersion(versionCode) }
}
