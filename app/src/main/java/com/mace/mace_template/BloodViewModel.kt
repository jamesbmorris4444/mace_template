package com.mace.mace_template

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import com.mace.mace_template.repository.RepositoryImpl
import com.mace.mace_template.repository.storage.Donor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class BloodViewModel(private val app: Application) : AndroidViewModel(app), KoinComponent {

    private val repository : RepositoryImpl by inject()

    @Composable
    fun RefreshRepository(refreshCompleted: () -> Unit) {
        repository.refreshDatabase(app.applicationContext, refreshCompleted)
    }

    fun handleSearchClick(searchKey: String, searchCompleted: (List<Donor>) -> Unit) {
        repository.handleSearchClick(searchKey, searchCompleted)
    }

    fun setBloodDatabase() {
        repository.setBloodDatabase(app.applicationContext)
    }

}