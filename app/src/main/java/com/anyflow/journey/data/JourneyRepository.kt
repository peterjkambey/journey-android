package com.anyflow.journey.data

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException

/**
 * Satu pintu akses API Journey. Semua request memakai Bearer token sesi;
 * error server (422/403/500) diterjemahkan jadi pesan yang bisa dibaca.
 */
class JourneyRepository(
    private val context: Context,
    private val session: SessionStore,
    private val api: JourneyApi = ApiClient.api,
) {
    private val bearer: String
        get() = "Bearer ${session.token.orEmpty()}"

    suspend fun login(email: String, password: String): LoginData {
        val data = api.login(LoginRequest(email = email, password = password)).data
        session.token = data.token
        session.email = data.user.email
        session.name = data.user.name
        session.initials = data.user.initials
        session.community = data.user.community
        return data
    }

    suspend fun logout() {
        runCatching { api.logout(bearer) }
        session.clear()
    }

    suspend fun home(): HomeData = api.home(bearer).data

    suspend fun programs(): ProgramsData = api.programs(bearer).data

    suspend fun program(id: Int): ProgramDetail = api.program(bearer, id).data

    suspend fun enroll(id: Int): EnrollData = api.enroll(bearer, id).data

    suspend fun programDay(programId: Int, dayNumber: Int): ProgramDayDetail =
        api.programDay(bearer, programId, dayNumber).data

    suspend fun completeDay(dayId: Int, step: String): CompleteData =
        api.completeDay(bearer, dayId, mapOf("step" to step)).data

    suspend fun journal(type: String): JournalData = api.journal(bearer, type).data

    suspend fun createJournal(type: String, theme: String?, text: String, scale: Int): JournalEntry =
        api.createJournal(bearer, JournalCreateRequest(type = type, theme = theme, text = text, scale = scale)).data

    suspend fun feed(scope: String, tab: String): FeedData = api.feed(bearer, scope, tab).data

    suspend fun circles(): CirclesData = api.circles(bearer).data

    suspend fun joinCircle(id: Int): CircleActionData = api.joinCircle(bearer, id).data

    suspend fun leaveCircle(id: Int): CircleActionData = api.leaveCircle(bearer, id).data

    suspend fun pray(id: Int): PrayData = api.pray(bearer, id).data

    suspend fun meSummary(): MeSummaryData = api.meSummary(bearer).data

    suspend fun insights(): List<InsightItem> = api.meInsights(bearer).data.insights

    /**
     * Unggah foto/video pendek dari sheet "Share this moment?" ke /moments
     * (multipart). Berkas dibaca dari content Uri hasil PickVisualMedia.
     */
    suspend fun shareMoment(
        uri: Uri,
        caption: String?,
        programDayId: Int?,
        circleId: Int?,
    ): MomentData {
        val resolver = context.contentResolver
        val mime = resolver.getType(uri) ?: "image/jpeg"
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalStateException("That file could not be read.")

        val extension = when {
            mime.startsWith("video") && mime.contains("quicktime") -> "mov"
            mime.startsWith("video") -> "mp4"
            mime.contains("png") -> "png"
            mime.contains("webp") -> "webp"
            else -> "jpg"
        }

        val filePart = MultipartBody.Part.createFormData(
            "media",
            "moment.$extension",
            bytes.toRequestBody(mime.toMediaTypeOrNull()),
        )

        val textType = "text/plain".toMediaTypeOrNull()

        return api.createMoment(
            bearer = bearer,
            media = filePart,
            caption = (caption ?: "").ifBlank { null }?.toRequestBody(textType),
            circleId = circleId?.toString()?.toRequestBody(textType),
            programDayId = programDayId?.toString()?.toRequestBody(textType),
            visibility = "circles".toRequestBody(textType),
        ).data
    }
}

/** Pesan error ramah untuk UI (diambil dari `message` / `errors` Laravel). */
fun friendlyError(error: Throwable): String = when (error) {
    is HttpException -> {
        val raw = runCatching { error.response()?.errorBody()?.string() }.getOrNull()

        val parsed = raw?.let { body ->
            runCatching {
                val json = JSONObject(body)
                when {
                    json.has("errors") -> {
                        val errors = json.getJSONObject("errors")
                        errors.keys().asSequence().firstOrNull()?.let { key ->
                            errors.getJSONArray(key).optString(0)
                        }
                    }
                    json.has("message") -> json.optString("message")
                    else -> null
                }
            }.getOrNull()
        }

        parsed?.takeIf { it.isNotBlank() } ?: "The server answered ${error.code()}. Please try again."
    }

    is java.io.IOException -> "Cannot reach the Journey server. Check your connection and try again."

    else -> error.message ?: "Something went wrong."
}
