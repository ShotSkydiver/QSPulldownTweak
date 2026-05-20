package me.connerowen.qspulldowntweak.hook

import android.view.MotionEvent
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField


@InjectYukiHookWithXposed(isUsingResourcesHook = true)
object HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        YukiHookAPI.configs {
            debugLog {
                tag = "QSPulldownTweak"
            }
            isDebug = false
        }
    }

    override fun onHook() = encase {
        loadApp(name = "com.android.systemui") {
            "com.android.systemui.shade.QuickSettingsControllerImpl".toClass()
                .resolve()
                .firstMethod {
                    name = "isOpenQsEvent"
                    parameters(MotionEvent::class)
                }.hook {
                    before {
                        val quickPulldownMode = prefs.getString("qs_pulldown_mode", "0").toIntOrNull() ?: 0

                        if (quickPulldownMode > 0) {
                            val ev = args[0] as MotionEvent
                            val mQs: Any? = getObjectField(instance, "mQs")
                            val mBarState: Int = getIntField(instance, "mBarState")
                            val mView = callMethod(mQs, "getView") as View?
                            val isLayoutRtl = callMethod(mView, "isLayoutRtl") as Boolean
                            val w = callMethod(mView, "getMeasuredWidth") as Int
                            val x = ev.getX()
                            val region = w * 1f / 4f
                            var showQsOverride = false

                            when (quickPulldownMode) {
                                1 -> showQsOverride =
                                    if (isLayoutRtl) x < region else w - region < x

                                2 -> showQsOverride =
                                    if (isLayoutRtl) w - region < x else x < region
                            }
                            showQsOverride = showQsOverride and (mBarState == 0)

                            if (showQsOverride) result = true
                        }
                    }
                }
        }
    }
}