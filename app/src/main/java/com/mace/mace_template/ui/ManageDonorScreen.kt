package com.mace.mace_template.ui

import android.annotation.SuppressLint
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
import com.mace.mace_template.BloodViewModel
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
    viewModel: BloodViewModel,
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

@OptIn(ExperimentalMaterialApi::class)
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
        var aboRhExpanded by remember { mutableStateOf(false) }
        var branchExpanded by remember { mutableStateOf(false) }
        var lastNameText by rememberSaveable { mutableStateOf(donor.lastName) }
        var firstNameText by rememberSaveable { mutableStateOf(donor.firstName) }
        var middleNameText by rememberSaveable { mutableStateOf(donor.middleName) }
        var dobText by rememberSaveable { mutableStateOf(donor.dob) }
        var aboRhText by rememberSaveable { mutableStateOf(donor.aboRh) }
        var branchText by rememberSaveable { mutableStateOf(donor.branch) }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row {
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
                label = { Text("Enter Blood Type:") },
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
                label = { Text("Enter Branch:") },
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
        Button(
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = {
                onUpdateButtonClicked()
            }) {
            Text(
                text = stringResource(R.string.update_button_text),
                fontSize = 16.sp,
                color = Color.White
            )
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