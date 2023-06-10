package com.mace.mace_template.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mace.mace_template.AppBarState
import com.mace.mace_template.BloodViewModel
import com.mace.mace_template.DrawerAppScreen
import com.mace.mace_template.R
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.Product
import kotlinx.coroutines.delay

@Composable
fun CreateProductsScreen(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    donor: Donor,
    onCompleteButtonClicked: (product: Product) -> Unit,
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    CreateProductsHandler(
        onComposing = onComposing,
        canNavigateBack = canNavigateBack,
        navigateUp = navigateUp,
        openDrawer = openDrawer,
        donor = donor,
//        viewModel = viewModel,
//        value = completed.value,
//        onCompleteButtonClicked = onCompleteButtonClicked,
//        modifier = modifier
    )
}

@Composable
fun CreateProductsHandler(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    donor: Donor,
//    viewModel: BloodViewModel,
//    value: Boolean,
//    onCompleteButtonClicked: (product: Product) -> Unit,
//    modifier: Modifier = Modifier
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
    val createProductsStringName = stringResource(DrawerAppScreen.CreateProducts.resId)
    val enterDinText = stringResource(R.string.enter_din_text)
    val dinTitle = stringResource(R.string.din_title)
    val enterProductCodeText = stringResource(R.string.enter_product_code)
    val productCodeTitle = stringResource(R.string.product_code_title)
    val enterExpirationText = stringResource(R.string.enter_expiration_text)
    val expirationTitle = stringResource(R.string.expiration_title)
    val aboRhTitle = stringResource(R.string.abo_rh_title)
    LaunchedEffect(key1 = true) {
        LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch DonateProductsScreen=$createProductsStringName")
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
