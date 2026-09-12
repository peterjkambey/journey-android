package com.anyflow.journey.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anyflow.journey.data.*
import com.anyflow.journey.ui.*

/**
 * Tab Home — sapaan, TODAY'S PRACTICE (langkah Prayer/Reflection mengembang
 * inline + bottom sheet "Share this moment?"), PRAYER REQUESTS (hati →
 * kartu hilang), banner jumlah yang mendoakan, cuplikan circle, dan kartu
 * program yang sedang diikuti.
 */
@Composable
fun HomeScreen(
    vm: JourneyViewModel,
    onOpenProgram: (Int) -> Unit,
    onOpenDay: (Int, Int, String?) -> Unit,
) {
    LaunchedEffect(Unit) { if (vm.home == null) vm.refreshHome() }

    var expandedStep by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brand.Canvas)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${greeting()}, ${vm.userFirstName}",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = vm.communityName,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            AvatarBadge(vm.userInitials, size = 42.dp)
        }

        val home = vm.home
        val streak = home?.user?.streak ?: 0
        if (streak > 0) {
            Spacer(Modifier.height(10.dp))
            Surface(shape = RoundedCornerShape(50), color = Brand.GoldSoft) {
                Text(
                    text = "$streak-day streak",
                    color = Brand.Gold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        when {
            vm.homeLoading && home == null -> LoadingNote("Loading your day…")
            home == null && vm.homeError != null -> ErrorNote(vm.homeError!!, onRetry = { vm.refreshHome() })
            home == null -> LoadingNote("Loading your day…")
            else -> {
                TodaysPracticeCard(
                    practice = home.todays_practice,
                    stepBusy = vm.stepBusy,
                    expandedStep = expandedStep,
                    onToggleStep = { key -> expandedStep = if (expandedStep == key) null else key },
                    onMarkStep = { dayId, key -> vm.markStep(dayId, key) },
                    onOpenDay = { dayNumber, name ->
                        home.todays_practice?.program?.id?.let { programId -> onOpenDay(programId, dayNumber, name) }
                    },
                )

                Spacer(Modifier.height(22.dp))

                SectionLabel("Prayer requests")
                Spacer(Modifier.height(10.dp))

                if (home.prayer_requests.isEmpty()) {
                    EmptyNote("You are all caught up — no prayer requests waiting for you.")
                } else {
                    home.prayer_requests.take(3).forEach { request ->
                        PrayerRequestCard(
                            request = request,
                            busy = vm.prayBusy == request.id,
                            onPray = { vm.prayOnHome(request.id) },
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                if (home.prayed_for_you_today > 0) {
                    Surface(shape = RoundedCornerShape(16.dp), color = Brand.WineSoft) {
                        Text(
                            text = "${home.prayed_for_you_today} people prayed for you today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Brand.WineDeep,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }
                    Spacer(Modifier.height(22.dp))
                }

                SectionLabel("From your circles")
                Spacer(Modifier.height(10.dp))

                if (home.circle_activity.isEmpty()) {
                    EmptyNote("Nothing from your circles yet today.")
                } else {
                    home.circle_activity.take(3).forEach { item ->
                        ActivityCard(item)
                        Spacer(Modifier.height(12.dp))
                    }
                }

                if (home.my_programs.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    SectionLabel("My programs")
                    Spacer(Modifier.height(10.dp))

                    home.my_programs.forEach { enrolled ->
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

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TodaysPracticeCard(
    practice: TodaysPractice?,
    stepBusy: String?,
    expandedStep: String?,
    onToggleStep: (String) -> Unit,
    onMarkStep: (Int, String) -> Unit,
    onOpenDay: (Int, String?) -> Unit,
) {
    SectionLabel("Today's practice")
    Spacer(Modifier.height(10.dp))

    JourneyCard {
        val day = practice?.day
        if (practice == null || day == null) {
            Text(
                text = "No daily practice published yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Brand.Muted,
            )
            return@JourneyCard
        }

        val program = practice.program

        Text(text = program?.name ?: "Daily practice", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Day ${day.day_number} of ${day.duration_days} · ${day.title.orEmpty()}",
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(Modifier.height(14.dp))
        DividerLine()
        Spacer(Modifier.height(14.dp))

        if (practice.completed) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DoneBadge("Complete")
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Today's practice complete — see you tomorrow.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Brand.Ink,
                )
            }
        } else {
            practice.steps.forEach { step ->
                val expanded = expandedStep == step.key

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleStep(step.key) },
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = step.label, style = MaterialTheme.typography.titleSmall)
                        }
                        if (step.done) {
                            DoneBadge()
                        } else {
                            Text(
                                text = if (expanded) "Hide" else "Open",
                                style = MaterialTheme.typography.labelMedium,
                                color = Brand.Wine,
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = step.prompt.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                    )

                    if (expanded && !step.done) {
                        Spacer(Modifier.height(12.dp))
                        WineButton(
                            text = "Mark ${step.label.lowercase()} done",
                            loading = stepBusy == step.key,
                            onClick = { onMarkStep(day.id, step.key) },
                        )
                    }

                    Spacer(Modifier.height(14.dp))
                    DividerLine()
                    Spacer(Modifier.height(14.dp))
                }
            }

            Text(
                text = "Open the full day for devotional, prayer prompt and reflection.",
                style = MaterialTheme.typography.bodySmall,
                color = Brand.Muted,
                modifier = Modifier.clickable { onOpenDay(day.day_number, program?.name) },
            )
        }
    }
}

@Composable
private fun PrayerRequestCard(request: PrayerRequest, busy: Boolean, onPray: () -> Unit) {
    JourneyCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarBadge(request.author?.initials, size = 34.dp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.author?.first_name ?: "Someone",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = listOfNotNull(request.circle?.name, request.time_ago).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(text = request.body.orEmpty(), style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(12.dp))
        OutlineButton(
            text = if (busy) "Praying…" else "Pray · ${request.prayed_count}",
            icon = {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = Brand.Wine,
                    modifier = Modifier.size(18.dp),
                )
            },
            onClick = onPray,
        )
    }
}

@Composable
fun ActivityCard(item: ActivityItem) {
    JourneyCard {
        if (!item.media_url.isNullOrBlank()) {
            MediaBlock(mediaUrl = item.media_url, mediaType = item.media_type)
            Spacer(Modifier.height(12.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarBadge(item.author?.initials, size = 30.dp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.author?.name ?: "Someone",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = listOfNotNull(item.circle?.name, item.time_ago).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(text = item.caption.orEmpty(), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun MyProgramCard(item: MyProgram, onOpen: () -> Unit, onContinue: () -> Unit) {
    val duration = item.duration_days.takeIf { it > 0 } ?: 1
    val fraction = item.completed_days.toFloat() / duration.toFloat()

    JourneyCard(modifier = Modifier.clickable { onOpen() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.program?.name ?: "Program", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Day ${item.current_day} of ${item.duration_days}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = item.progress_label ?: "",
                style = MaterialTheme.typography.titleSmall,
                color = Brand.Wine,
            )
        }

        Spacer(Modifier.height(12.dp))
        ProgressLine(fraction)
        Spacer(Modifier.height(14.dp))

        WineButton(
            text = item.continue_label ?: "Continue Day ${item.current_day}",
            onClick = onContinue,
        )
    }
}

/** Bottom sheet "Share this moment?" — dirender di shell supaya bisa muncul
 *  dari Home maupun dari layar hari. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareMomentSheet(vm: JourneyViewModel, prompt: SharePrompt, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    var caption by remember { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            vm.shareMoment(uri = uri, caption = caption, dayId = prompt.dayId) { ok -> if (ok) onDismiss() }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Brand.Paper,
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(text = prompt.message, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            Text(text = prompt.hint, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption (optional)") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))
            WineButton(
                text = "Add photo/video",
                loading = vm.momentBusy,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo),
                    )
                },
            )

            Spacer(Modifier.height(10.dp))
            OutlineButton(
                text = "Skip for now",
                modifier = Modifier.fillMaxWidth(),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = Brand.Wine,
                        modifier = Modifier.size(16.dp),
                    )
                },
                onClick = onDismiss,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

/** "Good morning" / afternoon / evening sesuai jam perangkat. */
private fun greeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }
}
