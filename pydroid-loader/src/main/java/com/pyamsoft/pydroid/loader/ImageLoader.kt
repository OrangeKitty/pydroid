/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.loader

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import com.pyamsoft.pydroid.loader.resource.ResourceLoader
import com.pyamsoft.pydroid.loader.resource.RxResourceLoader

object ImageLoader {


  @CheckResult
  fun <T : GenericLoader<*>> fromLoader(loader: T): T = loader


  @CheckResult
  fun fromResource(context: Context,
      @DrawableRes resource: Int): ResourceLoader = RxResourceLoader(context, resource)
}
