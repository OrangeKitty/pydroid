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

package com.pyamsoft.pydroid.ui.sec;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.social.Linker;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class TamperDialog extends DialogFragment {
  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(false);
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setTitle(
        "WARNING: THIS APPLICATION IS NOT OFFICIAL")
        .setMessage(R.string.tamper_msg)
        .setCancelable(false)
        .setPositiveButton("Take Me", (dialog, which) -> {
          Linker.getInstance().clickGooglePlay();
          killApp();
        })
        .setNegativeButton("Close", (dialogInterface, i) -> killApp())
        .create();
  }

  /**
   * Kills the app and clears the data to prevent any malicious services or code from possibly
   * running in the background
   */
  @SuppressWarnings("WeakerAccess") void killApp() {
    dismiss();
    getActivity().finish();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      final ActivityManager activityManager = (ActivityManager) getContext().getApplicationContext()
          .getSystemService(Context.ACTIVITY_SERVICE);
      activityManager.clearApplicationUserData();
    }
  }
}
