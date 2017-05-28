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

package com.pyamsoft.pydroid.ui.version

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.activity.BackPressConfirmActivity
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.version.VersionCheckPresenter
import com.pyamsoft.pydroid.version.VersionCheckProvider
import timber.log.Timber

abstract class VersionCheckActivity : BackPressConfirmActivity(), VersionCheckProvider {
  internal lateinit var presenter: VersionCheckPresenter
  internal var versionChecked: Boolean = false

  private // Always enabled for release builds
  val isVersionCheckEnabled: Boolean
    @CheckResult get() = !PYDroid.instance.isDebugMode || shouldCheckVersion()

  @CheckResult protected fun shouldCheckVersion(): Boolean {
    return true
  }

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    versionChecked = savedInstanceState != null && savedInstanceState.getBoolean(VERSION_CHECKED,
        false)

    PYDroid.instance.provideComponent().plusVersionCheckComponent().inject(this)
  }

  @CallSuper override fun onStart() {
    super.onStart()
    if (!versionChecked && isVersionCheckEnabled) {
      presenter.checkForUpdates(packageName, currentApplicationVersion,
          object : VersionCheckPresenter.UpdateCheckCallback {
            override fun onVersionCheckFinished() {
              Timber.d("License check finished, mark")
              versionChecked = true
            }

            override fun onUpdatedVersionFound(oldVersionCode: Int, updatedVersionCode: Int) {
              Timber.d("Updated version found. %d => %d", oldVersionCode, updatedVersionCode)
              DialogUtil.guaranteeSingleDialogFragment(this@VersionCheckActivity,
                  VersionUpgradeDialog.newInstance(provideApplicationName(), oldVersionCode,
                      updatedVersionCode), VersionUpgradeDialog.TAG)
            }
          })
    }
  }

  @CallSuper override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putBoolean(VERSION_CHECKED, versionChecked)
    super.onSaveInstanceState(outState)
  }

  companion object {

    private const val VERSION_CHECKED = "version_check_completed"
  }
}
