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
package com.sevtinge.hyperceiler.main.page.settings;

import static com.sevtinge.hyperceiler.hook.utils.PropUtils.getProp;
import static com.sevtinge.hyperceiler.hook.utils.api.ProjectApi.isDebug;
import static com.sevtinge.hyperceiler.main.page.settings.helper.HomepageEntrance.ANDROID_NS;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.sevtinge.hyperceiler.dashboard.SettingsPreferenceFragment;
import com.sevtinge.hyperceiler.hook.utils.log.AndroidLogUtils;
import com.sevtinge.hyperceiler.hook.utils.shell.ShellInit;
import com.sevtinge.hyperceiler.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SafeModeFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private String mPkgList;
    private SwitchPreference mHome;
    private SwitchPreference mSettings;
    private SwitchPreference mSystemUi;
    private SwitchPreference mSecurityCenter;
    private SwitchPreference mDemo;

    @Override
    public int getPreferenceScreenResId() {
        return R.xml.prefs_settings_safe_mode;
    }

    @Override
    public void initPrefs() {
        mPkgList = getProp("persist.hyperceiler.crash.report");
        mHome = findPreference("prefs_key_home_safe_mode_enable");
        mSettings = findPreference("prefs_key_system_settings_safe_mode_enable");
        mSystemUi = findPreference("prefs_key_system_ui_safe_mode_enable");
        mSecurityCenter = findPreference("prefs_key_security_center_safe_mode_enable");
        mDemo = findPreference("prefs_key_demo_safe_mode_enable");
        Objects.requireNonNull(mDemo).setVisible(isDebug());

        setCheckedState();

        mHome.setOnPreferenceChangeListener(this);
        mDemo.setOnPreferenceChangeListener(this);
        mSettings.setOnPreferenceChangeListener(this);
        mSystemUi.setOnPreferenceChangeListener(this);
        mSecurityCenter.setOnPreferenceChangeListener(this);

        setPreference();
    }

    private void setCheckedState() {
        // 用逗号分割避免 contains 误判
        String[] pkgs = mPkgList == null ? new String[0] : mPkgList.split(",");
        Set<String> pkgSet = new HashSet<>(Arrays.asList(pkgs));
        mSystemUi.setChecked(pkgSet.contains("systemui"));
        mSettings.setChecked(pkgSet.contains("settings"));
        mHome.setChecked(pkgSet.contains("home"));
        mSecurityCenter.setChecked(pkgSet.contains("center"));
        mDemo.setChecked(pkgSet.contains("demo"));
    }

    private void setPreference() {
        Resources resources = getResources();
        try (XmlResourceParser xml = resources.getXml(R.xml.prefs_settings_safe_mode)) {
            int event = xml.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG && "SwitchPreference".equals(xml.getName())) {
                    String key = xml.getAttributeValue(ANDROID_NS, "key");
                    String summary = xml.getAttributeValue(ANDROID_NS, "summary");
                    if (key != null && summary != null) {
                        Drawable icon = getPackageIcon(summary); // 替换为获取图标的方法
                        String name = getPackageName(summary);
                        SwitchPreference preferenceHeader = findPreference(key);
                        if (preferenceHeader != null) {
                            if (icon != null) preferenceHeader.setIcon(icon);
                            if (!"android".equals(summary) && name != null) preferenceHeader.setTitle(name);
                        }
                    }
                }
                event = xml.next();
            }
        } catch (XmlPullParserException | IOException e) {
            AndroidLogUtils.logE("SafeModeFragment", "An error occurred when reading the XML:", e);
        }
    }

    private Drawable getPackageIcon(String packageName) {
        try {
            return requireContext().getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private String getPackageName(String packageName) {
        try {
            return (String) requireContext().getPackageManager()
                    .getApplicationLabel(requireContext().getPackageManager().getApplicationInfo(packageName, 0));
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object o) {
        String key = null;
        if (preference == mHome) {
            key = "home";
        } else if (preference == mSettings) {
            key = "settings";
        } else if (preference == mSystemUi) {
            key = "systemui";
        } else if (preference == mSecurityCenter) {
            key = "center";
        } else if (preference == mDemo) {
            key = "demo";
        }
        if (key != null) {
            String mPkgList = getProp("persist.hyperceiler.crash.report");
            Set<String> pkgSet = new HashSet<>();
            if (mPkgList != null && !mPkgList.isEmpty()) {
                pkgSet.addAll(Arrays.asList(mPkgList.split(",")));
            }
            if ((boolean) o) {
                pkgSet.add(key);
            } else {
                pkgSet.remove(key);
            }
            String newPkgList = String.join(",", pkgSet);
            ShellInit.getShell().run("setprop persist.hyperceiler.crash.report \"" + newPkgList + "\"").sync();
        }
        return true;
    }
}
