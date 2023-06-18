package com.mace.mace_template

import android.app.Application
import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.os.IBinder
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.mace.mace_template.repository.DatabaseSelector
import com.mace.mace_template.repository.RepositoryImpl
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.DonorWithProducts
import com.mace.mace_template.repository.storage.Product
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class BloodViewModel(private val app: Application) : AndroidViewModel(app), KoinComponent {

    private val repository : RepositoryImpl by inject()

    fun refreshRepository(refreshCompleted: () -> Unit) {
        repository.refreshDatabase(app.applicationContext, refreshCompleted)
    }

    fun handleSearchClick(searchKey: String): List<Donor> {
        return repository.handleSearchClick(searchKey)
    }

    fun handleSearchClickWithProducts(searchKey: String) : List<DonorWithProducts> {
        return repository.handleSearchClickWithProducts(searchKey)
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

    fun isBloodDatabaseInvalid(): Boolean {
        return repository.isBloodDatabaseInvalid()
    }

    fun insertDonorAndProductsIntoDatabase(modalView: View, databaseSelector: DatabaseSelector, donor: Donor, products: List<Product>) {
        repository.insertDonorAndProductsIntoDatabase(modalView, databaseSelector, donor, products)
    }

    fun donorsFromFullNameWithProducts(searchLast: String, dob: String): List<DonorWithProducts> {
        return repository.donorsFromFullNameWithProducts(searchLast, dob)
    }

    fun stagingDatabaseDonorAndProductsList(): List<DonorWithProducts> {
        return repository.stagingDatabaseDonorAndProductsList()
    }

    fun mainDatabaseDonorAndProductsList(): List<DonorWithProducts> {
        return repository.mainDatabaseDonorAndProductsList()
    }

    fun insertReassociatedProductsIntoDatabase(donor: Donor, products: List<Product>) {
        repository.insertReassociatedProductsIntoDatabase(donor, products)
    }

    fun donorFromNameAndDateWithProducts(donor: Donor): DonorWithProducts {
        return repository.donorFromNameAndDateWithProducts(donor)
    }

}

class OnClearFromRecentService : Service(), KoinComponent {
    private val repository: RepositoryImpl by inject()
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY
    override fun onDestroy() = super.onDestroy()
    override fun onTaskRemoved(rootIntent: Intent?) {
        repository.saveStagingDatabase()
        stopSelf()
    }
}