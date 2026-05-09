@file:Suppress("SetTextI18n")

package me.connerowen.qspulldowntweak.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.add
import androidx.fragment.app.commit
import me.connerowen.qspulldowntweak.R
import com.highcapable.betterandroid.ui.component.activity.AppViewsActivity
import com.highcapable.yukihookapi.YukiHookAPI
import me.connerowen.qspulldowntweak.ui.view.PreferenceFragment
import android.R as Android_R

class MainActivity : AppViewsActivity() {

//    private val homeComponent by lazy { ComponentName(packageName, "${BuildConfig.APPLICATION_ID}.Home") }
//    private val prefs: YukiHookPrefsBridge by lazy { prefs() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<View>(Android_R.id.content).setBackgroundResource(R.color.colorThemeBackground)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<PreferenceFragment>(R.id.prefsFragmentContainerView)
            }

        refreshModuleStatus()
    }

    private fun refreshModuleStatus() {
        val moduleActivatedLayout = findViewById<LinearLayout>(R.id.main_lin_status)
        val moduleActivatedImg = findViewById<ImageFilterView>(R.id.main_img_status)
        val moduleActivatedText = findViewById<TextView>(R.id.main_text_status)

        moduleActivatedLayout.setBackgroundResource(
            when {
                YukiHookAPI.Status.isXposedModuleActive -> R.drawable.bg_green_round
                else -> R.drawable.bg_dark_round
            }
        )
        moduleActivatedImg.setImageResource(
            when {
                YukiHookAPI.Status.isXposedModuleActive -> R.mipmap.ic_success
                else -> R.mipmap.ic_warn
            }
        )
        moduleActivatedText.text = getString(
            when {
                YukiHookAPI.Status.isXposedModuleActive -> R.string.module_is_activated
                else -> R.string.module_not_activated
            }
        )
    }

}