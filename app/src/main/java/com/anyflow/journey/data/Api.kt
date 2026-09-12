package com.anyflow.journey.data

import com.anyflow.journey.BuildConfig
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Kontrak API Journey v1 — semua endpoint butuh Bearer token, kecuali login.
 * Token dikirim manual per request (pola sama dengan hris-android) supaya
 * jelas endpoint mana yang authenticated.
 */
interface JourneyApi {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Envelope<LoginData>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") bearer: String): Envelope<MessageData>

    @GET("me")
    suspend fun me(@Header("Authorization") bearer: String): Envelope<AuthUser>

    @GET("home")
    suspend fun home(@Header("Authorization") bearer: String): Envelope<HomeData>

    @GET("programs")
    suspend fun programs(@Header("Authorization") bearer: String): Envelope<ProgramsData>

    @GET("programs/{id}")
    suspend fun program(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Envelope<ProgramDetail>

    @POST("programs/{id}/enroll")
    suspend fun enroll(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Envelope<EnrollData>

    @GET("programs/{id}/days/{day}")
    suspend fun programDay(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
        @Path("day") day: Int,
    ): Envelope<ProgramDayDetail>

    @POST("program-days/{id}/complete")
    suspend fun completeDay(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
        @Body body: Map<String, String>,
    ): Envelope<CompleteData>

    @GET("journal")
    suspend fun journal(
        @Header("Authorization") bearer: String,
        @Query("type") type: String,
    ): Envelope<JournalData>

    @POST("journal")
    suspend fun createJournal(
        @Header("Authorization") bearer: String,
        @Body body: JournalCreateRequest,
    ): Envelope<JournalEntry>

    @GET("feed")
    suspend fun feed(
        @Header("Authorization") bearer: String,
        @Query("scope") scope: String,
        @Query("tab") tab: String,
        @Query("limit") limit: Int = 20,
    ): Envelope<FeedData>

    @GET("circles")
    suspend fun circles(@Header("Authorization") bearer: String): Envelope<CirclesData>

    @POST("circles/{id}/join")
    suspend fun joinCircle(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Envelope<CircleActionData>

    @DELETE("circles/{id}/leave")
    suspend fun leaveCircle(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Envelope<CircleActionData>

    @POST("prayer-requests/{id}/pray")
    suspend fun pray(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Envelope<PrayData>

    @POST("prayer-requests")
    suspend fun createPrayerRequest(
        @Header("Authorization") bearer: String,
        @Body body: PrayerRequestCreate,
    ): Envelope<PrayerRequest>

    @POST("prayer-requests/{id}/answer")
    suspend fun answerPrayerRequest(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
        @Body body: Map<String, String>,
    ): Envelope<PrayerRequest>

    @Multipart
    @POST("moments")
    suspend fun createMoment(
        @Header("Authorization") bearer: String,
        @Part media: MultipartBody.Part?,
        @Part("caption") caption: RequestBody?,
        @Part("circle_id") circleId: RequestBody?,
        @Part("program_day_id") programDayId: RequestBody?,
        @Part("visibility") visibility: RequestBody?,
    ): Envelope<MomentData>

    @DELETE("moments/{id}")
    suspend fun deleteMoment(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Envelope<MessageData>

    @GET("me/summary")
    suspend fun meSummary(@Header("Authorization") bearer: String): Envelope<MeSummaryData>

    @GET("me/insights")
    suspend fun meInsights(@Header("Authorization") bearer: String): Envelope<InsightsData>

    @PATCH("me")
    suspend fun updateMe(
        @Header("Authorization") bearer: String,
        @Body body: Map<String, String>,
    ): Envelope<AuthUser>
}

object ApiClient {
    val api: JourneyApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JourneyApi::class.java)
    }
}
