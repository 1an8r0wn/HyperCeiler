/*
 * This file is part of HyperCeiler.
 *
 * HyperCeiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2023-2025 HyperCeiler Contributions
 */

package com.sevtinge.hyperceiler.hook.module.hook.various.clipboard;

import com.hchen.hooktool.HCBase;
import com.hchen.hooktool.hook.IHook;

/**
 * 获取常用语的 classloader。
 * from <a href="https://github.com/HChenX/ClipboardList">ClipboardList</a>
 *
 * @author 焕晨HChen
 */
public class LoadInputMethodDex extends HCBase {
    private boolean isLoaded;

    @Override
    public void init() {
        hookMethod("android.inputmethodservice.InputMethodModuleManager",
            "loadDex",
            ClassLoader.class, String.class,
            new IHook() {
                @Override
                public void after() {
                    if (isLoaded) return;

                    ClassLoader classLoader = (ClassLoader) getArg(0);
                    ClipboardLimit.unlock(classLoader);

                    logI(TAG, "Input method classloader: " + classLoader);
                    isLoaded = true;
                }
            }
        );

    }

}
