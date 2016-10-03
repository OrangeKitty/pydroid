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

package com.pyamsoft.pydroid;

import android.annotation.SuppressLint;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import java.util.Locale;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

public final class TestUtils {

  private TestUtils() {
    throw new RuntimeException("No instances");
  }

  @SuppressLint("PrivateResource") @CheckResult @NonNull
  public static ActivityController<AppCompatActivity> getAppCompatActivityController() {
    // Hacky way to get around the Theme.AppCompat crash
    final ActivityController<AppCompatActivity> activityController =
        Robolectric.buildActivity(AppCompatActivity.class);
    activityController.get().setTheme(android.support.design.R.style.Theme_AppCompat);
    return activityController;
  }

  public static void log(String fmt, Object... args) {
    System.out.printf(Locale.getDefault(), fmt + "\n", args);
  }

  public static void expected(String fmt, Object... args) {
    System.err.printf(Locale.getDefault(), "EXPECTED: " + fmt + "\n", args);
  }
}
