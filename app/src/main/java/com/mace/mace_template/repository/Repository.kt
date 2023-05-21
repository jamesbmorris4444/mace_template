package com.mace.mace_template.repository

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fullsekurity.theatreblood.logger.LogUtils
import com.mace.mace_template.R
import com.mace.mace_template.repository.network.APIClient
import com.mace.mace_template.repository.network.APIInterface
import com.mace.mace_template.repository.storage.BloodDatabase
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.DonorWithProducts
import com.mace.mace_template.repository.storage.Product
import com.mace.mace_template.ui.theme.MaceTemplateTheme
import com.mace.mace_template.utils.Constants
import com.mace.mace_template.utils.Constants.MAIN_DATABASE_NAME
import com.mace.mace_template.utils.Constants.MODIFIED_DATABASE_NAME
import com.mace.mace_template.utils.SingleLiveEvent
import com.mace.mace_template.utils.Utils
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

interface Repository {
    fun setBloodDatabase(context: Context)
    fun refreshDatabase(context: Context, refreshCompleted: () -> Unit)
}

class RepositoryImpl : Repository {

    private val tag = Repository::class.java.simpleName
    lateinit var mainBloodDatabase: BloodDatabase
    lateinit var stagingBloodDatabase: BloodDatabase
    private val donorsService: APIInterface = APIClient.client

    private val liveDonorListEvent: SingleLiveEvent<List<Donor>> = SingleLiveEvent()
    fun getLiveDonorListEvent(): SingleLiveEvent<List<Donor>> { return liveDonorListEvent }

    var newDonor: Donor? = null
    var newDonorInProgress = false
    lateinit var donorsWithProductsListForReassociate: List<DonorWithProducts>

    override fun setBloodDatabase(context: Context) {
        val dbList = BloodDatabase.newInstance(context, MAIN_DATABASE_NAME, MODIFIED_DATABASE_NAME)
        mainBloodDatabase = dbList[0]
        stagingBloodDatabase = dbList[1]
    }

    // The code below here refreshes the main donations base

    override fun refreshDatabase(context: Context, refreshCompleted: () -> Unit) {
        saveDatabase(context, MAIN_DATABASE_NAME)
        var disposable: Disposable? = null
        disposable = donorsService.getDonors(Constants.API_KEY, Constants.LANGUAGE, 13)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .timeout(15L, TimeUnit.SECONDS)
            .subscribe ({ donorResponse ->
                disposable?.dispose()
                initializeDataBase(context, refreshCompleted, donorResponse.results, donorResponse.products)
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "refreshDatabase success: donorsSize=${donorResponse.results.size}       productsSize=${donorResponse.products.size}")
            },
            { throwable ->
                refreshCompleted()
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "refreshDatabase failure: message=${throwable.message}")
                disposable?.dispose()
                initializeDatabaseFailureModal(context, throwable.message)
            })
    }

    private fun initializeDataBase(context: Context, refreshCompleted: () -> Unit, donors: List<Donor>, products: List<List<Product>>) {
        for (donorIndex in donors.indices) {
            for (productIndex in products[donorIndex].indices) {
                products[donorIndex][productIndex].donorId = donors[donorIndex].id
            }
        }
        LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "initializeDataBase complete: donorsSize=${donors.size}")
        insertDonorsAndProductsIntoLocalDatabase(context, refreshCompleted, mainBloodDatabase, donors, products)
    }

    private fun insertDonorsAndProductsIntoLocalDatabase(context: Context, refreshCompleted: () -> Unit, database: BloodDatabase, donors: List<Donor>, products: List<List<Product>>) {
        var disposable: Disposable? = null
        disposable = Completable.fromAction { database.databaseDao().insertDonorsAndProductLists(donors, products) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe ({
                disposable?.dispose()
                refreshCompleted()
                liveDonorListEvent.value = donors
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "insertDonorsAndProductsIntoLocalDatabase success: donorsSize=${donors.size}")
//                ComposeView(context).apply {
//                    setContent {
//                        MaceTemplateTheme {
//                            Surface(modifier = Modifier.fillMaxSize()) {
//                                StandardModal(context)
//                            }
//                        }
//                    }
//                }

            },
            { throwable ->
                disposable?.dispose()
                refreshCompleted()
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "insertDonorsAndProductsIntoLocalDatabase failure: message=${throwable.message}")
            })
    }

    private fun initializeDatabaseFailureModal(context: Context, errorMessage: String?) {
        var error = errorMessage
        if (error == null) {
            error = "App cannot continue"
        }
        ComposeView(context).apply {
            setContent {
                MaceTemplateTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        StandardModal(context)
                    }
                }
            }
        }
    }

    private fun deleteDatabase(context: Context, databaseName: String) {
        context.deleteDatabase(databaseName)
    }

    private fun saveDatabase(context: Context, databaseName: String) {
        val db: File = context.getDatabasePath(databaseName)
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
        LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "Path Name $db exists and was backed up")
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

