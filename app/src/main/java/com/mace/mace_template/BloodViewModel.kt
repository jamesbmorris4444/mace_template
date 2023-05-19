package com.mace.mace_template

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import com.mace.mace_template.repository.RepositoryImpl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class BloodViewModel(private val app: Application) : AndroidViewModel(app), KoinComponent {

    private val repository : RepositoryImpl by inject()

    @Composable
    fun RefreshRepository(refreshCompleted: () -> Unit) {
        Log.d("JIMX","JJJJJJ  1")
        repository.refreshDatabase(app.applicationContext, refreshCompleted)
    }
}