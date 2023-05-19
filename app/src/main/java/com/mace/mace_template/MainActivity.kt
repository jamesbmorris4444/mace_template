package com.mace.mace_template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.mace.mace_template.ui.theme.MaceTemplateTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaceTemplateTheme {
                DrawerAppComponent(BloodViewModel(application), DrawerAppScreen.DonateProducts)
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

    ModalDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerContentComponent(
                currentScreen = currentScreen,
                closeDrawer = { coroutineScope.launch { drawerState.close() } }
            )
        },
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
    Column(modifier = Modifier.fillMaxSize()
        .height(120.dp)
        .width(80.dp)
        .padding(40.dp)
        .background(Color(0xff000000))
    ) {
        for (screen in DrawerAppScreen.values()) {
            Column(
                Modifier.clickable(onClick = { closeDrawer() }),
                content = {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (currentScreen.value == screen) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ) {
                        Text(text = screen.name, modifier = Modifier.padding(16.dp))
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
    when (currentScreen) {
        DrawerAppScreen.DonateProducts -> StartScreenApp(viewModel = bloodViewModel, currentScreen = currentScreen, openDrawer = openDrawer)
        DrawerAppScreen.ManageDonor -> ManageDonorComponent(openDrawer)
        DrawerAppScreen.ReassociateDonation -> ReassociateDonationComponent(openDrawer)
        DrawerAppScreen.ViewDonorList -> ViewDonorListComponent(openDrawer)
    }
}

@Composable
fun ManageDonorComponent(openDrawer: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Manage Donor") },
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
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Text(text = "Manage Donor")
                }
            )
        }
    }
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
                horizontalAlignment = Alignment.CenterHorizontally,
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
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Text(text = "View Donor List")
                }
            )
        }
    }
}

enum class DrawerAppScreen(val screenName: String) {
    DonateProducts("Donate Products"),
    ManageDonor("Manage Donor"),
    ReassociateDonation("Reassociate Donation"),
    ViewDonorList("View Donor List"),
}