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

package com.pyamsoft.pydroid.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 23)
public class AppUtilTest {

  @Test public void testConvertToDp() {
    final Context context = RuntimeEnvironment.application.getApplicationContext();

    // Zero test
    final float zero = 0F;
    Assert.assertEquals(zero, AppUtil.convertToDP(context, 0), 0);

    // Reproducibility test
    final float oneDp = AppUtil.convertToDP(context, 1);
    Assert.assertNotSame(oneDp, zero);
    Assert.assertEquals(oneDp, AppUtil.convertToDP(context, 1), 0);

    // Correct logic
    Assert.assertNotSame(oneDp * 2, zero);
    Assert.assertEquals(oneDp * 2, AppUtil.convertToDP(context, 2), 0);

    Assert.assertNotSame(oneDp * 4, zero);
    Assert.assertEquals(oneDp * 4, AppUtil.convertToDP(context, 4), 0);

    Assert.assertNotSame(oneDp * 8, zero);
    Assert.assertEquals(oneDp * 8, AppUtil.convertToDP(context, 8), 0);

    Assert.assertNotSame(oneDp * 16, zero);
    Assert.assertEquals(oneDp * 16, AppUtil.convertToDP(context, 16), 0);
  }

  @Test public void testGetApplicationInfoIntent() {
    // Check that URI points to the same place
    String packageName = "com.pyamsoft.padlock";
    Intent intent = AppUtil.getApplicationInfoIntent(packageName);
    Uri uri = Uri.fromParts("package", packageName, null);
    Assert.assertEquals(intent.getData().toString(), uri.toString());

    packageName = "com.test.example";
    intent = AppUtil.getApplicationInfoIntent(packageName);
    uri = Uri.fromParts("package", packageName, null);
    Assert.assertEquals(intent.getData().toString(), uri.toString());
  }

  @Test public void testGuaranteeSingleDialogFragment() throws InterruptedException {
    final ActivityController<AppCompatActivity> activityController =
        TestUtils.getAppCompatActivityController();
    final AppCompatActivity activity = activityController.create().start().resume().visible().get();

    // First make sure adding a fragment works
    AppUtil.guaranteeSingleDialogFragment(activity, new DialogFragment(), "tag");
    Assert.assertEquals(1, activity.getSupportFragmentManager().getFragments().size());

    // Adding a fragment with the same tag removes the old one
    AppUtil.guaranteeSingleDialogFragment(activity, new DialogFragment(), "tag");
    // There are two fragments
    Assert.assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
    // But the first one (old) is NULL
    Assert.assertNull(activity.getSupportFragmentManager().getFragments().toArray()[0]);
    // And the second one is not
    Assert.assertNotNull(activity.getSupportFragmentManager().getFragments().toArray()[1]);

    // Adding a fragment with a new tag adds it
    AppUtil.guaranteeSingleDialogFragment(activity, new DialogFragment(), "newtag");
    Assert.assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
  }
}
