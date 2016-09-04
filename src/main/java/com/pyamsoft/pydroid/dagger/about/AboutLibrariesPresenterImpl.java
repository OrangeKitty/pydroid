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

package com.pyamsoft.pydroid.dagger.about;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.app.about.AboutLibrariesPresenter;
import com.pyamsoft.pydroid.app.about.Licenses;
import com.pyamsoft.pydroid.bus.AboutItemBus;
import com.pyamsoft.pydroid.dagger.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class AboutLibrariesPresenterImpl extends SchedulerPresenter<AboutLibrariesPresenter.View>
    implements AboutLibrariesPresenter {

  @NonNull private final AboutLibrariesInteractor interactor;
  @NonNull private Subscription licenseSubscription = Subscriptions.empty();
  @NonNull private Subscription loadLicenseBus = Subscriptions.empty();

  @Inject AboutLibrariesPresenterImpl(@NonNull AboutLibrariesInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onBind(@NonNull View view) {
    super.onBind(view);
    registerOnLicenseBus(view);
  }

  private void registerOnLicenseBus(@NonNull View view) {
    unregisterLicenseBus();
    loadLicenseBus = AboutItemBus.get()
        .register()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(licenseLoadEvent -> {
          loadLicenseText(view, licenseLoadEvent.position(), licenseLoadEvent.licenses());
        }, throwable -> {
          Timber.e(throwable, "onError registerOnLicenseBus");
        });
  }

  private void unregisterLicenseBus() {
    if (!loadLicenseBus.isUnsubscribed()) {
      loadLicenseBus.unsubscribe();
    }
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unregisterLicenseBus();
    unsubLoadLicense();
  }

  void loadLicenseText(@NonNull View view, int position, @NonNull Licenses licenses) {
    unsubLoadLicense();
    if (licenses == Licenses.EMPTY) {
      getView().onLicenseTextLoaded(position, "");
    } else {
      licenseSubscription = interactor.loadLicenseText(licenses)
          .subscribeOn(getSubscribeScheduler())
          .observeOn(getObserveScheduler())
          .subscribe(license -> view.onLicenseTextLoaded(position, license), throwable -> {
            Timber.e(throwable, "Failed to load license");
            view.onLicenseTextLoaded(position, "Failed to load license");
          }, this::unsubLoadLicense);
    }
  }

  void unsubLoadLicense() {
    if (!licenseSubscription.isUnsubscribed()) {
      licenseSubscription.unsubscribe();
    }
  }
}
