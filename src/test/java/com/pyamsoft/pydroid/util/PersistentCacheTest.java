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
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.dagger.presenter.PresenterBase;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 23)
public class PersistentCacheTest {

  @Nullable private final Bundle NULL_STATE = null;

  /**
   * Test that we generate two different keys when instances are not saved
   */
  @Test public void test_generateDifferent() {
    PersistentCache.clear();

    // Generate first key
    final String instance1 = "i1";
    final long firstResult = PersistentCache.generateKey(instance1, NULL_STATE);

    // Generate second key
    final String instance2 = "i2";
    final long secondResult = PersistentCache.generateKey(instance2, NULL_STATE);

    Assert.assertNotEquals(firstResult, secondResult);
  }

  /**
   * Test that we generate the same key when instances are saved
   */
  @Test public void test_generateSame() {
    PersistentCache.clear();

    // Generate first key
    final String instance = "i1";
    final long firstResult = PersistentCache.generateKey(instance, NULL_STATE);

    // Save the key
    final Bundle outState = new Bundle();
    PersistentCache.saveKey(instance, outState, firstResult);

    // Fetch the key again
    final long secondResult = PersistentCache.generateKey(instance, outState);

    Assert.assertEquals(firstResult, secondResult);
  }

  /**
   * To be used with test_loadSynchronous
   */
  @CheckResult long doLoadSynchronous(@NonNull String instance, @Nullable Bundle instanceState,
      @NonNull AtomicInteger createCount, @NonNull AtomicInteger loadCount) {
    return PersistentCache.load(instance, instanceState, new PersistLoader.Callback<Object>() {
      @NonNull @Override public PersistLoader<Object> createLoader() {
        return new PersistLoader<Object>(RuntimeEnvironment.application) {
          @NonNull @Override public Object loadPersistent() {
            createCount.getAndIncrement();
            return createCount;
          }
        };
      }

      @Override public void onPersistentLoaded(@NonNull Object persist) {
        loadCount.getAndIncrement();
      }
    });
  }

  /**
   * Test that the cache loads the same key when passed in the same values, and that objects are
   * only created a single time and then cached
   */
  @Test public void test_loadSynchronous() {
    PersistentCache.clear();

    // Keeps track of the number of new creates
    final AtomicInteger createCount = new AtomicInteger(0);
    // Keeps track of the number of total loads
    final AtomicInteger loadCount = new AtomicInteger(0);

    // Generate first key
    final String instance = "i1";

    // First generate will not have a saved state
    final long loadedKey = doLoadSynchronous(instance, null, createCount, loadCount);

    // Synchronous so we can check here
    Assert.assertEquals(createCount.get(), loadCount.get());

    // Now save state
    final Bundle outState = new Bundle();
    PersistentCache.saveKey(instance, outState, loadedKey);

    // Reload
    final long newKey = doLoadSynchronous(instance, outState, createCount, loadCount);

    // Check that keys are the same
    Assert.assertEquals(loadedKey, newKey);

    // Check that counts are different
    Assert.assertNotEquals(createCount.get(), loadCount.get());

    // Check that load count is more than create
    Assert.assertTrue(loadCount.get() > createCount.get());
  }

  /**
   * Test that clean up properly destroys a destroyable object like a presenter
   */
  @Test public void test_cleanup() {
    PersistentCache.clear();

    // First load a presenter
    final TestPresenter[] presenterHack = new TestPresenter[1];
    final long loadedKey =
        PersistentCache.load("I", null, new PersistLoader.Callback<TestPresenter>() {
          @NonNull @Override public PersistLoader<TestPresenter> createLoader() {
            return new PersistLoader<TestPresenter>(RuntimeEnvironment.application) {
              @NonNull @Override public TestPresenter loadPersistent() {
                return new TestPresenter();
              }
            };
          }

          @Override public void onPersistentLoaded(@NonNull TestPresenter persist) {
            presenterHack[0] = persist;
          }
        });

    // Make sure it is not destroyed
    final TestPresenter testPresenter = presenterHack[0];
    Assert.assertFalse(testPresenter.isDestroyed());

    // Unload the presenter
    PersistentCache.unload(loadedKey);

    // Check again
    Assert.assertTrue(testPresenter.isDestroyed());
  }

