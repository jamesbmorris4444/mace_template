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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mace.mace_template.AppBarState
import com.mace.mace_template.BloodViewModel
import com.mace.mace_template.R
import com.mace.mace_template.ScreenNames
import com.mace.mace_template.StandardModalArgs
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.utils.Constants.LOG_TAG

@Composable
fun DonateProductsScreen(
    configAppBar: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    onItemButtonClicked: (donor: Donor) -> Unit,
    viewModel: BloodViewModel,
    title: String
) {

    @Composable
    fun CustomCircularProgressBar(){
        CircularProgressIndicator(
            modifier = Modifier.size(120.dp),
            color = Color.Green,
            strokeWidth = 6.dp)
    }

    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "Compose: ${ScreenNames.DonateProductsSearch.name}")
    viewModel.setBloodDatabase()
    val showStandardModalState = viewModel.showStandardModalState.observeAsState().value ?: StandardModalArgs()
    val isInvalid = viewModel.databaseInvalidState.observeAsState().value ?: viewModel.isBloodDatabaseInvalid()
    val completed = viewModel.refreshCompletedState.observeAsState().value ?: false
    val failure = viewModel.refreshFailureState.observeAsState().value ?: ""
    when {
        isInvalid -> {
            viewModel.refreshRepository() // updates isInvalid, completed, and failure when API call completes
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CustomCircularProgressBar()
            }
        }
        failure.isNotEmpty() -> {
            if (showStandardModalState.topIconResId >= 0) {
                StandardModal(
                    showStandardModalState.topIconResId,
                    showStandardModalState.titleText,
                    showStandardModalState.bodyText,
                    showStandardModalState.positiveText,
                    showStandardModalState.negativeText,
                    showStandardModalState.neutralText,
                    showStandardModalState.onDismiss
                )
            } else {
                viewModel.changeShowStandardModalState(
                    StandardModalArgs(
                        topIconResId = R.drawable.notification,
                        titleText = viewModel.getResources().getString(R.string.failure_db_entries_title_text),
                        bodyText = viewModel.getResources().getString(R.string.failure_db_entries_body_text, failure),
                        positiveText = viewModel.getResources().getString(R.string.positive_button_text_ok),
                    ) {
                        navigateUp()
                        viewModel.changeShowStandardModalState(StandardModalArgs())
                        viewModel.changeRefreshFailureState("")
                    }
                )
            }
        }
        completed -> {
            DonateProductsHandler(
                configAppBar = configAppBar,
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp,
                openDrawer = openDrawer,
                viewModel = viewModel,
                title = title,
                onItemButtonClicked = onItemButtonClicked)
        }
        else -> {
            viewModel.changeDatabaseInvalidState(false)
            viewModel.changeRefreshCompletedState(true)
            viewModel.changeRefreshFailureState("")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DonateProductsHandler(
    configAppBar: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    viewModel: BloodViewModel,
    title: String,
    onItemButtonClicked: (donor: Donor) -> Unit
) {
    val donors = viewModel.donorsAvailableState.observeAsState().value ?: listOf()

    fun handleSearchClick(searchKey: String) {
        viewModel.handleSearchClick(searchKey = searchKey)
    }

    @Composable
    fun DonorList(donors: List<Donor>, onItemButtonClicked: (donor: Donor) -> Unit) {
        LazyColumn(
            modifier = Modifier
                .testTag("LazyColumn")
        ) {
            items(items = donors) {
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
                        it.branch,
                        it.gender
                    )
                }
                Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
            }
        }
    }

    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch DonateProductsScreen=$title    donors=$donors")

    LaunchedEffect(key1 = true) {
        configAppBar(
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
                        IconButton(onClick = navigateUp.also { viewModel.resetDonateProductsScreen() }) {
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
                        .height(60.dp)
                        .testTag("OutlinedTextField"),
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
                    viewModel.resetDonateProductsScreen()
                },
                buttonText = stringResource(R.string.new_donor_button_text)
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (donors.isNotEmpty()) {
                Divider(color = colorResource(id = R.color.black), thickness = 2.dp)
            }
            DonorList(donors, onItemButtonClicked)
        }
    }
}