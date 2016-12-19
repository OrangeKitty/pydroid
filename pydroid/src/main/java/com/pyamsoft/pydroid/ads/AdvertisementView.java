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

package com.pyamsoft.pydroid.ads;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.pyamsoft.pydroid.AdvertisementViewLoaderCallback;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public class AdvertisementView extends FrameLayout implements AdvertisementPresenter.AdView {

  @NonNull private static final String KEY_ADVERTISEMENT = "__key_advertisement_presenter";
  @NonNull private final AdSource offlineAdSource = new OfflineAdSource();
  @SuppressWarnings("WeakerAccess") @Nullable Handler handler;
  @SuppressWarnings("WeakerAccess") @Nullable AdvertisementPresenter presenter;
  private long loadedKey;
  @Nullable private AdSource onlineAdSource;

  public AdvertisementView(Context context) {
    super(context);
    init();
  }

  public AdvertisementView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public AdvertisementView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AdvertisementView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    handler = new Handler(Looper.getMainLooper());
    ViewCompat.setElevation(this, AppUtil.convertToDP(getContext(), 2));
  }

  @SuppressWarnings("WeakerAccess")
  public final void create(@Nullable AdSource adSource, @Nullable Bundle savedInstanceState) {
    loadedKey = PersistentCache.get()
        .load(KEY_ADVERTISEMENT, savedInstanceState, new AdvertisementViewLoaderCallback() {

          @Override public void onPersistentLoaded(@NonNull AdvertisementPresenter persist) {
            presenter = persist;
          }
        });

    // Default to gone
    setVisibility(View.GONE);

    addView(offlineAdSource.create(getContext(), savedInstanceState));

    onlineAdSource = adSource;
    if (onlineAdSource != null) {
      addView(onlineAdSource.create(getContext(), savedInstanceState));
    }
  }

  public final void start() {
    if (presenter == null) {
      throw new IllegalStateException("NULL presenter");
    }
    Timber.d("Start adView");
    presenter.bindView(this);

    offlineAdSource.start();
    if (onlineAdSource != null) {
      onlineAdSource.start();
    }
  }

  public final void stop() {
    if (presenter == null) {
      throw new IllegalStateException("NULL presenter");
    }
    if (handler == null) {
      throw new IllegalStateException("NULL presenter");
    }
    Timber.d("Stop adView");
    presenter.unbindView();
    handler.removeCallbacksAndMessages(null);

    offlineAdSource.stop();
    if (onlineAdSource != null) {
      onlineAdSource.stop();
    }
  }

  public final void destroy(boolean isChangingConfigurations) {
    onHidden();

    if (!isChangingConfigurations) {
      PersistentCache.get().unload(loadedKey);
    }

    removeView(offlineAdSource.destroy(isChangingConfigurations));
    if (onlineAdSource != null) {
      removeView(onlineAdSource.destroy(isChangingConfigurations));
    }
  }

  public final void showAd() {
    if (presenter == null) {
      throw new IllegalStateException("NULL presenter");
    }
    if (presenter.isBound()) {
      presenter.showAd();
    }
  }

  public final void hideAd() {
    if (presenter == null) {
      throw new IllegalStateException("NULL presenter");
    }
    if (presenter.isBound()) {
      presenter.hideAd();
    }
  }

  public final void saveState(@NonNull Bundle outState) {
    PersistentCache.get()
        .saveKey(outState, KEY_ADVERTISEMENT, loadedKey, AdvertisementPresenter.class);
    offlineAdSource.saveState(outState);
    if (onlineAdSource != null) {
      onlineAdSource.saveState(outState);
    }
  }

  @Override public void onShown() {
    Timber.d("Show ad view");
    setVisibility(View.VISIBLE);
    queueAdRefresh();
  }

  @Override public void onHidden() {
    Timber.d("Hide ad view");
    setVisibility(View.GONE);

    offlineAdSource.hideAd();
    if (onlineAdSource != null) {
      onlineAdSource.hideAd();
    }
  }

  @SuppressWarnings("WeakerAccess") void queueAdRefresh() {
    if (handler == null) {
      throw new IllegalStateException("NULL handler");
    }
    if (onlineAdSource != null && NetworkUtil.hasConnection(getContext())) {
      onlineAdSource.showAd();
    } else {
      offlineAdSource.showAd();
    }

    Timber.d("Post new ad in 60 seconds");
    handler.postDelayed(this::queueAdRefresh, 60 * 1000L);
  }
}
