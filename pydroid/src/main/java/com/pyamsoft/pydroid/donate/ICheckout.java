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

package com.pyamsoft.pydroid.donate;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Sku;

interface ICheckout {

  void init(@NonNull Inventory.Callback inventoryCallback,
      @NonNull DonateInteractor.OnBillingSuccessListener successListener,
      @NonNull DonateInteractor.OnBillingErrorListener errorListener);

  void createForActivity(@NonNull Activity activity);

  void loadInventory();

  void start();

  void stop();

  void purchase(@NonNull Sku sku);

  void consume(@NonNull String token);

  void beginPurchaseFlow();

  void endPurchaseFlow();

  @CheckResult boolean processBillingResult(int requestCode, int resultCode, @Nullable Intent data);
}
