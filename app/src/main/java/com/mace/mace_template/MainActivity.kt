package com.mace.mace_template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.mace.mace_template.repository.RepositoryImpl
import com.mace.mace_template.ui.theme.MaceTemplateTheme
import com.mace.mace_template.utils.Constants
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainActivity : ComponentActivity(), KoinComponent {
    private val repository: RepositoryImpl by inject()
    private val bloodViewModel: BloodViewModel by inject()
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
    override fun onDestroy() {
        super.onDestroy()
        repository.saveStagingDatabase(Constants.MODIFIED_DATABASE_NAME, bloodViewModel.fetchApplication().applicationContext.getDatabasePath(Constants.MODIFIED_DATABASE_NAME))
    }
}