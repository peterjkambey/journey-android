package com.anyflow.journey.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anyflow.journey.ui.screens.FeedScreen
import com.anyflow.journey.ui.screens.HomeScreen
import com.anyflow.journey.ui.screens.JournalScreen
import com.anyflow.journey.ui.screens.LoginScreen
import com.anyflow.journey.ui.screens.ProgramDayScreen
import com.anyflow.journey.ui.screens.ProgramDetailScreen
import com.anyflow.journey.ui.screens.ProgramsScreen
import com.anyflow.journey.ui.screens.YouScreen

/** Bottom nav 5 tab sesuai mockup. */
enum class Tab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),
    PROGRAMS("Programs", Icons.Filled.MenuBook),
    JOURNAL("Journal", Icons.Filled.EditNote),
    FEED("Feed", Icons.Filled.Groups),
    YOU("You", Icons.Filled.Person),
}

/** Layar di luar tab (dibuka dari Home / Programs). */
sealed interface Route {
    data class ProgramDetail(val id: Int) : Route
    data class ProgramDay(val programId: Int, val dayNumber: Int, val programName: String?) : Route
}

@Composable
fun AppRoot(initialTab: Tab? = null) {
    val vm: JourneyViewModel = viewModel()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(vm.toast) {
        val message = vm.toast
        if (message != null) {
            vm.consumeToast()
            snackbar.showSnackbar(message)
        }
    }

    if (!vm.loggedIn) {
        LoginScreen(vm)
    } else {
        LaunchedEffect(Unit) { vm.refreshHome() }
        MainShell(vm = vm, startTab = initialTab ?: Tab.HOME, snackbar = snackbar)
    }
}

@Composable
private fun MainShell(
    vm: JourneyViewModel,
    startTab: Tab,
    snackbar: SnackbarHostState,
) {
    var tab by remember { mutableStateOf(startTab) }
    var route by remember { mutableStateOf<Route?>(null) }

    BackHandler(enabled = route != null) { route = null }

    val openDay: (Int, Int, String?) -> Unit = { programId, dayNumber, name ->
        route = Route.ProgramDay(programId, dayNumber, name)
    }

    Scaffold(
        containerColor = Brand.Canvas,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (route == null) {
                JourneyBottomNav(current = tab, onSelect = { tab = it })
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val current = route) {
                is Route.ProgramDetail -> ProgramDetailScreen(
                    vm = vm,
                    programId = current.id,
                    onOpenDay = { dayNumber -> openDay(current.id, dayNumber, vm.programDetail?.name) },
                    onBack = { route = null; vm.clearProgramDetail() },
                )

                is Route.ProgramDay -> ProgramDayScreen(
                    vm = vm,
                    programId = current.programId,
                    dayNumber = current.dayNumber,
                    onBack = { route = null; vm.clearProgramDay() },
                )

                null -> when (tab) {
                    Tab.HOME -> HomeScreen(
                        vm = vm,
                        onOpenProgram = { route = Route.ProgramDetail(it) },
                        onOpenDay = openDay,
                    )

                    Tab.PROGRAMS -> ProgramsScreen(
                        vm = vm,
                        onOpenProgram = { route = Route.ProgramDetail(it) },
                        onOpenDay = openDay,
                    )

                    Tab.JOURNAL -> JournalScreen(vm = vm)
                    Tab.FEED -> FeedScreen(vm = vm)
                    Tab.YOU -> YouScreen(
                        vm = vm,
                        onSignOut = {
                            tab = Tab.HOME
                            vm.signOut()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun JourneyBottomNav(current: Tab, onSelect: (Tab) -> Unit) {
    NavigationBar(containerColor = Brand.Paper, tonalElevation = 3.dp) {
        Tab.entries.forEach { target ->
            NavigationBarItem(
                selected = current == target,
                onClick = { onSelect(target) },
                icon = { Icon(target.icon, contentDescription = target.label) },
                label = { androidx.compose.material3.Text(target.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Brand.Wine,
                    selectedTextColor = Brand.Wine,
                    indicatorColor = Brand.WineSoft,
                    unselectedIconColor = Brand.Muted,
                    unselectedTextColor = Brand.Muted,
                ),
            )
        }
    }
}
