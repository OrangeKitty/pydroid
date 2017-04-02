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

package com.pyamsoft.pydroid.version;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import retrofit2.HttpException;
import timber.log.Timber;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class VersionCheckPresenter
    extends SchedulerPresenter {

  @NonNull private final VersionCheckInteractor interactor;
  @NonNull private Disposable disposable = Disposables.empty();

  VersionCheckPresenter(@NonNull VersionCheckInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = Checker.checkNonNull(interactor);
  }

  @Override protected void onStop() {
    disposable = DisposableHelper.dispose(disposable);
  }

  public void checkForUpdates(@NonNull String packageName, int currentVersionCode,
      @NonNull UpdateCheckCallback callback) {
    disposable = DisposableHelper.dispose(disposable);
    disposable = interactor.checkVersion(packageName)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(versionCheckResponse -> {
          Timber.i("Update check finished");
          Timber.i("Current version: %d", currentVersionCode);
          Timber.i("Latest version: %d", versionCheckResponse.currentVersion());
          callback.onVersionCheckFinished();
          if (currentVersionCode < versionCheckResponse.currentVersion()) {
            callback.onUpdatedVersionFound(currentVersionCode,
                versionCheckResponse.currentVersion());
          }
        }, throwable -> {
          if (throwable instanceof HttpException) {
            Timber.w(throwable, "onError: Not successful CODE: %d",
                ((HttpException) throwable).code());
          } else {
            Timber.e(throwable, "onError");
          }
        });
  }

  public interface UpdateCheckCallback {

    void onVersionCheckFinished();

    void onUpdatedVersionFound(int oldVersionCode, int updatedVersionCode);
  }
}
