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

package com.pyamsoft.pydroid.app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.SocialMediaLoaderCallback;
import com.pyamsoft.pydroid.VersionCheckLoaderCallback;
import com.pyamsoft.pydroid.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.donate.DonateDialog;
import com.pyamsoft.pydroid.donate.DonationActivity;
import com.pyamsoft.pydroid.rating.RatingDialog;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import com.pyamsoft.pydroid.version.VersionCheckPresenter;
import com.pyamsoft.pydroid.version.VersionCheckProvider;
import com.pyamsoft.pydroid.version.VersionUpgradeDialog;
import java.util.Locale;
import timber.log.Timber;

public abstract class ActionBarSettingsPreferenceFragment extends ActionBarPreferenceFragment
    implements VersionCheckPresenter.View, VersionCheckProvider, SocialMediaPresenter.View {

  @NonNull private static final String KEY_LICENSE_PRESENTER = "key_license_presenter";
  @NonNull private static final String KEY_SOCIAL_PRESENTER = "key_rate_media_presenter";
  @SuppressWarnings("WeakerAccess") VersionCheckPresenter presenter;
  @SuppressWarnings("WeakerAccess") SocialMediaPresenter socialPresenter;
  private long loadedKey;
  private Toast toast;
  private long ratingKey;

  @CallSuper @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    loadedKey = PersistentCache.get()
        .load(KEY_LICENSE_PRESENTER, savedInstanceState, new VersionCheckLoaderCallback() {

          @Override public void onPersistentLoaded(@NonNull VersionCheckPresenter persist) {
            presenter = persist;
          }
        });

    ratingKey = PersistentCache.get()
        .load(KEY_SOCIAL_PRESENTER, savedInstanceState, new SocialMediaLoaderCallback() {

          @Override public void onPersistentLoaded(@NonNull SocialMediaPresenter persist) {
            socialPresenter = persist;
          }
        });
  }

  @SuppressLint("ShowToast") @CallSuper @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    toast = Toast.makeText(getContext(), "Checking for updates...", Toast.LENGTH_SHORT);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @CallSuper @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
      PersistentCache.get().unload(ratingKey);
    }
  }

  @CallSuper @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_LICENSE_PRESENTER, loadedKey);
    PersistentCache.get().saveKey(outState, KEY_SOCIAL_PRESENTER, ratingKey);
    super.onSaveInstanceState(outState);
  }

  @Override public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    @XmlRes final int xmlResId = getPreferenceXmlResId();
    if (xmlResId != 0) {
      addPreferencesFromResource(xmlResId);
    }
    addPreferencesFromResource(R.xml.pydroid);

    final Preference applicationSettings = findPreference("application_settings");
    if (applicationSettings != null) {
      applicationSettings.setTitle(
          String.format(Locale.getDefault(), "%s Settings", getApplicationName()));
    }
  }

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Preference upgradeInfo = findPreference(getString(R.string.upgrade_info_key));
    if (upgradeInfo != null) {
      if (hideUpgradeInformation()) {
        upgradeInfo.setVisible(false);
      } else {
        upgradeInfo.setOnPreferenceClickListener(preference -> showChangelog());
      }
    }

    final SwitchPreferenceCompat showAds =
        (SwitchPreferenceCompat) findPreference(getString(R.string.adview_key));
    showAds.setOnPreferenceChangeListener((preference, newValue) -> toggleAdVisibility(newValue));

    final Preference showAboutLicenses = findPreference(getString(R.string.about_license_key));
    showAboutLicenses.setOnPreferenceClickListener(preference -> onLicenseItemClicked());

    final Preference checkVersion = findPreference(getString(R.string.check_version_key));
    checkVersion.setOnPreferenceClickListener(preference -> checkForUpdate());

    final Preference clearAll = findPreference(getString(R.string.clear_all_key));
    if (clearAll != null) {
      if (hideClearAll()) {
        clearAll.setVisible(false);
      } else {
        clearAll.setOnPreferenceClickListener(preference -> onClearAllPreferenceClicked());
      }
    }

    final Preference rateApplication = findPreference(getString(R.string.rating_key));
    rateApplication.setOnPreferenceClickListener(preference -> {
      socialPresenter.clickAppPage(preference.getContext().getPackageName());
      return true;
    });

    final Preference donation = findPreference(getString(R.string.donation_key));
    donation.setOnPreferenceClickListener(preference -> {
      DonateDialog.show(getFragmentManager());
      return true;
    });
  }

  @CallSuper @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
    socialPresenter.bindView(this);
  }

  @CallSuper @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
    socialPresenter.unbindView();
  }

  @CheckResult protected boolean showChangelog() {
    final FragmentActivity activity = getActivity();
    if (activity instanceof RatingDialog.ChangeLogProvider) {
      final RatingDialog.ChangeLogProvider provider = (RatingDialog.ChangeLogProvider) activity;
      RatingDialog.showRatingDialog(activity, provider, true);
    } else {
      throw new ClassCastException("Activity is not a change log provider");
    }
    return true;
  }

  @CheckResult protected boolean toggleAdVisibility(Object object) {
    if (object instanceof Boolean) {
      final boolean b = (boolean) object;
      final FragmentActivity activity = getActivity();
      if (activity instanceof DonationActivity) {
        final DonationActivity donationActivity = (DonationActivity) getActivity();
        if (b) {
          Timber.d("Turn on ads");
          donationActivity.showAd();
        } else {
          Timber.d("Turn off ads");
          donationActivity.hideAd();
        }
        return true;
      } else {
        Timber.e("Activity is not AdvertisementActivity");
        return false;
      }
    }
    return false;
  }

  @CheckResult protected boolean showAboutLicensesFragment(@IdRes int containerId,
      @NonNull AboutLibrariesFragment.Styling styling) {
    Timber.d("Show about licenses fragment");
    AboutLibrariesFragment.show(getActivity(), containerId, styling, isLastOnBackStack());
    return true;
  }

  @NonNull @CheckResult protected AboutLibrariesFragment.BackStackState isLastOnBackStack() {
    return AboutLibrariesFragment.BackStackState.NOT_LAST;
  }

  @SuppressWarnings("SameReturnValue") @CheckResult protected boolean checkForUpdate() {
    toast.show();
    presenter.checkForUpdates(getCurrentApplicationVersion());
    return true;
  }

  @Override public void onVersionCheckFinished() {
    Timber.d("License check finished, mark");
  }

  @Override public void onUpdatedVersionFound(int currentVersionCode, int updatedVersionCode) {
    Timber.d("Updated version found. %d => %d", currentVersionCode, updatedVersionCode);
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(),
        VersionUpgradeDialog.newInstance(provideApplicationName(), currentVersionCode,
            updatedVersionCode), VersionUpgradeDialog.TAG);
  }

  @Override public void onSocialMediaClicked(@NonNull String link) {
    NetworkUtil.newLink(getContext(), link);
  }

  @CheckResult @NonNull private DonationActivity getDonationActivity() {
    final FragmentActivity activity = getActivity();
    if (activity instanceof DonationActivity) {
      return (DonationActivity) activity;
    } else {
      throw new ClassCastException("Cannot cast to Donation activity");
    }
  }

  @NonNull @Override public String provideApplicationName() {
    return getDonationActivity().provideApplicationName();
  }

  @Override public int getCurrentApplicationVersion() {
    return getDonationActivity().getCurrentApplicationVersion();
  }

  @CheckResult protected boolean onClearAllPreferenceClicked() {
    return true;
  }

  @CheckResult @XmlRes protected int getPreferenceXmlResId() {
    return 0;
  }

  @CheckResult @NonNull protected AboutLibrariesFragment.Styling getAboutFragmentStyling() {
    return AboutLibrariesFragment.Styling.LIGHT;
  }

  @CheckResult protected boolean hideUpgradeInformation() {
    return false;
  }

  @CheckResult protected boolean hideClearAll() {
    return false;
  }

  @CheckResult protected boolean onLicenseItemClicked() {
    return showAboutLicensesFragment(getRootViewContainer(), getAboutFragmentStyling());
  }

  @CheckResult @IdRes protected abstract int getRootViewContainer();

  @CheckResult @NonNull protected abstract String getApplicationName();
}
