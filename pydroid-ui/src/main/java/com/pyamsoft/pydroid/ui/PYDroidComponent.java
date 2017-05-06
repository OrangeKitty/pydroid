/*
 * Copyright 2017 Peter Kenji Yamanaka
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
import com.pyamsoft.pydroid.about.AboutLibrariesModule;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.rating.RatingModule;
import com.pyamsoft.pydroid.ui.about.AboutLibrariesComponent;
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent;
import com.pyamsoft.pydroid.ui.rating.RatingComponent;
import com.pyamsoft.pydroid.ui.social.SocialComponent;
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent;
import com.pyamsoft.pydroid.version.VersionCheckModule;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class PYDroidComponent {

  @NonNull private final VersionCheckComponent versionCheckComponent;
  @NonNull private final AboutLibrariesComponent aboutLibrariesComponent;
  @NonNull private final AppComponent appComponent;
  @NonNull private final RatingComponent ratingComponent;
  @NonNull private final SocialComponent socialComponent;

  private PYDroidComponent(@NonNull PYDroidModule module) {
    module = Checker.checkNonNull(module);
    VersionCheckModule versionCheckModule = new VersionCheckModule(module);
    AboutLibrariesModule aboutLibrariesModule = new AboutLibrariesModule(module);
    versionCheckComponent = new VersionCheckComponent(versionCheckModule);
    aboutLibrariesComponent = new AboutLibrariesComponent(aboutLibrariesModule);
    appComponent = new AppComponent(versionCheckModule);
    ratingComponent = new RatingComponent(new RatingModule(module));
    socialComponent = new SocialComponent(module.provideContext());
  }

  @CheckResult @NonNull static PYDroidComponent withModule(@NonNull PYDroidModule module) {
    return new PYDroidComponent(module);
  }

  @CheckResult @NonNull public VersionCheckComponent plusVersionCheckComponent() {
    return versionCheckComponent;
  }

  @CheckResult @NonNull public AboutLibrariesComponent plusAboutLibrariesComponent() {
    return aboutLibrariesComponent;
  }

  @CheckResult @NonNull public AppComponent plusAppComponent() {
    return appComponent;
  }

  @CheckResult @NonNull public RatingComponent plusRatingComponent() {
    return ratingComponent;
  }

  @CheckResult @NonNull public SocialComponent plusSocialComponent() {
    return socialComponent;
  }
}
