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

package com.pyamsoft.pydroid.ui.social

import android.content.Context
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.BaseBoundPreference
import kotlinx.android.synthetic.main.view_social_media.view.blogger
import kotlinx.android.synthetic.main.view_social_media.view.facebook
import kotlinx.android.synthetic.main.view_social_media.view.google_play
import kotlinx.android.synthetic.main.view_social_media.view.google_plus
import timber.log.Timber

class SocialMediaPreference : BaseBoundPreference {

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
      context, attrs, defStyleAttr, defStyleRes) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init()
  }

  constructor(context: Context) : super(context) {
    init()
  }

  private fun init() {
    layoutResource = R.layout.view_social_media
  }

  override fun onBindViewHolder(holder: PreferenceViewHolder) {
    super.onBindViewHolder(holder)
    Timber.d("onBindViewHolder")
    holder.itemView.google_play.setOnClickListener { Linker.with(it.context).clickGooglePlay() }
    holder.itemView.google_plus.setOnClickListener { Linker.with(it.context).clickGooglePlus() }
    holder.itemView.blogger.setOnClickListener { Linker.with(it.context).clickBlogger() }
    holder.itemView.facebook.setOnClickListener { Linker.with(it.context).clickFacebook() }
  }

  override fun onUnbindViewHolder(holder: PreferenceViewHolder?) {
    super.onUnbindViewHolder(holder)
    if (holder != null) {
      holder.itemView.google_play.setOnClickListener(null)
      holder.itemView.google_plus.setOnClickListener(null)
      holder.itemView.blogger.setOnClickListener(null)
      holder.itemView.facebook.setOnClickListener(null)
    }
  }
}
