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

package com.pyamsoft.pydroid.support;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.tool.Offloader;
import com.pyamsoft.pydroid.tool.SerialOffloader;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

class SupportInteractorImpl implements SupportInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final ICheckout checkout;

  SupportInteractorImpl(@NonNull ICheckout checkout) {
    this.checkout = checkout;
  }

  @Override public void create(@NonNull Inventory.Callback callback,
      @NonNull OnBillingSuccessListener success, @NonNull OnBillingErrorListener error) {

    Timber.d("Create checkout purchase flow");
    checkout.setSuccessListener(success);
    checkout.setErrorListener(error);
    checkout.setInventoryCallback(callback);
    checkout.start();
  }

  @Override public void destroy() {
    Timber.d("Stop checkout purchase flow");
    checkout.stop();
    checkout.setSuccessListener(null);
    checkout.setErrorListener(null);
    checkout.setInventoryCallback(null);
  }

  @Override public void loadInventory() {
    checkout.loadInventory();
  }

  @Override public void purchase(@NonNull Sku sku) {
    Timber.i("Purchase item: %s", sku.id);
    checkout.purchase(sku);
  }

  @Override public void consume(@NonNull String token) {
    Timber.d("Attempt consume token: %s", token);
    checkout.consume(token);
  }

  @NonNull @Override public Offloader<Boolean> processBillingResult(int requestCode, int resultCode,
      @Nullable Intent data) {
    Timber.i("Process billing onFinish");
    return new SerialOffloader<Boolean>().onProcess(
        () -> checkout.processBillingResult(requestCode, resultCode, data));
  }
}
