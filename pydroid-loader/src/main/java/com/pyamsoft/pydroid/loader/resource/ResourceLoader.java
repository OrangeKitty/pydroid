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

package com.pyamsoft.pydroid.loader.resource;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import com.pyamsoft.pydroid.function.ActionSingle;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.loader.GenericLoader;
import com.pyamsoft.pydroid.loader.loaded.Loaded;
import com.pyamsoft.pydroid.loader.targets.DrawableImageTarget;
import com.pyamsoft.pydroid.loader.targets.Target;
import com.pyamsoft.pydroid.util.DrawableUtil;

/**
 * Loads Images from Resources.
 *
 * Supports Drawable resource types
 */
public abstract class ResourceLoader extends GenericLoader<ResourceLoader, Drawable> {

  @DrawableRes private final int resource;
  @NonNull final Context appContext;

  ResourceLoader(@NonNull Context context, @DrawableRes int resource) {
    appContext = context.getApplicationContext();
    this.resource = resource;

    if (this.resource == 0) {
      throw new IllegalStateException("No resource to load");
    }
  }

  @NonNull @Override public ResourceLoader tint(@ColorRes int color) {
    this.tint = color;
    return this;
  }

  @NonNull @Override
  public ResourceLoader setStartAction(@NonNull ActionSingle<Target<Drawable>> startAction) {
    this.startAction = Checker.Companion.checkNonNull(startAction);
    return this;
  }

  @NonNull @Override
  public ResourceLoader setErrorAction(@NonNull ActionSingle<Target<Drawable>> errorAction) {
    this.errorAction = Checker.Companion.checkNonNull(errorAction);
    return this;
  }

  @NonNull @Override
  public ResourceLoader setCompleteAction(@NonNull ActionSingle<Target<Drawable>> completeAction) {
    this.completeAction = Checker.Companion.checkNonNull(completeAction);
    return this;
  }

  @Override @NonNull public final Loaded into(@NonNull ImageView imageView) {
    return into(DrawableImageTarget.forImageView(imageView));
  }

  @Override @NonNull public final Loaded into(@NonNull Target<Drawable> target) {
    return load(target, resource);
  }

  @CheckResult @NonNull final Drawable loadResource(@NonNull Context context) {
    context = Checker.Companion.checkNonNull(context);
    Drawable loaded = AppCompatResources.getDrawable(context, resource);
    if (loaded == null) {
      throw new NullPointerException("Could not load drawable for resource: " + resource);
    }

    if (tint != 0) {
      loaded = DrawableUtil.Companion.tintDrawableFromRes(context, loaded, tint);
    }
    return loaded;
  }

  @CheckResult @NonNull
  protected abstract Loaded load(@NonNull Target<Drawable> target, @DrawableRes int resource);
}
