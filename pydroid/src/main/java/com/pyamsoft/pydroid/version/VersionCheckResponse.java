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

package com.pyamsoft.pydroid.version;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue abstract class VersionCheckResponse {

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static TypeAdapter<VersionCheckResponse> typeAdapter(final Gson gson) {
    return new AutoValue_VersionCheckResponse.GsonTypeAdapter(gson);
  }

  @CheckResult @SerializedName("CURRENT_VERSION") abstract int currentVersion();
}