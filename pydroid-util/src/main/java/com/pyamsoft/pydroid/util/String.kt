/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.util

import android.content.Context
import android.content.Intent
import android.support.annotation.CheckResult
import androidx.net.toUri

@CheckResult
fun String.hyperlink(c: Context): HyperlinkIntent {
  val intent = Intent(Intent.ACTION_VIEW).also {
    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    it.data = this.toUri()
  }

  return HyperlinkIntent(c.applicationContext, intent, this)
}
