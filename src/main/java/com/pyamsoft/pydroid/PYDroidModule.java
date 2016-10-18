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

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.about.AboutLibrariesModule;
import com.pyamsoft.pydroid.ads.AdvertisementModule;
import com.pyamsoft.pydroid.social.SocialMediaModule;
import com.pyamsoft.pydroid.support.SupportModule;
import com.pyamsoft.pydroid.version.ApiModule;
import com.pyamsoft.pydroid.version.VersionCheckModule;

public class PYDroidModule {

  @NonNull private final Provider provider;

  PYDroidModule(@NonNull Context context) {
    provider = new Provider(context);
  }

  // Create a new one every time
  @CheckResult @NonNull final AboutLibrariesModule provideAboutLibrariesModule() {
    return new AboutLibrariesModule(provider);
  }

  // Create a new one every time
  @CheckResult @NonNull final SupportModule provideSupportModule(@NonNull Activity activity) {
    return new SupportModule(provider, activity);
  }

  // Create a new one every time
  @CheckResult @NonNull final SocialMediaModule provideSocialMediaModule() {
    return new SocialMediaModule(provider);
  }

  // Create a new one every time
  @CheckResult @NonNull final VersionCheckModule provideVersionCheckModule() {
    return new VersionCheckModule(provider, new ApiModule());
  }

  // Create a new one every time
  //
  // NOTE: Makes a new SocialMediaModule
  @CheckResult @NonNull final AdvertisementModule provideAdvertisementModule() {
    return new AdvertisementModule(provider, provideSocialMediaModule());
  }

  public static class Provider {

    // Singleton
    @NonNull private final Context appContext;

    Provider(final @NonNull Context context) {
      appContext = context.getApplicationContext();
    }

    // Singleton
    @CheckResult @NonNull public final Context provideContext() {
      return appContext;
    }
  }
}
