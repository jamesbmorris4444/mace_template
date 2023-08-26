package com.mace.mace_template

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
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
    private var itemWasClicked = false

    @get: Rule
    val composeTestRule = createComposeRule()
    private val composeActivityTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun initialScreenTest() {
        //composeTestRule.onRoot().printToLog("JIMX")
        setup("Morris", "04 Jan 1967")
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
        setup("Morris", "04 Jan 1967")
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
        setup("", "")
        composeTestRule.onAllNodes(hasTestTag("OutlinedTextField")).apply {
            fetchSemanticsNodes().forEachIndexed { index, _ ->
                when (index) {
                    0 -> get(index).assertTextContains("")
                    1 -> get(index).assertTextContains("James")
                    2 -> get(index).assertTextContains("Bertram")
                    3 -> get(index).assertTextContains("")
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

//    @Test
//    fun initialScreenWithDatabaseRefreshFailureTest() {
//        setup(true, "Refresh Failure")
//        composeTestRule.onAllNodesWithTag("StandardModal").apply {
//            val textValue = composeTestRule.onNodeWithText(substring = true, text = "Refresh Failure")
//            val text = textValue.fetchSemanticsNode().config[SemanticsProperties.Text][0].text
//            assertEquals("Failure Message is: Refresh Failure", text)
//        }
//    }

    private fun setup(lastName: String, dob: String) {
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
                        dob = dob,
                        gender = true,
                        aboRh = "B-Positive",
                        branch = "Navy"
                    ),
                    viewModel = bloodViewModel,
                    navController = rememberNavController(),
                    transitionToCreateProductsScreen = false,
                    donateProductsSearchStringName = donateProductsSearchStringName,
                    createProductsStringName = createProductsStringName
                )
            }
        }
    }

}