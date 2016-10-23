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

package com.pyamsoft.pydroid.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.content.res.AppCompatResources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;
import timber.log.Timber;

public final class AppUtil {

  private AppUtil() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @NonNull
  public static Intent getApplicationInfoIntent(final @NonNull String packageName) {
    final Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    i.addCategory(Intent.CATEGORY_DEFAULT);
    i.setData(Uri.fromParts("package", packageName, null));
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    return i;
  }

  /**
   * Using the fragment manager to handle transactions, this guarantees that any old
   * versions of the dialog fragment are removed before a new one is added.
   */
  public static void guaranteeSingleDialogFragment(final @NonNull FragmentActivity fragmentActivity,
      final @NonNull DialogFragment dialogFragment, final @NonNull String tag) {
    guaranteeSingleDialogFragment(fragmentActivity.getSupportFragmentManager(), dialogFragment,
        tag);
  }

  /**
   * Using the fragment manager to handle transactions, this guarantees that any old
   * versions of the dialog fragment are removed before a new one is added.
   */
  @SuppressLint("CommitTransaction") public static void guaranteeSingleDialogFragment(
      final @NonNull FragmentManager fragmentManager, final @NonNull DialogFragment dialogFragment,
      final @NonNull String tag) {
    final FragmentTransaction ft = fragmentManager.beginTransaction();
    final Fragment prev = fragmentManager.findFragmentByTag(tag);
    if (prev != null) {
      Timber.d("Remove existing fragment with tag: %s", tag);
      ft.remove(prev);
    }

    Timber.d("Add new fragment with tag: %s", tag);
    dialogFragment.show(ft, tag);
  }

  @CheckResult public static float convertToDP(final @NonNull Context c, final float px) {
    final DisplayMetrics m = c.getResources().getDisplayMetrics();
    final float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, m);
    Timber.d("Convert %f px to %f dp", px, dp);
    return dp;
  }

  public static void setVectorIconForNotification(@NonNull Context context,
      @NonNull RemoteViews remoteViews, @IdRes int id, @DrawableRes int icon) {
    setVectorIconForNotification(context, remoteViews, id, icon, 0);
  }

  public static void setVectorIconForNotification(@NonNull Context context,
      @NonNull RemoteViews remoteViews, @IdRes int id, @DrawableRes int icon, @ColorRes int color) {
    final Context appContext = context.getApplicationContext();
    Drawable d = AppCompatResources.getDrawable(appContext, icon);
    if (d == null) {
      Timber.e("Drawable was null for icon: %d", icon);
      return;
    }

    if (color != 0) {
      d = DrawableUtil.tintDrawableFromRes(appContext, d, color);
    }

    final Bitmap b =
        Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    final Canvas c = new Canvas(b);
    d.setBounds(0, 0, c.getWidth(), c.getHeight());
    d.draw(c);
    remoteViews.setImageViewBitmap(id, b);
  }
}
