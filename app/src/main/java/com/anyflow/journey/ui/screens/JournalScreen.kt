package com.anyflow.journey.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anyflow.journey.data.JournalEntry
import com.anyflow.journey.ui.*
import kotlin.math.roundToInt

/**
 * Tab Journal — "Prayers & entries" dengan filter All / Prayer / Journal.
 * Mockup hanya menampilkan daftar (read-only); tombol tulis (FAB) ditambahkan
 * karena API menyediakan POST /journal (lubang mockup → keputusan user).
 */
@Composable
fun JournalScreen(vm: JourneyViewModel) {
    LaunchedEffect(Unit) { if (vm.journal == null) vm.refreshJournal() }

    var showForm by remember { mutableStateOf(false) }

    if (showForm) {
        JournalFormSheet(vm = vm, onDismiss = { showForm = false })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Text(
                text = vm.journal?.headline ?: "Prayers & entries",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "What you prayed, what you noticed, what changed.",
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(Modifier.height(16.dp))

            val counts = vm.journal?.counts
            PillTabs(
                options = listOf(
                    "All" to counts?.all,
                    "Prayer" to counts?.prayer,
                    "Journal" to counts?.journal,
                ),
                selected = vm.journalFilter.replaceFirstChar { it.uppercase() },
                onSelect = { vm.selectJournalFilter(it.lowercase()) },
            )

            Spacer(Modifier.height(16.dp))

            when {
                vm.journalLoading && vm.journal == null -> LoadingNote("Loading your entries…")
                vm.journal == null && vm.journalError != null ->
                    ErrorNote(vm.journalError!!, onRetry = { vm.refreshJournal() })
                vm.journal == null -> LoadingNote("Loading your entries…")
                else -> {
                    val entries = vm.journal!!.entries
                    if (entries.isEmpty()) {
                        EmptyNote("No entries yet. Tap the pen to write your first one.")
                    } else {
                        entries.forEach { entry ->
                            JournalEntryCard(entry)
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }

        FloatingActionButton(
            onClick = { showForm = true },
            containerColor = Brand.Wine,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Write an entry")
        }
    }
}

@Composable
private fun JournalEntryCard(entry: JournalEntry) {
    JourneyCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = entry.date_label.orEmpty(),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            EntryScaleBadge(text = "${entry.scale}/10")
        }

        Spacer(Modifier.height(4.dp))
        Text(text = entry.meta_label.orEmpty(), style = MaterialTheme.typography.bodySmall)

        Spacer(Modifier.height(10.dp))
        Text(text = entry.text.orEmpty(), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun EntryScaleBadge(text: String) {
    androidx.compose.material3.Surface(shape = RoundedCornerShape(50), color = Brand.WineSoft) {
        Text(
            text = text,
            color = Brand.WineDeep,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
        )
    }
}

/** Form tulis entri: type, theme, text, scale 1-10 → POST /journal. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalFormSheet(vm: JourneyViewModel, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    var type by remember { mutableStateOf("journal") }
    var theme by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var scale by remember { mutableStateOf(5) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Brand.Paper,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(text = "New entry", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Write it down — it becomes part of your growth timeline.",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(Modifier.height(16.dp))
            PillTabs(
                options = listOf("Prayer" to null, "Journal" to null),
                selected = type.replaceFirstChar { it.uppercase() },
                onSelect = { type = it.lowercase() },
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = theme,
                onValueChange = { theme = it },
                label = { Text("Theme (optional)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("What is on your heart?") },
                minLines = 4,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))
            Text(text = "How are you feeling? $scale/10", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = scale.toFloat(),
                onValueChange = { scale = it.roundToInt() },
                valueRange = 1f..10f,
                steps = 8,
            )

            Spacer(Modifier.height(16.dp))
            WineButton(
                text = "Save entry",
                loading = vm.journalSaving,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    vm.saveJournalEntry(type = type, theme = theme, text = text, scale = scale) { ok ->
                        if (ok) onDismiss()
                    }
                },
            )

            Spacer(Modifier.height(10.dp))
            OutlineButton(
                text = "Cancel",
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismiss,
            )

            Spacer(Modifier.height(28.dp))
        }
    }
}
