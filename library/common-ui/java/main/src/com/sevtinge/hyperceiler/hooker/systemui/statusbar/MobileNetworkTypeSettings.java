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
package com.sevtinge.hyperceiler.hooker.systemui.statusbar;

import static com.sevtinge.hyperceiler.hook.utils.devicesdk.SystemSDKKt.isMoreSmallVersion;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;

import com.sevtinge.hyperceiler.dashboard.DashboardFragment;
import com.sevtinge.hyperceiler.hook.utils.prefs.PrefsUtils;
import com.sevtinge.hyperceiler.ui.R;

import fan.preference.DropDownPreference;

public class MobileNetworkTypeSettings extends DashboardFragment
    implements Preference.OnPreferenceChangeListener {

    DropDownPreference mMobileMode;
    PreferenceCategory mMobileTypeGroup;
    SwitchPreference mMobileType;
    SwitchPreference mCompatibility;

    @Override
    public int getPreferenceScreenResId() {
        return R.xml.system_ui_status_bar_mobile_network_type;
    }

    @Override
    public void initPrefs() {
        int mobileMode = Integer.parseInt(PrefsUtils.getSharedStringPrefs(getContext(), "prefs_key_system_ui_status_bar_icon_show_mobile_network_type", "0"));
        mMobileMode = findPreference("prefs_key_system_ui_status_bar_icon_show_mobile_network_type");
        mMobileType = findPreference("prefs_key_system_ui_statusbar_mobile_type_enable");
        mMobileTypeGroup = findPreference("prefs_key_system_ui_statusbar_mobile_type_group");
        mCompatibility = findPreference("prefs_key_system_ui_status_bar_icon_mobile_network_type_compatibility");

        if (!isMoreSmallVersion(200, 2f)) {
            setFuncHint(mCompatibility, 1);
        }

        setMobileMode(mobileMode);
        mMobileMode.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object o) {
        if (preference == mMobileMode) {
            setMobileMode(Integer.parseInt((String) o));
        }
        return true;
    }

    private void setMobileMode(int mode) {
        mMobileType.setEnabled(mode != 3);
        if (mode == 3) {
            mMobileTypeGroup.setVisible(false);
            mMobileType.setChecked(false);
            mMobileType.setSummary(R.string.system_ui_status_bar_mobile_type_single_desc);
        } else {
            mMobileType.setSummary("");
        }
    }
}
