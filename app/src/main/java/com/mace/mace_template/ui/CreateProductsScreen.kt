package com.mace.mace_template.ui

import android.annotation.SuppressLint
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mace.mace_template.AppBarState
import com.mace.mace_template.BloodViewModel
import com.mace.mace_template.R
import com.mace.mace_template.ScreenNames
import com.mace.mace_template.repository.DatabaseSelector
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.Product

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun CreateProductsScreen(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    donor: Donor,
    modalView: View,
    viewModel: BloodViewModel,
    onCompleteButtonClicked: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val leftGridPadding = 20.dp
    val rightGridPadding = 20.dp
    val horizontalGridWidth = screenWidth - leftGridPadding - rightGridPadding
    val horizontalGridHeight = horizontalGridWidth * 2 / 3
    val gridCellWidth = horizontalGridWidth / 2
    val gridCellHeight = horizontalGridHeight / 2
    var dinText by rememberSaveable { mutableStateOf("") }
    var productCodeText by rememberSaveable { mutableStateOf("") }
    var expirationText by rememberSaveable { mutableStateOf("") }
    val createProductsStringName = stringResource(ScreenNames.CreateProducts.resId)
    val enterDinText = stringResource(R.string.enter_din_text)
    val dinTitle = stringResource(R.string.din_title)
    val enterProductCodeText = stringResource(R.string.enter_product_code)
    val productCodeTitle = stringResource(R.string.product_code_title)
    val enterExpirationText = stringResource(R.string.enter_expiration_text)
    val expirationTitle = stringResource(R.string.expiration_title)
    val aboRhTitle = stringResource(R.string.abo_rh_title)
    var clearButtonVisible by remember { mutableStateOf(true) }
    var confirmButtonVisible by remember { mutableStateOf(true) }
    var confirmNeeded by remember { mutableStateOf(false) }
    val products: MutableState<List<Product>> = remember { mutableStateOf(mutableListOf()) }

    fun processNewProduct() {
        val product = Product()
        val productList: MutableList<Product> = products.value.toMutableList()
        product.din = dinText
        product.aboRh = donor.aboRh
        product.productCode = productCodeText
        product.expirationDate = expirationText
        product.donorId = donor.id
        productList.add(product)
        products.value = productList
    }

    fun addDonorWithProductsToModifiedDatabase() {
        products.value.map { product ->
            product.donorId = donor.id
        }
        viewModel.insertDonorAndProductsIntoDatabase(modalView, DatabaseSelector.STAGING_DB, donor, products.value)
    }

    fun onClearClicked() {
        dinText = ""
        productCodeText = ""
        expirationText = ""
        clearButtonVisible = false
        confirmButtonVisible = false
        confirmNeeded = false
    }

    fun onConfirmClicked() {
        clearButtonVisible = true
        confirmButtonVisible = true
        confirmNeeded = false
        processNewProduct()
    }

    fun onCompleteClicked() {
        if (confirmNeeded) {
            StandardModalComposeView(
                modalView,
                topIconResId = R.drawable.notification,
                titleText = viewModel.getResources().getString(R.string.std_modal_noconfirm_title),
                bodyText = viewModel.getResources().getString(R.string.std_modal_noconfirm_body),
                positiveText = viewModel.getResources().getString(R.string.positive_button_text_yes),
                negativeText = viewModel.getResources().getString(R.string.negative_button_text_no),
            ) { dismissSelector ->
                when (dismissSelector) {
                    DismissSelector.POSITIVE -> {
                        processNewProduct()
                        addDonorWithProductsToModifiedDatabase()
                    }
                    else -> { }
                }
            }.show()
        } else {
            if (products.value.isNotEmpty()) {
                addDonorWithProductsToModifiedDatabase()
            }
        }
        onCompleteButtonClicked()
    }

    @Composable
    fun ProductList(products: MutableState<List<Product>>) {
        if (products.value.isNotEmpty()) {
            Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
        }
        LazyColumn {
            itemsIndexed(items = products.value, itemContent = { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .padding(start = 40.dp)
                            .height(40.dp)
                            .width(30.dp)
                            .clickable(
                                enabled = true,
                                onClick = {
                                    products.value = products.value.filterIndexed { filterIndex, _ -> filterIndex != index  }
                                }
                            ),
                        painter = painterResource(id = R.drawable.delete_icon),
                        contentDescription = "Dialog Alert"
                    )
                    Image(
                        modifier = Modifier
                            .padding(start = 40.dp)
                            .height(40.dp)
                            .width(40.dp)
                            .clickable(
                                enabled = true,
                                onClick = {
                                    dinText = products.value[index].din
                                    productCodeText = products.value[index].productCode
                                    expirationText = products.value[index].expirationDate
                                    products.value = products.value.filterIndexed { filterIndex, _ -> filterIndex != index  }
                                }
                            ),
                        painter = painterResource(id = R.drawable.edit_icon),
                        contentDescription = "Dialog Alert"
                    )
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp)
                    ) {
                        Text(
                            text = item.din,
                            color = colorResource(id = R.color.black),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = item.aboRh,
                            color = colorResource(id = R.color.black),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = item.productCode,
                            color = colorResource(id = R.color.black),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = item.expirationDate,
                            color = colorResource(id = R.color.black),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
            })
        }
    }

    LaunchedEffect(key1 = true) {
        onComposing(
            AppBarState(
                title = createProductsStringName,
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.height(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text (
                modifier = Modifier
                    .padding(PaddingValues(start = leftGridPadding)),
                text = String.format(stringResource(R.string.create_products_header_text), donor.lastName, donor.firstName)
            )
        }
        LazyVerticalGrid(
            modifier = Modifier
                .padding(PaddingValues(start = leftGridPadding, end = rightGridPadding)),
            columns = GridCells.Fixed(2)
        ) {
            item {
                LazyHorizontalGrid(
                    modifier = Modifier
                        .height(horizontalGridHeight),
                    rows = GridCells.Fixed(2)
                ) {
                    item { // upper left
                        Box(
                            modifier = Modifier
                                .size(gridCellWidth, gridCellHeight)
                                .borders(2.dp, DarkGray, left = true, top = true, bottom = true)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .height(80.dp)
                                    .padding(PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp))
                                    .align(Alignment.BottomStart),
                                value = dinText,
                                onValueChange = {
                                    dinText = it
                                    confirmNeeded = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                label = { Text(enterDinText) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(PaddingValues(start = 8.dp))
                                    .align(Alignment.TopStart),
                                text = dinTitle
                            )
                        }
                    }
                    item { // lower left
                        Box(
                            modifier = Modifier
                                .size(gridCellWidth, gridCellHeight)
                                .borders(2.dp, DarkGray, left = true, bottom = true)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .height(80.dp)
                                    .padding(PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp))
                                    .align(Alignment.BottomStart),
                                value = productCodeText,
                                onValueChange = {
                                    productCodeText = it
                                    confirmNeeded = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                label = { Text(enterProductCodeText) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(PaddingValues(start = 8.dp))
                                    .align(Alignment.TopStart),
                                text = productCodeTitle
                            )
                        }
                    }
                }
            }
            item {
                LazyHorizontalGrid(
                    modifier = Modifier
                        .height(horizontalGridHeight),
                    rows = GridCells.Fixed(2)
                ) {
                    item { // upper right
                        Box(
                            modifier = Modifier
                                .size(gridCellWidth, gridCellHeight)
                                .borders(
                                    2.dp,
                                    DarkGray,
                                    left = true,
                                    top = true,
                                    right = true,
                                    bottom = true
                                )
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(PaddingValues(start = 8.dp))
                                    .align(Alignment.TopStart),
                                text = aboRhTitle
                            )
                            Text(
                                modifier = Modifier
                                    .padding(PaddingValues(bottom = 32.dp))
                                    .align(Alignment.BottomCenter),
                                text = donor.aboRh
                            )
                        }
                    }
                    item { // lower right
                        Box(
                            modifier = Modifier
                                .size(gridCellWidth, gridCellHeight)
                                .borders(2.dp, DarkGray, left = true, right = true, bottom = true)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .height(80.dp)
                                    .padding(PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp))
                                    .align(Alignment.BottomStart),
                                value = expirationText,
                                onValueChange = {
                                    expirationText = it
                                    confirmNeeded = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                label = { Text(enterExpirationText) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(PaddingValues(start = 8.dp))
                                    .align(Alignment.TopStart),
                                text = expirationTitle
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            Button(
                modifier = Modifier.padding(PaddingValues(start = 8.dp, end = 8.dp)),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                onClick = {
                    onClearClicked()
                    keyboardController?.hide()
                }) {
                Text(
                    text = stringResource(R.string.clear_button_text),
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            Button(
                modifier = Modifier.padding(PaddingValues(start = 8.dp, end = 8.dp)),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                onClick = {
                    onConfirmClicked()
                    keyboardController?.hide()
                }) {
                Text(
                    text = stringResource(R.string.confirm_button_text),
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            Button(
                modifier = Modifier.padding(PaddingValues(start = 8.dp, end = 8.dp)),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                onClick = {
                    onCompleteClicked()
                    keyboardController?.hide()
                }) {
                Text(
                    text = stringResource(R.string.complete_button_text),
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
        ProductList(products)
    }
}

private fun Modifier.borders(strokeWidth: Dp, color: Color, left: Boolean = false, top: Boolean = false, right: Boolean = false, bottom: Boolean = false) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2
            if (left) {
                drawLine(
                    color = color,
                    start = Offset(x = 0f, y = height),
                    end = Offset(x = 0f , y = 0f),
                    strokeWidth = strokeWidthPx
                )
            }
            if (top) {
                drawLine(
                    color = color,
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = width , y = 0f),
                    strokeWidth = strokeWidthPx
                )
            }
            if (right) {
                drawLine(
                    color = color,
                    start = Offset(x = width, y = 0f),
                    end = Offset(x = width , y = height),
                    strokeWidth = strokeWidthPx
                )
            }
            if (bottom) {
                drawLine(
                    color = color,
                    start = Offset(x = width, y = height),
                    end = Offset(x = 0f , y = height),
                    strokeWidth = strokeWidthPx
                )
            }
        }
    }
)
