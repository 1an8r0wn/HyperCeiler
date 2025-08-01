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
package com.sevtinge.hyperceiler.hook.module.hook.aiasst

import com.sevtinge.hyperceiler.hook.module.base.BaseHook
import com.sevtinge.hyperceiler.hook.module.base.dexkit.DexKit
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import java.lang.reflect.Method

object UnlockAllCaptions : BaseHook() {
    private val mBuildConfigUtils by lazy {
        findClassIfExists("com.xiaomi.aiasst.vision.common.BuildConfigUtils")
    }
    private val getMethod by lazy<Method> {
        DexKit.findMember("BuildConfigUtils") {
            it.findMethod {
                matcher {
                    declaredClass {
                        addEqString("BuildConfigUtils")
                    }
                    addUsingField("Lmiui/os/Build;->DEVICE:Ljava/lang/String;")
                }
            }.single()
        }
    }

    override fun init() {
        // by PedroZ
        if (mBuildConfigUtils == null) {
            getMethod.createHook {
                returnConstant(true)
            }
        } else {
            loadClass("com.xiaomi.aiasst.vision.common.BuildConfigUtils").methodFinder()
                .filterByName("isSupplierOnline")
                .single().createHook {
                    returnConstant(true)
                }
        }
    }
}
