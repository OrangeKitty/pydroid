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

package com.pyamsoft.pydroid.loader;

import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.pyamsoft.pydroid.function.ActionSingle;
import com.pyamsoft.pydroid.loader.loaded.Loaded;
import com.pyamsoft.pydroid.loader.targets.Target;

public abstract class GenericLoader<R extends GenericLoader<?, ?>, T> {

  @Nullable protected ActionSingle<Target<T>> startAction;
  @Nullable protected ActionSingle<Target<T>> errorAction;
  @Nullable protected ActionSingle<Target<T>> completeAction;
  @ColorRes protected int tint;

  protected GenericLoader() {
  }

  @CheckResult @NonNull public abstract R tint(@ColorRes int color);

  @CheckResult @NonNull
  public abstract R setStartAction(@NonNull ActionSingle<Target<T>> startAction);

  @CheckResult @NonNull
  public abstract R setErrorAction(@NonNull ActionSingle<Target<T>> errorAction);

  @CheckResult @NonNull
  public abstract R setCompleteAction(@NonNull ActionSingle<Target<T>> completeAction);

  @CheckResult @NonNull public abstract Loaded into(@NonNull ImageView imageView);

  @CheckResult @NonNull public abstract Loaded into(@NonNull Target<T> target);
}
