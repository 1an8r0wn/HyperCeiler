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
package com.sevtinge.hyperceiler.hook.module.hook.systemsettings

import com.sevtinge.hyperceiler.hook.module.base.BaseHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook

object NoveltyHaptic : BaseHook() {
    override fun init() {
        if (mPrefsMap.getBoolean("system_settings_international_build")) return // 开启国际版设置界面将禁用此功能

        loadClass("com.android.settings.utils.SettingsFeatures").methodFinder()
            .filterByName("isNoveltyHaptic")
            .single().createHook {
                returnConstant(true)
            }
    }
}
