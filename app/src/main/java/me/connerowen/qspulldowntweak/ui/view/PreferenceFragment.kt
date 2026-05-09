package me.connerowen.qspulldowntweak.ui.view

import android.os.Bundle
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import me.connerowen.qspulldowntweak.R

class PreferenceFragment : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }

//    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
//        super.onSharedPreferenceChanged(sharedPreferences, key)
//        // Your code here.
//    }
}
