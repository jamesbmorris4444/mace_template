package com.mace.mace_template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.mace.mace_template.ui.theme.MaceTemplateTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        Timber.plant(Timber.DebugTree())
        setContent {
            MaceTemplateTheme {
                DrawerAppComponent(this.findViewById(android.R.id.content), BloodViewModel(application), ScreenNames.DonateProductsSearch)
            }
        }
    }
}