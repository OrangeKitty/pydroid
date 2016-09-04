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

package com.pyamsoft.pydroid.app.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.inject.LicenseCheckPresenterLoader;
import com.pyamsoft.pydroid.licensecheck.LicenseCheckPresenter;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public abstract class LicenseCheckActivity extends AdvertisementActivity
    implements LicenseCheckPresenter.View {

  @NonNull private static final String KEY_PRESENTER = "key_license_check_presenter";
  @NonNull private static final String KEY_HAS_CHECKED_LICENSE = "key_has_already_checked_license";
  LicenseCheckPresenter presenter;
  boolean licenseChecked;
  private long loadedKey;

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      licenseChecked = savedInstanceState.getBoolean(KEY_HAS_CHECKED_LICENSE, false);
    }

    loadedKey = PersistentCache.load(KEY_PRESENTER, savedInstanceState,
        new PersistLoader.Callback<LicenseCheckPresenter>() {
          @NonNull @Override public PersistLoader<LicenseCheckPresenter> createLoader() {
            licenseChecked = false;
            return new LicenseCheckPresenterLoader(getApplicationContext(), isAdDebugMode(),
                provideProjectName());
          }

          @Override public void onPersistentLoaded(@NonNull LicenseCheckPresenter persist) {
            presenter = persist;
          }
        });
  }

  @CallSuper @Override protected void onDestroy() {
    super.onDestroy();
    if (!isChangingConfigurations()) {
      PersistentCache.unload(loadedKey);
    }
  }

  @CallSuper @Override protected void onSaveInstanceState(Bundle outState) {
    PersistentCache.saveKey(KEY_PRESENTER, outState, loadedKey);
    outState.putBoolean(KEY_HAS_CHECKED_LICENSE, licenseChecked);
    super.onSaveInstanceState(outState);
  }

  @CallSuper @Override protected void onStart() {
    super.onStart();
    presenter.bindView(this);
    if (!licenseChecked) {
      presenter.checkForUpdates(getCurrentApplicationVersion());
    }
  }

  @CallSuper @Override protected void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onLicenseCheckFinished() {
    Timber.d("License check finished, mark");
    licenseChecked = true;
  }

  @Override public void onUpdatedVersionFound(int updatedVersionCode) {
    Timber.d("Updated version found. %d => %d", getCurrentApplicationVersion(), updatedVersionCode);
    // TODO
  }

  @CheckResult @NonNull protected abstract String provideProjectName();

  @CheckResult protected abstract int getCurrentApplicationVersion();
}
