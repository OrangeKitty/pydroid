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

package com.pyamsoft.pydroid.ui.about

import android.database.DataSetObserver
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.backstack.BackStack
import com.pyamsoft.pydroid.about.AboutLibrariesModel
import com.pyamsoft.pydroid.about.AboutLibrariesPresenter
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderMap
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarFragment
import com.pyamsoft.pydroid.ui.databinding.FragmentAboutLibrariesBinding
import com.pyamsoft.pydroid.ui.helper.postWith
import com.pyamsoft.pydroid.ui.helper.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled
import timber.log.Timber

class AboutLibrariesFragment : ToolbarFragment(), AboutLibrariesPresenter.View {

    internal lateinit var presenter: AboutLibrariesPresenter
    internal lateinit var imageLoader: ImageLoader
    internal lateinit var pagerAdapter: AboutPagerAdapter
    private lateinit var listener: ViewPager.OnPageChangeListener
    private lateinit var binding: FragmentAboutLibrariesBinding
    private val mapper = LoaderMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PYDroid.obtain().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        binding = FragmentAboutLibrariesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager(savedInstanceState)
        setupArrows()

        presenter.bind(viewLifecycle, this)
    }

    override fun onLicenseLoaded(model: AboutLibrariesModel) {
        binding.viewPager.postWith {
            if (view != null) {
                pagerAdapter.add(model)
            }
        }
    }

    override fun onAllLoaded() {
        binding.viewPager.postWith {
            if (view != null) {
                pagerAdapter.notifyDataSetChanged()
                binding.aboutTitle.text = pagerAdapter.getPageTitle(it.currentItem)
            }
        }
    }

    private fun setupViewPager(savedInstanceState: Bundle?) {
        pagerAdapter = AboutPagerAdapter(this)
        binding.apply {
            // Show spinner while loading
            progressSpinner.visibility = View.VISIBLE

            // Mark pager invisible while loading
            viewPager.visibility = View.INVISIBLE
            viewPager.adapter = pagerAdapter
            viewPager.offscreenPageLimit = 1
        }

        // We must observe the data set for when it finishes.
        // There is a race condition that can cause the UI to crash if pager.setCurrentItem is
        // called before notifyDataSetChanged finishes
        pagerAdapter.registerDataSetObserver(object : DataSetObserver() {

            override fun onChanged() {
                super.onChanged()
                Timber.d("Data set changed! Set current item and unregister self")
                pagerAdapter.unregisterDataSetObserver(this)

                // Hide spinner now that loading is done
                binding.apply {
                    progressSpinner.visibility = View.GONE
                    viewPager.visibility = View.VISIBLE

                    // Reload the last looked at page
                    if (savedInstanceState != null) {
                        viewPager.setCurrentItem(savedInstanceState.getInt(KEY_PAGE, 0), false)
                    }
                }
            }
        })

        val obj = object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                    positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                binding.aboutTitle.text = pagerAdapter.getPageTitle(position)
            }
        }

        listener = obj
        binding.viewPager.addOnPageChangeListener(obj)
    }

    private fun setupArrows() {
        mapper.put("left",
                imageLoader.fromResource(R.drawable.ic_arrow_down_24dp).into(binding.arrowLeft))
        binding.arrowLeft.rotation = 90F
        binding.arrowLeft.setOnDebouncedClickListener {
            binding.viewPager.arrowScroll(View.FOCUS_LEFT)
        }

        mapper.put("right",
                imageLoader.fromResource(R.drawable.ic_arrow_down_24dp).into(binding.arrowRight))
        binding.arrowRight.rotation = -90F
        binding.arrowRight.setOnDebouncedClickListener {
            binding.viewPager.arrowScroll(View.FOCUS_RIGHT)
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarActivity.withToolbar {
            it.title = "Open Source Licenses"
            it.setUpEnabled(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapper.clear()
        binding.viewPager.removeOnPageChangeListener(listener)
        binding.viewPager.adapter = null
        pagerAdapter.clear()

        binding.arrowLeft.setOnDebouncedClickListener(null)
        binding.arrowRight.setOnDebouncedClickListener(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_PAGE, binding.viewPager.currentItem)
        super.onSaveInstanceState(outState)
    }

    companion object {

        const val TAG = "AboutLibrariesFragment"
        private const val KEY_PAGE = "key_current_page"

        @JvmStatic
        fun show(activity: FragmentActivity, backStack: BackStack) {
            val fragmentManager = activity.supportFragmentManager
            if (fragmentManager.findFragmentByTag(TAG) == null) {
                backStack.add(TAG) { AboutLibrariesFragment() }
            }
        }

    }
}

