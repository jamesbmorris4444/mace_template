package com.mace.mace_template

import android.app.Application
import android.content.res.Resources
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
    var databaseInvalidForTesting = false
    var databaseInvalidForTestingFailureMessage = ""

    private val _showStandardModalState = MutableLiveData(StandardModalArgs())
    val showStandardModalState: LiveData<StandardModalArgs>
        get() = _showStandardModalState

    fun changeShowStandardModalState(standardModalArgs: StandardModalArgs) {
        _showStandardModalState.value = standardModalArgs
    }

    // Start Donate Products Screen state

    private val _databaseInvalidState = MutableLiveData<Boolean>()
    val databaseInvalidState: LiveData<Boolean>
        get() = _databaseInvalidState

    private val _refreshCompletedState = MutableLiveData<Boolean>()
    val refreshCompletedState: LiveData<Boolean>
        get() = _refreshCompletedState

    private val _refreshFailureState = MutableLiveData<String>()
    val refreshFailureState: LiveData<String?>
        get() = _refreshFailureState

    private val _donorsAvailableState = MutableLiveData<List<Donor>>(listOf())
    val donorsAvailableState: LiveData<List<Donor>>
        get() = _donorsAvailableState

    fun changeDatabaseInvalidState(state: Boolean) {
        _databaseInvalidState.postValue(state)
    }

    fun changeRefreshCompletedState(state: Boolean) {
        _refreshCompletedState.postValue(state)
    }

    fun changeRefreshFailureState(state: String) {
        _refreshFailureState.postValue(state)
    }

    fun resetDonateProductsScreen() {
        _refreshFailureState.value = ""
        _refreshCompletedState.value = false
        _databaseInvalidState.value = false
        _donorsAvailableState.value = listOf()
    }

    // End Donate Products Screen state

    // Start Reassociate Donation Screen state

    private val _correctDonorsWithProductsState = MutableLiveData<List<DonorWithProducts>>(listOf())
    val correctDonorsWithProductsState: LiveData<List<DonorWithProducts>>
        get() = _correctDonorsWithProductsState

    private val _incorrectDonorsWithProductsState = MutableLiveData<List<DonorWithProducts>>(listOf())
    val incorrectDonorsWithProductsState: LiveData<List<DonorWithProducts>>
        get() = _incorrectDonorsWithProductsState

    fun changeCorrectDonorsWithProductsState(list: List<DonorWithProducts>) {
        _correctDonorsWithProductsState.value = list
    }

    fun changeIncorrectDonorsWithProductsState(list: List<DonorWithProducts>) {
        _incorrectDonorsWithProductsState.value = list
    }

    private val _correctDonorWithProductsState = MutableLiveData(DonorWithProducts(Donor()))
    val correctDonorWithProductsState: LiveData<DonorWithProducts>
        get() = _correctDonorWithProductsState

    private val _incorrectDonorWithProductsState = MutableLiveData(DonorWithProducts(Donor()))
    val incorrectDonorWithProductsState: LiveData<DonorWithProducts>
        get() = _incorrectDonorWithProductsState

    fun changeCorrectDonorWithProductsState(donor: Donor) {
        _correctDonorWithProductsState.value = donorFromNameAndDateWithProducts(donor)
    }

    fun changeIncorrectDonorWithProductsState(donor: Donor) {
        _incorrectDonorWithProductsState.value = donorFromNameAndDateWithProducts(donor)
    }

    private val _singleSelectedProductListState = MutableLiveData<List<Product>>(listOf())
    val singleSelectedProductListState: LiveData<List<Product>>
        get() = _singleSelectedProductListState

    fun changeSingleSelectedProductListState(list: List<Product>) {
        _singleSelectedProductListState.value = list
    }

    private val _incorrectDonorSelectedState = MutableLiveData(false)
    val incorrectDonorSelectedState: LiveData<Boolean>
        get() = _incorrectDonorSelectedState

    private val _isProductSelectedState = MutableLiveData(false)
    val isProductSelectedState: LiveData<Boolean>
        get() = _isProductSelectedState

    private val _isReassociateCompletedState = MutableLiveData(false)
    val isReassociateCompletedState: LiveData<Boolean>
        get() = _isReassociateCompletedState

    fun changeIncorrectDonorSelectedState(state: Boolean) {
        _incorrectDonorSelectedState.value = state
    }

    fun changeIsProductSelectedState(state: Boolean) {
        _isProductSelectedState.value = state
    }

    fun changeIsReassociateCompletedState(state: Boolean) {
        _isReassociateCompletedState.value = state
    }

    fun resetReassociateCompletedScreen() {
        _correctDonorsWithProductsState.value = listOf()
        _incorrectDonorsWithProductsState.value = listOf()
        _correctDonorWithProductsState.value = DonorWithProducts(Donor())
        _incorrectDonorWithProductsState.value = DonorWithProducts(Donor())
        _singleSelectedProductListState.value = listOf()
        _incorrectDonorSelectedState.value = false
        _isProductSelectedState.value = false
        _isReassociateCompletedState.value = false
    }

    // End Reassociate Donation Screen state

    // Start Create Products Screen state

    private val _dinTextState = MutableLiveData("")
    val dinTextState: LiveData<String>
        get() = _dinTextState

    private val _productCodeTextState  = MutableLiveData("")
    val productCodeTextState : LiveData<String>
        get() = _productCodeTextState

    private val _expirationTextState = MutableLiveData("")
    val expirationTextState: LiveData<String>
        get() = _expirationTextState

    fun changeDinTextState(text: String) {
        _dinTextState.value = text
    }

    fun changeProductCodeTextState(text: String) {
        _productCodeTextState.value = text
    }

    fun changeExpirationTextState(text: String) {
        _expirationTextState.value = text
    }

    private val _clearButtonVisibleState = MutableLiveData(true)
    val clearButtonVisibleState: LiveData<Boolean>
        get() = _clearButtonVisibleState

    private val _confirmButtonVisibleState = MutableLiveData(true)
    val confirmButtonVisibleState: LiveData<Boolean>
        get() = _confirmButtonVisibleState

    private val _confirmNeededState = MutableLiveData(false)
    val confirmNeededState: LiveData<Boolean>
        get() = _confirmNeededState

    fun changeClearButtonVisibleState(state: Boolean) {
        _clearButtonVisibleState.value = state
    }

    fun changeConfirmButtonVisibleState(state: Boolean) {
        _confirmButtonVisibleState.value = state
    }

    fun changeConfirmNeededState(state: Boolean) {
        _confirmNeededState.value = state
    }

    private val _productsListState = MutableLiveData<List<Product>>(listOf())
    val productsListState: LiveData<List<Product>>
        get() = _productsListState

    fun changeProductsListState(list: List<Product>) {
        _productsListState.value = list
    }

    private val _displayedProductListState = MutableLiveData<List<Product>>(listOf())
    val displayedProductListState: LiveData<List<Product>>
        get() = _displayedProductListState

    fun changeDisplayedProductListState(list: List<Product>) {
        _displayedProductListState.value = list
    }

    // End Create Products Screen state

    fun refreshRepository() {
        if (databaseInvalidForTesting) {
            changeDatabaseInvalidState(false)
            changeRefreshCompletedState(true)
            changeRefreshFailureState(databaseInvalidForTestingFailureMessage)
        } else {
            repository.refreshDatabase(
                refreshCompleted = {
                    changeDatabaseInvalidState(false)
                    changeRefreshCompletedState(true)
                    changeRefreshFailureState("")
                }
            ) {
                changeDatabaseInvalidState(false)
                changeRefreshCompletedState(true)
                changeRefreshFailureState(it)
            }
        }
    }

    fun handleSearchClick(searchKey: String) {
        _donorsAvailableState.value = repository.handleSearchClick(searchKey)
    }

    fun handleSearchClickWithProductsCorrectDonor(searchKey: String) {
        _correctDonorsWithProductsState.value = repository.handleSearchClickWithProducts(searchKey)
    }

    fun handleSearchClickWithProductsIncorrectDonor(searchKey: String) {
        _incorrectDonorsWithProductsState.value = repository.handleSearchClickWithProducts(searchKey)
    }

    fun insertDonorIntoDatabase(donor: Donor) {
        repository.insertDonorIntoDatabase(donor)
    }

    fun getResources(): Resources {
        return app.resources
    }

    fun setBloodDatabase() {
        repository.setBloodDatabase(app)
    }

    fun isBloodDatabaseInvalid(): Boolean {
        return if (databaseInvalidForTesting) true else repository.isBloodDatabaseInvalid()
    }

    fun insertDonorAndProductsIntoDatabase(donor: Donor, products: List<Product>) {
        repository.insertDonorAndProductsIntoDatabase(donor, products)
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

    private fun donorFromNameAndDateWithProducts(donor: Donor): DonorWithProducts {
        return repository.donorFromNameAndDateWithProducts(donor)
    }

}