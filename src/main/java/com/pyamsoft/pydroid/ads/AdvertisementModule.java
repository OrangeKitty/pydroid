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

package com.pyamsoft.pydroid.ads;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.ActivityScope;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class AdvertisementModule {

  @ActivityScope @Provides AdvertisementPresenter provideAdvertisementPresenter(
      @NonNull AdvertisementInteractor interactor, @NonNull SocialMediaPresenter presenter,
      @Named("main") Scheduler mainScheduler, @Named("computation") Scheduler ioScheduler) {
    return new AdvertisementPresenterImpl(interactor, presenter, mainScheduler, ioScheduler);
  }

  @ActivityScope @Provides AdvertisementInteractor provideAdvertisementInteractor(
      @NonNull Context context) {
    return new AdvertisementInteractorImpl(context);
  }
}
