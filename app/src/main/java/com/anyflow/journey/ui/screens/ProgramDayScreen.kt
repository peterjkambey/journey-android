package com.anyflow.journey.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anyflow.journey.ui.*

/**
 * Layar hari: header "<PROGRAM> · DAY N OF M", judul hari, DEVOTIONAL,
 * PRAYER PROMPT, REFLECTION, dan tombol "Mark day complete".
 */
@Composable
fun ProgramDayScreen(
    vm: JourneyViewModel,
    programId: Int,
    dayNumber: Int,
    onBack: () -> Unit,
) {
    LaunchedEffect(programId, dayNumber) { vm.openProgramDay(programId, dayNumber) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        DetailTopBar(title = vm.programDay?.header ?: "Day $dayNumber", onBack = onBack)

        val day = vm.programDay

        when {
            vm.programDayLoading && day == null -> LoadingNote("Loading the day…")
            day == null && vm.programDayError != null ->
                ErrorNote(vm.programDayError!!, onRetry = { vm.openProgramDay(programId, dayNumber) })
            day == null -> LoadingNote("Loading the day…")
            else -> {
                SectionLabel(day.header.orEmpty())
                Spacer(Modifier.height(8.dp))
                Text(text = day.title.orEmpty(), style = MaterialTheme.typography.headlineSmall)

                if (day.day_complete) {
                    Spacer(Modifier.height(10.dp))
                    DoneBadge("Day complete")
                }

                Spacer(Modifier.height(18.dp))

                if (!day.devotional.isNullOrBlank()) {
                    SectionLabel("Devotional")
                    Spacer(Modifier.height(6.dp))
                    Text(text = day.devotional, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(18.dp))
                }

                if (!day.prayer_prompt.isNullOrBlank()) {
                    SectionLabel("Prayer prompt")
                    Spacer(Modifier.height(6.dp))
                    JourneyCard(padding = 16.dp) {
                        Text(text = day.prayer_prompt, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.height(18.dp))
                }

                if (!day.reflection_prompt.isNullOrBlank()) {
                    SectionLabel("Reflection")
                    Spacer(Modifier.height(6.dp))
                    JourneyCard(padding = 16.dp) {
                        Text(text = day.reflection_prompt, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.height(22.dp))
                }

                val steps = day.steps
                if (steps != null) {
                    Text(
                        text = "Prayer: ${if (steps.prayer) "done" else "to do"} · Reflection: ${if (steps.reflection) "done" else "to do"}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(12.dp))
                }

                if (day.day_complete) {
                    DoneBadge("Today's practice complete — see you tomorrow.")
                } else {
                    WineButton(
                        text = "Mark day complete",
                        loading = vm.stepBusy == "day",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { vm.markStep(day.id, "day") },
                    )
                }

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
