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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import java.util.Locale;

public class VersionUpgradeDialog extends DialogFragment implements SocialMediaPresenter.View {

  @NonNull public static final String TAG = "VersionUpgradeDialog";
  @NonNull private static final String KEY_NAME = "key_name";
  @NonNull private static final String KEY_LATEST_VERSION = "key_latest_version";
  @NonNull private static final String KEY_CURRENT_VERSION = "key_current_version";
  @NonNull private static final String KEY_VERSION_PRESENTER = "key_version_presenter";
  @SuppressWarnings("WeakerAccess") SocialMediaPresenter presenter;
  private int latestVersion;
  private int currentVersion;
  private String applicationName;
  private long loadedKey;

  @CheckResult @NonNull
  public static VersionUpgradeDialog newInstance(@NonNull String applicationName,
      int currentVersion, int latestVersion) {
    final Bundle args = new Bundle();
    final VersionUpgradeDialog fragment = new VersionUpgradeDialog();
    args.putString(KEY_NAME, applicationName);
    args.putInt(KEY_CURRENT_VERSION, currentVersion);
    args.putInt(KEY_LATEST_VERSION, latestVersion);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    latestVersion = getArguments().getInt(KEY_LATEST_VERSION, 0);
    if (latestVersion == 0) {
      throw new RuntimeException("Could not find latest version");
    }

    currentVersion = getArguments().getInt(KEY_CURRENT_VERSION, 0);
    if (currentVersion == 0) {
      throw new RuntimeException("Could not find current version");
    }

    applicationName = getArguments().getString(KEY_NAME, null);
    if (applicationName == null) {
      throw new RuntimeException("Coult not find application name");
    }

    loadedKey = PersistentCache.get()
        .load(KEY_VERSION_PRESENTER, savedInstanceState,
            new SocialMediaLoaderCallback(getContext()) {

              @Override public void onPersistentLoaded(@NonNull SocialMediaPresenter persist) {
                presenter = persist;
              }
            });
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    final String message = String.format(Locale.getDefault(),
        "A new version of %s is available!\nCurrent version: %d\nLatest verson: %d",
        applicationName, currentVersion, latestVersion);
    return new AlertDialog.Builder(getActivity()).setTitle("New version available")
        .setMessage(message)
        .setPositiveButton("Update", (dialogInterface, i) -> {
          presenter.clickAppPage(getContext().getPackageName());
          dismiss();
        })
        .setNegativeButton("Later", (dialogInterface, i) -> dismiss())
        .create();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_VERSION_PRESENTER, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @Override public void onSocialMediaClicked(@NonNull String link) {
    NetworkUtil.newLink(getContext().getApplicationContext(), link);
  }
}
