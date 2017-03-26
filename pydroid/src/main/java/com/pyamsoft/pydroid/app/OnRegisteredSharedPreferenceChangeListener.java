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

package com.pyamsoft.pydroid.app;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.helper.Checker;

public abstract class OnRegisteredSharedPreferenceChangeListener
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  private boolean isRegistered = false;

  public void register(@NonNull SharedPreferences preferences) {
    if (!isRegistered) {
      Checker.checkNonNull(preferences).registerOnSharedPreferenceChangeListener(this);
      isRegistered = true;
    }
  }

  @SuppressWarnings("unused") public void unregister(@NonNull SharedPreferences preferences) {
    if (isRegistered) {
      Checker.checkNonNull(preferences).unregisterOnSharedPreferenceChangeListener(this);
      isRegistered = false;
    }
  }
}
