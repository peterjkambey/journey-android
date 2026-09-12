package com.anyflow.journey.data

/**
 * DTO JSON API Journey v1 (Laravel — journey.anyflow.site/api/v1).
 *
 * Semua respons dibungkus `{ "data": ... }` → [Envelope].
 * Nama properti sengaja mengikuti snake_case JSON supaya tidak perlu anotasi
 * @SerializedName satu per satu. Field yang bisa null di server diketik
 * nullable (Gson tidak menjalankan default value konstruktor).
 */

data class Envelope<T>(val data: T)

data class MessageData(val message: String?)

/** Data yang dikirim saat POST /journal. */
data class JournalCreateRequest(
    val type: String,
    val theme: String?,
    val text: String,
    val scale: Int,
)

// ------------------------------------------------------------------ Auth ---

data class LoginRequest(
    val email: String,
    val password: String,
    val device_name: String = "journey-android",
)

data class AuthUser(
    val id: Int = 0,
    val name: String? = null,
    val email: String? = null,
    val initials: String? = null,
    val whatsapp_number: String? = null,
    val start_date: String? = null,
    val client: String? = null,
    val community: String? = null,
)

data class LoginData(val token: String, val user: AuthUser)

// ------------------------------------------------------------------ Home ---

data class HomeUser(
    val id: Int = 0,
    val name: String? = null,
    val first_name: String? = null,
    val initials: String? = null,
    val streak: Int = 0,
)

data class CommunityInfo(val name: String? = null, val circles_count: Int = 0)

data class CommunityHeader(val name: String? = null, val description: String? = null)

data class Author(
    val id: Int = 0,
    val name: String? = null,
    val first_name: String? = null,
    val initials: String? = null,
)

data class ProgramRef(
    val id: Int = 0,
    val name: String? = null,
    val category: String? = null,
    val is_daily_practice: Boolean = false,
    val duration_days: Int = 0,
)

data class PracticeDay(
    val id: Int = 0,
    val day_number: Int = 0,
    val duration_days: Int = 0,
    val title: String? = null,
    val devotional: String? = null,
    val prayer_prompt: String? = null,
    val reflection_prompt: String? = null,
)

data class PracticeStep(
    val key: String = "",
    val label: String = "",
    val prompt: String? = null,
    val done: Boolean = false,
)

data class TodaysPractice(
    val program: ProgramRef? = null,
    val day: PracticeDay? = null,
    val steps: List<PracticeStep> = emptyList(),
    val completed: Boolean = false,
    val completed_days: Int = 0,
    val started_at: String? = null,
)

data class CircleRef(val id: Int = 0, val name: String? = null, val city: String? = null)

data class PrayerRequest(
    val id: Int = 0,
    val body: String? = null,
    val author: Author? = null,
    val circle: CircleRef? = null,
    val prayed_count: Int = 0,
    val prayed_by_me: Boolean = false,
    val is_answered: Boolean = false,
    val answered_note: String? = null,
    val created_at: String? = null,
    val time_ago: String? = null,
)

data class ActivityItem(
    val id: String = "",
    val type: String? = null,
    val caption: String? = null,
    val media_url: String? = null,
    val media_type: String? = null,
    val author: Author? = null,
    val circle: CircleRef? = null,
    val occurred_at: String? = null,
    val time_ago: String? = null,
)

data class MyProgram(
    val program: ProgramRef? = null,
    val completed_days: Int = 0,
    val duration_days: Int = 0,
    val current_day: Int = 0,
    val progress_label: String? = null,
    val continue_label: String? = null,
    val started_at: String? = null,
)

data class Circle(
    val id: Int = 0,
    val name: String? = null,
    val city: String? = null,
    val description: String? = null,
    val members_count: Int = 0,
    val is_joined: Boolean = false,
    val is_discoverable: Boolean = false,
)

data class HomeData(
    val user: HomeUser? = null,
    val community: CommunityInfo? = null,
    val todays_practice: TodaysPractice? = null,
    val prayer_requests: List<PrayerRequest> = emptyList(),
    val prayed_for_you_today: Int = 0,
    val circle_activity: List<ActivityItem> = emptyList(),
    val my_programs: List<MyProgram> = emptyList(),
    val my_circles: List<Circle> = emptyList(),
)

// -------------------------------------------------------------- Programs ---

data class ProgramSummary(
    val id: Int = 0,
    val name: String? = null,
    val category: String? = null,
    val description: String? = null,
    val duration_days: Int = 0,
    val status: String? = null,
    val is_daily_practice: Boolean = false,
    val days_count: Int = 0,
)

