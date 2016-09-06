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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pyamsoft.pydroid.model.Licenses;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class AboutLibrariesInteractorImpl implements AboutLibrariesInteractor {

  @NonNull final Context appContext;
  @NonNull final HashMap<Licenses, String> cachedLicenses;

  @Inject AboutLibrariesInteractorImpl(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
    cachedLicenses = new HashMap<>();
  }

  @Override public void clearCache() {
    cachedLicenses.clear();
  }

  @NonNull @VisibleForTesting @CheckResult String getLicenseFileName(@NonNull Licenses license) {
    final String fileLocation;
    switch (license) {
      case FIREBASE:
        fileLocation = "licenses/firebase.txt";
        break;

      // Apache 2
      case RETROFIT2:
        // Fall through
      case LEAK_CANARY:
        // Fall through
      case FAST_ADAPTER:
        // Fall through
      case DAGGER:
        // Fall through
      case BUTTERKNIFE:
        // Fall through
      case AUTO_VALUE:
        // Fall through
      case ANDROID_IN_APP_BILLING:
        // Fall through
      case ANDROID:
        // Fall through
      case ANDROID_SUPPORT:
        // Fall through
      case PYDROID:
        // Fall through
      case RXJAVA:
        // Fall through
      case SQLBRITE:
        // Fall through
      case SQLDELIGHT:
        // Fall through
      case RXANDROID:
        fileLocation = "licenses/apache2.txt";
        break;

      // MIT
      case ANDROID_PRIORITY_JOBQUEUE:
        fileLocation = "licenses/mit.txt";
        break;
      default:
        throw new RuntimeException("Invalid license type: " + license.name());
    }
    return fileLocation;
  }

  @VisibleForTesting @NonNull @CheckResult Observable<String> loadNewLicense(
      @NonNull Licenses licenses) {
    return Observable.defer(() -> {
      if (licenses == Licenses.EMPTY) {
        Timber.w("Empty license passed");
        return Observable.just("");
      }

      if (licenses == Licenses.GOOGLE_PLAY_SERVICES) {
        Timber.d("License is Google Play services");
        final Observable<String> result = Observable.just(
            GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(appContext));
        Timber.i("Finished loading Google Play services license");
        return result;
      }

      Timber.d("Load license for: %s", licenses.name());
      String licenseText;
      final StringBuilder text = new StringBuilder();
      final String licenseFileName = getLicenseFileName(licenses);
      try (
          final InputStream fileInputStream = appContext.getAssets().open(licenseFileName);
          final BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream))) {
        String line = br.readLine();
        while (line != null) {
          text.append(line).append('\n');
          line = br.readLine();
        }
        licenseText = text.toString();
      } catch (IOException e) {
        e.printStackTrace();
        licenseText = "Could not load license text";
      }

      Timber.i("Finished loading license for: %s", licenses.name());
      return Observable.just(licenseText);
    });
  }

  @NonNull @Override public Observable<String> loadLicenseText(@NonNull Licenses licenses) {
    return Observable.defer(() -> {
      if (cachedLicenses.containsKey(licenses)) {
        return Observable.just(cachedLicenses.get(licenses));
      } else {
        return loadNewLicense(licenses);
      }
    }).map(s -> {
      if (!cachedLicenses.containsKey(licenses)) {
        cachedLicenses.put(licenses, s);
      }

      return s;
    });
  }
}
