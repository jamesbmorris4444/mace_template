package com.mace.mace_template.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mace.mace_template.AppBarState
import com.mace.mace_template.BloodViewModel
import com.mace.mace_template.R
import com.mace.mace_template.ScreenNames
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.DonorWithProducts
import com.mace.mace_template.utils.Utils
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ViewDonorListScreen(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    viewModel: BloodViewModel
) {
    val donorsAndProducts: MutableState<List<DonorWithProducts>> = remember { mutableStateOf(listOf()) }
    var nameConstraint by rememberSaveable { mutableStateOf("") }
    val bloodTypeList = stringArrayResource(R.array.abo_rh_array_with_no_value)
    val aboRhArray: MutableState<Array<String>> = remember { mutableStateOf(bloodTypeList) }
    var aboRhConstraint by rememberSaveable { mutableStateOf(aboRhArray.value[0]) }

    @Composable
    fun DonorsAndProductsList(donorsAndProducts: MutableState<List<DonorWithProducts>>) {
        LazyColumn {
            items(items = donorsAndProducts.value, itemContent = {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(PaddingValues(start = 24.dp, end = 24.dp))
                ) {
                    Row {
                        Text(
                            text = "${it.donor.lastName}, ${it.donor.firstName} ${it.donor.middleName} (${if (it.donor.gender) "Male" else "Female"})",
                            color = colorResource(id = R.color.black),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row {
                        Text(
                            text = "DOB:${it.donor.dob}  AboRh:${it.donor.aboRh}  Branch:${it.donor.branch}",
                            color = colorResource(id = R.color.black),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (it.products.isNotEmpty()) {
                        Divider(color = colorResource(id = R.color.red), thickness = 2.dp)

                    }
                    it.products.forEach {
                        Column(modifier = Modifier
                            .height(IntrinsicSize.Min)
                        ) {
                            Spacer(modifier = Modifier.padding(top = 8.dp))
                            Row {
                                Text(
                                    text = "DIN: ${it.din}   Blood Type: ${it.aboRh}",
                                    color = colorResource(id = R.color.black),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row {
                                Text(
                                    text = "Product Code: ${it.productCode}    Expires: ${it.expirationDate}",
                                    color = colorResource(id = R.color.black),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
            })
        }
    }

    fun handleNameOrAboRhTextEntry() {
        val stagingDatabaseEntries = viewModel.stagingDatabaseDonorAndProductsList()
        val mainDatabaseEntries = viewModel.mainDatabaseDonorAndProductsList()
        val resultList = mainDatabaseEntries.map { mainDonorWithProducts ->
            stagingDatabaseEntries.firstOrNull {
                stagingDonorWithProducts -> Utils.donorComparisonByString(mainDonorWithProducts.donor) == Utils.donorComparisonByString(stagingDonorWithProducts.donor)
            } ?: mainDonorWithProducts
        }
        val newEntryList = stagingDatabaseEntries.filter { stagingDonorWithProducts ->
            mainDatabaseEntries.none {
                mainDonorWithProducts -> Utils.donorComparisonByString(mainDonorWithProducts.donor) == Utils.donorComparisonByString(stagingDonorWithProducts.donor)
            }
        }
        val nameResultList = resultList.plus(newEntryList).filter { finalDonorWithProducts -> Utils.donorLastNameComparisonByString(finalDonorWithProducts.donor).lowercase(Locale.ROOT).startsWith(nameConstraint) }
        val finalResultList = if (aboRhConstraint == aboRhArray.value[0]) {
            nameResultList
        } else {
            nameResultList.filter { finalDonorWithProducts -> Utils.donorBloodTypeComparisonByString(finalDonorWithProducts.donor) == aboRhConstraint }
        }
        donorsAndProducts.value = finalResultList
    }
    val viewDonorListStringName = stringResource(ScreenNames.ViewDonorList.resId)
    LaunchedEffect(key1 = true) {
        LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch ViewDonorList Screen=$viewDonorListStringName")
        onComposing(
            AppBarState(
                title = viewDonorListStringName,
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
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var lastNameTextEntered by rememberSaveable { mutableStateOf("") }
        var aboRhText by rememberSaveable { mutableStateOf(aboRhArray.value[0]) }
        var aboRhExpanded by remember { mutableStateOf(false) }
        val donorSearchStringText = stringResource(R.string.donor_search_view_donor_list_text)
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = lastNameTextEntered,
                onValueChange = {
                    lastNameTextEntered = it
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text(donorSearchStringText) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        nameConstraint = lastNameTextEntered
                        handleNameOrAboRhTextEntry()
                    })
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        ExposedDropdownMenuBox(
            expanded = aboRhExpanded,
            onExpandedChange = {
                aboRhExpanded = !aboRhExpanded
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = aboRhText,
                readOnly = true,
                onValueChange = {
                    aboRhText = it
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text(stringResource(R.string.enter_blood_type_text)) },
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = aboRhExpanded
                    )
                }
            )
            ExposedDropdownMenu(
                expanded = aboRhExpanded,
                onDismissRequest = { aboRhExpanded = false }
            ) {
                aboRhArray.value.forEach { label ->
                    DropdownMenuItem(
                        modifier = Modifier.background(colorResource(R.color.teal_100)),
                        onClick = {
                            aboRhExpanded = false
                            aboRhText = label
                            aboRhConstraint = aboRhText
                            handleNameOrAboRhTextEntry()
                        }
                    ) {
                        Text(
                            text = label
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        if (donorsAndProducts.value.isNotEmpty()) {
            Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
        }
        DonorsAndProductsList(donorsAndProducts)
    }
}