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

@file:JvmName("DisposablesKt")

package com.pyamsoft.pydroid.helper

import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables

/**
 * Extension function for Kotlin
 *
 * If the Disposable is non-null, disposes of it and then returns the new disposable
 *
 * The new disposable is by default, the empty disposable, so that all memory references
 * held by the disposable are marked for GC
 */
fun Disposable?.dispose(defaultDisposable: Disposable = Disposables.empty()): Disposable {
  if (this == null) {
    return defaultDisposable
  }

  if (!isDisposed) {
    dispose()
  }

  return defaultDisposable
}