//    fun insertDonorIntoDatabase(database: BloodDatabase, donor: Donor, transitionToCreateDonation: Boolean, showList: () -> Unit) {
//        var disposable: Disposable? = null
//        disposable = Completable.fromAction { database.databaseDao().insertDonor(donor) }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe ({
//                disposable?.dispose()
//                StandardModal(
//                    callbacks,
//                    modalType = StandardModal.ModalType.STANDARD,
//                    titleText = callbacks.fetchActivity().getString(R.string.std_modal_insert_donor_staging_title),
//                    bodyText = callbacks.fetchActivity().getString(R.string.std_modal_insert_donor_staging_body),
//                    positiveText = callbacks.fetchActivity().getString(R.string.std_modal_ok),
//                    dialogFinishedListener = object : StandardModal.DialogFinishedListener {
//                        override fun onPositive(string: String) {
//                            if (transitionToCreateDonation) {
//                                callbacks.fetchActivity().loadCreateProductsFragment(donor)
//                            } else {
//                                callbacks.fetchActivity().onBackPressed()
//                            }
//                        }
//                        override fun onNegative() { }
//                        override fun onNeutral() { }
//                        override fun onBackPressed() {
//                            callbacks.fetchActivity().onBackPressed()
//                        }
//                    }
//                ).show(callbacks.fetchActivity().supportFragmentManager, "MODAL")
//                showList()
//            },
//            { throwable ->
//                disposable?.dispose()
//                insertDonorIntoDatabaseFailure(transitionToCreateDonation, donor, "insertDonorIntoDatabase", throwable)
//            })
//    }
//    private fun insertDonorIntoDatabaseFailure(transition: Boolean, donor: Donor, method: String, throwable: Throwable) {
//        LogUtils.E(LogUtils.FilterTags.withTags(LogUtils.TagFilter.EXC), method, throwable)
//        if (transition) {
//            callbacks.fetchActivity().loadCreateProductsFragment(donor)
//        } else {
//            callbacks.fetchActivity().onBackPressed()
//        }
//    }
//
//    fun insertDonorAndProductsIntoDatabase(database: BloodDatabase, donor: Donor, products: List<Product>, showList: () -> Unit) {
//        var disposable: Disposable? = null
//        disposable = Completable.fromAction { database.databaseDao().insertDonorAndProducts(donor, products) }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe ({
//                disposable?.dispose()
//                StandardModal(
//                    callbacks,
//                    modalType = StandardModal.ModalType.STANDARD,
//                    titleText = callbacks.fetchActivity().getString(R.string.std_modal_insert_products_staging_title),
//                    bodyText = callbacks.fetchActivity().getString(R.string.std_modal_insert_products_staging_body),
//                    positiveText = callbacks.fetchActivity().getString(R.string.std_modal_ok),
//                    dialogFinishedListener = object : StandardModal.DialogFinishedListener {
//                        override fun onPositive(string: String) {
//                            callbacks.fetchActivity().supportFragmentManager.popBackStack(Constants.ROOT_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                            callbacks.fetchActivity().loadDonateProductsFragment(true)
//                        }
//                        override fun onNegative() { }
//                        override fun onNeutral() { }
//                        override fun onBackPressed() {
//                            callbacks.fetchActivity().supportFragmentManager.popBackStack(Constants.ROOT_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                            callbacks.fetchActivity().loadDonateProductsFragment(true)
//                        }
//                    }
//                ).show(callbacks.fetchActivity().supportFragmentManager, "MODAL")
//                showList()
//            },
//            { throwable ->
//                disposable?.dispose()
//                insertDonorAndProductsIntoDatabaseFailure("insertDonorAndProductsIntoDatabase", throwable)
//            })
//    }
//    private fun insertDonorAndProductsIntoDatabaseFailure(method: String, throwable: Throwable) {
//        LogUtils.E(LogUtils.FilterTags.withTags(LogUtils.TagFilter.EXC), method, throwable)
//        callbacks.fetchActivity().supportFragmentManager.popBackStack(Constants.ROOT_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//        callbacks.fetchActivity().loadDonateProductsFragment(true)
//    }
//
//    fun insertReassociatedProductsIntoDatabase(database: BloodDatabase, donor: Donor, products: List<Product>, initializeView: () -> Unit) {
//        var disposable: Disposable? = null
//        val completeableAction = database.databaseDao().insertDonorAndProducts(donor, products)
//        disposable = Completable.fromAction { completeableAction }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe ({
//                disposable?.dispose()
//                StandardModal(
//                    callbacks,
//                    modalType = StandardModal.ModalType.STANDARD,
//                    titleText = callbacks.fetchActivity().getString(R.string.std_modal_insert_products_staging_title),
//                    bodyText = callbacks.fetchActivity().getString(R.string.std_modal_insert_products_staging_body),
//                    positiveText = callbacks.fetchActivity().getString(R.string.std_modal_ok),
//                    dialogFinishedListener = object : StandardModal.DialogFinishedListener {
//                        override fun onPositive(string: String) {
//                            initializeView()
//                        }
//                        override fun onNegative() { }
//                        override fun onNeutral() { }
//                        override fun onBackPressed() {
//                            initializeView()
//                        }
//                    }
//                ).show(callbacks.fetchActivity().supportFragmentManager, "MODAL")
//            },
//            { throwable ->
//                disposable?.dispose()
//                insertReassociatedProductsIntoDatabaseFailure("insertReassociatedProductsIntoDatabase", throwable, initializeView)
//            })
//    }
//
//    private fun insertReassociatedProductsIntoDatabaseFailure(method: String, throwable: Throwable, initializeView: () -> Unit) {
//        LogUtils.E(LogUtils.FilterTags.withTags(LogUtils.TagFilter.EXC), method, throwable)
//        initializeView()
//    }
//
//    /**
//     * @param   donorsAndProductsList     callback method in ViewModel when asynchronous operation finishes
//     * Calls the callback method with the donor and product list of entries in the staging database
//     *
//     */
//    fun getListOfDonorsAndProducts(donorsAndProductsList: (donorsAndProductsList: List<DonorWithProducts>) -> Unit) {
//        var disposable: Disposable? = null
//        disposable = databaseDonorAndProductsList(stagingBloodDatabase)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ donorsAndProductsList ->
//                disposable?.dispose()
//                donorsAndProductsList(donorsAndProductsList)
//            }, { throwable ->
//                disposable?.dispose()
//                LogUtils.E(LogUtils.FilterTags.withTags(LogUtils.TagFilter.EXC), "getListOfDonorsAndProducts", throwable)
//            })
//    }
//    private fun databaseDonorAndProductsList(database: BloodDatabase): Single<List<DonorWithProducts>> {
//        return database.databaseDao().loadAllDonorsWithProducts()
//    }
//
    /**
     * Shows the donor and product counts in both the main database and the staging database in a modal popup
     */
    fun databaseCounts() {
        val entryCountList = listOf(
            databaseDonorCount(stagingBloodDatabase),
            databaseDonorCount(mainBloodDatabase)
        )
        var disposable: Disposable? = null
        disposable = Single.zip(entryCountList) { args -> listOf(args) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ responseList ->
                disposable?.dispose()
                val response = responseList[0]
                getProductEntryCount(response[0] as Int, response[1] as Int)
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "database donors count success: mainDonorCount=${response[0] as Int}     backupDonorCount=${response[1] as Int}")
            },
            { throwable ->
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "database donors count failure: message=${throwable.message}")
                disposable?.dispose()
            })
    }
    private fun databaseDonorCount(database: BloodDatabase): Single<Int> {
        return database.databaseDao().getDonorEntryCount()
    }
    private fun getProductEntryCount(modifiedDonors: Int, mainDonors: Int) {
        val entryCountList = listOf(
            databaseProductCount(stagingBloodDatabase),
            databaseProductCount(mainBloodDatabase)
        )
        var disposable: Disposable? = null
        disposable = Single.zip(entryCountList) { args -> listOf(args) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ responseList ->
                disposable?.dispose()
                val response = responseList[0]
//                StandardModal(
//                    callbacks,
//                    modalType = StandardModal.ModalType.STANDARD,
//                    titleText = callbacks.fetchActivity().getString(R.string.std_modal_staging_database_count_title),
//                    bodyText = String.format(callbacks.fetchActivity().getString(R.string.std_modal_staging_database_count_body), modifiedDonors, mainDonors, response[0] as Int, response[1] as Int),
//                    positiveText = callbacks.fetchActivity().getString(R.string.std_modal_ok),
//                    dialogFinishedListener = object : StandardModal.DialogFinishedListener {
//                        override fun onPositive(password: String) { }
//                        override fun onNegative() { }
//                        override fun onNeutral() { }
//                        override fun onBackPressed() { }
//                    }
//                ).show(callbacks.fetchActivity().supportFragmentManager, "MODAL")
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "database products count success: mainProductCount=${response[0] as Int}     backupProductCount=${response[1] as Int}")
            },
            { throwable ->
                disposable?.dispose()
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "database products count failure: message=${throwable.message}")
            })
    }
    private fun databaseProductCount(database: BloodDatabase): Single<Int> {
        return database.databaseDao().getProductEntryCount()
    }