data class DailyPracticeSummary(
    val id: Int = 0,
    val name: String? = null,
    val category: String? = null,
    val description: String? = null,
    val duration_days: Int = 0,
    val status: String? = null,
    val is_daily_practice: Boolean = true,
    val days_count: Int = 0,
    val today: TodaysPractice? = null,
)

data class BrowseCategory(val category: String? = null, val programs: List<ProgramSummary> = emptyList())

data class ProgramsData(
    val headline: String? = null,
    val daily_practice: DailyPracticeSummary? = null,
    val my_programs: List<MyProgram> = emptyList(),
    val browse: List<BrowseCategory> = emptyList(),
)

data class WhatsInside(
    val id: Int = 0,
    val day_number: Int = 0,
    val title: String? = null,
    val completed: Boolean = false,
)

data class ProgramDetail(
    val id: Int = 0,
    val name: String? = null,
    val category: String? = null,
    val description: String? = null,
    val duration_days: Int = 0,
    val status: String? = null,
    val is_daily_practice: Boolean = false,
    val days_count: Int = 0,
    val enrolled: Boolean = false,
    val started_at: String? = null,
    val completed_days: Int = 0,
    val current_day: Int = 0,
    val continue_label: String? = null,
    val whats_inside: List<WhatsInside> = emptyList(),
)

data class StepStatus(val prayer: Boolean = false, val reflection: Boolean = false)

data class ProgramDayDetail(
    val program: ProgramRef? = null,
    val header: String? = null,
    val id: Int = 0,
    val day_number: Int = 0,
    val title: String? = null,
    val devotional: String? = null,
    val prayer_prompt: String? = null,
    val reflection_prompt: String? = null,
    val steps: StepStatus? = null,
    val day_complete: Boolean = false,
)

data class CompleteData(
    val program_day_id: Int = 0,
    val steps: StepStatus? = null,
    val day_complete: Boolean = false,
    val share_prompt: Boolean = false,
    val share_message: String? = null,
    val share_hint: String? = null,
    val completed_label: String? = null,
)

data class EnrollData(
    val program_id: Int = 0,
    val enrolled: Boolean = false,
    val started_at: String? = null,
    val message: String? = null,
)

// --------------------------------------------------------------- Journal ---

data class JournalCounts(val all: Int = 0, val prayer: Int = 0, val journal: Int = 0)

data class JournalEntry(
    val id: Int = 0,
    val type: String? = null,
    val theme: String? = null,
    val text: String? = null,
    val scale: Int = 5,
    val note: String? = null,
    val date: String? = null,
    val date_label: String? = null,
    val meta_label: String? = null,
    val program_day_id: Int? = null,
    val created_at: String? = null,
)

data class JournalData(
    val headline: String? = null,
    val active_filter: String? = null,
    val counts: JournalCounts = JournalCounts(),
    val entries: List<JournalEntry> = emptyList(),
)

// ------------------------------------------------------------------ Feed ---

data class FeedData(
    val scope: String? = null,
    val tab: String? = null,
    val community: CommunityHeader? = null,
    val prayer_requests: List<PrayerRequest> = emptyList(),
    val activity: List<ActivityItem> = emptyList(),
)

data class CirclesData(
    val headline: String? = null,
    val subheadline: String? = null,
    val joined: List<Circle> = emptyList(),
    val discover: List<Circle> = emptyList(),
)

data class CircleActionData(val circle: Circle? = null, val message: String? = null)

data class PrayData(
    val prayer_request: PrayerRequest? = null,
    val prayed_by_me: Boolean = false,
    val message: String? = null,
)

data class PrayerRequestCreate(val body: String, val circle_id: Int? = null)

// ------------------------------------------------------------------- You ---

data class MeStats(
    val streak: Int = 0,
    val check_ins: Int = 0,
    val circles: Int = 0,
    val prayers: Int = 0,
)

data class Noticed(val title: String? = null, val body: String? = null)

data class MeSummaryData(
    val headline: String? = null,
    val stats: MeStats = MeStats(),
    val noticed: Noticed? = null,
    val timeline_headline: String? = null,
    val timeline_subheadline: String? = null,
    val circles: List<Circle> = emptyList(),
)

data class InsightItem(
    val id: Int = 0,
    val type: String? = null,
    val title: String? = null,
    val body: String? = null,
    val date: String? = null,
    val date_label: String? = null,
    val type_label: String? = null,
)

data class InsightsData(val insights: List<InsightItem> = emptyList())

// ---------------------------------------------------------------- Moment ---

data class MomentData(
    val id: Int = 0,
    val caption: String? = null,
    val media_url: String? = null,
    val media_type: String? = null,
    val circle: CircleRef? = null,
    val shared_at: String? = null,
    val message: String? = null,
)
