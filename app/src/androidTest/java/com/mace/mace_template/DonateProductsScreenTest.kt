package com.mace.mace_template

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mace.mace_template.ui.DonateProductsScreen
import com.mace.mace_template.ui.theme.MaceTemplateTheme
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DonateProductsScreenTest {

    private val mockApplication: Application = mockk()
    private val bloodViewModel = BloodViewModel(mockApplication)
    private var itemWasClicked = false

    @get: Rule
    val composeTestRule = createComposeRule()
    private val composeActivityTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            every { mockApplication.applicationContext } returns LocalContext.current
            every { mockApplication.resources } returns LocalContext.current.resources
            MaceTemplateTheme {
                var appBarState by remember { mutableStateOf(AppBarState()) }
                val openDrawer: () -> Unit = { }
                DonateProductsScreen(
                    onComposing = {
                        appBarState = it
                    },
                    canNavigateBack = true,
                    navigateUp = { },
                    openDrawer = openDrawer,
                    onItemButtonClicked = { itemWasClicked = true },
                    viewModel = bloodViewModel,
                    title = "Donate Products Screen"
                )
            }
        }
    }

    @Test
    fun initialScreenTest() {
        //composeTestRule.onRoot().printToLog("JIMX")
        composeTestRule.onNodeWithText(bloodViewModel.getResources().getString(R.string.initial_letters_of_last_name_text)).assertExists()
    }

    @Test
    fun searchSetupTest() {
        val textValue = composeTestRule.onNodeWithTag("OutlinedTextField")
        textValue.performTextInput("lew")
        for ((key, value) in textValue.fetchSemanticsNode().config) {
            if (key.name == "EditableText") {
                assertEquals("lew", value.toString())
            }
        }
    }

    @Test
    fun searchTest() {
        val textValue = composeTestRule.onNodeWithTag("OutlinedTextField")
        textValue.performTextInput("lew")
        textValue.performImeAction()
        composeTestRule.onAllNodes(hasTestTag("item")).apply {
            fetchSemanticsNodes().forEachIndexed { index, _ ->
                get(index).assertTextContains("Lewis")
                if (index == 0) {
                    get(index).assertHasClickAction()
                    get(index).performClick()
                    assert(itemWasClicked)
                }
            }
        }
    }

}