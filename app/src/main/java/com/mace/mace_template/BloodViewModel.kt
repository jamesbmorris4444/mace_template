package com.mace.mace_template

import android.app.Application
import android.content.res.Resources
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.mace.mace_template.repository.RepositoryImpl
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.DonorWithProducts
import com.mace.mace_template.repository.storage.Product
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class BloodViewModel(private val app: Application) : AndroidViewModel(app), KoinComponent {

    private val repository : RepositoryImpl by inject()

    fun refreshRepository(refreshCompleted: () -> Unit, refreshFailure: (String?) -> Unit) {
        repository.refreshDatabase(app.applicationContext, refreshCompleted, refreshFailure)
    }

    fun handleSearchClick(searchKey: String): List<Donor> {
        return repository.handleSearchClick(searchKey)
    }

    fun handleSearchClickWithProducts(searchKey: String) : List<DonorWithProducts> {
        return repository.handleSearchClickWithProducts(searchKey)
    }

    fun insertDonorIntoDatabase(donor: Donor) {
        repository.insertDonorIntoDatabase(donor)
    }

    fun getResources(): Resources {
        return app.resources
    }

    fun fetchApplication(): Application {
        return app
    }

    fun setBloodDatabase() {
        repository.setBloodDatabase(app.applicationContext)
    }

    fun isBloodDatabaseInvalid(): Boolean {
        return repository.isBloodDatabaseInvalid()
    }

    fun insertDonorAndProductsIntoDatabase(modalView: View, donor: Donor, products: List<Product>) {
        repository.insertDonorAndProductsIntoDatabase(modalView, donor, products)
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