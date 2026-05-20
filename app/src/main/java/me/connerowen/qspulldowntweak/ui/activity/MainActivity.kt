@file:Suppress("SetTextI18n")

package me.connerowen.qspulldowntweak.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import me.connerowen.qspulldowntweak.R
import me.connerowen.qspulldowntweak.ui.theme.AppTheme
import me.connerowen.qspulldowntweak.data.DataConst
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preferenceCategory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainScreen() {
        val isActivated = YukiHookAPI.Status.isXposedModuleActive
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
       //  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        val qsPulldownModeOptions: Array<String> = resources.getStringArray(R.array.qs_pulldown_entries)
        // TOFIX: This is a pretty ugly way to do prefs i think
        var newPulldownMode by remember { mutableStateOf(prefs().get(DataConst.KV_DATA, "qs_pulldown_mode")) }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { /* Handle GitHub click if needed */ },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.mipmap.ic_github),
                                contentDescription = "GitHub",
                                modifier = Modifier.size(27.dp),
                                tint = colorResource(id = R.color.colorTextGray).copy(alpha = 0.85f)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            containerColor = colorResource(id = R.color.colorThemeBackground)
        ) { innerPadding ->
            ProvidePreferenceLocals {
                LazyColumn(
//                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding() + 16.dp,
                        bottom = innerPadding.calculateBottomPadding() + 16.dp,
                    ),
                ) {
                    item {
                        StatusCard(isActivated)
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    preferenceCategory(
                        key = "category_tweak_settings",
                        title = { Text(stringResource(R.string.tweak_settings)) },
                    )

                    listPreference(
                        key = "qs_pulldown_mode",
                        values = qsPulldownModeOptions.toList(),
                        defaultValue = qsPulldownModeOptions[0],
                        // value = prefs().get(DataConst.KV_DATA, "qs_pulldown_mode"),
                        value = newPulldownMode,
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.expansion_panels),
                                contentDescription = "QS Pulldown Mode Icon",
                            )
                        },
                        titleId = R.string.qs_pulldown_setting_title,
                        onValueChange = {
                            prefs().edit { put(DataConst.KV_DATA, it) }
                            newPulldownMode = it
                                        },
                        valueToText = { it },
//                        title = {
//                            Text(
//                                text = stringResource(R.string.qs_pulldown_setting_title),
//                                style = MaterialTheme.typography.bodyLarge,
//                            )
//                        },
                        summary = { Text(stringResource(R.string.qs_pulldown_setting_description)) }
                    )

                    item { Spacer(Modifier.height(16.dp)) }

                    item {
                        AboutCard(
                            iconRes = R.mipmap.ic_yukihookapi,
                            textRes = R.string.about_module
                        )
                    }

                    item {
                        AboutCard(
                            iconRes = R.mipmap.ic_kavaref,
                            textRes = R.string.about_module_extension
                        )
                    }
                }
            }
        }
    }

    private fun LazyListScope.listPreference(
        key: String,
        defaultValue: String,
        value: String,
        // onValueChange: (MutableState<String>, String) -> Boolean = { _, _ -> true },
        onValueChange: (String) -> Unit,
        values: List<String>,
        @StringRes titleId: Int,
//        title: @Composable (Boolean) -> Unit,
        icon: @Composable () -> Unit,
        summary: @Composable () -> Unit,
        type: ListPreferenceType = ListPreferenceType.ALERT_DIALOG,
        valueToText: (String) -> String
    ) = item(key = key, contentType = "ListPreference") {
        ListPreference(
            // value = prefs().getInt("qs_pulldown_mode"),
            // value = prefsValue,
            value = value,
            // onValueChange = { prefs().edit { put(DataConst.KV_DATA, it) } },
            onValueChange = onValueChange,
            values = values,
            title = { Text(text = stringResource(titleId)) },
            // icon = { Icon(imageVector = icon, contentDescription = null) },
            icon = icon,
            // summary = { Text(text = summary(valueToText(prefs().getInt("qs_pulldown_mode")))) },
            summary = summary,
            type = type,
            valueToText = { AnnotatedString(valueToText(it)) })
    }

    @Composable
    private fun StatusCard(isActivated: Boolean) {
        val bgColor = if (isActivated) Color(0xFF26A69A) else Color(0x661B1B1B)
        val iconRes = if (isActivated) R.mipmap.ic_success else R.mipmap.ic_warn
        val statusTextRes = if (isActivated) R.string.module_is_activated else R.string.module_not_activated

        Row(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .fillMaxWidth()
                .background(color = bgColor, shape = RoundedCornerShape(15.dp))
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 25.dp, end = 5.dp)
                    .size(25.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(id = statusTextRes),
                    color = Color.White,
                    fontSize = 18.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                Text(
                    text = stringResource(id = R.string.module_version),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }
    }

    @Composable
    private fun AboutCard(iconRes: Int, textRes: Int) {
        Row(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
                .fillMaxWidth()
                .background(color = Color(0x66E4E4E4), shape = RoundedCornerShape(15.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(35.dp)
            )

            Text(
                text = stringResource(id = textRes),
                color = colorResource(id = R.color.colorTextGray),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                maxLines = 2
            )
        }
    }
}
