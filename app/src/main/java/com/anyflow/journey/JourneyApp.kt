package com.anyflow.journey

import android.app.Application
import com.anyflow.journey.data.ApiClient
import com.anyflow.journey.data.JourneyApi
import com.anyflow.journey.data.SessionStore

/** Application global: satu SessionStore + satu instance API untuk semua layar. */
class JourneyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: JourneyApp
            private set

        val session: SessionStore
            get() = SessionStore(instance)

        val api: JourneyApi
            get() = ApiClient.api
    }
}
