package com.mace.mace_template

import android.annotation.SuppressLint
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.ui.CreateProductsScreen
import com.mace.mace_template.ui.DonateProductsScreen
import com.mace.mace_template.ui.ManageDonorScreen
import com.mace.mace_template.ui.StandardModalComposeView
import com.mace.mace_template.utils.Constants

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


// Called one time at app startup
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StartScreenApp(
    view: View,
    viewModel: BloodViewModel,
    currentScreen: DrawerAppScreen,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit
) {
    lateinit var donor: Donor
    LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "StartScreenApp in StartScreen: ${currentScreen.name}}")
    var appBarState by remember { mutableStateOf(AppBarState()) }
    Scaffold(
        topBar = {
            StartScreenAppBar(appBarState = appBarState)
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { internalPadding ->
        Box(modifier = Modifier.padding(internalPadding)) {
            NavHost(
                navController = navController,
                startDestination = DrawerAppScreen.DonateProductsSearch.name,
            ) {
                composable(route = DrawerAppScreen.DonateProductsSearch.name) {
                    LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch DonateProductsScreen 1=${DrawerAppScreen.DonateProductsSearch.screenName}")
                    DonateProductsScreen(
                        onComposing = {
                            appBarState = it
                            LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "appBarState1=$appBarState")
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        onItemButtonClicked = {
                            donor = it
                            navController.navigate(DrawerAppScreen.ManageDonor.name)
                        },
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_large))
                    )
                }
                composable(route = DrawerAppScreen.ManageDonor.name) {
                    LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch ManageDonorScreen=${DrawerAppScreen.ManageDonor.screenName}")
                    ManageDonorScreen(
                        onComposing = {
                            appBarState = it
                            LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "appBarState2=$appBarState")
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        donor = donor,
                        viewModel = viewModel
                    ) {
                        StandardModalComposeView(
                            view,
                            numberOfButtons = 3,
                            topIconResId = R.drawable.notification,
                            titleText = "Database Operation",
                            bodyText = "Now is the time for all good men to come to the aid of their country",
                            positiveText = "BKG:Positive",
                            negativeText = "Negative",
                            neutralText = "Neutral",
                        ) { navController.navigate(DrawerAppScreen.CreateProducts.name) }.show()
                    }
                }
                composable(route = DrawerAppScreen.CreateProducts.name) {
                    LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch ManageDonorScreen=${DrawerAppScreen.CreateProducts.screenName}")
                    CreateProductsScreen(
                        onComposing = {
                            appBarState = it
                            LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "appBarState2=$appBarState")
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        viewModel = viewModel,
                        onCompleteButtonClicked = {
                            //navController.navigate(StartScreenNames.DonateProducts.name)
                        }
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
    LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "StartScreenAppBar=$appBarState")
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
                    navController.navigate(navItem.route)
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

enum class DismissSelector {
    POSITIVE,
    NEGATIVE,
    NEUTRAL
}