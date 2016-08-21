/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.base.presenter;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import timber.log.Timber;

public abstract class Presenter<I> {

  @Nullable private I view;

  @CheckResult @NonNull protected final I getView() {
    if (this.view == null) {
      throw new IllegalStateException("No view is bound to this presenter");
    }

    return view;
  }

  public final void bindView(@NonNull I view) {
    bindView(view, true);
  }

  public final void unbindView() {
    unbindView(true);
  }

  public final void bindView(@NonNull I view, boolean runHook) {
    if (this.view != null) {
      throw new IllegalStateException("Must call unbindView before calling bindView again");
    }
    this.view = view;

    if (runHook) {
      Timber.d("Run onBind hook");
      onBind(this.view);
    }
  }

  public final void unbindView(boolean runHook) {
    if (this.view == null) {
      throw new IllegalStateException("Must call bindView before calling unbindView again.");
    }

    if (runHook) {
      Timber.d("Run onUnbind hook");
      onUnbind(this.view);
    }

    this.view = null;
  }

  public final void start() {
    if (this.view == null) {
      throw new IllegalStateException("Cannot start without a bound View");
    }

    onStart(this.view);
  }

  public final void stop() {
    if (this.view == null) {
      throw new IllegalStateException("Cannot stop without a bound View");
    }

    onStop(this.view);
  }

  public final void resume() {
    if (this.view == null) {
      throw new IllegalStateException("Cannot resume without a bound View");
    }

    onResume(this.view);
  }

  public final void pause() {
    if (this.view == null) {
      throw new IllegalStateException("Cannot pause without a bound View");
    }

    onPause(this.view);
  }

  protected void onBind(@NonNull I view) {

  }

  protected void onUnbind(@NonNull I view) {

  }

  protected void onStart(@NonNull I view) {

  }

  protected void onStop(@NonNull I view) {

  }

  protected void onResume(@NonNull I view) {

  }

  protected void onPause(@NonNull I view) {

  }
}
