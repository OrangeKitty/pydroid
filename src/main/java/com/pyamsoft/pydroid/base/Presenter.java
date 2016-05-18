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

package com.pyamsoft.pydroid.base;

import android.support.annotation.NonNull;

public interface Presenter<I> {

  /**
   * Bind the View to this presenter
   *
   * Usually called during the onCreate/onCreateView calls
   */
  void onCreateView(@NonNull I view);

  /**
   * Unbind the View to this presenter
   * Discard any data associated
   *
   * Usually called during the onDestroy/onDestroyView calls
   */
  void onDestroyView();

  /**
   * Used for registering the presenter to various bus subscriptions
   *
   * Generally called during onResume
   */
  void onResume();

  /**
   * Used for unregistering the presenter from various bus subscriptions
   *
   * Generally called during onPause
   */
  void onPause();
}
