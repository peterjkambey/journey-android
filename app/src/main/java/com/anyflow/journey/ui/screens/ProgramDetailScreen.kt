package com.anyflow.journey.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anyflow.journey.ui.*

/**
 * Detail program: durasi + deskripsi, tombol Continue · Day N / Start program
 * (enroll), dan daftar "What's inside" per hari + tanda selesai.
 */
@Composable
fun ProgramDetailScreen(
    vm: JourneyViewModel,
    programId: Int,
    onOpenDay: (Int) -> Unit,
    onBack: () -> Unit,
) {
    LaunchedEffect(programId) { vm.openProgram(programId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        DetailTopBar(title = "Program", onBack = onBack)

        val detail = vm.programDetail

        when {
            vm.programDetailLoading && detail == null -> LoadingNote("Loading program…")
            detail == null && vm.programDetailError != null ->
                ErrorNote(vm.programDetailError!!, onRetry = { vm.openProgram(programId) })
            detail == null -> LoadingNote("Loading program…")
            else -> {
                Text(text = detail.name.orEmpty(), style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = listOfNotNull(
                        detail.category,
                        "${detail.duration_days} days",
                        "${detail.days_count} sessions",
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                )

                if (detail.status == "coming_soon") {
                    Spacer(Modifier.height(8.dp))
                    ComingSoonBadge()
                }

                if (!detail.description.isNullOrBlank()) {
                    Spacer(Modifier.height(14.dp))
                    Text(text = detail.description, style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(Modifier.height(18.dp))

                if (detail.enrolled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${detail.completed_days} of ${detail.duration_days} days complete",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "${detail.completed_days}/${detail.duration_days}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Brand.Wine,
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    ProgressLine(
                        fraction = detail.completed_days.toFloat() / detail.duration_days.coerceAtLeast(1).toFloat(),
                    )
                    Spacer(Modifier.height(16.dp))
                    WineButton(
                        text = detail.continue_label ?: "Continue · Day ${detail.current_day}",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOpenDay(detail.current_day) },
                    )
                } else {
                    WineButton(
                        text = "Start program",
                        loading = vm.enrollBusy,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { vm.enrollProgram(detail.id) },
                    )
                }

                Spacer(Modifier.height(24.dp))
                SectionLabel("What's inside")
                Spacer(Modifier.height(10.dp))

                if (detail.whats_inside.isEmpty()) {
                    EmptyNote("No sessions published for this program yet.")
                } else {
                    detail.whats_inside.forEach { day ->
                        JourneyCard(padding = 14.dp) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Day ${day.day_number}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Brand.Wine,
                                    modifier = Modifier.width(58.dp),
                                )
                                Text(
                                    text = day.title.orEmpty(),
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.weight(1f),
                                )
                                if (day.completed) DoneBadge()
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (detail.enrolled) "Tap Continue to open today's session." else "Start the program to open Day 1.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Brand.Muted,
                        modifier = Modifier.clickable {
                            onOpenDay(if (detail.enrolled) detail.current_day else 1)
                        },
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
