package com.anyflow.journey.ui

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.anyflow.journey.JourneyApp
import com.anyflow.journey.data.*
import kotlinx.coroutines.launch

/** Data yang dibutuhkan bottom sheet "Share this moment?". */
data class SharePrompt(
    val dayId: Int,
    val message: String,
    val hint: String,
)

/**
 * Satu ViewModel untuk seluruh tab: menahan state Home / Programs / Journal /
 * Feed / You supaya pindah tab tidak memuat ulang dari nol.
 */
class JourneyViewModel(application: Application) : AndroidViewModel(application) {

    private val session: SessionStore = JourneyApp.session
    private val repo = JourneyRepository(application.applicationContext, session)

    // ------------------------------------------------------------- sesi ---
    var loggedIn by mutableStateOf(session.isLoggedIn())
        private set
    var authBusy by mutableStateOf(false)
        private set
    var authError by mutableStateOf<String?>(null)
        private set

    val userName: String get() = session.name ?: "Friend"
    val userFirstName: String get() = (session.name ?: "Friend").substringBefore(' ')
    val userInitials: String get() = session.initials ?: initialsOf(session.name)
    val communityName: String get() = session.community ?: "Journey"

    /** Pesan sekali-pakai untuk Snackbar. */
    var toast by mutableStateOf<String?>(null)

    fun consumeToast() {
        toast = null
    }

    // ------------------------------------------------------------- Home ---
    var home by mutableStateOf<HomeData?>(null)
        private set
    var homeLoading by mutableStateOf(false)
        private set
    var homeError by mutableStateOf<String?>(null)
        private set
    var stepBusy by mutableStateOf<String?>(null)
        private set
    var prayBusy by mutableStateOf<Int?>(null)
        private set
    var sharePrompt by mutableStateOf<SharePrompt?>(null)
        private set
    var momentBusy by mutableStateOf(false)
        private set

    // --------------------------------------------------------- Programs ---
    var programs by mutableStateOf<ProgramsData?>(null)
        private set
    var programsLoading by mutableStateOf(false)
        private set
    var programsError by mutableStateOf<String?>(null)
        private set

    var programDetail by mutableStateOf<ProgramDetail?>(null)
        private set
    var programDetailLoading by mutableStateOf(false)
        private set
    var programDetailError by mutableStateOf<String?>(null)
        private set
    var enrollBusy by mutableStateOf(false)
        private set

    var programDay by mutableStateOf<ProgramDayDetail?>(null)
        private set
    var programDayLoading by mutableStateOf(false)
        private set
    var programDayError by mutableStateOf<String?>(null)
        private set

    // ---------------------------------------------------------- Journal ---
    var journalFilter by mutableStateOf("all")
        private set
    var journal by mutableStateOf<JournalData?>(null)
        private set
    var journalLoading by mutableStateOf(false)
        private set
    var journalError by mutableStateOf<String?>(null)
        private set
    var journalSaving by mutableStateOf(false)
        private set

    // ------------------------------------------------------------- Feed ---
    var feedScope by mutableStateOf("circles")
        private set
    var feedTab by mutableStateOf("prayer")
        private set
    var feed by mutableStateOf<FeedData?>(null)
        private set
    var feedLoading by mutableStateOf(false)
        private set
    var feedError by mutableStateOf<String?>(null)
        private set
    var feedPrayBusy by mutableStateOf<Int?>(null)
        private set

    var circles by mutableStateOf<CirclesData?>(null)
        private set
    var circlesLoading by mutableStateOf(false)
        private set
    var circlesError by mutableStateOf<String?>(null)
        private set
    var circlesPanelOpen by mutableStateOf(false)
        private set
    var circleBusy by mutableStateOf<Int?>(null)
        private set

    // -------------------------------------------------------------- You ---
    var meSummary by mutableStateOf<MeSummaryData?>(null)
        private set
    var insights by mutableStateOf<List<InsightItem>>(emptyList())
        private set
    var meLoading by mutableStateOf(false)
        private set
    var meError by mutableStateOf<String?>(null)
        private set

