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

package com.pyamsoft.pydroid.design.fab

import android.support.annotation.CheckResult
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.view.View
import com.pyamsoft.pydroid.design.util.hide
import com.pyamsoft.pydroid.design.util.show
import timber.log.Timber

/**
 * Floating Action Button behavior which hides button after scroll distance is passed
 */
class HideScrollFABBehavior(
  private val distanceNeeded: Int = 0,
  private val onHidden: (FloatingActionButton) -> Unit = {},
  private val onShown: (FloatingActionButton) -> Unit = {}
) : FloatingActionButton.Behavior() {

  var isAnimating = false
    @get:CheckResult get
    private set

  fun endAnimation() {
    this.isAnimating = false
  }

  override fun onNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: FloatingActionButton,
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int
  ) {
    if (dyConsumed > distanceNeeded && child.isShown) {
      if (!isAnimating) {
        isAnimating = true
        Timber.w("Hide FAB")
        child.hide {
          Timber.w(
              "Support library as on 25.1.0 sets FAB visibility to GONE, making it ignore other scrolling event."
          )
          Timber.w("Set it to invisible to fix this problem")
          visibility = View.INVISIBLE
          isAnimating = false
          onHidden(this)
        }
      }
    } else if (dyConsumed < -distanceNeeded && !child.isShown) {
      if (!isAnimating) {
        isAnimating = true
        Timber.w("Show FAB")
        child.show {
          visibility = View.VISIBLE
          isAnimating = false
          onShown(this)
        }
      }
    }
  }

  override fun onStartNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: FloatingActionButton,
    directTargetChild: View,
    target: View,
    axes: Int,
    type: Int
  ): Boolean = (axes == ViewCompat.SCROLL_AXIS_VERTICAL)
}
