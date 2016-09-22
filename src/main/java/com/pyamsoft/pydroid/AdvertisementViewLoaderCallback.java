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
import com.pyamsoft.pydroid.ads.AdvertisementPresenter;
import com.pyamsoft.pydroid.ads.AdvertisementPresenterLoader;
import com.pyamsoft.pydroid.app.PersistLoader;
import javax.inject.Inject;

public abstract class AdvertisementViewLoaderCallback
    implements PersistLoader.Callback<AdvertisementPresenter> {

  @NonNull private final Context context;
  @SuppressWarnings("WeakerAccess") @Inject AdvertisementPresenterLoader loader;

  protected AdvertisementViewLoaderCallback(@NonNull Context context) {
    this.context = context.getApplicationContext();
  }

  @NonNull @Override public PersistLoader<AdvertisementPresenter> createLoader() {
    PYDroidApplication.get(context).provideComponent().plusAdvertisementComponent().inject(this);
    return loader;
  }
}