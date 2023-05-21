package com.mace.mace_template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.fullsekurity.theatreblood.logger.LogUtils
import com.mace.mace_template.ui.theme.MaceTemplateTheme
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        Timber.plant(Timber.DebugTree())
        setContent {
            MaceTemplateTheme {
                DrawerAppComponent(BloodViewModel(application), DrawerAppScreen.DonateProductsSearch)
            }
        }
    }
}

// See https://www.geeksforgeeks.org/android-jetpack-compose-implement-navigation-drawer/ for Navigation Drawer

@Composable
fun DrawerAppComponent(bloodViewModel: BloodViewModel, requestedScreen: DrawerAppScreen) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val currentScreen = remember { mutableStateOf(requestedScreen) }
    val coroutineScope = rememberCoroutineScope()
    bloodViewModel.setBloodDatabase()
    ModalDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DrawerContentComponent(
                currentScreen = currentScreen,
                closeDrawer = { coroutineScope.launch { drawerState.close() } }
            )
        },
        drawerBackgroundColor = colorResource(id = R.color.black),
        content = {
            BodyContentComponent(
                currentScreen = currentScreen.value,
                openDrawer = { coroutineScope.launch { drawerState.open() } },
                bloodViewModel = bloodViewModel
            )
        }
    )
}

@Composable
fun DrawerContentComponent(
    currentScreen: MutableState<DrawerAppScreen>,
    closeDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 200.dp)
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
                text = "Walking Blood Bank",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.white)
            )
        }
        Spacer(Modifier.height(24.dp))
        for (screen in DrawerAppScreen.values()) {
            Column(
                Modifier.clickable(onClick = {
                    closeDrawer()
                    currentScreen.value = screen
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
                            text = screen.name,
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

@Composable
fun BodyContentComponent(
    currentScreen: DrawerAppScreen,
    openDrawer: () -> Unit,
    bloodViewModel: BloodViewModel
) {
    LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "BodyContentComponent in MainActivity: ${currentScreen.name}}")
    StartScreenApp(transitionToCreateDonation = true, viewModel = bloodViewModel, currentScreen = currentScreen, openDrawer = openDrawer)
//    when (currentScreen) {
//        DrawerAppScreen.DonateProductsSearch -> StartScreenApp(transitionToCreateDonation = true, viewModel = bloodViewModel, currentScreen = currentScreen, openDrawer = openDrawer)
//        DrawerAppScreen.ManageDonorSearch -> StartScreenApp(transitionToCreateDonation = false, viewModel = bloodViewModel, currentScreen = currentScreen, openDrawer = openDrawer)
//        DrawerAppScreen.ManageDonor -> StartScreenApp(viewModel = bloodViewModel, currentScreen = currentScreen, openDrawer = openDrawer)
//        DrawerAppScreen.ReassociateDonation -> ReassociateDonationComponent(openDrawer)
//        DrawerAppScreen.ViewDonorList -> ViewDonorListComponent(openDrawer)
//    }
}

@Composable
fun ReassociateDonationComponent(openDrawer: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Reassociate Donation") },
            navigationIcon = {
                IconButton(
                    onClick = openDrawer) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                }
            }
        )
        Surface(color = Color(0xFFffd7d7.toInt()), modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally,
                content = {
                    Text(text = "Reassociate Donation")
                }
            )
        }
    }
}

@Composable
fun ViewDonorListComponent(openDrawer: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("View Donor List") },
            navigationIcon = {
                IconButton(
                    onClick = openDrawer) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                }
            }
        )
        Surface(color = Color(0xFFffd7d7.toInt()), modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally,
                content = {
                    Text(text = "View Donor List")
                }
            )
        }
    }
}

enum class DrawerAppScreen(val screenName: String) {
    DonateProductsSearch("Donate Products Search"),
    ManageDonorSearch("Manage Donor Search"),
    ManageDonor("Manage Donor after Search"),
    ReassociateDonation("Reassociate Donation"),
    ViewDonorList("View Donor List"),
}