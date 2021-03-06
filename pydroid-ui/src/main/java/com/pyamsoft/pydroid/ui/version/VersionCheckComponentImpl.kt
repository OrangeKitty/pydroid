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

package com.pyamsoft.pydroid.ui.version

import com.pyamsoft.pydroid.base.version.VersionCheckModule

internal class VersionCheckComponentImpl internal constructor(
  private val versionCheckModule: VersionCheckModule,
  private val packageName: String,
  private val currentVersion: Int
) : VersionCheckComponent {

  override fun inject(activity: VersionCheckActivity) {
    activity.presenter = versionCheckModule.getPresenter(packageName, currentVersion)
  }
}
