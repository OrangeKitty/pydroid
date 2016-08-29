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

package com.pyamsoft.pydroid.tool;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import java.util.List;
import timber.log.Timber;

/**
 * A fragment that retains data,
 */
public final class DataHolderFragment<T> extends Fragment {

  @NonNull private final SparseArray<T> sparseArray = new SparseArray<>();

  /**
   * Get an Instance of DataHolderFragment
   */
  @CheckResult @NonNull public static <I> DataHolderFragment<I> getInstance(
      final @NonNull FragmentActivity fragmentActivity, final @NonNull String tag) {
    return getInstance(fragmentActivity.getSupportFragmentManager(), tag);
  }

  /**
   * Get an Instance of DataHolderFragment
   */
  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static <I> DataHolderFragment<I> getInstance(
      final @NonNull FragmentManager fragmentManager, final @NonNull String tag) {
    @SuppressWarnings("unchecked") DataHolderFragment<I> dataHolderFragment =
        (DataHolderFragment<I>) fragmentManager.findFragmentByTag(tag);
    if (dataHolderFragment == null) {
      Timber.d("Create a new DataHolderFragment with TAG: %s", tag);
      dataHolderFragment = new DataHolderFragment<>();
      fragmentManager.beginTransaction().add(dataHolderFragment, tag).commit();
    }
    return dataHolderFragment;
  }

  /**
   * Remove all DataHolderFragments from the Fragment Manager
   */
  public static void removeAll(final @NonNull FragmentActivity fragmentActivity) {
    removeAll(fragmentActivity.getSupportFragmentManager());
  }

  /**
   * Remove all DataHolderFragments from the Fragment Manager
   */
  @SuppressWarnings("WeakerAccess") public static void removeAll(
      final @NonNull FragmentManager fragmentManager) {
    final List<Fragment> fragmentList = fragmentManager.getFragments();
    for (final Fragment fragment : fragmentList) {
      if (fragment instanceof DataHolderFragment) {
        fragmentManager.beginTransaction().remove(fragment).commit();
      }
    }
  }

  /**
   * Remove a fragment based on its TAG
   */
  public static <I> void remove(final @NonNull FragmentActivity fragmentActivity,
      final @NonNull String tag) {
    remove(fragmentActivity.getSupportFragmentManager(), tag);
  }

  /**
   * Remove a fragment based on its TAG
   */
  @SuppressWarnings("WeakerAccess") public static <I> void remove(
      final @NonNull FragmentManager fragmentManager, final @NonNull String tag) {
    @SuppressWarnings("unchecked") final DataHolderFragment<I> dataHolderFragment =
        (DataHolderFragment<I>) fragmentManager.findFragmentByTag(tag);
    if (dataHolderFragment != null) {
      fragmentManager.beginTransaction().remove(dataHolderFragment).commit();
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Timber.d("Create DataHolderFragment");
    setRetainInstance(true);
  }

  @Override public void onDestroy() {
    super.onDestroy();

    Timber.d("Destroy DataHolderFragment");
    clear();
  }

  /**
   * Clear the sparse array
   */
  @SuppressWarnings("WeakerAccess") public final void clear() {
    Timber.d("Clear data sparseArray");
    sparseArray.clear();
  }

  /**
   * Puts a new Value into the table at a given key
   *
   * It is up to the caller to remember the key
   */
  public final void put(final int key, final @Nullable T value) {
    Timber.d("Put value: %s into key: %d", value, key);
    sparseArray.put(key, value);
  }

  /**
   * Retrieves the value from the table and then removes the object
   */
  @CheckResult @Nullable public final T pop(final int key) {
    Timber.d("Pop value at key: %d", key);
    final T result = sparseArray.get(key);
    sparseArray.remove(key);
    return result;
  }
}
