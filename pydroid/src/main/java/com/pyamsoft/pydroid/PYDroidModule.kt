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

package com.pyamsoft.pydroid

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope.LIBRARY
import com.pyamsoft.pydroid.about.AboutLibrariesModel
import com.pyamsoft.pydroid.about.Licenses
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@RestrictTo(LIBRARY) class PYDroidModule(context: Context, val isDebug: Boolean) {

  // Singleton
  private val appContext = context.applicationContext
  private val licenses = Licenses.getLicenses()
  private val preferences = PYDroidPreferencesImpl(appContext)

  // Singleton
  @CheckResult internal fun provideContext(): Context {
    return appContext
  }

  // Singleton
  @CheckResult internal fun provideRatingPreferences(): RatingPreferences {
    return preferences
  }

  // Singleton
  @CheckResult internal fun provideLicenseMap(): List<AboutLibrariesModel> {
    return licenses
  }

  // Singleton
  @CheckResult internal fun provideSubScheduler(): Scheduler {
    return Schedulers.io()
  }

  // Singleton
  @CheckResult internal fun provideObsScheduler(): Scheduler {
    return AndroidSchedulers.mainThread()
  }
}
