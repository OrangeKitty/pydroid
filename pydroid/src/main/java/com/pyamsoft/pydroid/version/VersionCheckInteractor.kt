/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.version

import android.support.annotation.CheckResult
import io.reactivex.Single
import timber.log.Timber

class VersionCheckInteractor(protected @JvmField val versionCheckService: VersionCheckService) {

  @JvmField protected var cachedResponse: Single<VersionCheckResponse>? = null

  @CheckResult fun checkVersion(packageName: String, force: Boolean): Single<Int> {
    return Single.defer {
      val dataSource: Single<VersionCheckResponse>
      if (cachedResponse == null || force) {
        Timber.d("Fetch from Network. Force: %s", force)
        dataSource = versionCheckService.checkVersion(packageName).cache()
        cachedResponse = dataSource
      } else {
        Timber.d("Fetch from cached response")
        dataSource = cachedResponse!!
      }

      return@defer dataSource
    }.map { it.currentVersion() }
  }
}