  /**
   * Test that clean up does not destroy an object if it does not implement the Destroyable
   * interface
   */
  @Test public void test_doNotDestroy() {
    PersistentCache.clear();

    // First load a presenter
    final DoNotDestroy[] hack = new DoNotDestroy[1];
    final long loadedKey =
        PersistentCache.load("I", null, new PersistLoader.Callback<DoNotDestroy>() {
          @NonNull @Override public PersistLoader<DoNotDestroy> createLoader() {
            return new PersistLoader<DoNotDestroy>(RuntimeEnvironment.application) {
              @NonNull @Override public DoNotDestroy loadPersistent() {
                return new DoNotDestroy();
              }
            };
          }

          @Override public void onPersistentLoaded(@NonNull DoNotDestroy persist) {
            hack[0] = persist;
          }
        });

    // Make sure it is not destroyed
    final DoNotDestroy doNotDestroy = hack[0];
    Assert.assertFalse(doNotDestroy.isDestroyed());

    // Unload the presenter
    PersistentCache.unload(loadedKey);

    // Check again
    Assert.assertFalse(doNotDestroy.isDestroyed());
  }

  void doCreationPerformanceTest(int keySize) {
    PersistentCache.clear();

    final long startTime = System.nanoTime();

    // We will load 1000 keys
    final long[] keys = new long[keySize];
    for (int i = 0; i < keySize; ++i) {
      keys[i] =
          PersistentCache.load("DOESN'T MATTER", NULL_STATE, new PersistLoader.Callback<Object>() {
            @NonNull @Override public PersistLoader<Object> createLoader() {
              return new PersistLoader<Object>(RuntimeEnvironment.application) {
                @NonNull @Override public Object loadPersistent() {
                  return new Object();
                }
              };
            }

            @Override public void onPersistentLoaded(@NonNull Object persist) {

            }
          });
    }

    final long endTime = System.nanoTime();

    final long difference = endTime - startTime;
    final long differenceMillis = difference / 1000000;
    final long differenceSeconds = differenceMillis / 1000;
    System.out.printf("Loading %s keys took: %d milliseconds (%d seconds)\n", keySize,
        differenceMillis, differenceSeconds);
  }

  @Test public void test_creationPerformance1000() {
    doCreationPerformanceTest(1000);
  }

  @Test public void test_creationPerformance10000() {
    doCreationPerformanceTest(10000);
  }

  @Test public void test_creationPerformance100000() {
    doCreationPerformanceTest(100000);
  }

  @Test public void test_creationPerformance1000000() {
    doCreationPerformanceTest(1000000);
  }

  void doRetrievePerformanceTest(int keySize) {
    PersistentCache.clear();

    // We will load 1000 keys
    final long[] keys = new long[keySize];
    for (int i = 0; i < keySize; ++i) {
      keys[i] =
          PersistentCache.load("DOESN'T MATTER", NULL_STATE, new PersistLoader.Callback<Object>() {
            @NonNull @Override public PersistLoader<Object> createLoader() {
              return new PersistLoader<Object>(RuntimeEnvironment.application) {
                @NonNull @Override public Object loadPersistent() {
                  return new Object();
                }
              };
            }

            @Override public void onPersistentLoaded(@NonNull Object persist) {

            }
          });
    }

    // Save them for later
    final Bundle[] bundles = new Bundle[keySize];
    for (int i = 0; i < keySize; ++i) {
      bundles[i] = new Bundle();
      PersistentCache.saveKey(String.valueOf(keys[i]), bundles[i], keys[i]);
    }

    // Load keys again
    final long startTime = System.nanoTime();

    for (int i = 0; i < keySize; ++i) {
      //noinspection CheckResult
      PersistentCache.load(String.valueOf(keys[i]), bundles[i],
          new PersistLoader.Callback<Object>() {
            @NonNull @Override public PersistLoader<Object> createLoader() {
              throw new RuntimeException("Should not be called");
            }

            @Override public void onPersistentLoaded(@NonNull Object persist) {

            }
          });
    }

    final long endTime = System.nanoTime();

    final long difference = endTime - startTime;
    final long differenceMillis = difference / 1000000;
    final long differenceSeconds = differenceMillis / 1000;
    System.out.printf("Retrieving %s keys took: %d milliseconds (%d seconds)\n", keySize,
        differenceMillis, differenceSeconds);
  }

  @Test public void test_retrievePerformance1000() {
    doRetrievePerformanceTest(1000);
  }

  @Test public void test_retrievePerformance10000() {
    doRetrievePerformanceTest(10000);
  }

  @Test public void test_retrievePerformance100000() {
    doRetrievePerformanceTest(100000);
  }

  @Test public void test_retrievePerformance1000000() {
    doRetrievePerformanceTest(1000000);
  }

  static class DoNotDestroy {

    private boolean destroyed = false;

    public boolean isDestroyed() {
      return destroyed;
    }
  }

  static class TestPresenter extends PresenterBase<String> {

    private boolean destroyed = false;

    public boolean isDestroyed() {
      return destroyed;
    }

    @Override protected void onDestroy() {
      super.onDestroy();
      destroyed = true;
    }
  }
}
