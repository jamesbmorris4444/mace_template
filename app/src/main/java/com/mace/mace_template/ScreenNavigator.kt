package com.mace.mace_template

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.ui.CreateProductsScreen
import com.mace.mace_template.ui.DismissSelector
import com.mace.mace_template.ui.DonateProductsScreen
import com.mace.mace_template.ui.ManageDonorScreen
import com.mace.mace_template.ui.ReassociateDonationScreen
import com.mace.mace_template.ui.ViewDonorListScreen
import com.mace.mace_template.utils.Constants
import com.mace.mace_template.utils.Constants.LOG_TAG


data class AppBarState(
    val title: String = "",
    val actions: (@Composable RowScope.() -> Unit)? = null,
    val navigationIcon: (@Composable () -> Unit)? = null
)

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route:String,
)

data class StandardModalArgs(
    val topIconResId: Int = -1,
    val titleText: String = "",
    val bodyText: String = "",
    val positiveText: String = "",
    val negativeText: String = "",
    val neutralText: String = "",
    val onDismiss: (DismissSelector) -> Unit = { }
)

// Called one time at app startup
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenNavigator(
    viewModel: BloodViewModel,
    currentScreen: ScreenNames,
    navController: NavHostController,
    openDrawer: () -> Unit
) {
    var donor by remember { mutableStateOf(Donor()) }
    var appBarState by remember { mutableStateOf(AppBarState()) }
    var transitionToCreateProductsScreen by remember { mutableStateOf(true) }
    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "Start Initial Screen in ScreenNavigator: name=${currentScreen.name}")
    Scaffold(
        topBar = {
            StartScreenAppBar(appBarState = appBarState)
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { internalPadding ->
        Box(modifier = Modifier.padding(internalPadding)) {
            val donateProductsSearchStringName = stringResource(ScreenNames.DonateProductsSearch.resId)
            val manageDonorAfterSearchStringName = stringResource(ScreenNames.ManageDonorAfterSearch.resId)
            val createProductsStringName = stringResource(ScreenNames.CreateProducts.resId)
            val viewDonorListStringName = stringResource(ScreenNames.ViewDonorList.resId)
            val manageDonorFromDrawer = stringResource(ScreenNames.ManageDonorFromDrawer.resId)
            val reassociateDonationSearchStringName = stringResource(ScreenNames.ReassociateDonation.resId)
            NavHost(
                navController = navController,
                startDestination = donateProductsSearchStringName,
            ) {
                composable(route = donateProductsSearchStringName) {
                    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "ScreenNavigator: launch screen=$donateProductsSearchStringName")
                    DonateProductsScreen(
                        onComposing = {
                            appBarState = it
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        onItemButtonClicked = {
                            donor = it
                            transitionToCreateProductsScreen = true
                            navController.navigate(manageDonorAfterSearchStringName)
                        },
                        viewModel = viewModel,
                        title = donateProductsSearchStringName
                    )
                }
                composable(route = manageDonorFromDrawer) {
                    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "ScreenNavigator: launch screen=$manageDonorFromDrawer")
                    DonateProductsScreen(
                        onComposing = {
                            appBarState = it
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        onItemButtonClicked = {
                            donor = it
                            transitionToCreateProductsScreen = false
                            navController.navigate(manageDonorAfterSearchStringName)
                        },
                        viewModel = viewModel,
                        title = manageDonorFromDrawer
                    )
                }
                composable(route = manageDonorAfterSearchStringName) {
                    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "ScreenNavigator: launch screen=$manageDonorAfterSearchStringName")
                    ManageDonorScreen(
                        onComposing = {
                            appBarState = it
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        donor = donor,
                        viewModel = viewModel,
                        navController = navController,
                        transitionToCreateProductsScreen = transitionToCreateProductsScreen,
                        donateProductsSearchStringName = donateProductsSearchStringName,
                        createProductsStringName = createProductsStringName
                    )
                }
                composable(route = createProductsStringName) {
                    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "ScreenNavigator: launch screen=$createProductsStringName")
                    CreateProductsScreen(
                        onComposing = {
                            appBarState = it
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        donor = donor,
                        viewModel = viewModel,
                        onCompleteButtonClicked = {
                            navController.popBackStack(route = createProductsStringName, inclusive = true)
                            navController.navigate(donateProductsSearchStringName)
                        }
                    )
                }
                composable(route = viewDonorListStringName) {
                    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "ScreenNavigator: launch screen=$viewDonorListStringName")
                    ViewDonorListScreen(
                        onComposing = {
                            appBarState = it
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        viewModel = viewModel
                    )
                }
                composable(route = reassociateDonationSearchStringName) {
                    LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "ScreenNavigator: launch screen=$reassociateDonationSearchStringName")
                    ReassociateDonationScreen(
                        onComposing = {
                            appBarState = it
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        viewModel = viewModel,
                        title = reassociateDonationSearchStringName
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreenAppBar(
    appBarState: AppBarState
) {
    TopAppBar(
        title = { Text(appBarState.title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.teal_200)
        ),
        actions = { appBarState.actions?.invoke(this) },
        navigationIcon = { appBarState.navigationIcon?.invoke() }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigation(
        backgroundColor = colorResource(R.color.teal_200),
        contentColor = colorResource(R.color.black)

    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Constants.BottomNavItems.forEach { navItem ->
            BottomNavigationItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.id) { inclusive = true}
                    }
                },
                icon = {
                    Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                },
                label = {
                    Text(text = navItem.label)
                },
                alwaysShowLabel = true
            )
        }
    }
}