//
//
//
//    /**
//     * @param   searchKey                           first n characters of the last name, case insensitive
//     * @param   completeReassociationToNewDonor     callback method in ViewModel when asynchronous operation finishes
//     * Queries the staging database to find a donor from last name, first name, middle name, and date of birth.
//     */
//    fun retrieveDonorFromNameAndDob(progressBar: ProgressBar, donor: Donor, completeReassociationToNewDonor: (completeReassociationToNewDonor: Donor) -> Unit) {
//        var disposable: Disposable? = null
//        disposable = stagingBloodDatabase.databaseDao().donorFromNameAndDate(donor.lastName, donor.firstName, donor.middleName, donor.dob)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe({ donorObtained ->
//                disposable?.dispose()
//                progressBar.visibility = View.GONE
//                completeReassociationToNewDonor(donorObtained)
//            },
//                { throwable ->
//                    disposable?.dispose()
//                    LogUtils.E(LogUtils.FilterTags.withTags(LogUtils.TagFilter.EXC), "donorFromNameAndDateStoreAndRetrieve", throwable)
//                })
//    }
//
//    /**
//     * @param   searchKey      first n characters of the last name, case insensitive
//     * @param   showDonors     callback method in ViewModel when asynchronous operation finishes
//     * Queries both the staging database and the main database to find a donor from the search key.
//     */
    @Suppress("UNCHECKED_CAST")
    fun handleSearchClick(searchKey: String, showDonors: (donorList: List<Donor>) -> Unit) {
        val fullNameResponseList = listOf(
            donorsFromFullName(mainBloodDatabase, searchKey),
            donorsFromFullName(stagingBloodDatabase, searchKey)
        )
        var disposable: Disposable? = null
        disposable = Single.zip(fullNameResponseList) { args -> listOf(args) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ responseList ->
                disposable?.dispose()
                val response = responseList[0]
                val stagingDatabaseList = response[1] as List<Donor>
                val mainDatabaseList = response[0] as List<Donor>
                val newList = stagingDatabaseList.union(mainDatabaseList).distinctBy { donor -> Utils.donorComparisonByString(donor) }
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "handleSearchClick success: searchKey=$searchKey     returnList=$newList")
                showDonors(newList)
            },
            { throwable ->
                disposable?.dispose()
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "handleSearchClick failure: message=${throwable.message}")
            })
    }
    private fun donorsFromFullName(database: BloodDatabase, search: String): Single<List<Donor>> {
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

//    /**
//     * @param   searchKey                 first n characters of the last name, case insensitive
//     * @param   showDonorsAndProducts     callback method in ViewModel when asynchronous operation finishes
//     * Queries both the staging database and the main database to find a donor (with attached products) from the search key.
//     * Called both before and after the incorrect donor has been identified, but with a different callback method in ech case.
//     */
//    @Suppress("UNCHECKED_CAST")
//    fun handleReassociateSearchClick(view: View, searchKey: String, showDonorsAndProducts: (donorsAndProductsList: List<DonorWithProducts>) -> Unit) {
//        val fullNameResponseList = listOf(
//            donorsFromFullNameWithProducts(mainBloodDatabase, searchKey),
//            donorsFromFullNameWithProducts(stagingBloodDatabase, searchKey)
//        )
//        var disposable: Disposable? = null
//        disposable = Single.zip(fullNameResponseList) { args -> listOf(args) }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe ({ responseList ->
//                disposable?.dispose()
//                val response = responseList[0]
//                val stagingDatabaseList = response[1] as List<DonorWithProducts>
//                val mainDatabaseList = response[0] as List<DonorWithProducts>
//                if (stagingDatabaseList.isEmpty()) {
//                    showDonorsAndProducts(mainDatabaseList)
//                    donorsWithProductsListForReassociate = mainDatabaseList
//                } else {
//                    showDonorsAndProducts(stagingDatabaseList)
//                    donorsWithProductsListForReassociate = stagingDatabaseList
//                }
//            },
//            { throwable ->
//                disposable?.dispose()
//                LogUtils.E(LogUtils.FilterTags.withTags(LogUtils.TagFilter.EXC), "handleReassociateSearchClick", throwable)
//            })
//
//    }

//    private fun donorsFromFullNameWithProducts(database: BloodDatabase, search: String): Single<List<DonorWithProducts>> {
//        var searchLast: String
//        var searchFirst = "%"
//        val index = search.indexOf(',')
//        if (index < 0) {
//            searchLast = "$search%"
//        } else {
//            val last = search.substring(0, index)
//            val first = search.substring(index + 1)
//            searchFirst = "$first%"
//            searchLast = "$last%"
//        }
//        return database.databaseDao().donorsFromFullNameWithProducts(searchLast, searchFirst)
//    }

}

