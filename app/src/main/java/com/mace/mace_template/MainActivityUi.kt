package com.mace.mace_template

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

// See https://www.geeksforgeeks.org/android-jetpack-compose-implement-navigation-drawer/ for Navigation Drawer

@Composable
fun DrawerAppComponent(
    view: View,
    bloodViewModel: BloodViewModel,
    requestedScreen: ScreenNames
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val currentScreen = remember { mutableStateOf(requestedScreen) }
    val coroutineScope = rememberCoroutineScope()
    val navController: NavHostController = rememberNavController()

    @Composable
    fun DrawerContentComponent(
        currentScreen: MutableState<ScreenNames>,
        navController: NavHostController,
        closeDrawer: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .padding(top = 100.dp)
                .background(color = colorResource(id = R.color.black)),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .align(CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fs_logo),
                    contentDescription = stringResource(id = R.string.fs_logo_content_description),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(120.dp)
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    text = stringResource(R.string.walking_blood_bank_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.white)
                )
            }
            Spacer(Modifier.height(24.dp))
            for (screen in ScreenNames.values()) {
                if (screen.inDrawer) {
                    Column(
                        Modifier.clickable(onClick = {
                            closeDrawer()
                            currentScreen.value = screen
                            navController.navigate(view.context.resources.getString(screen.resId))
                        }),
                        content = {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = if (currentScreen.value == screen) {
                                    colorResource(id = R.color.white)
                                } else {
                                    colorResource(id = R.color.darkMagenta)
                                }
                            ) {
                                Text(
                                    text = view.context.resources.getString(screen.resId),
                                    modifier = Modifier.padding(16.dp),
                                    color = if (currentScreen.value == screen) {
                                        colorResource(id = R.color.black)
                                    } else {
                                        colorResource(id = R.color.white)
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun BodyContentComponent(
        view: View,
        currentScreen: ScreenNames,
        openDrawer: () -> Unit,
        bloodViewModel: BloodViewModel
    ) {
        ScreenNavigator(view, viewModel = bloodViewModel, currentScreen = currentScreen, openDrawer = openDrawer, navController = navController)
    }

    ModalDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DrawerContentComponent(
                currentScreen = currentScreen,
                navController = navController,
                closeDrawer = { coroutineScope.launch { drawerState.close() } }
            )
        },
        drawerBackgroundColor = colorResource(id = R.color.black),
        content = {
            BodyContentComponent(
                view = view,
                currentScreen = currentScreen.value,
                openDrawer = { coroutineScope.launch { drawerState.open() } },
                bloodViewModel = bloodViewModel
            )
        }
    )
}

enum class ScreenNames(val inDrawer: Boolean, val resId: Int) {
    DonateProductsSearch(true, R.string.Search_for_donor_title),
    CreateProducts(false, R.string.create_blood_product_title),
    ManageDonorAfterSearch(false, R.string.manage_donor_after_search_title),
    ManageDonorFromDrawer(true, R.string.manage_donor_from_drawer_title),
    ReassociateDonation(true, R.string.reassociate_donation_title),
    ViewDonorList(true, R.string.view_donor_list_title)
}