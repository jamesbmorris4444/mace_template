package com.mace.mace_template

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.ui.DonateProductsScreen
import com.mace.mace_template.ui.ManageDonorScreen

data class AppBarState(
    val title: String = "",
    val actions: (@Composable RowScope.() -> Unit)? = null,
    val navigationIcon: (@Composable () -> Unit)? = null
)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StartScreenApp(
    viewModel: BloodViewModel,
    currentScreen: DrawerAppScreen,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit,
    transitionToCreateDonation: Boolean = false
) {
    lateinit var donor: Donor
    LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "StartScreenApp in StartScreen: ${currentScreen.name}}")
    var appBarState by remember { mutableStateOf(AppBarState()) }
    Scaffold(
        topBar = {
            StartScreenAppBar(appBarState = appBarState)
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = DrawerAppScreen.DonateProductsSearch.name
        ) {
            composable(route = DrawerAppScreen.DonateProductsSearch.name) {
                LogUtils.D("LogUtilsTag", LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "launch DonateProductsScreen 1=${DrawerAppScreen.DonateProductsSearch.screenName}")
                DonateProductsScreen(
                    onComposing = {
                        appBarState = it
                    },
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    openDrawer = openDrawer,
                    transitionToCreateDonation = transitionToCreateDonation,
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
                    },
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    openDrawer = openDrawer,
                    donor = donor,
                    onUpdateButtonClicked = {
                        //navController.navigate(StartScreenNames.DonateProducts.name)
                    }
                )
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
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = colorResource(R.color.teal_200)
        ),
        actions = { appBarState.actions?.invoke(this) },
        navigationIcon = { appBarState.navigationIcon }
    )
}