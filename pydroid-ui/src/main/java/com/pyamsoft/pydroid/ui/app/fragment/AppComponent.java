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

package com.pyamsoft.pydroid.ui.app.fragment;

import android.support.annotation.RestrictTo;
import com.android.annotations.NonNull;
import com.pyamsoft.pydroid.social.SocialMediaModule;
import com.pyamsoft.pydroid.version.VersionCheckModule;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class AppComponent {

  @NonNull private final SocialMediaModule socialMediaModule;
  @NonNull private final VersionCheckModule versionCheckModule;

  public AppComponent(SocialMediaModule socialMediaModule, VersionCheckModule versionCheckModule) {
    this.socialMediaModule = socialMediaModule;
    this.versionCheckModule = versionCheckModule;
  }

  void inject(@NonNull ActionBarSettingsPreferenceFragment fragment) {
    fragment.presenter = versionCheckModule.getPresenter();
    fragment.socialPresenter = socialMediaModule.getPresenter();
  }
}