package com.anyflow.journey.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anyflow.journey.data.Circle
import com.anyflow.journey.data.PrayerRequest
import com.anyflow.journey.ui.*

/**
 * Tab Feed — header komunitas, sub-tab Circles / Global Community,
 * sub-tab Prayer requests / Activity, dan panel INLINE
 * "View and manage your circles" (JOINED + DISCOVER).
 */
@Composable
fun FeedScreen(vm: JourneyViewModel) {
    LaunchedEffect(Unit) { if (vm.feed == null) vm.refreshFeed() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Text(
            text = vm.feed?.community?.name ?: vm.communityName,
            style = MaterialTheme.typography.headlineSmall,
        )
        val description = vm.feed?.community?.description
        if (!description.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))

        PillTabs(
            options = listOf("Circles" to null, "Global Community" to null),
            selected = if (vm.feedScope == "circles") "Circles" else "Global Community",
            onSelect = { vm.selectFeedScope(if (it == "Circles") "circles" else "community") },
        )

        Spacer(Modifier.height(10.dp))

        PillTabs(
            options = listOf("Prayer requests" to null, "Activity" to null),
            selected = if (vm.feedTab == "prayer") "Prayer requests" else "Activity",
            onSelect = { vm.selectFeedTab(if (it == "Prayer requests") "prayer" else "activity") },
        )

        Spacer(Modifier.height(14.dp))

        OutlineButton(
            text = if (vm.circlesPanelOpen) "Hide your circles" else "View and manage your circles",
            onClick = { vm.toggleCirclesPanel() },
        )

        if (vm.circlesPanelOpen) {
            Spacer(Modifier.height(12.dp))
            CirclesPanel(vm = vm)
        }

        Spacer(Modifier.height(18.dp))

        when {
            vm.feedLoading && vm.feed == null -> LoadingNote("Loading the feed…")
            vm.feed == null && vm.feedError != null -> ErrorNote(vm.feedError!!, onRetry = { vm.refreshFeed() })
            vm.feed == null -> LoadingNote("Loading the feed…")
            vm.feedTab == "prayer" -> {
                val requests = vm.feed!!.prayer_requests
                if (requests.isEmpty()) {
                    EmptyNote("No prayer requests in this scope right now.")
                } else {
                    requests.forEach { request ->
                        FeedPrayerCard(
                            request = request,
                            busy = vm.feedPrayBusy == request.id,
                            onToggle = { vm.togglePrayInFeed(request.id) },
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            else -> {
                val activity = vm.feed!!.activity
                if (activity.isEmpty()) {
                    EmptyNote("No activity in this scope yet.")
                } else {
                    activity.forEach { item ->
                        ActivityCard(item)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

/** Panel inline (bukan halaman/popup): JOINED + DISCOVER dengan tombol Join. */
@Composable
private fun CirclesPanel(vm: JourneyViewModel) {
    JourneyCard {
        SectionLabel("Joined")
        Spacer(Modifier.height(8.dp))

        val joined = vm.circles?.joined.orEmpty()
        if (vm.circlesLoading && vm.circles == null) {
            LoadingNote("Loading circles…")
        } else if (joined.isEmpty()) {
            EmptyNote("You have not joined a circle yet.")
        } else {
            joined.forEach { circle ->
                CircleRow(
                    circle = circle,
                    actionLabel = "Leave",
                    busy = vm.circleBusy == circle.id,
                    onClick = { vm.leaveCircle(circle.id) },
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        DividerLine()
        Spacer(Modifier.height(14.dp))

        SectionLabel("Discover")
        Spacer(Modifier.height(8.dp))

        val discover = vm.circles?.discover.orEmpty()
        if (discover.isEmpty()) {
            EmptyNote("No new circles to discover right now.")
        } else {
            discover.forEach { circle ->
                CircleRow(
                    circle = circle,
                    actionLabel = "Join",
                    busy = vm.circleBusy == circle.id,
                    onClick = { vm.joinCircle(circle.id) },
                )
            }
        }

        val error = vm.circlesError
        if (error != null) {
            Spacer(Modifier.height(10.dp))
            Text(text = error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun CircleRow(circle: Circle, actionLabel: String, busy: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = circle.name.orEmpty(), style = MaterialTheme.typography.titleSmall)
            Text(
                text = listOfNotNull(circle.city, "${circle.members_count} members").joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        OutlineButton(text = if (busy) "…" else actionLabel, onClick = onClick)
    }
}

@Composable
private fun FeedPrayerCard(request: PrayerRequest, busy: Boolean, onToggle: () -> Unit) {
    JourneyCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarBadge(request.author?.initials, size = 34.dp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.author?.name ?: "Someone",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = listOfNotNull(request.circle?.name, request.time_ago).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (request.is_answered) DoneBadge("Answered")
        }

        Spacer(Modifier.height(10.dp))
        Text(text = request.body.orEmpty(), style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(12.dp))

        val label = if (request.prayed_by_me) {
            if (busy) "Saving…" else "Prayed · ${request.prayed_count}"
        } else {
            if (busy) "Saving…" else "Pray for this · ${request.prayed_count}"
        }

        val icon = @Composable {
            Icon(
                imageVector = if (request.prayed_by_me) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = Brand.Wine,
                modifier = Modifier.size(18.dp),
            )
        }

        if (request.prayed_by_me) {
            OutlineButton(text = label, icon = icon, onClick = onToggle)
        } else {
            WineButton(text = label, loading = busy, onClick = onToggle)
        }
    }
}
