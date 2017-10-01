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

package com.pyamsoft.pydroid.ui.widget

import android.content.Context
import android.support.annotation.CallSuper
import android.support.v7.widget.AppCompatCheckedTextView
import android.util.AttributeSet

/**
 * Attempts to fix TextView memory leak

 * https://github.com/square/leakcanary/issues/180
 */
class NoLeakCheckedTextView : AppCompatCheckedTextView {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr)

  @CallSuper override fun onDetachedFromWindow() {
    viewTreeObserver?.removeOnPreDrawListener(this)
    super.onDetachedFromWindow()
  }
}
