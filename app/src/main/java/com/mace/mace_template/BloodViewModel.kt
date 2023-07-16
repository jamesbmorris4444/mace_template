package com.mace.mace_template

import android.app.Application
import android.content.res.Resources
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mace.mace_template.repository.RepositoryImpl
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.DonorWithProducts
import com.mace.mace_template.repository.storage.Product
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class BloodViewModel(private val app: Application) : AndroidViewModel(app), KoinComponent {

    private val repository : RepositoryImpl by inject()

    private val _databaseInvalidState = MutableLiveData(false)
    val databaseInvalidState: LiveData<Boolean>
        get() = _databaseInvalidState

    private val _refreshCompletedState = MutableLiveData(false)
    val refreshCompletedState: LiveData<Boolean>
        get() = _refreshCompletedState

    private val _refreshFailureState = MutableLiveData("")
    val refreshFailureState: LiveData<String?>
        get() = _refreshFailureState

    private val _donorsAvailableState = MutableLiveData<List<Donor>>(listOf())
    val donorsAvailableState: LiveData<List<Donor>>
        get() = _donorsAvailableState

    fun refreshRepository() {
        repository.refreshDatabase(
            app.applicationContext,
            refreshCompleted = {
                _refreshCompletedState.value = true
                _databaseInvalidState.value = false
            }
        ) {
            _refreshFailureState.value = it
            _databaseInvalidState.value = false
        }
    }

    fun handleSearchClick(searchKey: String) {
        _donorsAvailableState.value = repository.handleSearchClick(searchKey)
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

    fun isBloodDatabaseInvalid() {
        if (repository.isBloodDatabaseInvalid()) {
            _databaseInvalidState.value = true
        } else {
            _refreshCompletedState.value = true
            _databaseInvalidState.value = false
        }
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