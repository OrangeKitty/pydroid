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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.PYDroidModule;
import java.util.List;
import org.solovyev.android.checkout.Billing;

public class DonateModule {

  @NonNull private final DonateInteractor interactor;
  @NonNull private final DonatePresenter presenter;
  @NonNull private final DonatePresenterLoader loader;

  public DonateModule(@NonNull PYDroidModule.Provider pyDroidModule) {
    interactor = new DonateInteractorImpl(CheckoutFactory.create(pyDroidModule.provideBilling(),
        pyDroidModule.provideInAppPurchaseList()));
    presenter = new DonatePresenterImpl(interactor);
    loader = new DonatePresenterLoader(presenter);
  }

  @NonNull @CheckResult public DonatePresenterLoader getLoader() {
    return loader;
  }

  @SuppressWarnings("WeakerAccess") static final class CheckoutFactory {
    private CheckoutFactory() {
      throw new RuntimeException("No instances");
    }

    @CheckResult @NonNull
    static ICheckout create(@NonNull Billing billing, @NonNull List<String> inAppPurchaseList) {
      return new RealCheckout(billing, inAppPurchaseList);
    }
  }
}