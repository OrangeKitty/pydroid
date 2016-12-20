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
 *
 */

package com.pyamsoft.pydroid.presenter;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.Destroyable;

public interface Presenter<I> extends Destroyable {

  @CheckResult boolean isBound();

  void bindView(I view);

  void unbindView();

  @SuppressWarnings("unused") interface BoundView<I> extends ActionSingle<I> {

    @Override void call(@NonNull I view);
  }
}