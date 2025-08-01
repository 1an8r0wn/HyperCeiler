/*
 * This file is part of HyperCeiler.

 * HyperCeiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.

 * Copyright (C) 2023-2025 HyperCeiler Contributions
 */
package com.sevtinge.hyperceiler.main;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sevtinge.hyperceiler.dashboard.base.activity.ActivityCallback;
import com.sevtinge.hyperceiler.main.fragment.ContentFragment;

import fan.appcompat.app.Fragment;
import fan.core.utils.AttributeResolver;
import fan.navigator.Navigator;
import fan.navigator.NavigatorStrategy;
import fan.navigator.app.NavigatorActivity;
import fan.navigator.navigatorinfo.NavigatorInfoProvider;
import fan.navigator.navigatorinfo.UpdateFragmentNavInfo;

public class NaviBaseActivity extends NavigatorActivity implements ActivityCallback {

    public void checkTheme() {
        if (AttributeResolver.resolve(this, fan.navigator.R.attr.isNavigatorTheme) < 0) {
            Log.d("NotesNaviActivityTAG", "reset Theme");
            setTheme(com.sevtinge.hyperceiler.ui.R.style.NavigatorActivityTheme);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        checkTheme();
        super.onCreate(savedInstanceState);
        registerObserver(getApplicationContext());
    }

    @Override
    public int getBottomTabMenu() {
        return com.sevtinge.hyperceiler.ui.R.menu.bottom_nav_menu;
    }

    @Override
    public int getNavigationOptionMenu() {
        return 0;
    }

    @Override
    public Bundle getNavigatorInitArgs() {
        NavigatorStrategy navigatorStrategy = new NavigatorStrategy();
        navigatorStrategy.setCompactMode(Navigator.Mode.C);
        navigatorStrategy.setRegularMode(Navigator.Mode.C);
        navigatorStrategy.setLargeMode(Navigator.Mode.C);
        Bundle bundle = new Bundle();
        bundle.putParcelable("miuix:navigatorStrategy", navigatorStrategy);
        return bundle;
    }

    @Override
    public NavigatorInfoProvider getBottomTabMenuNavInfoProvider() {
        return id -> {
            Bundle bundle = new Bundle();
            if (id == 1000) {
                bundle.putInt("page", 0);
            } else if (id == 1001) {
                bundle.putInt("page", 1);
            } else if (id == 1002) {
                bundle.putInt("page", 2);
            } else {
                return null;
            }
            return new UpdateFragmentNavInfo(id, getDefaultContentFragment(), bundle);
        };
    }

    @Override
    public Class<? extends Fragment> getDefaultContentFragment() {
        return ContentFragment.class;
    }

    @Override
    public void onCreatePrimaryNavigation(Navigator navigator, Bundle bundle) {
        UpdateFragmentNavInfo navInfoToHome = getUpdateFragmentNavInfo(0, 1000);
        UpdateFragmentNavInfo navInfoToSettings = getUpdateFragmentNavInfo(1, 1001);
        UpdateFragmentNavInfo navInfoToAbout = getUpdateFragmentNavInfo(2, 1002);
        newLabel(getString(com.sevtinge.hyperceiler.ui.R.string.navigation_home_title), com.sevtinge.hyperceiler.ui.R.drawable.ic_navigation_home, navInfoToHome);
        newLabel(getString(com.sevtinge.hyperceiler.ui.R.string.navigation_settings_title), com.sevtinge.hyperceiler.ui.R.drawable.ic_navigation_settings, navInfoToSettings);
        newLabel(getString(com.sevtinge.hyperceiler.ui.R.string.navigation_about_title), com.sevtinge.hyperceiler.ui.R.drawable.ic_navigation_about, navInfoToAbout);
        navigator.navigate(navInfoToHome);
    }

    private UpdateFragmentNavInfo getUpdateFragmentNavInfo(int position, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("page", position);
        return new UpdateFragmentNavInfo(id, getDefaultContentFragment(), bundle);
    }

    @Override
    public void onCreateOtherNavigation(Navigator navigator, Bundle bundle) {

    }
}
