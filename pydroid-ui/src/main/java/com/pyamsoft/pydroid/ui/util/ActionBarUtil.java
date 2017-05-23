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

package com.pyamsoft.pydroid.ui.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.pyamsoft.pydroid.helper.Checker;

public class ActionBarUtil {

  private ActionBarUtil() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @NonNull public static ActionBar getActionBar(@NonNull Activity activity) {
    activity = Checker.Companion.checkNonNull(activity);

    if (activity instanceof AppCompatActivity) {
      AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
      return Checker.Companion.checkNonNull(appCompatActivity.getSupportActionBar());
    } else {
      throw new ClassCastException("Activity not instance of AppCompatActivity");
    }
  }

  public static void setActionBarUpEnabled(@NonNull Activity activity, boolean up) {
    setActionBarUpEnabled(activity, up, null);
  }

  public static void setActionBarUpEnabled(@NonNull Activity activity, boolean up,
      @DrawableRes int icon) {
    activity = Checker.Companion.checkNonNull(activity);

    final Drawable d;
    if (icon != 0) {
      d = ContextCompat.getDrawable(activity, icon);
    } else {
      d = null;
    }

    setActionBarUpEnabled(activity, up, d);
  }

  public static void setActionBarUpEnabled(@NonNull Activity activity, boolean up,
      @Nullable Drawable icon) {
    activity = Checker.Companion.checkNonNull(activity);

    ActionBar bar = getActionBar(activity);
    bar.setHomeButtonEnabled(up);
    bar.setDisplayHomeAsUpEnabled(up);
    bar.setHomeAsUpIndicator(icon);
  }

  public static void setActionBarTitle(@NonNull Activity activity, @NonNull CharSequence title) {
    getActionBar(activity).setTitle(title);
  }

  public static void setActionBarTitle(@NonNull Activity activity, @StringRes int title) {
    getActionBar(activity).setTitle(title);
  }
}
