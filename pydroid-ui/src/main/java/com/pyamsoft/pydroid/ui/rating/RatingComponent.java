/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.rating;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.rating.RatingModule;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class RatingComponent {

  @NonNull private final RatingModule ratingModule;

  public RatingComponent(@NonNull RatingModule ratingModule) {
    this.ratingModule = Checker.Companion.checkNonNull(ratingModule);
  }

  void inject(@NonNull RatingDialog.Launcher launcher) {
    Checker.Companion.checkNonNull(launcher).presenter = ratingModule.getPresenter();
  }
}
