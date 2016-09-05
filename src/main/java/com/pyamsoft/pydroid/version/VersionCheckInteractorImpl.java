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

package com.pyamsoft.pydroid.version;

import android.content.Context;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import rx.Observable;

class VersionCheckInteractorImpl implements VersionCheckInteractor {

  @NonNull private final Context appContext;
  @NonNull private final LicenseCheckService licenseCheckService;

  @Inject VersionCheckInteractorImpl(@NonNull Context context,
      @NonNull LicenseCheckService licenseCheckService) {
    this.appContext = context.getApplicationContext();
    this.licenseCheckService = licenseCheckService;
  }

  @NonNull @Override public Observable<VersionCheckResponse> checkVersion() {
    return licenseCheckService.checkVersion(appContext.getPackageName());
  }
}
