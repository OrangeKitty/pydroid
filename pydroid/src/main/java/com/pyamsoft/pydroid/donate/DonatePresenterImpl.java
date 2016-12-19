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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.pydroid.presenter.PresenterBase;
import com.pyamsoft.pydroid.tool.ExecutedOffloader;
import com.pyamsoft.pydroid.tool.OffloaderHelper;
import org.solovyev.android.checkout.Inventory;
import timber.log.Timber;

class DonatePresenterImpl extends PresenterBase<DonatePresenter.View>
    implements DonatePresenter, Inventory.Callback {

  @SuppressWarnings("WeakerAccess") @NonNull @VisibleForTesting
  final DonateInteractor.OnBillingSuccessListener successListener;
  @SuppressWarnings("WeakerAccess") @NonNull @VisibleForTesting
  final DonateInteractor.OnBillingErrorListener errorListener;
  @NonNull private final DonateInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull ExecutedOffloader billingResult =
      new ExecutedOffloader.Empty();

  DonatePresenterImpl(@NonNull DonateInteractor interactor) {
    this.interactor = interactor;
    successListener = () -> getView(View::onBillingSuccess);
    errorListener = () -> getView(View::onBillingError);
  }

  @Override protected void onBind() {
    super.onBind();
    interactor.init(this, successListener, errorListener);
  }

  @Override public void create(@NonNull Activity activity) {
    interactor.create(activity);
    loadInventory();
  }

  @Override public void loadInventory() {
    interactor.loadInventory();
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    interactor.destroy();
    OffloaderHelper.cancel(billingResult);
  }

  @Override public void onBillingResult(int requestCode, int resultCode, @Nullable Intent data) {
    OffloaderHelper.cancel(billingResult);
    billingResult =
        interactor.processBillingResult(requestCode, resultCode, data).onError(throwable -> {
          Timber.e(throwable, "Error processing Billing onFinish");
          getView(View::onProcessResultError);
        }).onResult(result -> getView(view -> {
          if (result) {
            view.onProcessResultSuccess();
          } else {
            view.onProcessResultFailed();
          }
        })).onFinish(() -> OffloaderHelper.cancel(billingResult)).execute();
  }

  @Override public void checkoutInAppPurchaseItem(@NonNull SkuModel skuModel) {
    final String token = skuModel.token();
    if (token != null) {
      interactor.consume(token);
    } else {
      interactor.purchase(skuModel.sku());
    }
  }

  @Override public void onLoaded(@NonNull Inventory.Products products) {
    Timber.d("Products are loaded");
    getView(view -> view.onInventoryLoaded(products));
  }
}
