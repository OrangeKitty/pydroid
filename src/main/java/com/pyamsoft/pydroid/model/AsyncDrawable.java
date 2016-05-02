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

package com.pyamsoft.pydroid.model;

import android.content.Context;

public final class AsyncDrawable {

  private final Context context;
  private final int icon;

  public AsyncDrawable(Context context, int icon) {
    this.context = context;
    this.icon = icon;
  }

  public final Context context() {
    return context;
  }

  public final int icon() {
    return icon;
  }
}
