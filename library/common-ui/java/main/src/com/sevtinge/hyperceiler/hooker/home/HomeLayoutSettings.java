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
package com.sevtinge.hyperceiler.hooker.home;

import static com.sevtinge.hyperceiler.hook.utils.devicesdk.MiDeviceAppUtilsKt.isPad;
import static com.sevtinge.hyperceiler.hook.utils.devicesdk.SystemSDKKt.isMoreHyperOSVersion;

import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;

import com.sevtinge.hyperceiler.dashboard.DashboardFragment;
import com.sevtinge.hyperceiler.ui.R;

import fan.preference.DropDownPreference;

public class HomeLayoutSettings extends DashboardFragment {

    SwitchPreference mIconLayout;
    SwitchPreference mIconLayoutNew;
    SwitchPreference mHotseatsMarginTopSwitchPref;

    DropDownPreference mFolderTitlePosDropDownPref;
    SwitchPreference mFolderHorPaddingSwitchPref;

    PreferenceCategory mOldFunc;

    @Override
    public int getPreferenceScreenResId() {
        return R.xml.home_layout;
    }

    @Override
    public void initPrefs() {
        mIconLayout = findPreference("prefs_key_home_layout_unlock_grids");
        mIconLayoutNew = findPreference("prefs_key_home_layout_unlock_grids_new");
        mHotseatsMarginTopSwitchPref = findPreference("prefs_key_home_layout_hotseats_margin_top_enable");
        mFolderTitlePosDropDownPref = findPreference("prefs_key_home_folder_title_pos");
        mFolderHorPaddingSwitchPref = findPreference("prefs_key_home_folder_horizontal_padding_enable");
        mOldFunc = findPreference("prefs_key_home_layout_old_func");

        if (isMoreHyperOSVersion(2f)) {
            mOldFunc.setVisible(false);
            cleanKey("prefs_key_home_other_show_clock");
            cleanKey("prefs_key_personal_assistant_overlap_mode");
        }

        if (isPad()) {
            setHide(mIconLayout, false);
            setHide(mIconLayoutNew, false);
        } else if (isMoreHyperOSVersion(2f)) {
            setHide(mIconLayout, false);
            setHide(mHotseatsMarginTopSwitchPref, false);
        } else {
            setHide(mIconLayoutNew, false);
            setHide(mFolderTitlePosDropDownPref, false);
            setHide(mFolderHorPaddingSwitchPref, false);
        }
    }
}
