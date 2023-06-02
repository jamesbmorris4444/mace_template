package com.mace.mace_template.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mace.mace_template.AppBarState
import com.mace.mace_template.DrawerAppScreen
import com.mace.mace_template.R
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Donor

@Composable
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun ManageDonorScreen(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    donor: Donor,
    onUpdateButtonClicked: () -> Unit
) {
    ManageDonorHandler(
        onComposing = onComposing,
        canNavigateBack = canNavigateBack,
        navigateUp = navigateUp,
        openDrawer = openDrawer,
        donor = donor,
        onUpdateButtonClicked = onUpdateButtonClicked)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDonorHandler(
    onComposing: (AppBarState) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    donor: Donor,
    onUpdateButtonClicked: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch ManageDonorScreen=${DrawerAppScreen.ManageDonor.screenName}")
        onComposing(
            AppBarState(
                title = DrawerAppScreen.ManageDonor.screenName,
                actions = {
                    if (canNavigateBack) {
                        IconButton(onClick = navigateUp) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button_content_description)
                            )
                        }
                    }
                    IconButton(onClick = openDrawer) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu_content_description)
                        )
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = stringResource(R.string.menu_content_description)

                    )
                }
            )
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            var lastNameText by rememberSaveable { mutableStateOf(donor.lastName) }
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = lastNameText,
                onValueChange = {
                    lastNameText = it
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text("Enter Last Name") },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            var firstNameText by rememberSaveable { mutableStateOf(donor.firstName) }
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = firstNameText,
                onValueChange = {
                    firstNameText = it
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text("Enter First Name") },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            var middleNameText by rememberSaveable { mutableStateOf(donor.middleName) }
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = middleNameText,
                onValueChange = {
                    middleNameText = it
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text("Enter Middle Name") },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            var dobText by rememberSaveable { mutableStateOf(donor.dob) }
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = dobText,
                onValueChange = {
                    dobText = it
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text("Enter DOB: 13 Mar 2022") },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
            HorizontalRadioButtons(donor.gender)
        }
        Column {
            var aboRhText by rememberSaveable { mutableStateOf(donor.aboRh) }
            var expanded by remember { mutableStateOf(false) }
            val icon = if (expanded)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = aboRhText,
                onValueChange = {
                    aboRhText = it
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text("Enter Blood Type:") },
                singleLine = true,
                trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable { expanded = !expanded })
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .height(60.dp)
            ) {
                val aboRhArray = stringArrayResource(R.array.abo_rh_array)
                aboRhArray.forEach { label ->
                    DropdownMenuItem(onClick = { expanded = false; aboRhText = label }) { Text(text = label) }
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Column {
            var branchText by rememberSaveable { mutableStateOf(donor.branch) }
            var expanded by remember { mutableStateOf(false) }
            val icon = if (expanded)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown
            OutlinedTextField(
                modifier = Modifier
                    .height(60.dp),
                value = branchText,
                onValueChange = {
                    branchText = it
                },
                shape = RoundedCornerShape(10.dp),
                label = { Text("Enter Branch:") },
                singleLine = true,
                trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable { expanded = !expanded })
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .height(60.dp)
            ) {
                val branchArray = stringArrayResource(R.array.military_branch_array)
                branchArray.forEach { label ->
                    DropdownMenuItem(onClick = { expanded = false; branchText = label }) { Text(text = label) }
                }
            }
        }
    }
}

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

@Preview
@Composable
fun DefaultPreview() {
    HorizontalRadioButtons(true)
}