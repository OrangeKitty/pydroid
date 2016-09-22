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

package com.pyamsoft.pydroid.about;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.pyamsoft.pydroid.AboutLibrariesLoaderCallback;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.R2;
import com.pyamsoft.pydroid.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.util.CircularRevealFragmentUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class AboutLibrariesFragment extends ActionBarFragment
    implements AboutLibrariesPresenter.View {

  @NonNull public static final String TAG = "AboutLibrariesFragment";
  @NonNull private static final String KEY_STYLING = "key_styling";
  @NonNull private static final String KEY_BACK_STACK = "key_back_stack";
  @NonNull private static final String KEY_ABOUT_PRESENTER = "key_about_presenter";
  @SuppressWarnings("WeakerAccess") AboutLibrariesPresenter presenter;
  @BindView(R2.id.recycler_about_libraries) RecyclerView recyclerView;
  @ColorInt private int backgroundColor;
  private FastItemAdapter<AboutAdapterItem> fastItemAdapter;
  private long loadedKey;
  private boolean lastOnBackStack;
  private Unbinder unbinder;

  public static void show(@NonNull FragmentActivity activity, @IdRes int containerResId,
      @NonNull Styling styling, @NonNull BackStackState backStackState) {
    final FragmentManager fragmentManager = activity.getSupportFragmentManager();
    if (fragmentManager.findFragmentByTag(TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(containerResId, AboutLibrariesFragment.newInstance(styling, backStackState), TAG)
          .addToBackStack(TAG)
          .commit();
    }
  }

  @CheckResult @NonNull private static AboutLibrariesFragment newInstance(@NonNull Styling styling,
      @NonNull BackStackState backStackState) {
    final Bundle args = new Bundle();
    final AboutLibrariesFragment fragment = new AboutLibrariesFragment();

    args.putString(KEY_STYLING, styling.name());
    args.putString(KEY_BACK_STACK, backStackState.name());
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final String stylingName = getArguments().getString(KEY_STYLING, null);
    if (stylingName == null) {
      throw new NullPointerException("Styling is NULL");
    }

    // We have to do this because fragments will not set their own background color, this allows us
    // to safely draw over contents
    final Styling styling = Styling.valueOf(stylingName);
    switch (styling) {
      case LIGHT:
        backgroundColor = ContextCompat.getColor(getContext(), R.color.material_light_background);
        break;
      case DARK:
        backgroundColor = ContextCompat.getColor(getContext(), R.color.material_dark_background);
        break;
      default:
        throw new RuntimeException("Invalid styling: " + stylingName);
    }

    final String backStackStateName = getArguments().getString(KEY_BACK_STACK, null);
    if (backStackStateName == null) {
      throw new NullPointerException("BackStateState is NULL");
    }

    final BackStackState backStackState = BackStackState.valueOf(backStackStateName);
    switch (backStackState) {
      case LAST:
        lastOnBackStack = true;
        break;
      case NOT_LAST:
        lastOnBackStack = false;
        break;
      default:
        throw new RuntimeException("Invalid back stack state: " + backStackStateName);
    }

    loadedKey = PersistentCache.get()
        .load(KEY_ABOUT_PRESENTER, savedInstanceState,
            new AboutLibrariesLoaderCallback(getContext()) {
              @Override public void onPersistentLoaded(@NonNull AboutLibrariesPresenter persist) {
                presenter = persist;
              }
            });
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_about_libraries, container, false);
    unbinder = ButterKnife.bind(this, view);
    view.setBackgroundColor(backgroundColor);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());
    fastItemAdapter = new FastItemAdapter<>();
    fastItemAdapter.withSelectable(true);

    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(fastItemAdapter);

    final List<AboutAdapterItem> items = new ArrayList<>();
    Licenses.forEach(aboutLicenseItem -> {
      final boolean add;
      if (aboutLicenseItem.getName().equals(Licenses.Names.GOOGLE_PLAY)) {
        final String googlePlayLicense =
            Licenses.licenses(getContext()).provideGoogleOpenSourceLicenses();
        add = (googlePlayLicense != null);
      } else {
        add = true;
      }

      if (add) {
        items.add(new AboutAdapterItem(aboutLicenseItem));
      }
    });
    fastItemAdapter.add(items);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_ABOUT_PRESENTER, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    if (lastOnBackStack) {
      Timber.d("About is last on backstack, set up false");
      setActionBarUpEnabled(false);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @Override public void onLicenseTextLoaded(int position, @NonNull String text) {
    fastItemAdapter.getAdapterItem(position).setLicenseText(text);
    fastItemAdapter.notifyItemChanged(position);
  }

  public enum Styling {
    LIGHT, DARK
  }

  public enum BackStackState {
    LAST, NOT_LAST
  }
}
