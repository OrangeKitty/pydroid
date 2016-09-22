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

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import com.pyamsoft.pydroid.social.SocialMediaPresenterLoader;
import javax.inject.Inject;

public abstract class SocialMediaLoaderCallback
    implements PersistLoader.Callback<SocialMediaPresenter> {

  @NonNull private final Context context;
  @Inject SocialMediaPresenterLoader loader;

  protected SocialMediaLoaderCallback(@NonNull Context context) {
    this.context = context.getApplicationContext();
  }

  @NonNull @Override public PersistLoader<SocialMediaPresenter> createLoader() {
    PYDroidApplication.get(context.getApplicationContext())
        .provideComponent()
        .plusSocialMediaComponent()
        .inject(this);
    return loader;
  }
}