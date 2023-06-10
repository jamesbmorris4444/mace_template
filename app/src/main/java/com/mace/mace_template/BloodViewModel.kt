package com.mace.mace_template

import android.app.Application
import android.content.res.Resources
import android.view.View
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import com.mace.mace_template.repository.DatabaseSelector
import com.mace.mace_template.repository.RepositoryImpl
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.Product
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

    fun insertDonorIntoDatabase(donor: Donor, completed: (Boolean) -> Unit) {
        repository.insertDonorIntoDatabase(DatabaseSelector.STAGING_DB, donor, completed)
    }

    fun getResources(): Resources {
        return app.resources
    }

    fun setBloodDatabase() {
        repository.setBloodDatabase(app.applicationContext)
    }

    fun insertDonorAndProductsIntoDatabase(modalView: View, databaseSelector: DatabaseSelector, donor: Donor, products: List<Product>) {
        repository.insertDonorAndProductsIntoDatabase(modalView, databaseSelector, donor, products)
    }

}