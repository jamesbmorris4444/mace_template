package com.mace.mace_template.repository

import android.app.Application
import android.content.Context
import android.view.View
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.network.APIClient
import com.mace.mace_template.repository.network.APIInterface
import com.mace.mace_template.repository.storage.BloodDatabase
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.DonorWithProducts
import com.mace.mace_template.repository.storage.Product
import com.mace.mace_template.utils.Constants
import com.mace.mace_template.utils.Constants.LOG_TAG
import com.mace.mace_template.utils.Constants.MAIN_DATABASE_NAME
import com.mace.mace_template.utils.Constants.MODIFIED_DATABASE_NAME
import com.mace.mace_template.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

interface Repository {
    fun setBloodDatabase(context: Context)
    fun isBloodDatabaseInvalid(): Boolean
    fun saveStagingDatabase()
    fun refreshDatabase(context: Context, refreshCompleted: () -> Unit, refreshFailure: (String?) -> Unit)
    fun insertDonorIntoDatabase(donor: Donor)
    fun insertDonorAndProductsIntoDatabase(modalView: View, donor: Donor, products: List<Product>)
    fun stagingDatabaseDonorAndProductsList(): List<DonorWithProducts>
    fun mainDatabaseDonorAndProductsList(): List<DonorWithProducts>
    fun donorsFromFullNameWithProducts(searchLast: String, dob: String): List<DonorWithProducts>
    fun handleSearchClick(searchKey: String) : List<Donor>
    fun handleSearchClickWithProducts(searchKey: String) : List<DonorWithProducts>
    fun insertReassociatedProductsIntoDatabase(donor: Donor, products: List<Product>)
    fun donorFromNameAndDateWithProducts(donor: Donor): DonorWithProducts
}

class RepositoryImpl(private val app: Application) : Repository {

    private lateinit var mainBloodDatabase: BloodDatabase
    private lateinit var stagingBloodDatabase: BloodDatabase
    private val donorsService: APIInterface = APIClient.client

    override fun setBloodDatabase(context: Context) {
        val dbList = BloodDatabase.newInstance(context, MAIN_DATABASE_NAME, MODIFIED_DATABASE_NAME)
        mainBloodDatabase = dbList[0]
        stagingBloodDatabase = dbList[1]
    }

    override fun isBloodDatabaseInvalid(): Boolean {
        return databaseDonorCount(mainBloodDatabase) == 0
    }

