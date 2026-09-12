package com.anyflow.journey.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anyflow.journey.data.DailyPracticeSummary
import com.anyflow.journey.data.ProgramSummary
import com.anyflow.journey.ui.*

/**
 * Tab Programs — "Grow with structure": daily practice untuk semua orang,
 * program yang sedang diikuti, dan katalog BROWSE per kategori
 * (badge "Coming soon" untuk status coming_soon).
 */
@Composable
fun ProgramsScreen(
    vm: JourneyViewModel,
    onOpenProgram: (Int) -> Unit,
    onOpenDay: (Int, Int, String?) -> Unit,
) {
    LaunchedEffect(Unit) { if (vm.programs == null) vm.refreshPrograms() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Text(
            text = vm.programs?.headline ?: "Grow with structure",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Daily practice, guided programs, and small steps that add up.",
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(Modifier.height(20.dp))

        when {
            vm.programsLoading && vm.programs == null -> LoadingNote("Loading programs…")
            vm.programs == null && vm.programsError != null ->
                ErrorNote(vm.programsError!!, onRetry = { vm.refreshPrograms() })
            vm.programs == null -> LoadingNote("Loading programs…")
            else -> {
                val data = vm.programs!!

                SectionLabel("Your daily practice")
                Spacer(Modifier.height(10.dp))
                DailyPracticeRow(
                    practice = data.daily_practice,
                    onOpenDay = onOpenDay,
                    onOpenProgram = onOpenProgram,
                )

                Spacer(Modifier.height(22.dp))
                SectionLabel("My programs")
                Spacer(Modifier.height(10.dp))

                if (data.my_programs.isEmpty()) {
                    EmptyNote("You have not joined a program yet — pick one below.")
                } else {
                    data.my_programs.forEach { enrolled ->
                        MyProgramCard(
                            item = enrolled,
                            onOpen = { onOpenProgram(enrolled.program?.id ?: 0) },
                            onContinue = {
                                onOpenDay(
                                    enrolled.program?.id ?: 0,
                                    enrolled.current_day,
                                    enrolled.program?.name,
                                )
                            },
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Spacer(Modifier.height(10.dp))
                SectionLabel("Browse")
                Spacer(Modifier.height(10.dp))

                if (data.browse.isEmpty()) {
                    EmptyNote("No programs published yet.")
                } else {
                    data.browse.forEach { category ->
                        Text(
                            text = category.category.orEmpty(),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Spacer(Modifier.height(10.dp))

                        category.programs.forEach { program ->
                            BrowseProgramCard(program = program, onOpen = { onOpenProgram(program.id) })
                            Spacer(Modifier.height(12.dp))
                        }

                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyPracticeRow(
    practice: DailyPracticeSummary?,
    onOpenDay: (Int, Int, String?) -> Unit,
    onOpenProgram: (Int) -> Unit,
) {
    JourneyCard {
        if (practice == null) {
            Text(
                text = "No daily practice published yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Brand.Muted,
            )
            return@JourneyCard
        }

        Text(text = practice.name ?: "Daily practice", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            text = listOfNotNull(
                "Day ${practice.today?.day?.day_number ?: practice.today?.completed_days ?: 1} of ${practice.duration_days}",
                practice.today?.day?.title,
            ).joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(Modifier.height(12.dp))
        DividerLine()
        Spacer(Modifier.height(12.dp))

        val steps = practice.today?.steps.orEmpty()
        if (steps.isEmpty()) {
            Text(text = "No steps today.", style = MaterialTheme.typography.bodyMedium, color = Brand.Muted)
        } else {
            steps.forEach { step ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp),
                ) {
                    Text(text = step.label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    if (step.done) DoneBadge() else Text(text = "Open", style = MaterialTheme.typography.labelMedium, color = Brand.Muted)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        val dayNumber = practice.today?.day?.day_number ?: 1
        if (practice.today?.completed == true) {
            DoneBadge("Today's practice complete")
        } else {
            WineButton(
                text = "Continue today's practice",
                onClick = { onOpenDay(practice.id, dayNumber, practice.name) },
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "About this program",
            style = MaterialTheme.typography.labelMedium,
            color = Brand.Wine,
            modifier = Modifier.clickable { onOpenProgram(practice.id) },
        )
    }
}

@Composable
private fun BrowseProgramCard(program: ProgramSummary, onOpen: () -> Unit) {
    val comingSoon = program.status == "coming_soon"

    JourneyCard(modifier = if (comingSoon) Modifier else Modifier.clickable { onOpen() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = program.name.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            if (comingSoon) ComingSoonBadge()
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "${program.duration_days} days · ${program.days_count} sessions",
            style = MaterialTheme.typography.bodySmall,
        )

        if (!program.description.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(text = program.description, style = MaterialTheme.typography.bodyMedium)
        }

        if (!comingSoon) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "See what's inside",
                style = MaterialTheme.typography.labelMedium,
                color = Brand.Wine,
            )
        }
    }
}

/** Header layar detail (di luar tab) dengan tombol back. */
@Composable
fun DetailTopBar(title: String?, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Brand.Wine,
            )
        }
        Text(
            text = title.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
            color = Brand.Muted,
            modifier = Modifier.weight(1f),
        )
    }
}
