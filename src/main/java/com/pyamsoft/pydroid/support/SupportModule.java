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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.PYDroidModule;

public class SupportModule {

  @NonNull private final SupportPresenter presenter;
  @NonNull private final SupportPresenterLoader loader;

  public SupportModule(@NonNull PYDroidModule.Provider pyDroidModule) {
    presenter = new SupportPresenterImpl(pyDroidModule.provideObsScheduler(),
        pyDroidModule.provideSubScheduler());
    loader = new SupportPresenterLoader(pyDroidModule.provideContext(), presenter);
  }

  @NonNull @CheckResult public SupportPresenterLoader getLoader() {
    return loader;
  }
}
