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

package com.pyamsoft.pydroid.ui.donate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.pyamsoft.pydroid.ActionNone;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.DonatePresenterLoader;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.donate.DonatePresenter;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity;
import com.pyamsoft.pydroid.util.PersistentCache;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

public abstract class DonationActivity extends VersionCheckActivity
    implements DonatePresenter.View {

  @NonNull private static final String KEY_DONATE_PRESENTER = "__key_donate_presenter";
  @SuppressWarnings("WeakerAccess") DonatePresenter donatePresenter;
  private long loadedKey;

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    loadedKey = PersistentCache.get()
        .load(KEY_DONATE_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<DonatePresenter>() {

              @NonNull @Override public PersistLoader<DonatePresenter> createLoader() {
                return new DonatePresenterLoader();
              }

              @Override public void onPersistentLoaded(@NonNull DonatePresenter persist) {
                donatePresenter = persist;
              }
            });
  }

  @CallSuper @Override protected void onStart() {
    super.onStart();
    donatePresenter.bindView(this);
    donatePresenter.create(this);
  }

  @CallSuper @Override protected void onStop() {
    super.onStop();
    donatePresenter.unbindView();
  }

  @CallSuper @Override protected void onDestroy() {
    super.onDestroy();

    if (!isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @CallSuper @Override protected void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_DONATE_PRESENTER, loadedKey, DonatePresenter.class);
    super.onSaveInstanceState(outState);
  }

  /**
   * onActivityResult is always called after onStart, so we will always be bound
   */
  @CallSuper @Override protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    donatePresenter.onBillingResult(requestCode, resultCode, data);
  }

  @CallSuper @Override public boolean onCreateOptionsMenu(@NonNull Menu menu) {
    super.onCreateOptionsMenu(menu);
    final MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_support, menu);
    return true;
  }

  @CallSuper @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    final int itemId = item.getItemId();
    boolean handled;
    if (itemId == R.id.menu_support) {
      DonateDialog.show(getSupportFragmentManager());
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  @CheckResult @NonNull DonatePresenter getDonatePresenter() {
    if (donatePresenter == null) {
      throw new IllegalStateException("SupportPresenter is NULL");
    }
    return donatePresenter;
  }

  private void passToSupportDialog(@NonNull ActionSingle<DonateDialog> actionWithDialog,
      @Nullable ActionNone actionWithoutDialog) {
    final Fragment fragment = getSupportFragmentManager().findFragmentByTag(DonateDialog.TAG);
    if (fragment instanceof DonateDialog) {
      actionWithDialog.call((DonateDialog) fragment);
    } else {
      if (actionWithoutDialog != null) {
        actionWithoutDialog.call();
      }
    }
  }

  @Override public final void onBillingSuccess() {
    passToSupportDialog(DonateDialog::onBillingSuccess,
        () -> Toast.makeText(getApplicationContext(), R.string.purchase_success_msg,
            Toast.LENGTH_SHORT).show());
  }

  @Override public final void onBillingError() {
    passToSupportDialog(DonateDialog::onBillingError,
        () -> Toast.makeText(getApplicationContext(), R.string.purchase_error_msg,
            Toast.LENGTH_SHORT).show());
  }

  @Override public final void onProcessResultSuccess() {
    passToSupportDialog(DonateDialog::onProcessResultSuccess, null);
  }

  @Override public final void onProcessResultError() {
    passToSupportDialog(DonateDialog::onProcessResultError, this::onBillingError);
  }

  @Override public final void onProcessResultFailed() {
    passToSupportDialog(DonateDialog::onProcessResultFailed, this::onBillingError);
  }

  @Override public final void onInventoryLoaded(@NonNull Inventory.Products products) {
    final Inventory.Product product = products.get(ProductTypes.IN_APP);
    if (product.supported) {
      Timber.i("IAP Billing is supported");
      // Only reveal non-consumable items
      for (Sku sku : product.getSkus()) {
        Timber.d("Add sku: %s", sku.id);
        final Purchase purchase = product.getPurchaseInState(sku, Purchase.State.PURCHASED);
        if (purchase != null) {
          Timber.i("Item is purchased already, attempt to auto-consume it.");
          final SkuUIItem item = new SkuUIItem(sku, purchase.token);
          getDonatePresenter().checkoutInAppPurchaseItem(item.getModel());
        }
      }
    }

    passToSupportDialog(view -> view.onInventoryLoaded(products), null);
  }
}
