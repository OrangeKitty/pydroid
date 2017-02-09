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

package com.pyamsoft.pydroid.ui;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.PYDroidModule;
import com.pyamsoft.pydroid.ui.ads.AdvertisementComponent;
import com.pyamsoft.pydroid.ui.donate.DonateComponent;
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class PYDroidComponent {

  @NonNull private final DonateComponent donateComponent;
  @NonNull private final VersionCheckComponent versionCheckComponent;
  @NonNull private final AdvertisementComponent advertisementComponent;

  private PYDroidComponent(@NonNull PYDroidModule module) {
    donateComponent = new DonateComponent(module);
    versionCheckComponent = new VersionCheckComponent();
    advertisementComponent = new AdvertisementComponent(module);
  }

  @CheckResult @NonNull static PYDroidComponent withModule(@NonNull PYDroidModule module) {
    return new PYDroidComponent(module);
  }

  @CheckResult @NonNull public DonateComponent provideDonateComponent() {
    return donateComponent;
  }

  @CheckResult @NonNull public VersionCheckComponent provideVersionCheckComponent() {
    return versionCheckComponent;
  }

  @CheckResult @NonNull public AdvertisementComponent provideAdvertisementComponent() {
    return advertisementComponent;
  }
}
