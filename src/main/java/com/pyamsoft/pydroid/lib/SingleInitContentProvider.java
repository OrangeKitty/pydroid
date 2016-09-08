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

package com.pyamsoft.pydroid.lib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.BuildConfig;
import timber.log.Timber;

/**
 * Application objects are initialized once per process,
 * meaning that there can be multiple instances of the Application class when
 * using a multi-process application, such as one which uses Firebase Crash reporting.
 */
public abstract class SingleInitContentProvider extends ContentProvider {

  private static boolean created;

  static {
    created = false;
  }

  @CheckResult @Override public final boolean onCreate() {
    if (created) {
      // Workaround for https://code.google.com/p/android/issues/detail?id=172655
      Timber.w("SingleInitContentProvider has already been initialized, no-op");
      return false;
    }

    final Context context = getContext();
    if (context == null) {
      throw new NullPointerException("SingleInitContentProvider context is NULL");
    }

    install(context.getApplicationContext());

    // Return false so that we do not actually initialize this fake provider
    Timber.d("onCreate SingleInitContentProvider");
    return false;
  }

  @CallSuper protected void install(@NonNull Context context) {
    if (BuildConfig.DEBUG) {
      Timber.uprootAll();
      Timber.plant(new Timber.DebugTree());
      setStrictMode();
      installInDebugMode(context.getApplicationContext());
    }
  }

  protected void installInDebugMode(@NonNull Context context) {

  }

  private void setStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
        .penaltyLog()
        .penaltyDeath()
        .permitDiskReads()
        .permitDiskWrites()
        .penaltyFlashScreen()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
  }

  @Nullable @Override @CheckResult
  public final Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1,
      String s1) {
    throw new RuntimeException("Not a real ContentProvider: query");
  }

  @CheckResult @Nullable @Override public final String getType(@NonNull Uri uri) {
    throw new RuntimeException("Not a real ContentProvider: getType");
  }

  @CheckResult @Nullable @Override
  public final Uri insert(@NonNull Uri uri, ContentValues contentValues) {
    throw new RuntimeException("Not a real ContentProvider: insert");
  }

  @CheckResult @Override public final int delete(@NonNull Uri uri, String s, String[] strings) {
    throw new RuntimeException("Not a real ContentProvider: delete");
  }

  @Override @CheckResult
  public final int update(@NonNull Uri uri, ContentValues contentValues, String s,
      String[] strings) {
    throw new RuntimeException("Not a real ContentProvider: update");
  }
}
