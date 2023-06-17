package com.mace.mace_template.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mace.mace_template.R
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Product

@Composable
fun ProductListContent(
    canScrollVertically: Boolean,
    products: List<Product>,
    onProductsChange: (List<Product>) -> Unit,
    onDinTextChange: (String) -> Unit,
    onProductCodeTextChange: (String) -> Unit,
    onExpirationTextChange: (String) -> Unit
) {
    Column(
        modifier = if (canScrollVertically) Modifier.verticalScroll(rememberScrollState()) else Modifier
    ) {
        LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "columnSize=${products.size}")
        products.forEachIndexed { index, item ->
            LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "index=$index")
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
                                onProductsChange(products.filterIndexed { filterIndex, _ -> filterIndex != index })
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
                                onDinTextChange(products[index].din)
                                onProductCodeTextChange(products[index].productCode)
                                onExpirationTextChange(products[index].expirationDate)
                                onProductsChange(products.filterIndexed { filterIndex, _ -> filterIndex != index })
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
        }
    }
}

@Composable
fun ProductListScreen(
    canScrollVertically: Boolean,
    productList: List<Product>,
    onProductsChange: (List<Product>) -> Unit,
    onDinTextChange: (String) -> Unit = { },
    onProductCodeTextChange: (String) -> Unit = { },
    onExpirationTextChange: (String) -> Unit = { }
) {
    ProductListContent(
        canScrollVertically,
        products = productList,
        onProductsChange = onProductsChange,
        onDinTextChange = onDinTextChange,
        onProductCodeTextChange = onProductCodeTextChange,
        onExpirationTextChange = onExpirationTextChange
    )
}