package com.mace.mace_template

import SampleData
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mace.mace_template.ui.DonateProductsScreen

data class Message(val author: String, val body: String)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StartScreenApp(
    viewModel: BloodViewModel,
    currentScreen: DrawerAppScreen,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit,
) {
    Scaffold(
        topBar = {
            StartScreenAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                openDrawer = openDrawer
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = DrawerAppScreen.DonateProducts.name
        ) {
            composable(route = DrawerAppScreen.DonateProducts.name) {
                DonateProductsScreen(
                    transitionToCreateDonation = true,
//                    onNextButtonClicked = {
//                        navController.navigate(StartScreenNames.DonateProductsNoList.name)
//                    },
                    viewModel,
                    SampleData.conversationSample,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_large))
                )
            }
            composable(route = DrawerAppScreen.DonateProducts.name) {
                DonateProductsScreen(
                    transitionToCreateDonation = false,
//                    onNextButtonClicked = {
//                        navController.navigate(StartScreenNames.DonateProducts.name)
//                    },
                    viewModel,
                    SampleData.conversationSample,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_large))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreenAppBar(
    currentScreen: DrawerAppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(currentScreen.screenName) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
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
        }
    )
}


// ===============================

//private val screens: List<String> = listOf(
//    "Donate Products",
//    "Manage Donor",
//    "Reassociate Donation",
//    "View Donor List"
//)
//
//@Composable
//fun Drawer(
//    modifier: Modifier = Modifier,
//    onDestinationClicked: (route: String) -> Unit) {
//    Column(
//        modifier
//            .fillMaxSize()
//            .padding(start = 24.dp, top = 48.dp)
//    ) {
//        val imageModifier = Modifier.size(150.dp)
//        Image(
//            painter = painterResource(id = R.drawable.fs_logo),
//            contentDescription = stringResource(id = R.string.fs_logo_content_description),
//            contentScale = ContentScale.Fit,
//            modifier = imageModifier
//        )
//        screens.forEach { screen ->
//            Spacer(Modifier.height(24.dp))
//            Text(
//                text = screen,
//                style = MaterialTheme.typography.displayLarge,
//                modifier = Modifier.clickable {
//                    onDestinationClicked(screen)
//                }
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun DrawerPreview() {
//    MaceTemplateTheme {
//        Drawer(onDestinationClicked = { })
//    }
//}
//
//@Preview
//@Composable
//fun DrawerScreenPreview() {
//    MaceTemplateTheme {
//        DrawerScreen()
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DrawerScreen() {
//    val navController = rememberNavController()
//    Surface(color = MaterialTheme.colorScheme.background) {
//        val drawerState = rememberDrawerState(DrawerValue.Closed)
//        val scope = rememberCoroutineScope()
//        val openDrawer = {
//            scope.launch {
//                drawerState.open()
//            }
//        }
//
//        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr ) {
//            ModalNavigationDrawer(
//                drawerState = drawerState,
//                gesturesEnabled = drawerState.isOpen,
//                drawerContent = {
//                    Drawer(
//                        onDestinationClicked = { route ->
//                            scope.launch {
//                                drawerState.close()
//                            }
//                            navController.navigate(route) {
//                                popUpTo(route) { inclusive = true }
//                                launchSingleTop = true
//                            }
//                        }
//                    )
//                }
//            ) {
//                NavHost(
//                    navController = navController,
//                    startDestination = "Donate Products"
//                ) {
//                    composable("Donate Products") {
//                        DonateProducts(openDrawer = { openDrawer() })
//                    }
//                    composable("Manage Donor") {
//                        ManageDonor(openDrawer = { openDrawer() })
//                    }
//                    composable("ReassociateDonor") {
//                        ReassociateDonor(openDrawer = { openDrawer() })
//                    }
//                    composable("View Donor List") {
//                        ViewDonorList(openDrawer = { openDrawer() })
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TopBar(title: String = "", buttonIcon: ImageVector, onButtonClicked: () -> Unit) {
//    TopAppBar(
//        title = {
//            Text(
//                text = title
//            )
//        },
//        navigationIcon = {
//            IconButton(onClick = { onButtonClicked() } ) {
//                Icon(buttonIcon, contentDescription = "")
//            }
//        },
//        colors = TopAppBarDefaults.mediumTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        )
//    )
//}
//
//@Composable
//fun DonateProducts(openDrawer: () -> Unit) {
//    Column(modifier = Modifier.fillMaxSize()) {
//        TopBar(
//            title = "Donate Products",
//            buttonIcon = Icons.Filled.Menu,
//            onButtonClicked = { openDrawer() }
//        )
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(text = "Donate Products", style = MaterialTheme.typography.displayMedium)
//        }
//    }
//}
//
//@Composable
//fun ManageDonor(openDrawer: () -> Unit) {
//    Column(modifier = Modifier.fillMaxSize()) {
//        TopBar(
//            title = "Manage Donor",
//            buttonIcon = Icons.Filled.Menu,
//            onButtonClicked = { openDrawer() }
//        )
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(text = "Manage Donor", style = MaterialTheme.typography.displayMedium)
//        }
//    }
//}
//
//@Composable
//fun ReassociateDonor(openDrawer: () -> Unit) {
//    Column(modifier = Modifier.fillMaxSize()) {
//        TopBar(
//            title = "Reassocciate Donor",
//            buttonIcon = Icons.Filled.Menu,
//            onButtonClicked = { openDrawer() }
//        )
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(text = "Reassocciate Donor", style = MaterialTheme.typography.displayMedium)
//        }
//    }
//}
//
//@Composable
//fun ViewDonorList(openDrawer: () -> Unit) {
//    Column(modifier = Modifier.fillMaxSize()) {
//        TopBar(
//            title = "View Donor List",
//            buttonIcon = Icons.Filled.Menu,
//            onButtonClicked = { openDrawer() }
//        )
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(text = "View Donor List", style = MaterialTheme.typography.displayMedium)
//        }
//    }
//}