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

import android.app.Application;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import com.pyamsoft.pydroid.PYDroidApplication;
import com.squareup.leakcanary.RefWatcher;

interface ActionBarProvider {

  @CheckResult @Nullable ActionBar getActionBar();

  void setActionBarUpEnabled(boolean up);

  final class Util {

    private Util() {
      throw new RuntimeException("No instances");
    }

    @CheckResult @NonNull static RefWatcher getRefWatcher(@NonNull Fragment fragment) {
      final Application application = fragment.getActivity().getApplication();
      if (application instanceof PYDroidApplication) {
        final PYDroidApplication pyDroidApplication = (PYDroidApplication) application;
        return pyDroidApplication.getRefWatcher();
      } else {
        throw new ClassCastException("Application is not PYDroidApplication");
      }
    }
  }
}
