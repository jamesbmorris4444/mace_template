package com.mace.mace_template.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mace.mace_template.AppBarState
import com.mace.mace_template.R
import com.mace.mace_template.ScreenNames
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Donor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManageDonorScreen(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    donor: Donor,
    onUpdateButtonClicked: (databaseModified: Boolean) -> Unit
) {
    val manageDonorSearchStringName = stringResource(ScreenNames.ManageDonorSearch.resId)
    LaunchedEffect(key1 = true) {
        LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch ManageDonorScreen=$manageDonorSearchStringName")
        onComposing(
            AppBarState(
                title = manageDonorSearchStringName,
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
    val stateVertical = rememberScrollState(0)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(state = stateVertical),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var databaseModified by remember { mutableStateOf(false) }
        var aboRhExpanded by remember { mutableStateOf(false) }
        var branchExpanded by remember { mutableStateOf(false) }
        var lastNameText by rememberSaveable { mutableStateOf(donor.lastName) }
        var firstNameText by rememberSaveable { mutableStateOf(donor.firstName) }
        var middleNameText by rememberSaveable { mutableStateOf(donor.middleName) }
        var dobText by rememberSaveable { mutableStateOf(donor.dob) }
        var aboRhText by rememberSaveable { mutableStateOf(donor.aboRh) }
        var branchText by rememberSaveable { mutableStateOf(donor.branch) }
        val enterLastNameText = stringResource(R.string.enter_last_name_text)
        val enterFirstNameText = stringResource(R.string.enter_first_name_text)
        val enterMiddleNameText = stringResource(R.string.enter_middle_name_text)
        val enterDobText = stringResource(R.string.enter_dob_text)
        val enterBloodTtypeText = stringResource(R.string.enter_blood_type_text)
        val enterBranchText = stringResource(R.string.enter_branch_text)

        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = lastNameText,
                onValueChange = {
                    lastNameText = it
                    databaseModified = true
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text(enterLastNameText) },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = firstNameText,
                onValueChange = {
                    firstNameText = it
                    databaseModified = true
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text(enterFirstNameText) },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = middleNameText,
                onValueChange = {
                    middleNameText = it
                    databaseModified = true
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text(enterMiddleNameText) },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = dobText,
                onValueChange = {
                    dobText = it
                    databaseModified = true
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text(enterDobText) },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            HorizontalRadioButtons(donor.gender)
        }
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
                label = { Text(enterBloodTtypeText) },
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
                val aboRhArray = stringArrayResource(R.array.abo_rh_array)
                aboRhArray.forEach { label ->
                    DropdownMenuItem(
                        modifier = Modifier.background(colorResource(R.color.teal_100)),
                        onClick = {
                            aboRhExpanded = false
                            aboRhText = label
                            databaseModified = true
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
        ExposedDropdownMenuBox(
            expanded = branchExpanded,
            onExpandedChange = {
                branchExpanded = !branchExpanded
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = branchText,
                readOnly = true,
                onValueChange = {
                    branchText = it
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text(enterBranchText) },
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = branchExpanded
                    )
                }
            )
            ExposedDropdownMenu(
                expanded = branchExpanded,
                onDismissRequest = { branchExpanded = false }
            ) {
                val branchArray = stringArrayResource(R.array.military_branch_array)
                branchArray.forEach { label ->
                    DropdownMenuItem(
                        modifier = Modifier.background(colorResource(R.color.teal_100)),
                        onClick = {
                            branchExpanded = false
                            branchText = label
                            databaseModified = true
                        }
                    ) {
                        Text(
                            text = label
                        )
                    }
                }
            }
        }
        Button(
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = {
                onUpdateButtonClicked(databaseModified || radioButtonChanged)
                radioButtonChanged = false
            }) {
            Text(
                text = stringResource(R.string.update_button_text),
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

private var radioButtonChanged = false
@Composable
fun HorizontalRadioButtons(isMale: Boolean) {
    val radioOptions = listOf("Male", "Female")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[if (isMale) 0 else 1]) }
    Row {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .height(60.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) }
                    )
                    .padding(horizontal = 16.dp)
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = {
                        radioButtonChanged = true
                        onOptionSelected(text)
                    }
                )
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = text
                )
            }
        }
    }
}