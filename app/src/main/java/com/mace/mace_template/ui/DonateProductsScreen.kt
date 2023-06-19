package com.mace.mace_template.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
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
import com.mace.mace_template.utils.Constants.LOG_TAG

@Composable
fun DonateProductsScreen(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    onItemButtonClicked: (donor: Donor) -> Unit,
    viewModel: BloodViewModel,
    title: String
) {
    viewModel.setBloodDatabase()
    val isInvalid = viewModel.isBloodDatabaseInvalid()
    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "isInvalid=$isInvalid")
    var completed by remember { mutableStateOf(!isInvalid) }
    if (isInvalid) {
        viewModel.refreshRepository { completed = true }
    }
    DonateProductsHandler(
        onComposing = onComposing,
        canNavigateBack = canNavigateBack,
        navigateUp = navigateUp,
        openDrawer = openDrawer,
        viewModel = viewModel,
        title = title,
        completed = completed,
        onItemButtonClicked = onItemButtonClicked)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DonateProductsHandler(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    viewModel: BloodViewModel,
    title: String,
    completed: Boolean,
    onItemButtonClicked: (donor: Donor) -> Unit
) {
    val donors: MutableState<List<Donor>> = rememberSaveable { mutableStateOf(listOf()) }
    val donateProductsSearchStringName = stringResource(ScreenNames.DonateProductsSearch.resId)

    fun handleSearchClick(searchKey: String) {
        donors.value = viewModel.handleSearchClick(searchKey = searchKey)
    }

    @Composable
    fun CustomCircularProgressBar(){
        CircularProgressIndicator(
            modifier = Modifier.size(120.dp),
            color = Color.Green,
            strokeWidth = 6.dp)
    }

    @Composable
    fun DonorList(donors: MutableState<List<Donor>>, onItemButtonClicked: (donor: Donor) -> Unit) {
        LazyColumn {
            items(items = donors.value) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemButtonClicked(it) }
                ) {
                    DonorElementText(
                        it.firstName,
                        it.middleName,
                        it.lastName,
                        it.dob,
                        it.aboRh,
                        it.branch
                    )
                }
                Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
            }
        }
    }

    LaunchedEffect(key1 = true) {
        LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch DonateProductsScreen=$donateProductsSearchStringName")
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
        val keyboardController = LocalSoftwareKeyboardController.current
        if (completed) {
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
                        label = { Text(stringResource(R.string.initial_letters_of_last_name_text)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                handleSearchClick(text)
                            })
                    )
                }
                WidgetButton(
                    padding = PaddingValues(top = 16.dp),
                    onClick = {
                        onItemButtonClicked(Donor(lastName = "", middleName = "", firstName = "", dob = "", aboRh = "", branch = ""))
                    },
                    buttonText = stringResource(R.string.new_donor_button_text)
                )
                Spacer(modifier = Modifier.height(20.dp))
                if (donors.value.isNotEmpty()) {
                    Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
                }
                DonorList(donors, onItemButtonClicked)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CustomCircularProgressBar()
            }
        }
    }
}