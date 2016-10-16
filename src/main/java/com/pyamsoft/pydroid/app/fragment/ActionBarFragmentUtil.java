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

package com.pyamsoft.pydroid.app.fragment;

import android.app.Activity;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

final class ActionBarFragmentUtil {

  private ActionBarFragmentUtil() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @Nullable private static ActionBar getActionBar(@NonNull Activity activity) {
    if (activity instanceof AppCompatActivity) {
      final AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
      return appCompatActivity.getSupportActionBar();
    } else {
      throw new ClassCastException("Activity not instance of AppCompatActivity");
    }
  }

  static void setActionBarUpEnabled(@NonNull Activity activity, boolean up) {
    final ActionBar bar = getActionBar(activity);
    if (bar != null) {
      bar.setHomeButtonEnabled(up);
      bar.setDisplayHomeAsUpEnabled(up);
    }
  }

  static void setActionBarTitle(@NonNull Activity activity, @NonNull CharSequence title) {
    final ActionBar bar = getActionBar(activity);
    if (bar != null) {
      bar.setTitle(title);
    }
  }

  static void setActionBarTitle(@NonNull Activity activity, @StringRes int title) {
    final ActionBar bar = getActionBar(activity);
    if (bar != null) {
      bar.setTitle(title);
    }
  }
}
