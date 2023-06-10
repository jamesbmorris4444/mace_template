package com.mace.mace_template.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mace.mace_template.DismissSelector
import com.mace.mace_template.R
import com.mace.mace_template.logger.LogUtils

@SuppressLint("ViewConstructor")
class StandardModalComposeView(
    private val composeView: View,
    private val topIconResId: Int = 0,
    private val titleText: String = "",
    private val bodyText: String = "",
    private val positiveText: String = "",
    private val negativeText: String = "",
    private val neutralText: String = "",
    private val onDismiss: (DismissSelector) -> Unit
) : AbstractComposeView(composeView.context) {
    private val windowManager = composeView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var params: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
            gravity = Gravity.TOP
            type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
            token = composeView.applicationWindowToken
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            format = PixelFormat.TRANSLUCENT
            flags = flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    }

    @Composable
    override fun Content() {
        StandardModal(
            topIconResId,
            titleText,
            bodyText,
            positiveText,
            negativeText,
            neutralText
        ) {
            dismissSelector ->  run {
                onDismiss(dismissSelector)
                windowManager.removeView(this)
            }
        }
    }

    init {
        setViewTreeLifecycleOwner(lifecycleOwner = composeView.findViewTreeLifecycleOwner())
        setViewTreeViewModelStoreOwner(viewModelStoreOwner = composeView.findViewTreeViewModelStoreOwner())
        setViewTreeSavedStateRegistryOwner(composeView.findViewTreeSavedStateRegistryOwner())
    }

    fun show() {
        windowManager.addView(this, params)
    }
}

@Composable
fun StandardModal(
    topIconResId: Int,
    titleText: String = "",
    bodyText: String = "",
    positiveText: String = "",
    negativeText: String = "",
    neutralText: String = "",
    onDismiss: (DismissSelector) -> Unit
) {
    var shouldShowDialog by remember { mutableStateOf(true) }
    val numberOfButtons = when {
        negativeText.isEmpty() && neutralText.isEmpty() -> 1
        neutralText.isEmpty() -> 2
        else -> 3
    }
    if (shouldShowDialog) {
        Dialog(
            onDismissRequest = { shouldShowDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (topIconResId > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(color = colorResource(R.color.teal_200)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(top = 22.dp)
                                    .height(160.dp)
                                    .width(120.dp),
                                painter = painterResource(id = topIconResId),
                                contentDescription = "Dialog Alert"
                            )
                        }
                    }

                    if (titleText.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = if (bodyText.isEmpty()) 0.dp else 16.dp),
                            text = titleText,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.avenir_bold, FontWeight.Bold)),
                                fontSize = 16.sp
                            )
                        )
                    }

                    if (bodyText.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            text = bodyText,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.avenir_regular, FontWeight.Normal)),
                                fontSize = 14.sp
                            )
                        )
                    }

                    val positiveButtonTopSpace = 12.dp
                    val positiveTextButtonTopSpace = 8.dp
                    val otherButtonTopSpace = 16.dp
                    val otherTextButtonTopSpace = 8.dp
                    val buttonBottomSpace = 20.dp
                    val textButtonBottomSpace = 24.dp
                    var index = positiveText.indexOf(':')
                    when  {
                        index == 3 && positiveText.substring(0,3) == "BKG" -> {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = positiveButtonTopSpace, start = 36.dp, end = 36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.teal_200)),
                                onClick = {
                                    shouldShowDialog = false
                                    onDismiss(DismissSelector.POSITIVE)
                                }
                            ) {
                                TextForButton(positiveText.substring(index + 1), true)
                            }
                            if (numberOfButtons == 1) {
                                Spacer(modifier = Modifier.padding(bottom = buttonBottomSpace))
                            }
                        }
                        else -> {
                            TextButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = positiveTextButtonTopSpace, start = 36.dp, end = 36.dp),
                                onClick = {
                                    shouldShowDialog = false
                                    onDismiss(DismissSelector.POSITIVE)
                                }
                            ) {
                                TextForButton(positiveText, false)
                            }
                            if (numberOfButtons == 1) {
                                Spacer(modifier = Modifier.padding(bottom = textButtonBottomSpace))
                            }
                        }
                    }

                    if (numberOfButtons > 1) {
                        index = negativeText.indexOf(':')
                        when  {
                            index == 3 && negativeText.substring(0,3) == "BKG" -> {
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = otherButtonTopSpace, start = 36.dp, end = 36.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.teal_200)),
                                    onClick = {
                                        shouldShowDialog = false
                                        onDismiss(DismissSelector.NEGATIVE)
                                    }
                                ) {
                                    TextForButton(negativeText.substring(index + 1), true)
                                }
                                if (numberOfButtons == 2) {
                                    Spacer(modifier = Modifier.padding(bottom = buttonBottomSpace))
                                }
                            }
                            else -> {
                                TextButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = otherTextButtonTopSpace, start = 36.dp, end = 36.dp),
                                    onClick = {
                                        shouldShowDialog = false
                                        onDismiss(DismissSelector.NEGATIVE)
                                    }
                                ) {
                                    TextForButton(negativeText, false)
                                }
                                if (numberOfButtons == 2) {
                                    Spacer(modifier = Modifier.padding(bottom = textButtonBottomSpace))
                                }
                            }
                        }
                    }

                    if (numberOfButtons > 2) {
                        index = neutralText.indexOf(':')
                        when  {
                            index == 3 && neutralText.substring(0,3) == "BKG" -> {
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = otherButtonTopSpace, start = 36.dp, end = 36.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.teal_200)),
                                    onClick = {
                                        shouldShowDialog = false
                                        onDismiss(DismissSelector.NEUTRAL)
                                    }
                                ) {
                                    TextForButton(neutralText.substring(index + 1), true)
                                }
                                Spacer(modifier = Modifier.padding(bottom = buttonBottomSpace))
                            }
                            else -> {
                                TextButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = otherTextButtonTopSpace, start = 36.dp, end = 36.dp),
                                    onClick = {
                                        shouldShowDialog = false
                                        onDismiss(DismissSelector.NEUTRAL)
                                    }
                                ) {
                                    TextForButton(neutralText, false)
                                }
                                Spacer(modifier = Modifier.padding(bottom = textButtonBottomSpace))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextForButton(text: String, isBackgrounded: Boolean) {
    Text(
        text = text,
        color = if (isBackgrounded) colorResource(R.color.white) else colorResource(R.color.teal_700),
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.avenir_bold, FontWeight.Bold)),
            fontSize = 16.sp
        )
    )
}

@Preview
@Composable
fun StandardModalPreview() {
    StandardModal(
        R.drawable.notification,
        titleText = "Staging entry for donor insertion",
        bodyText = "An entry was made to the staging database for insertion of a new donor into the remote database",
        positiveText = "BKG:OK"
    ) {}
}