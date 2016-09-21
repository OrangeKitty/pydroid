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
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Provider;
import rx.Scheduler;

@Module public class AboutLibrariesModule {

  @ActivityScope @Provides AboutLibrariesPresenter provideAboutLibrariesPresenter(
      @NonNull AboutLibrariesInteractor interactor, @Named("main") Scheduler mainScheduler,
      @Named("io") Scheduler ioScheduler) {
    return new AboutLibrariesPresenterImpl(interactor, mainScheduler, ioScheduler);
  }

  @ActivityScope @Provides AboutLibrariesInteractor provideAboutLibrariesInteractor(
      @NonNull Context context) {
    return new AboutLibrariesInteractorImpl(context);
  }

  @ActivityScope @Provides AboutLibrariesPresenterLoader provideAboutLibrariesPresenterLoader(
      @NonNull Context context, @NonNull Provider<AboutLibrariesPresenter> presenterProvider) {
    return new AboutLibrariesPresenterLoader(context, presenterProvider);
  }
}
