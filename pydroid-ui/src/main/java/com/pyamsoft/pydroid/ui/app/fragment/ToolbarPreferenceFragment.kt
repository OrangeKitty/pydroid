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

package com.pyamsoft.pydroid.ui.app.fragment

import android.arch.lifecycle.Lifecycle.Event.ON_CREATE
import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.Lifecycle.Event.ON_PAUSE
import android.arch.lifecycle.Lifecycle.Event.ON_RESUME
import android.arch.lifecycle.Lifecycle.Event.ON_START
import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.pyamsoft.pydroid.ui.app.activity.ToolbarActivity

abstract class ToolbarPreferenceFragment : PreferenceFragmentCompat(),
    BackPressHandler,
    ToolbarProvider,
    ViewLifecycleProvider {

  private val viewLifecycleOwner = ViewLifecycleOwner()
  final override val viewLifecycle: LifecycleOwner = viewLifecycleOwner

  override val toolbarActivity: ToolbarActivity
    @get:CheckResult get() {
      val a = activity
      if (a is ToolbarActivity) {
        return a
      } else {
        throw ClassCastException("Activity does not implement ToolbarActivity")
      }
    }

  override fun onBackPressed(): Boolean {
    return false
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    viewLifecycleOwner.registry.handleLifecycleEvent(ON_CREATE)
  }

  override fun onStart() {
    super.onStart()
    viewLifecycleOwner.registry.handleLifecycleEvent(ON_START)
  }

  override fun onResume() {
    super.onResume()
    viewLifecycleOwner.registry.handleLifecycleEvent(ON_RESUME)
  }

  override fun onPause() {
    super.onPause()
    viewLifecycleOwner.registry.handleLifecycleEvent(ON_PAUSE)
  }

  override fun onStop() {
    super.onStop()
    viewLifecycleOwner.registry.handleLifecycleEvent(ON_STOP)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    viewLifecycleOwner.registry.handleLifecycleEvent(ON_DESTROY)
  }
}