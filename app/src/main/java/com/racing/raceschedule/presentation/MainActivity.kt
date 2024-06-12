package com.racing.raceschedule.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.racing.raceschedule.presentation.ui.RaceScreen
import com.racing.raceschedule.data.di.appModule
import com.racing.raceschedule.presentation.ui.theme.RaceScheduleTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start Koin for dependency injection
        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        // Set the content view to the RaceScheduleTheme
        setContent {
            RaceScheduleTheme {
                RaceScreen() // Display the main screen with races
            }
        }
    }
}