    // -------------------------------------------------------------- auth ---
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            authError = "Enter your email and password."
            return
        }
        authBusy = true
        authError = null
        viewModelScope.launch {
            try {
                repo.login(email.trim(), password)
                loggedIn = true
                refreshHome()
                refreshPrograms()
                refreshMe()
            } catch (e: Exception) {
                authError = friendlyError(e)
            } finally {
                authBusy = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repo.logout()
            home = null
            programs = null
            programDetail = null
            programDay = null
            journal = null
            feed = null
            circles = null
            meSummary = null
            insights = emptyList()
            circlesPanelOpen = false
            loggedIn = false
        }
    }

    // -------------------------------------------------------------- home ---
    fun refreshHome() {
        if (homeLoading) return
        homeLoading = true
        homeError = null
        viewModelScope.launch {
            try {
                home = repo.home()
            } catch (e: Exception) {
                homeError = friendlyError(e)
            } finally {
                homeLoading = false
            }
        }
    }

    /** Satu langkah latihan harian selesai (prayer / reflection / day). */
    fun markStep(dayId: Int, step: String) {
        if (stepBusy != null) return
        stepBusy = step
        viewModelScope.launch {
            try {
                val result = repo.completeDay(dayId, step)
                applyStepResult(dayId, result)
                if (result.share_prompt) {
                    sharePrompt = SharePrompt(
                        dayId = dayId,
                        message = result.share_message ?: "Share this moment?",
                        hint = result.share_hint ?: "Let your circle know you showed up today.",
                    )
                }
                if (result.day_complete) {
                    refreshPrograms()
                    refreshMe()
                }
            } catch (e: Exception) {
                toast = friendlyError(e)
            } finally {
                stepBusy = null
            }
        }
    }

    fun dismissSharePrompt() {
        sharePrompt = null
    }

    fun shareMoment(uri: Uri, caption: String, dayId: Int?, onDone: (Boolean) -> Unit) {
        momentBusy = true
        viewModelScope.launch {
            try {
                val result = repo.shareMoment(
                    uri = uri,
                    caption = caption.ifBlank { null },
                    programDayId = dayId,
                    circleId = home?.my_circles?.firstOrNull()?.id,
                )
                toast = result.message ?: "Your moment is shared with your circle."
                sharePrompt = null
                refreshHome()
                if (feed != null) refreshFeed()
                onDone(true)
            } catch (e: Exception) {
                toast = friendlyError(e)
                onDone(false)
            } finally {
                momentBusy = false
            }
        }
    }

    /** Hati di Home: sudah didoakan → kartu langsung hilang dari daftar. */
    fun prayOnHome(requestId: Int) {
        if (prayBusy != null) return
        prayBusy = requestId
        viewModelScope.launch {
            try {
                val result = repo.pray(requestId)
                if (result.prayed_by_me) {
                    home = home?.let { current ->
                        current.copy(prayer_requests = current.prayer_requests.filterNot { it.id == requestId })
                    }
                }
                val updated = result.prayer_request
                if (updated != null) {
                    feed = feed?.let { f ->
                        f.copy(prayer_requests = f.prayer_requests.map { if (it.id == updated.id) updated else it })
                    }
                }
                toast = result.message
            } catch (e: Exception) {
                toast = friendlyError(e)
            } finally {
                prayBusy = null
            }
        }
    }

    private fun applyStepResult(dayId: Int, result: CompleteData) {
        val status = result.steps ?: StepStatus()

        home = home?.let { current ->
            val practice = current.todays_practice
            if (practice?.day?.id != dayId) {
                current
            } else {
                current.copy(
                    todays_practice = practice.copy(
                        steps = practice.steps.map { step ->
                            when (step.key) {
                                "prayer" -> step.copy(done = status.prayer)
                                "reflection" -> step.copy(done = status.reflection)
                                else -> step
                            }
                        },
                        completed = result.day_complete || practice.completed,
                        completed_days = if (result.day_complete) practice.completed_days + 1 else practice.completed_days,
                    ),
                )
            }
        }

        programDay = programDay?.let { current ->
            if (current.id == dayId) {
                current.copy(steps = status, day_complete = result.day_complete || current.day_complete)
            } else {
                current
            }
        }
    }

    // ---------------------------------------------------------- programs ---
    fun refreshPrograms() {
        programsLoading = true
        programsError = null
        viewModelScope.launch {
            try {
                programs = repo.programs()
            } catch (e: Exception) {
                programsError = friendlyError(e)
            } finally {
                programsLoading = false
            }
        }
    }

    fun openProgram(id: Int) {
        programDetailLoading = true
        programDetailError = null
        viewModelScope.launch {
            try {
                programDetail = repo.program(id)
            } catch (e: Exception) {
                programDetailError = friendlyError(e)
            } finally {
                programDetailLoading = false
            }
        }
    }

    fun enrollProgram(id: Int) {
        enrollBusy = true
        viewModelScope.launch {
            try {
                val result = repo.enroll(id)
                toast = result.message
                programDetail = repo.program(id)
                refreshPrograms()
                refreshHome()
            } catch (e: Exception) {
                toast = friendlyError(e)
            } finally {
                enrollBusy = false
            }
        }
    }

    fun openProgramDay(programId: Int, dayNumber: Int) {
        programDayLoading = true
        programDayError = null
        viewModelScope.launch {
            try {
                programDay = repo.programDay(programId, dayNumber)
            } catch (e: Exception) {
                programDayError = friendlyError(e)
            } finally {
                programDayLoading = false
            }
        }
    }

    fun clearProgramDetail() {
        programDetail = null
        programDetailError = null
    }

    fun clearProgramDay() {
        programDay = null
        programDayError = null
    }

    // ----------------------------------------------------------- journal ---
    fun selectJournalFilter(type: String) {
        if (journalFilter == type) return
        journalFilter = type
        refreshJournal()
    }

    fun refreshJournal() {
        if (journalLoading) return
        journalLoading = true
        journalError = null
        viewModelScope.launch {
            try {
                journal = repo.journal(journalFilter)
            } catch (e: Exception) {
                journalError = friendlyError(e)
            } finally {
                journalLoading = false
            }
        }
    }

    fun saveJournalEntry(type: String, theme: String?, text: String, scale: Int, onDone: (Boolean) -> Unit) {
        if (text.isBlank()) {
            toast = "Write something before saving."
            onDone(false)
            return
        }
        journalSaving = true
        viewModelScope.launch {
            try {
                repo.createJournal(type = type, theme = theme?.ifBlank { null }, text = text, scale = scale)
                toast = "Entry saved."
                journalFilter = type
                refreshJournal()
                refreshMe()
                onDone(true)
            } catch (e: Exception) {
                toast = friendlyError(e)
                onDone(false)
            } finally {
                journalSaving = false
            }
        }
    }

    // -------------------------------------------------------------- feed ---
    fun selectFeedScope(scope: String) {
        if (feedScope == scope) return
        feedScope = scope
        refreshFeed()
    }

    fun selectFeedTab(tab: String) {
        if (feedTab == tab) return
        feedTab = tab
    }

    fun refreshFeed() {
        if (feedLoading) return
        feedLoading = true
        feedError = null
        viewModelScope.launch {
            try {
                feed = repo.feed(feedScope, feedTab)
            } catch (e: Exception) {
                feedError = friendlyError(e)
            } finally {
                feedLoading = false
            }
        }
    }

    fun toggleCirclesPanel() {
        circlesPanelOpen = !circlesPanelOpen
        if (circlesPanelOpen && circles == null) refreshCircles()
    }

    fun refreshCircles() {
        circlesLoading = true
        circlesError = null
        viewModelScope.launch {
            try {
                circles = repo.circles()
            } catch (e: Exception) {
                circlesError = friendlyError(e)
            } finally {
                circlesLoading = false
            }
        }
    }

    fun joinCircle(id: Int) {
        circleBusy = id
        viewModelScope.launch {
            try {
                val result = repo.joinCircle(id)
                toast = result.message
                refreshCircles()
                refreshFeed()
                refreshHome()
                refreshMe()
            } catch (e: Exception) {
                toast = friendlyError(e)
            } finally {
                circleBusy = null
            }
        }
    }

    fun leaveCircle(id: Int) {
        circleBusy = id
        viewModelScope.launch {
            try {
                val result = repo.leaveCircle(id)
                toast = result.message
                refreshCircles()
                refreshFeed()
                refreshHome()
                refreshMe()
            } catch (e: Exception) {
                toast = friendlyError(e)
            } finally {
                circleBusy = null
            }
        }
    }

    /** Hati di Feed: toggle Pray for this ↔ Prayed. */
    fun togglePrayInFeed(requestId: Int) {
        if (feedPrayBusy != null) return
        feedPrayBusy = requestId
        viewModelScope.launch {
            try {
                val result = repo.pray(requestId)
                val updated = result.prayer_request
                if (updated != null) {
                    feed = feed?.let { f ->
                        f.copy(prayer_requests = f.prayer_requests.map { if (it.id == updated.id) updated else it })
                    }
                }
                toast = result.message
            } catch (e: Exception) {
                toast = friendlyError(e)
            } finally {
                feedPrayBusy = null
            }
        }
    }

    // --------------------------------------------------------------- you ---
    fun refreshMe() {
        if (meLoading) return
        meLoading = true
        meError = null
        viewModelScope.launch {
            try {
                meSummary = repo.meSummary()
                insights = repo.insights()
            } catch (e: Exception) {
                meError = friendlyError(e)
            } finally {
                meLoading = false
            }
        }
    }

    companion object {
        fun initialsOf(name: String?): String {
            val parts = (name ?: "").trim().split(' ').filter { it.isNotBlank() }
            return when {
                parts.isEmpty() -> "?"
                parts.size == 1 -> parts.first().take(1).uppercase()
                else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
            }
        }
    }
}
