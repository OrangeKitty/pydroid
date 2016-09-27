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

package com.pyamsoft.pydroid.about;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.tool.AsyncCallbackTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

class AboutLibrariesInteractorImpl implements AboutLibrariesInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final Map<String, String> cachedLicenses;
  @SuppressWarnings("WeakerAccess") @NonNull private final LicenseProvider licenseProvider;
  @SuppressWarnings("WeakerAccess") @NonNull private final AssetManager assetManager;

  AboutLibrariesInteractorImpl(@NonNull Context context) {
    assetManager = context.getAssets();
    licenseProvider = Licenses.licenses(context);
    cachedLicenses = new HashMap<>();
  }

  @Override public void clearCache() {
    cachedLicenses.clear();
  }

  @NonNull @Override public AsyncTask<AboutLicenseItem, Void, String> loadLicenseText(
      @NonNull AboutLicenseItem license, @NonNull ActionSingle<String> onLoaded) {
    return new LicenseLoadTask(cachedLicenses, license, licenseProvider, assetManager, onLoaded);
  }

  @SuppressWarnings("WeakerAccess") static class LicenseLoadTask
      extends AsyncCallbackTask<AboutLicenseItem, String> {

    @NonNull private final Map<String, String> cachedLicenses;
    @NonNull private final AboutLicenseItem license;
    @NonNull private final LicenseProvider licenseProvider;
    @NonNull private final AssetManager assetManager;

    LicenseLoadTask(@NonNull Map<String, String> cachedLicenses, @NonNull AboutLicenseItem license,
        @NonNull LicenseProvider licenseProvider, @NonNull AssetManager assetManager,
        @NonNull ActionSingle<String> onLoaded) {
      super(onLoaded);
      this.cachedLicenses = cachedLicenses;
      this.license = license;
      this.licenseProvider = licenseProvider;
      this.assetManager = assetManager;
    }

    @Override protected String doInBackground(AboutLicenseItem... params) {
      if (cachedLicenses.containsKey(license.getName())) {
        Timber.d("Fetch from cache");
        return cachedLicenses.get(license.getName());
      } else {
        Timber.d("Load from asset location");
        final String licenseText = loadNewLicense(license.getName(), license.getLicenseLocation());
        Timber.d("Put into cache");
        cachedLicenses.put(license.getName(), licenseText);
        return licenseText;
      }
    }

    @SuppressWarnings("WeakerAccess") @VisibleForTesting @NonNull @CheckResult @WorkerThread
    String loadNewLicense(@NonNull String licenseName, @NonNull String licenseLocation) {
      if (licenseLocation.isEmpty()) {
        Timber.w("Empty license passed");
        return "";
      }

      if (licenseName.equals(Licenses.Names.GOOGLE_PLAY)) {
        Timber.d("License is Google Play services");
        final String googleOpenSourceLicenses = licenseProvider.provideGoogleOpenSourceLicenses();
        final String result =
            googleOpenSourceLicenses == null ? "Unable to load Google Play Open Source Licenses"
                : googleOpenSourceLicenses;
        Timber.i("Finished loading Google Play services license");
        return result;
      }

      String licenseText;
      try (InputStream fileInputStream = assetManager.open(licenseLocation)) {

        // Standard Charsets is only KitKat, add this extra check to support Home Button
        final InputStreamReader inputStreamReader;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        } else {
          inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
        }

        try (BufferedReader br = new BufferedReader(inputStreamReader)) {
          final StringBuilder text = new StringBuilder();
          String line = br.readLine();
          while (line != null) {
            text.append(line).append('\n');
            line = br.readLine();
          }
          licenseText = text.toString();
        }
      } catch (IOException e) {
        e.printStackTrace();
        licenseText = "Could not load license text";
      }

      return licenseText;
    }
  }
}
