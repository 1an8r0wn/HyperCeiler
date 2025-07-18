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
package com.sevtinge.hyperceiler.hook.module.hook.packageinstaller

import com.sevtinge.hyperceiler.hook.module.base.BaseHook
import com.sevtinge.hyperceiler.hook.module.base.dexkit.DexKit
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHooks
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Method

object InstallRiskDisable : BaseHook() {
    override fun init() {
        DexKit.findMemberList<Method>("InstallRiskDisable") {
            it.findMethod {
                matcher {
                    addUsingString("secure_verify_enable", StringMatchType.Equals)
                    returnType = "boolean"
                }
                matcher {
                    addUsingString("installerOpenSafetyModel", StringMatchType.Equals)
                    returnType = "boolean"
                }
                matcher {
                    addUsingString("android.provider.MiuiSettings\$Ad", StringMatchType.Equals)
                    returnType = "boolean"
                }
            }
        }.createHooks {
            returnConstant(false)
        }
    }
}
