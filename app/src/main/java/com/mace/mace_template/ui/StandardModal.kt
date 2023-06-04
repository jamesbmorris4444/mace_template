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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
    private val numberOfButtons: Int = 2,
    private val topIconResId: Int = 0,
    private val titleText: String = "",
    private val bodyText: String = "",
    private val positiveText: String = "",
    private val negativeText: String = "",
    private val neutralText: String = "",
    private val onDismiss: (DismissSelector) -> Unit
) : AbstractComposeView(composeView.context) {
    private val windowManager = composeView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var params: WindowManager.LayoutParams =
        WindowManager.LayoutParams().apply {
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
            numberOfButtons, 
            topIconResId,
            titleText,
            bodyText,
            positiveText,
            negativeText,
            neutralText
        ) {
            dismissSelector -> onDismiss(dismissSelector)
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
    numberOfButtons: Int = 2,
    topIconResId: Int,
    titleText: String = "",
    bodyText: String = "",
    positiveText: String = "",
    negativeText: String = "",
    neutralText: String = "",
    onDismiss: (DismissSelector) -> Unit
) {
    val shouldShowDialog = remember { mutableStateOf(true) }
    if (shouldShowDialog.value) {
        Dialog(
            onDismissRequest = { },
            content = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
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
                                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                                text = titleText,
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.avenir_bold, FontWeight.Bold)),
                                    fontSize = 20.sp
                                )
                            )
                        }

                        if (bodyText.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                text = bodyText,
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.avenir_regular, FontWeight.Normal)),
                                    fontSize = 14.sp
                                )
                            )
                        }
                        
                        var index = positiveText.indexOf(':')
                        when  {
                            index == 3 && positiveText.substring(0,3) == "BKG" -> {
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, start = 36.dp, end = 36.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.teal_200)),
                                    onClick = {
                                        LogUtils.D("JIMX", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "onDismiss P1")
                                        shouldShowDialog.value = false
                                        onDismiss(DismissSelector.POSITIVE)
                                    }
                                ) {
                                    TextForButton(positiveText.substring(index + 1), true)
                                }
                            }
                            else -> {
                                TextButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, start = 36.dp, end = 36.dp),
                                    onClick = {
                                        LogUtils.D("JIMX", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "onDismiss P2")
                                        shouldShowDialog.value = false
                                        onDismiss(DismissSelector.POSITIVE)
                                    }
                                ) {
                                    TextForButton(positiveText, false)
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
                                            .padding(top = 12.dp, start = 36.dp, end = 36.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.teal_200)),
                                        onClick = {
                                            LogUtils.D("JIMX", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "onDismiss N1")
                                            shouldShowDialog.value = false
                                            onDismiss(DismissSelector.NEGATIVE)
                                        }
                                    ) {
                                        TextForButton(negativeText.substring(index + 1), true)
                                    }
                                }
                                else -> {
                                    TextButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp, start = 36.dp, end = 36.dp),
                                        onClick = {
                                            LogUtils.D("JIMX", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "onDismiss N2")
                                            shouldShowDialog.value = false
                                            onDismiss(DismissSelector.NEGATIVE)
                                        }
                                    ) {
                                        TextForButton(negativeText, false)
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
                                            .padding(top = 12.dp, start = 36.dp, end = 36.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.teal_200)),
                                        onClick = {
                                            LogUtils.D("JIMX", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "onDismiss U1")
                                            shouldShowDialog.value = false
                                            onDismiss(DismissSelector.NEUTRAL)
                                        }
                                    ) {
                                        TextForButton(neutralText.substring(index + 1), true)
                                    }
                                }
                                else -> {
                                    TextButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp, start = 36.dp, end = 36.dp),
                                        onClick = {
                                            LogUtils.D("JIMX", LogUtils.FilterTags.withTags(LogUtils.TagFilter.RPO), "onDismiss U2")
                                            shouldShowDialog.value = false
                                            onDismiss(DismissSelector.NEUTRAL)
                                        }
                                    ) {
                                        TextForButton(neutralText, false)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.padding(bottom = 16.dp))
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun TextForButton(text: String, isBackgrounded: Boolean) {
    Text(
        text = text,
        color = if (isBackgrounded) colorResource(R.color.white) else colorResource(R.color.teal_700),
        style = TextStyle(
            fontFamily = FontFamily(
                Font(
                    R.font.avenir_book,
                    FontWeight.Normal
                )
            ),
            fontSize = 14.sp
        )
    )
}