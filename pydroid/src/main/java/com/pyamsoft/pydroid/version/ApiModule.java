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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pyamsoft.pydroid.BuildConfig;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiModule {

  @NonNull private static final String GITHUB_URL = "raw.githubusercontent.com";
  @NonNull private static final String CURRENT_VERSION_REPO_BASE_URL =
      "https://" + GITHUB_URL + "/pyamsoft/android-project-versions/master/";
  @NonNull private final Gson gson;
  @NonNull private final OkHttpClient okHttpClient;
  @NonNull private final Retrofit retrofit;
  @NonNull private final VersionCheckApi versionCheckApi;

  public ApiModule() {
    gson = provideGson();
    okHttpClient = provideOkHttpClient();
    retrofit = provideRetrofit();
    versionCheckApi = new VersionCheckApi(retrofit);
  }

  @CheckResult @NonNull private Gson provideGson() {
    final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(
        new VersionCheckApi.AutoValueTypeAdapterFactory());
    return gsonBuilder.create();
  }

  @CheckResult @NonNull private OkHttpClient provideOkHttpClient() {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
    if (BuildConfig.DEBUG) {
      final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addInterceptor(logging);
    }

    final CertificatePinner pinner = new CertificatePinner.Builder().add(GITHUB_URL,
        "sha256/m41PSCmB5CaR0rKh7VMMXQbDFgCNFXchcoNFm3RuoXw=")
        .add(GITHUB_URL, "sha256/k2v657xBsOVe1PQRwOsHsw3bsGT2VzIqz5K+59sNQws=")
        .add(GITHUB_URL, "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=")
        .build();
    builder.certificatePinner(pinner);

    return builder.build();
  }

  @CheckResult @NonNull private Retrofit provideRetrofit() {
    return new Retrofit.Builder().baseUrl(CURRENT_VERSION_REPO_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
  }

  @NonNull @CheckResult VersionCheckApi getVersionCheckApi() {
    return versionCheckApi;
  }
}
