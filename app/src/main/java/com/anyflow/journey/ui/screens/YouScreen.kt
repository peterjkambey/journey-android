package com.anyflow.journey.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anyflow.journey.BuildConfig
import com.anyflow.journey.ui.*

/**
 * Tab You — "Your journey": 4 statistik, "Noticed by your journey",
 * timeline pertumbuhan (insight: answered prayer, recurring theme, milestone),
 * dan pintu keluar (sign out).
 */
@Composable
fun YouScreen(vm: JourneyViewModel, onSignOut: () -> Unit) {
    LaunchedEffect(Unit) { if (vm.meSummary == null) vm.refreshMe() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vm.meSummary?.headline ?: "Your journey",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(text = vm.userName, style = MaterialTheme.typography.bodySmall)
            }
            AvatarBadge(vm.userInitials, size = 42.dp)
        }

        Spacer(Modifier.height(18.dp))

        when {
            vm.meLoading && vm.meSummary == null -> LoadingNote("Loading your journey…")
            vm.meSummary == null && vm.meError != null -> ErrorNote(vm.meError!!, onRetry = { vm.refreshMe() })
            vm.meSummary == null -> LoadingNote("Loading your journey…")
            else -> {
                val stats = vm.meSummary!!.stats

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile(value = stats.streak.toString(), label = "Streak", modifier = Modifier.weight(1f))
                    StatTile(value = stats.check_ins.toString(), label = "Check-ins", modifier = Modifier.weight(1f))
                    StatTile(value = stats.circles.toString(), label = "Circles", modifier = Modifier.weight(1f))
                    StatTile(value = stats.prayers.toString(), label = "Prayers", modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(22.dp))

                val noticed = vm.meSummary!!.noticed
                if (!noticed?.body.isNullOrBlank()) {
                    JourneyCard {
                        SectionLabel(noticed?.title ?: "Noticed by your journey")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = noticed?.body.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    Spacer(Modifier.height(22.dp))
                }

                SectionLabel(vm.meSummary!!.timeline_headline ?: "Personal growth timeline")
                Spacer(Modifier.height(4.dp))
                Text(
                    text = vm.meSummary!!.timeline_subheadline.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(12.dp))

                if (vm.insights.isEmpty()) {
                    EmptyNote("Keep showing up — insights appear after a few days.")
                } else {
                    vm.insights.forEach { insight ->
                        JourneyCard {
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Text(
                                    text = insight.date_label.orEmpty(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Brand.Wine,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = insight.type_label.orEmpty(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Brand.Muted,
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(text = insight.title.orEmpty(), style = MaterialTheme.typography.titleMedium)
                            if (!insight.body.isNullOrBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(text = insight.body, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Spacer(Modifier.height(10.dp))

                JourneyCard {
                    SectionLabel("Account")
                    Spacer(Modifier.height(8.dp))
                    Text(text = vm.userName, style = MaterialTheme.typography.titleSmall)
                    Text(text = vm.communityName, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(4.dp))
                    Text(text = BuildConfig.API_BASE_URL, style = MaterialTheme.typography.labelSmall, color = Brand.Muted)

                    Spacer(Modifier.height(14.dp))
                    OutlineButton(text = "Sign out", onClick = onSignOut)
                }

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
