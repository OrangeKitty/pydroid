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

package com.pyamsoft.pydroid.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public final class AsyncDrawable {

  @NonNull private final Context appContext;

  private AsyncDrawable(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
  }

  @CheckResult @NonNull public static AsyncDrawable with(@NonNull Context context) {
    return new AsyncDrawable(context.getApplicationContext());
  }

  @CheckResult @NonNull public final Loader load(@DrawableRes int drawableRes) {
    return new Loader(appContext, drawableRes);
  }

  public static final class Loader {

    @NonNull final Context appContext;
    @DrawableRes final int resource;
    Scheduler subscribeScheduler;
    Scheduler observeScheduler;
    @ColorRes int tint;

    Loader(@NonNull Context context, int resource) {
      this.appContext = context.getApplicationContext();
      this.resource = resource;
      subscribeScheduler = Schedulers.computation();
      observeScheduler = AndroidSchedulers.mainThread();
      tint = 0;
    }

    @CheckResult @NonNull public final Loader tint(@ColorRes int color) {
      this.tint = color;
      return this;
    }

    @CheckResult @NonNull public final Loader subscribeOn(@NonNull Scheduler scheduler) {
      this.subscribeScheduler = scheduler;
      return this;
    }

    @CheckResult @NonNull public final Loader observeOn(@NonNull Scheduler scheduler) {
      this.observeScheduler = scheduler;
      return this;
    }

    @CheckResult @NonNull public final Subscription into(@NonNull ImageView imageView) {
      return Observable.defer(() -> {
        Drawable loaded = AppCompatResources.getDrawable(appContext, resource);
        if (loaded == null) {
          throw new NullPointerException("Could not load drawable for resource: " + resource);
        }

        if (tint != 0) {
          loaded = DrawableUtil.tintDrawableFromRes(appContext, loaded, tint);
        }
        return Observable.just(loaded);
      })
          .subscribeOn(subscribeScheduler)
          .observeOn(observeScheduler)
          .subscribe(imageView::setImageDrawable, throwable -> {
            Timber.e(throwable, "Error loading Drawable into ImageView");
          });
    }
  }

  /**
   * A map that makes it convenient to load AsyncDrawables
   */
  public static final class Mapper {

    @NonNull private final HashMap<String, Subscription> map;

    public Mapper() {
      this.map = new HashMap<>();
    }

    /**
     * Puts a new element into the map
     *
     * If an old element exists, its task is cancelled first before adding the new one
     */
    public final void put(@NonNull String tag, @NonNull Subscription subscription) {
      if (map.containsKey(tag)) {
        final Subscription old = map.get(tag);
        cancelSubscription(tag, old);
      }

      Timber.d("Insert new subscription for tag: %s", tag);
      map.put(tag, subscription);
    }

    /**
     * Clear all elements in the map
     *
     * If the elements have not been cancelled yet, cancel them before removing them
     */
    public final void clear() {
      for (final Map.Entry<String, Subscription> entry : map.entrySet()) {
        cancelSubscription(entry.getKey(), entry.getValue());
      }

      Timber.d("Clear AsyncDrawableMap");
      map.clear();
    }

    /**
     * Cancels a task
     */
    private void cancelSubscription(@NonNull String tag, @Nullable Subscription subscription) {
      if (subscription != null) {
        if (!subscription.isUnsubscribed()) {
          Timber.d("Unsubscribe for tag: %s", tag);
          subscription.unsubscribe();
        }
      }
    }
  }
}
