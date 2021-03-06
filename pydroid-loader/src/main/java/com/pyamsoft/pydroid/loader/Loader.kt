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

package com.pyamsoft.pydroid.loader

import android.support.annotation.CheckResult
import android.support.annotation.ColorRes
import android.widget.ImageView
import com.pyamsoft.pydroid.loader.loaded.Loaded
import com.pyamsoft.pydroid.loader.targets.Target

interface Loader<T : Any> {

  @CheckResult
  fun tint(@ColorRes tint: Int): Loader<T>

  @CheckResult
  fun withStartAction(action: () -> Unit): Loader<T>

  @CheckResult
  fun withErrorAction(action: (Throwable) -> Unit): Loader<T>

  @CheckResult
  fun withCompleteAction(action: (T) -> Unit): Loader<T>

  @CheckResult
  fun into(imageView: ImageView): Loaded

  @CheckResult
  fun into(target: Target<T>): Loaded
}
