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

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.pydroid.base.Destroyable;
import com.pyamsoft.pydroid.base.PersistLoader;
import java.util.HashMap;
import java.util.Locale;
import timber.log.Timber;

public final class PersistentCache {

  private PersistentCache() {
    throw new RuntimeException("No instances");
  }

  /**
   * Get a key for a given instance, either stored in the savedInstanceState or generated
   */
  @SuppressWarnings("WeakerAccess") @VisibleForTesting @CheckResult @NonNull
  static String generateKey(@Nullable String key, @Nullable Bundle savedInstanceState) {
    if (savedInstanceState == null || key == null) {
      // Generate a new key
      key = Persist.getInstance().generateKey();
      Timber.d("Generate a new key: %s", key);
    } else {
      // Retrieve the key from the saved instance
      key = savedInstanceState.getString(key, null);
      Timber.d("Retrieve stored key from %s", key);
    }

    if (key == null) {
      throw new NullPointerException("Key is NULL");
    }

    return key;
  }

  /**
   * Saves the generated key into a bundle which will be restored later in the lifecycle
   */
  public static void saveKey(@NonNull Bundle outState, @NonNull String key) {
    outState.putString(key, key);
  }

  /**
   * Load a piece of data that the user wishes to persist over the lifecycle
   *
   * NOTE: If you always pass in NULL for the savedInstanceState, your state will never be cached.
   * You must pass THE savedInstanceState from the Android lifecycle
   */
  @CheckResult @NonNull public static <T> String load(@Nullable String key,
      @Nullable Bundle savedInstanceState, @NonNull PersistLoader.Callback<T> callback) {

    // Attempt to fetch the persistent object from the cache
    key = generateKey(key, savedInstanceState);

    @SuppressWarnings("unchecked") T persist = (T) Persist.getInstance().getCachedObject(key);

    // If the persistent object is NULL it did not exist in the cache
    if (persist == null) {
      // Load a fresh object
      persist = callback.createLoader().loadPersistent();
      Timber.d("Created new persistable: %s [%s]", persist, key);

      // Save the presenter to the cache
      Persist.getInstance().persist(key, persist);
    } else {
      Timber.d("Loaded cached persistable: %s [%s]", persist, key);
    }

    callback.onPersistentLoaded(persist);

    // Return the key to the caller
    return key;
  }

  /**
   * Removes the persistent object from the cache
   *
   * This call is meant to be guarded using checks for Activity.isChangingConfigurations()
   */
  public static void unload(@NonNull String key) {
    Persist.getInstance().remove(key);
  }

  /**
   * Testing function, clears the cache and starts fresh
   */
  @VisibleForTesting static void clear() {
    Persist.getInstance().clear();
  }

  static class Persist {

    @NonNull private static final Persist INSTANCE = new Persist();

    /**
     * KLUDGE Use a more efficient data structure that doesn't do all this unboxing
     */
    @NonNull private final HashMap<String, Object> cache;

    Persist() {
      cache = new HashMap<>();
    }

    @NonNull @CheckResult static Persist getInstance() {
      return INSTANCE;
    }

    @CheckResult @NonNull final String generateKey() {
      return String.format(Locale.getDefault(), "CACHE: %d", System.nanoTime());
    }

    @Nullable @CheckResult final Object getCachedObject(@NonNull String key) {
      return cache.get(key);
    }

    @VisibleForTesting final void clear() {
      Timber.w("Clearing PersistentCache for TESTING");
      cache.clear();
    }

    final void persist(@NonNull String key, @NonNull Object persistable) {
      cache.put(key, persistable);
      if (cache.size() > 100) {
        Timber.w(
            "WARNING: Cache performance will decrease significantly if the size goes over 100 items");
      }
    }

    final void remove(@NonNull String key) {
      final Object persist = cache.get(key);
      if (persist != null) {
        cache.remove(key);
        Timber.d("Remove persistable from cache: %s [%s]", persist, key);
        if (persist instanceof Destroyable) {
          final Destroyable destroyable = (Destroyable) persist;
          destroyable.destroy();
        }
      } else {
        Timber.e("Persisted object was NULL [%s]", key);
        Timber.e("This is usually indicative of a lifecycle error. Check your Fragments!");
      }
    }
  }
}