@Composable
fun StandardModal(context: Context) {
    ComposeView(context).apply {
        setContent {
            MaceTemplateTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        var openDialog by remember {
                            mutableStateOf(false) // Initially dialog is closed
                        }

                        ButtonClick(buttonText = "Open Dialog") {
                            openDialog = true
                        }

                        if (openDialog) {
                            DialogBox2FA {
                                openDialog = false
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun DialogBox2FA(onDismiss: () -> Unit) {
    val contextForToast = LocalContext.current.applicationContext
    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(color = Color(0xFF35898F)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 16.dp),
                        painter = painterResource(id = R.drawable.notification),
                        contentDescription = "2-Step Verification",
                        alignment = Alignment.Center
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    text = "2-Step Verification",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.avenir_bold, FontWeight.Bold)),
                        fontSize = 20.sp
                    )
                )

                Text(
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                    text = "Setup 2-Step Verification to add additional layer of security to your account.",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.avenir_regular, FontWeight.Normal)),
                        fontSize = 14.sp
                    )
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 36.dp, start = 36.dp, end = 36.dp, bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF35898F)),
                    onClick = {
                        onDismiss()
                        Toast.makeText(
                            contextForToast,
                            "Click: Setup Now",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    Text(
                        text = "Setup Now",
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = FontFamily(
                                Font(
                                    R.font.avenir_book,
                                    FontWeight.Medium
                                )
                            ),
                            fontSize = 16.sp
                        )
                    )
                }

                TextButton(
                    onClick = {
                        onDismiss()
                        Toast.makeText(
                            contextForToast,
                            "Click: I'll Do It Later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    Text(
                        text = "I'll Do It Later",
                        color = Color(0xFF35898f),
                        style = TextStyle(
                            fontFamily = FontFamily(
                                Font(
                                    R.font.avenir_book,
                                    FontWeight.Normal
                                )
                            ),
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ButtonClick(
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        onClick = {
            onButtonClick()
        }) {
        Text(
            text = buttonText,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}