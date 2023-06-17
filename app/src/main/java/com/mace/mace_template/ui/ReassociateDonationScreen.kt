package com.mace.mace_template.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.mace.mace_template.repository.storage.DonorWithProducts
import com.mace.mace_template.repository.storage.Product

@Composable
fun ReassociateDonationScreen(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    onItemButtonClicked: (donorWithProducts: DonorWithProducts) -> Unit,
    viewModel: BloodViewModel,
    title: String,
    modifier: Modifier = Modifier
) {
    ReassociateDonationHandler(
        onComposing = onComposing,
        canNavigateBack = canNavigateBack,
        navigateUp = navigateUp,
        openDrawer = openDrawer,
        viewModel = viewModel,
        title = title,
        onItemButtonClicked = onItemButtonClicked,
        modifier = modifier)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReassociateDonationHandler(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    viewModel: BloodViewModel,
    title: String,
    onItemButtonClicked: (donorWithProducts: DonorWithProducts) -> Unit,
    modifier: Modifier = Modifier
) {
    val donorsWithProducts: MutableState<List<DonorWithProducts>> = rememberSaveable { mutableStateOf(listOf()) }
    var products: MutableState<List<Product>>
    val reassociateDonationSearchStringName = stringResource(ScreenNames.ReassociateDonation.resId)

    fun handleSearchClickWithProducts(searchKey: String) {
        donorsWithProducts.value = viewModel.handleSearchClickWithProducts(searchKey = searchKey)
    }

    @Composable
    fun DonorListWithProducts(onItemButtonClicked: (donor: DonorWithProducts) -> Unit) {
        LazyColumn {
            items(items = donorsWithProducts.value) {
                products = remember { mutableStateOf(it.products) }
                LogUtils.D("JIMX", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "products SIZE=${products.value.size}")
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemButtonClicked(it) }
                ) {
                    Text(
                        text = it.donor.lastName,
                        color = colorResource(id = R.color.black),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = it.donor.firstName,
                        color = colorResource(id = R.color.black),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = it.donor.middleName,
                        color = colorResource(id = R.color.black),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = it.donor.dob,
                        color = colorResource(id = R.color.black),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = it.donor.aboRh,
                        color = colorResource(id = R.color.black),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = it.donor.branch,
                        color = colorResource(id = R.color.black),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
                ProductListScreen(
                    canScrollVertically = false,
                    productList = products.value,
                    onProductsChange = { productList -> products.value = productList },
                )
            }
        }
    }

    LaunchedEffect(key1 = true) {
        LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch ReassociateDonationScreen=$reassociateDonationSearchStringName")
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
    BoxWithConstraints(modifier = modifier.fillMaxWidth(1f)) {
        val keyboardController = LocalSoftwareKeyboardController.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                var text by rememberSaveable { mutableStateOf("") }
                OutlinedTextField(
                    modifier = Modifier
                        .weight(0.7f)
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
                            handleSearchClickWithProducts(text)
                        })
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (donorsWithProducts.value.isNotEmpty()) {
                Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
            }
            DonorListWithProducts(onItemButtonClicked)
        }
    }
}