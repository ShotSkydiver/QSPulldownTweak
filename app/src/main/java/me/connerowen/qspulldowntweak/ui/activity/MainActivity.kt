@file:Suppress("SetTextI18n")

package me.connerowen.qspulldowntweak.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.highcapable.yukihookapi.YukiHookAPI
import me.connerowen.qspulldowntweak.R
import me.connerowen.qspulldowntweak.ui.theme.AppTheme
import me.connerowen.qspulldowntweak.data.DataConst
import me.connerowen.qspulldowntweak.ui.theme.ColorConst
import me.connerowen.qspulldowntweak.util.rememberState
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preferenceCategory

private const val REPO_URL = "https://github.com/ShotSkydiver/QSPulldownTweak"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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
        val context = LocalContext.current

        var pulldownMode by DataConst.PREF_QS_PULLDOWN_MODE.rememberState()
        val entries = stringArrayResource(R.array.qs_pulldown_entries)
        val values = stringArrayResource(R.array.qs_pulldown_values)

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    actions = {
                        IconButton(
                            onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, REPO_URL.toUri())) },
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
            }
        ) { innerPadding ->
            val preferenceColor = ColorConst.cardContainerColor

            ProvidePreferenceLocals {
                LazyColumn(
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
                        key = DataConst.PREF_QS_PULLDOWN_MODE.key,
                        modifier = Modifier.preferenceModifier(preferenceColor, RoundedCornerShape(24.dp)),
                        value = pulldownMode,
                        onValueChange = { pulldownMode = it },
                        values = values.toList(),
                        valueToText = { entries[values.indexOf(it)] },
                        icon = { Icon(painterResource(R.drawable.expansion_panels), null) },
                        titleId = R.string.qs_pulldown_setting_title,
                        summaryId = R.string.qs_pulldown_setting_description,
                    )
                }
            }
        }
    }

    private fun LazyListScope.listPreference(
        key: String,
        value: String,
        onValueChange: (String) -> Unit,
        values: List<String>,
        modifier: Modifier = Modifier.fillMaxWidth(),
        @StringRes titleId: Int,
        @StringRes summaryId: Int,
        icon: @Composable () -> Unit,
        type: ListPreferenceType = ListPreferenceType.ALERT_DIALOG,
        valueToText: (String) -> String
    ) = item(key = key, contentType = "ListPreference") {
        ListPreference(
            value = value,
            onValueChange = onValueChange,
            values = values,
            modifier = modifier,
            title = { Text(text = stringResource(titleId)) },
            summary = { Text(stringResource(summaryId)) },
            icon = icon,
            type = type,
            valueToText = { AnnotatedString(valueToText(it)) })
    }

    private fun Modifier.preferenceModifier(
        surface: Color,
        shape: Shape,
    ): Modifier = padding(horizontal = 8.dp).background(color = surface, shape = shape).clip(shape)

    @Composable
    private fun StatusCard(isActivated: Boolean) {
        val bgColor = if (isActivated) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
        val contentColor = if (isActivated) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        val iconRes = if (isActivated) R.mipmap.ic_success else R.mipmap.ic_warn
        val statusTextRes = if (isActivated) R.string.module_is_activated else R.string.module_not_activated

        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
                .background(color = bgColor, shape = RoundedCornerShape(24.dp))
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 25.dp, end = 5.dp)
                    .size(25.dp),
                colorFilter = ColorFilter.tint(contentColor)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = statusTextRes),
                    color = contentColor,
                    fontSize = 20.sp,
                    maxLines = 1
                )

                Text(
                    text = stringResource(id = R.string.module_version),
                    color = contentColor,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }
    }
}
