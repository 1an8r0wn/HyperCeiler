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
package com.sevtinge.hyperceiler.hook.module.hook.systemui.statusbar.icon.v

import android.service.notification.StatusBarNotification
import android.view.Choreographer
import android.widget.TextView
import com.hchen.superlyricapi.SuperLyricData
import com.sevtinge.hyperceiler.hook.module.base.pack.systemui.MusicBaseHook
import com.sevtinge.hyperceiler.hook.utils.callMethod
import com.sevtinge.hyperceiler.hook.utils.getFloatField
import com.sevtinge.hyperceiler.hook.utils.getObjectFieldOrNull
import com.sevtinge.hyperceiler.hook.utils.getObjectFieldOrNullAs
import com.sevtinge.hyperceiler.hook.utils.setFloatField
import com.sevtinge.hyperceiler.hook.utils.setLongField
import com.sevtinge.hyperceiler.hook.utils.setObjectField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import io.github.kyuubiran.ezxhelper.core.finder.ConstructorFinder.`-Static`.constructorFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createAfterHook
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createBeforeHook
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// author git@wuyou-123
// co-author git@lingqiqi5211
object FocusNotifLyric : MusicBaseHook() {
    private var speed = -0.1f
    private var lastLyric = ""
    private val runnablePool = mutableMapOf<Int, Runnable>()
    private val focusTextViewList = mutableListOf<TextView>()
    private val textViewWidth by lazy {
        mPrefsMap.getInt("system_ui_statusbar_music_width", 0)
    }
    private val MARQUEE_DELAY by lazy {
        mPrefsMap.getInt("system_ui_statusbar_music_scroll_delay", 12) * 100L
    }
    private val SPEED_INCREASE by lazy {
        mPrefsMap.getInt("system_ui_statusbar_music_speed", 18) * 0.1f
    }
    private val isShowNotific by lazy {
        mPrefsMap.getBoolean("system_ui_statusbar_music_show_notific")
    }
    private val isShowApp by lazy {
        mPrefsMap.getBoolean("system_ui_statusbar_music_show_app")
    }

    override fun init() {
        // 拦截构建通知的函数
        if (!isShowNotific) {
            loadClass("com.android.systemui.statusbar.notification.row.NotifBindPipeline").methodFinder()
                .filterByName("requestPipelineRun").first().createBeforeHook {
                    val statusBarNotification =
                        it.args[0].getObjectFieldOrNullAs<StatusBarNotification>("mSbn")
                    if (statusBarNotification!!.notification.channelId == CHANNEL_ID) {
                        it.result = null
                    }
                }
        }

        // 拦截初始化状态栏焦点通知文本布局
        var unhook: XC_MethodHook.Unhook? = null
        loadClass("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment").methodFinder()
            .filterByName("onCreateView")
            .first().createHook {
                before {
                    unhook =
                        loadClass("com.android.systemui.statusbar.widget.FocusedTextView").constructorFinder()
                            .filterByParamCount(3)
                            .first().createAfterHook {
                                focusTextViewList += it.thisObject as TextView
                            }
                }
                after {
                    unhook?.unhook()
                }
            }

        // 构建通知栏通知函数
        // loadClass("com.android.systemui.statusbar.notification.row.NotificationContentInflaterInjector").methodFinder()
        //     .filterByName("createRemoteViews").first())
        // 重设 mLastAnimationTime，取消闪烁动画(让代码以为刚播放过动画，所以这次不播放)
        loadClass("com.android.systemui.statusbar.phone.FocusedNotifPromptView").methodFinder()
            .filterByName("setData")
            .first().createBeforeHook {
                it.thisObject.setLongField("mLastAnimationTime", System.currentTimeMillis())
            }

    }

    fun initLoader(classLoader: ClassLoader) {
        runCatching {
            loadClass("miui.systemui.notification.NotificationSettingsManager", classLoader)
                .methodFinder().filterByName("canShowFocus")
                .first().createHook {
                    // 允许全部应用发送焦点通知
                    returnConstant(true)
                }

        }.onFailure {
            logE(TAG, "canShowFocus failed, ${it.message}")
        }
        runCatching {
            loadClass("miui.systemui.notification.NotificationSettingsManager", classLoader)
                .methodFinder().filterByName("canCustomFocus")
                .first().createHook {
                    // 允许全部应用发送自定义焦点通知
                    returnConstant(true)
                }

        }.onFailure {
            logE(TAG, "canCustomFocus failed, ${it.message}")
        }
        // 启用debug日志
        // setStaticObject(loadClass("miui.systemui.notification.NotificationUtil", classLoader), "DEBUG", true)
    }

    override fun onSuperLyric(data: SuperLyricData) {
        val lyric = data.lyric
        focusTextViewList.forEach { textView ->
            textView.post {
                if (lastLyric == textView.text) {
                    if (XposedHelpers.getAdditionalStaticField(textView, "is_scrolling") == 1) {
                        val m0 = textView.getObjectFieldOrNull("mMarquee")
                        m0?.apply {
                            // 设置速度并且调用停止函数,重置歌词位置
                            setFloatField("mPixelsPerMs", 0f)
                            callMethod("stop")
                        }
                    }
                    textView.text = lyric
                    lastLyric = lyric
                }
                val key = textView.hashCode()
                val startScroll = runnablePool.getOrPut(key) {
                    Runnable {
                        startScroll(textView)
                        runnablePool.remove(key)
                    }
                }
                textView.handler?.removeCallbacks(startScroll)
                textView.postDelayed(startScroll, MARQUEE_DELAY)
            }
        }

        if (!isShowApp && data.lyric.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                sendNotification(data.lyric, data)
            }
        }
    }

    private fun startScroll(textView: TextView) {
        runCatching {
            // 开始滚动
            textView.callMethod("setMarqueeRepeatLimit", 1)
            textView.callMethod("startMarqueeLocal")
            val key = textView.hashCode()
            val m = textView.getObjectFieldOrNull("mMarquee") ?: return
            if (speed == -0.1f) {
                // 初始化滚动速度
                speed = m.getFloatField("mPixelsPerMs") * SPEED_INCREASE
            }

            val width = (textView.width - textView.compoundPaddingLeft - textView.compoundPaddingRight - textViewWidth).toFloat()
            val lineWidth = textView.layout?.getLineWidth(0)

            if (lineWidth != null) {
                // 重设最大滚动宽度,只能滚动到文本结束
                m.setFloatField("mMaxScroll", lineWidth - width)
                // 重设速度
                m.setFloatField("mPixelsPerMs", speed)
                // 移除回调,防止滚动结束之后重置滚动位置
                m.setObjectField("mRestartCallback", Choreographer.FrameCallback {})
                // 滚动完成后清理状态
                textView.postDelayed({
                    XposedHelpers.setAdditionalStaticField(textView, "is_scrolling", 1)
                    runnablePool.remove(key) //移除任务引用
                }, computeScrollDuration(lineWidth, width, speed)) // 根据速度和距离计算时长
            }
        }.onFailure {
            logE(TAG, lpparam.packageName, "error: ${it.message}")
        }
    }

    private fun computeScrollDuration(lineWidth: Float, width: Float, speed: Float): Long {
        val maxScroll = (lineWidth - width).coerceAtLeast(0f) // 与 mMaxScroll 一致
        val pixelsPerMs = speed // 与 mPixelsPerMs 一致
        return if (pixelsPerMs > 0) (maxScroll / pixelsPerMs).toLong() else 0L
    }



    override fun onStop() {
        if (!isShowApp) cancelNotification()
    }
}
