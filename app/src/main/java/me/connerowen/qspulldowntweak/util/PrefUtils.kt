package me.connerowen.qspulldowntweak.util

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

@Composable
inline fun <reified T> PrefsData<T>.rememberState(): MutableState<T> {
    val context = LocalContext.current
    val state = remember { mutableStateOf(context.prefs().get(this)) }

    return remember {
        object : MutableState<T> by state {
            override var value: T
                get() = state.value
                set(newValue) {
                    state.value = newValue
                    context.prefs().edit { put(this@rememberState, newValue) }
                }
        }
    }
}