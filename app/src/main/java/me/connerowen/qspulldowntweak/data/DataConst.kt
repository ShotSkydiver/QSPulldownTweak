package me.connerowen.qspulldowntweak.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object DataConst {

    val KV_DATA = PrefsData("qs_pulldown_mode", "Default")

    val QS_PULLDOWN_SETTING_VALUES = arrayOf("Disabled", "From right", "From left")
}