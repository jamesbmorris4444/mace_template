package com.mace.mace_template.ui

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mace.mace_template.AppBarState
import com.mace.mace_template.BloodViewModel
import com.mace.mace_template.R
import com.mace.mace_template.ScreenNames
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.DonorWithProducts
import com.mace.mace_template.repository.storage.Product
import com.mace.mace_template.utils.Constants.LOG_TAG

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReassociateDonationScreen(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    viewModel: BloodViewModel,
    modalView: View,
    title: String
) {
    val incorrectDonorsWithProducts: MutableState<List<DonorWithProducts>> = remember { mutableStateOf(listOf()) }
    val correctDonorsWithProducts: MutableState<List<DonorWithProducts>> = remember { mutableStateOf(listOf()) }
    val singleSelectedProductList: MutableState<List<Product>> = remember { mutableStateOf(listOf()) }
    val reassociateDonationSearchStringName = stringResource(ScreenNames.ReassociateDonation.resId)
    var incorrectDonorWithProducts: DonorWithProducts by remember { mutableStateOf(DonorWithProducts(Donor())) }
    var correctDonorWithProducts: DonorWithProducts by remember { mutableStateOf(DonorWithProducts(Donor())) }
    var incorrectDonorSelected by remember { mutableStateOf(false) }
    var isProductSelected by remember { mutableStateOf(false) }
    var isReassociateCompleted by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun handleSearchClickWithProducts(isCorrectDonorProcessing: Boolean, searchKey: String) {
        if (isCorrectDonorProcessing) {
            correctDonorsWithProducts.value = viewModel.handleSearchClickWithProducts(searchKey = searchKey)
        } else {
            incorrectDonorsWithProducts.value = viewModel.handleSearchClickWithProducts(searchKey = searchKey)
        }
    }

    fun moveProductsToCorrectDonor(correctDonor: Donor) {
        incorrectDonorsWithProducts.value.map { donorWithProducts ->
            donorWithProducts.products.map { product ->
                if (product.removedForReassociation) product.donorId = correctDonor.id
            }
            viewModel.insertReassociatedProductsIntoDatabase(correctDonor, donorWithProducts.products)
            StandardModalComposeView(
                composeView = modalView,
                topIconResId = R.drawable.notification,
                titleText = viewModel.getResources().getString(R.string.made_reassociate_entries_body_text),
                positiveText = viewModel.getResources().getString(R.string.positive_button_text_ok),
            ) {
                correctDonorWithProducts = viewModel.donorFromNameAndDateWithProducts(correctDonor)
                isReassociateCompleted = true
            }.show()
        }
    }

    @Composable
    fun DonorListWithProducts(
        isCorrectDonorProcessing: Boolean,
        donorsWithProducts: List<DonorWithProducts>,
        displayForDonor: (DonorWithProducts) -> Boolean,
        enablerForDonor: (DonorWithProducts) -> Boolean,
        enablerForProducts: (Product) -> Boolean
    ) {
        LazyColumn {
            items(items = donorsWithProducts) {
                if (displayForDonor(it)) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = enablerForDonor(it)
                        ) {
                            if (incorrectDonorSelected) {
                                moveProductsToCorrectDonor(it.donor)
                            } else {
                                if (isCorrectDonorProcessing) {
                                    correctDonorsWithProducts.value = listOf(it)
                                    correctDonorWithProducts = it
                                } else {
                                    incorrectDonorsWithProducts.value = listOf(it)
                                    incorrectDonorWithProducts = it
                                    incorrectDonorSelected = true
                                }
                            }
                        }
                    ) {
                        DonorElementText(
                            it.donor.firstName,
                            it.donor.middleName,
                            it.donor.lastName,
                            it.donor.dob,
                            it.donor.aboRh,
                            it.donor.branch,
                            it.donor.gender
                        )
                    }
                    Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
                    ProductListScreen(
                        canScrollVertically = false,
                        productList = it.products,
                        useOnProductsChange = false,
                        onProductSelected = { productList ->
                            singleSelectedProductList.value = productList
                            isProductSelected = true
                        },
                        enablerForProducts = enablerForProducts
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch ReassociateDonationScreen=$reassociateDonationSearchStringName")
        onComposing(
            AppBarState(
                title = title,
                actions = {
                    IconButton(onClick = openDrawer) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu_content_description)
                        )
                    }
                },
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = navigateUp) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button_content_description)
                            )
                        }
                    }
                }
            )
        )
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(start = 24.dp, end = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isReassociateCompleted) {
                // Fourth (Last) run
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = stringResource(R.string.reassociate_complete_title),
                    color = colorResource(id = R.color.black),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
                DonorListWithProducts(
                    true,
                    listOf(correctDonorWithProducts),
                    displayForDonor = { true },
                    enablerForDonor = { false },
                    enablerForProducts = { false }
                )
            } else {
                if (incorrectDonorSelected) {
                    // Second run
                    if (isProductSelected) {
                        // Third run
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = stringResource(R.string.incorrect_donor_and_product_title),
                            color = colorResource(id = R.color.black),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
                        DonorListWithProducts(
                            true,
                            listOf(DonorWithProducts(donor = incorrectDonorWithProducts.donor, products = singleSelectedProductList.value)),
                            displayForDonor = { true },
                            enablerForDonor = { false },
                            enablerForProducts = { false }
                        )
                    } else {
                        // Second run
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = stringResource(R.string.incorrect_donor_title),
                            color = colorResource(id = R.color.black),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = stringResource(R.string.choose_product_for_reassociation_title),
                            color = colorResource(id = R.color.red),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
                        DonorListWithProducts(
                            true,
                            incorrectDonorsWithProducts.value,
                            displayForDonor = { true },
                            enablerForDonor = { false },
                            enablerForProducts = { true }
                        )
                    }
                    if (isProductSelected) {
                        // Third run
                        Spacer(modifier = Modifier.height(4.dp))
                        var text by rememberSaveable { mutableStateOf("") }
                        OutlinedTextField(
                            modifier = Modifier
                                .height(60.dp),
                            value = text,
                            onValueChange = {
                                text = it
                            },
                            shape = RoundedCornerShape(10.dp),
                            label = { Text(stringResource(R.string.initial_letters_of_correct_donor_last_name_text)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    handleSearchClickWithProducts(true, text)
                                })
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (correctDonorsWithProducts.value.isNotEmpty()) {
                            Text(
                                modifier = Modifier.align(Alignment.Start),
                                text = stringResource(R.string.choose_correct_donor_title),
                                color = colorResource(id = R.color.red),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
                        DonorListWithProducts(
                            true,
                            correctDonorsWithProducts.value,
                            displayForDonor = { true },
                            enablerForDonor = { true },
                            enablerForProducts = { false }
                        )

                    }
                } else {
                    // First run
                    var text by remember { mutableStateOf("") }
                    OutlinedTextField(
                        modifier = Modifier
                            .height(60.dp),
                        value = text,
                        onValueChange = {
                            text = it
                        },

                        shape = RoundedCornerShape(10.dp),
                        label = { Text(stringResource(R.string.initial_letters_of_incorrect_donor_last_name_text)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                handleSearchClickWithProducts(false, text)
                            })
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (incorrectDonorsWithProducts.value.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Start),
                            text = stringResource(R.string.choose_incorrect_donor_title),
                            color = colorResource(id = R.color.red),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
                    }
                    DonorListWithProducts(
                        false,
                        incorrectDonorsWithProducts.value,
                        displayForDonor = { donorWithProducts -> donorWithProducts.products.isNotEmpty() },
                        enablerForDonor = { true },
                        enablerForProducts = { false }
                    )
                }
            }
        }
    }
}