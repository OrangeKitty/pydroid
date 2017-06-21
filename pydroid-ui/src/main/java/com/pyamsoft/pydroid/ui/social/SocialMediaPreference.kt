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
import com.pyamsoft.pydroid.ui.databinding.ViewSocialMediaBinding

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
    val binding = ViewSocialMediaBinding.bind(holder.itemView)
    binding.googlePlay.setOnClickListener { Linker.clickGooglePlay() }
    binding.googlePlus.setOnClickListener { Linker.clickGooglePlus() }
    binding.blogger.setOnClickListener { Linker.clickBlogger() }
    binding.facebook.setOnClickListener { Linker.clickFacebook() }
  }

  override fun onUnbindViewHolder(holder: PreferenceViewHolder?) {
    super.onUnbindViewHolder(holder)
    if (holder != null) {
      val binding = ViewSocialMediaBinding.bind(holder.itemView)
      binding.googlePlay.setOnClickListener(null)
      binding.googlePlus.setOnClickListener(null)
      binding.blogger.setOnClickListener(null)
      binding.facebook.setOnClickListener(null)
    }
  }
}
