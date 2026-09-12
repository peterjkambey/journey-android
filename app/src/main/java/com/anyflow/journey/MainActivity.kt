package com.anyflow.journey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.anyflow.journey.ui.AppRoot
import com.anyflow.journey.ui.JourneyTheme
import com.anyflow.journey.ui.Tab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bantu demo/screenshot: buka tab langsung dari ADB, contoh
        //   adb shell am start -n com.anyflow.journey/.MainActivity --es tab FEED
        val initialTab = intent?.getStringExtra("tab")
            ?.let { name -> Tab.entries.firstOrNull { it.name.equals(name, ignoreCase = true) } }

        setContent {
            JourneyTheme {
                AppRoot(initialTab = initialTab)
            }
        }
    }
}