    override fun refreshDatabase(context: Context, refreshCompleted: () -> Unit, refreshFailure: (String?) -> Unit) {
        var disposable: Disposable? = null
        disposable = donorsService.getDonors(Constants.API_KEY, Constants.LANGUAGE, 13)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .timeout(15L, TimeUnit.SECONDS)
            .subscribe ({ donorResponse ->
                disposable?.dispose()
                initializeDataBase(refreshCompleted, donorResponse.results, donorResponse.products)
                LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "refreshDatabase success: donorsSize=${donorResponse.results.size}       productsSize=${donorResponse.products.size}")
            },
            { throwable ->
                refreshFailure(throwable.message)
                LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "refreshDatabase failure: message=${throwable.message}")
                disposable?.dispose()
            })
    }

    private fun initializeDataBase(refreshCompleted: () -> Unit, donors: List<Donor>, products: List<List<Product>>) {
        List(donors.size) { donorIndex -> List(products[donorIndex].size) { productIndex -> products[donorIndex][productIndex].donorId = donors[donorIndex].id } }
        LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "initializeDataBase complete: donorsSize=${donors.size}")
        insertDonorsAndProductsIntoLocalDatabase(donors, products)
        refreshCompleted()
    }

    private fun insertDonorsAndProductsIntoLocalDatabase(donors: List<Donor>, products: List<List<Product>>) {
        mainBloodDatabase.databaseDao().insertDonorsAndProductLists(donors, products)
    }

    override fun saveStagingDatabase() {
        val databaseName = MODIFIED_DATABASE_NAME
        val db: File = app.applicationContext.getDatabasePath(databaseName)
        val dbShm = File(db.parent, "$databaseName-shm")
        val dbWal = File(db.parent, "$databaseName-wal")
        val dbBackup = File(db.parent, "$databaseName-backup")
        val dbShmBackup = File(db.parent, "$databaseName-backup-shm")
        val dbWalBackup = File(db.parent, "$databaseName-backup-wal")
        if (db.exists()) {
            db.copyTo(dbBackup, true)
        }
        if (dbShm.exists()) {
            dbShm.copyTo(dbShmBackup, true)
        }
        if (dbWal.exists()) {
            dbWal.copyTo(dbWalBackup, true)
        }
        LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "Path Name $db exists and was backed up")
    }

    /*
     *  The code below here does CRUD on the database
     */
    /**
     * The code below here does CRUD on the database
     * Methods:
     *   insertDonorIntoDatabase
     *   insertDonorAndProductsIntoDatabase
     *   insertReassociatedProductsIntoDatabase
     *   databaseCounts
     *   getProductEntryCount
     *   handleSearchClick
     *   handleReassociateSearchClick
     *   donorsFromFullName
     *   retrieveDonorFromNameAndDate
     */

    override fun insertDonorIntoDatabase(donor: Donor) {
        stagingBloodDatabase.databaseDao().insertDonor(donor)
    }

    override fun insertDonorAndProductsIntoDatabase(modalView: View, donor: Donor, products: List<Product>) {
        stagingBloodDatabase.databaseDao().insertDonorAndProducts(donor, products)
    }

    override fun insertReassociatedProductsIntoDatabase(donor: Donor, products: List<Product>) {
        stagingBloodDatabase.databaseDao().insertDonorAndProducts(donor, products)
    }

    override fun stagingDatabaseDonorAndProductsList(): List<DonorWithProducts> {
        return stagingBloodDatabase.databaseDao().loadAllDonorsWithProducts()
    }

    override fun donorFromNameAndDateWithProducts(donor: Donor): DonorWithProducts {
        return stagingBloodDatabase.databaseDao().donorFromNameAndDateWithProducts(donor.lastName, donor.dob)
    }

    override fun mainDatabaseDonorAndProductsList(): List<DonorWithProducts> {
        return mainBloodDatabase.databaseDao().loadAllDonorsWithProducts()
    }

    private fun databaseDonorCount(database: BloodDatabase): Int {
        return database.databaseDao().getDonorEntryCount()
    }

    override fun handleSearchClick(searchKey: String) : List<Donor> {
        val fullNameResponseList = listOf(
            donorsFromFullName(mainBloodDatabase, searchKey),
            donorsFromFullName(stagingBloodDatabase, searchKey)
        )
        val stagingDatabaseList = fullNameResponseList[1]
        val mainDatabaseList = fullNameResponseList[0]
        val newList = stagingDatabaseList.union(mainDatabaseList).distinctBy { donor -> Utils.donorComparisonByString(donor) }
        LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "handleSearchClick success: searchKey=$searchKey     returnList=$newList")
        return newList
    }

    private fun donorsFromFullName(database: BloodDatabase, search: String): List<Donor> {
        val searchLast: String
        var searchFirst = "%"
        val index = search.indexOf(',')
        if (index < 0) {
            searchLast = "$search%"
        } else {
            val last = search.substring(0, index)
            val first = search.substring(index + 1)
            searchFirst = "$first%"
            searchLast = "$last%"
        }
        return database.databaseDao().donorsFromFullName(searchLast, searchFirst)
    }

    override fun handleSearchClickWithProducts(searchKey: String) : List<DonorWithProducts> {
        val fullNameResponseList = listOf(
            donorsFromFullNameWithProducts(mainBloodDatabase, searchKey),
            donorsFromFullNameWithProducts(stagingBloodDatabase, searchKey)
        )
        val stagingDatabaseList = fullNameResponseList[1]
        val mainDatabaseList = fullNameResponseList[0]
        val newList = stagingDatabaseList.union(mainDatabaseList).distinctBy { donor -> Utils.donorComparisonByStringWithProducts(donor) }
        LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "handleSearchClickWithProducts success: searchKey=$searchKey     returnList=$newList")
        return newList
    }

    private fun donorsFromFullNameWithProducts(database: BloodDatabase, search: String): List<DonorWithProducts> {
        val searchLast: String
        var searchFirst = "%"
        val index = search.indexOf(',')
        if (index < 0) {
            searchLast = "$search%"
        } else {
            val last = search.substring(0, index)
            val first = search.substring(index + 1)
            searchFirst = "$first%"
            searchLast = "$last%"
        }
        return database.databaseDao().donorsFromFullNameWithProducts(searchLast, searchFirst)
    }

    override fun donorsFromFullNameWithProducts(searchLast: String, dob: String): List<DonorWithProducts> {
        return stagingBloodDatabase.databaseDao().donorsFromFullNameWithProducts(searchLast, dob)
    }

}