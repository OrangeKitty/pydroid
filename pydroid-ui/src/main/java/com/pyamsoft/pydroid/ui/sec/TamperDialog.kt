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

package com.pyamsoft.pydroid.ui.sec

import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.social.Linker

internal class TamperDialog : ToolbarDialog() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    activity!!.let {
      return AlertDialog.Builder(it)
          .setTitle("WARNING: THIS APPLICATION IS NOT OFFICIAL")
          .setMessage(R.string.tamper_msg)
          .setCancelable(false)
          .setPositiveButton("Take Me") { _, _ ->
            Linker.clickGooglePlay(it)
            killApp()
          }
          .setNegativeButton("Close") { _, _ -> killApp() }
          .create()
    }
  }

  /**
   * Kills the app and clears the data to prevent any malicious services or code from possibly
   * running in the background
   */
  private fun killApp() {
    dismiss()
    activity?.also {
      it.finish()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        val activityManager =
          it.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.clearApplicationUserData()
      }
    }
  }

  companion object {

    internal const val TAG = "TamperDialog"
  }
}
