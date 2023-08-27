package com.mace.mace_template

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.ui.ManageDonorScreen
import com.mace.mace_template.ui.theme.MaceTemplateTheme
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManageDonorScreenTest {

    private val mockApplication: Application = mockk()
    private val bloodViewModel = BloodViewModel(mockApplication)

    @get: Rule
    val composeTestRule = createComposeRule()
    private val composeActivityTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun initialScreenTestEithChange() {
        //composeTestRule.onRoot().printToLog("JIMX")
        setup()
        val textValue = composeTestRule.onNodeWithText("Bertram")
        //textValue.printToLog("JIMX")
        textValue.performTextClearance()
        val clearTextValue = composeTestRule.onNodeWithText("")
        clearTextValue.performTextInput("Miller")
        val newTextValue = composeTestRule.onNodeWithText("Miller")
        //newTextValue.printToLog("JIMX")
        newTextValue.apply {
            val text = newTextValue.fetchSemanticsNode().config[SemanticsProperties.EditableText].text
            assertEquals("Miller", text)
        }
    }

    @Test
    fun donorEditTextTestFromDonateProducts() {
        setup(transitionToCreateProductsScreen = true)
        composeTestRule.onAllNodes(hasTestTag("OutlinedTextField")).apply {
            fetchSemanticsNodes().forEachIndexed { index, _ ->
                when (index) {
                    0 -> get(index).assertTextContains("James")
                    1 -> get(index).assertTextContains("Bertram")
                    2 -> get(index).assertTextContains("B-Positive")
                    3 -> get(index).assertTextContains("Navy")
                }
            }
        }
        composeTestRule.onAllNodes(hasTestTag("RadioButton")).apply {
            fetchSemanticsNodes().forEachIndexed { index, _ ->
                when (index) {
                    0 -> get(index).assertTextContains("Male")
                    1 -> get(index).assertTextContains("Female")
                }
            }
        }
    }

    @Test
    fun donorEditTextTestFromNavDrawer() {
        setup()
        composeTestRule.onAllNodes(hasTestTag("OutlinedTextField")).apply {
            fetchSemanticsNodes().forEachIndexed { index, _ ->
                when (index) {
                    0 -> get(index).assertTextContains("Morris")
                    1 -> get(index).assertTextContains("James")
                    2 -> get(index).assertTextContains("Bertram")
                    3 -> get(index).assertTextContains("04 Jan 1967")
                    4 -> get(index).assertTextContains("B-Positive")
                    5 -> get(index).assertTextContains("Navy")
                }
                //get(index).printToLog("JIMX")
            }
        }
        composeTestRule.onAllNodes(hasTestTag("RadioButton")).apply {
            fetchSemanticsNodes().forEachIndexed { index, _ ->
                when (index) {
                    0 -> get(index).assertTextContains("Male")
                    1 -> get(index).assertTextContains("Female")
                }
            }
        }
    }

    @Test
    fun clickUpdateButtonTestWithNoChanges() {
        setup()
        val buttonValue = composeTestRule.onNodeWithTag("WidgetButton")
        buttonValue.apply {
            val text = buttonValue.fetchSemanticsNode().config[SemanticsProperties.Text][0].text
            assertEquals("Update", text)
        }
        buttonValue.assertHasClickAction()
        buttonValue.performClick()
        val standardModal = composeTestRule.onAllNodesWithTag("StandardModal")
        standardModal.apply {
            fetchSemanticsNodes().forEachIndexed { index, _ ->
                when (index) {
                    1 -> {
                        val text = get(index).fetchSemanticsNode().config[SemanticsProperties.Text][0].text
                        assertEquals("No entries made into the staging database", text)
                    }
                }
            }
        }
    }

    @Test
    fun clickUpdateButtonTestWithChange() {
        setup(lastName = "")
        val textToChangeValue = composeTestRule.onNodeWithText("Bertram")
        textToChangeValue.performTextInput("Pauley")
        val buttonValue = composeTestRule.onNodeWithTag("WidgetButton")
        buttonValue.apply {
            val text = buttonValue.fetchSemanticsNode().config[SemanticsProperties.Text][0].text
            assertEquals("Update", text)
        }
        buttonValue.assertHasClickAction()
        buttonValue.performClick()
        val standardModal = composeTestRule.onAllNodesWithTag("StandardModal")
        standardModal.apply {
            fetchSemanticsNodes().forEachIndexed { index, _ ->
                when (index) {
                    2 -> {
                        val text = get(index).fetchSemanticsNode().config[SemanticsProperties.Text][0].text
                        assertEquals("An entry was not made to the staging database because product has empty last name or dob", text)
                    }
                }
            }
        }
    }

    private fun setup(lastName: String = "Morris", transitionToCreateProductsScreen: Boolean = false) {
        composeTestRule.setContent {
            every { mockApplication.applicationContext } returns LocalContext.current
            every { mockApplication.resources } returns LocalContext.current.resources
            val donateProductsSearchStringName = bloodViewModel.getResources().getString(ScreenNames.DonateProductsSearch.resId)
            val createProductsStringName = bloodViewModel.getResources().getString(ScreenNames.CreateProducts.resId)
            MaceTemplateTheme {
                var appBarState by remember { mutableStateOf(AppBarState()) }
                val openDrawer: () -> Unit = { }
                ManageDonorScreen(
                    onComposing = {
                        appBarState = it
                    },
                    canNavigateBack = true,
                    navigateUp = { },
                    openDrawer = openDrawer,
                    donor = Donor(
                        firstName = "James",
                        middleName = "Bertram",
                        lastName = lastName,
                        dob = "04 Jan 1967",
                        gender = true,
                        aboRh = "B-Positive",
                        branch = "Navy"
                    ),
                    viewModel = bloodViewModel,
                    navController = rememberNavController(),
                    transitionToCreateProductsScreen = transitionToCreateProductsScreen,
                    donateProductsSearchStringName = donateProductsSearchStringName,
                    createProductsStringName = createProductsStringName
                )
            }
        }
    }

}