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
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class AboutLibrariesInteractorImpl implements AboutLibrariesInteractor {

  @NonNull final Context appContext;

  @Inject AboutLibrariesInteractorImpl(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
  }

  @NonNull @VisibleForTesting @CheckResult String getLicenseFileName(@NonNull Licenses license) {
    final String fileLocation;
    switch (license) {
      case FIREBASE:
        fileLocation = "licenses/firebase.txt";
        break;
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
      case RXANDROID:
        fileLocation = "licenses/apache2.txt";
        break;
      default:
        throw new RuntimeException("Invalid license type: " + license.name());
    }
    return fileLocation;
  }

  @NonNull @Override public Observable<String> loadLicenseText(@NonNull Licenses licenses) {
    return Observable.defer(() -> {
      Timber.d("Load license for: %s", licenses.name());
      if (licenses == Licenses.EMPTY) {
        Timber.w("Empty license passed");
        return Observable.just("");
      }

      if (licenses == Licenses.GOOGLE_PLAY_SERVICES) {
        Timber.d("License is Google Play services");
        return Observable.just(
            GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(appContext));
      }

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

      return Observable.just(licenseText);
    });
  }
}
