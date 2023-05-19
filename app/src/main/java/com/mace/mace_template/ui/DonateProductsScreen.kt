package com.mace.mace_template.ui

import SampleData
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mace.mace_template.BloodViewModel
import com.mace.mace_template.Message
import com.mace.mace_template.R
import com.mace.mace_template.ui.theme.MaceTemplateTheme

@Composable
fun DonateProductsScreen(
    transitionToCreateDonation:  Boolean,
    viewModel: BloodViewModel,
    dataList: List<Message>,
    modifier: Modifier = Modifier
) {
    val completed = remember { mutableStateOf(false) }
    viewModel.RefreshRepository { completed.value = true }
    Conversation(completed.value, dataList, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Conversation(value: Boolean, messages: List<Message>, modifier: Modifier = Modifier) {
    val buttonBackgroundColor = "0xFF0000FF"
    BoxWithConstraints(modifier = modifier.fillMaxWidth(1f)) {
        if (value) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                Row {
                    var text by rememberSaveable { mutableStateOf("") }
                    TextField(
                        value = text,
                        onValueChange = {
                            text = it
                        },
                        shape = RoundedCornerShape(10.dp),
                        label = { Text("Label") }
                    )
                    Button(
                        onClick = {

                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White)) {
                        Text(text = "Submit")
                    }
                }

                LazyColumn {
                    items(messages) { message ->
                        MessageCard(message)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CustomCircularProgressBar()
            }
        }
    }
}

@Composable
private fun CustomCircularProgressBar(){
    CircularProgressIndicator(
        modifier = Modifier.size(120.dp),
        color = Color.Green,
        strokeWidth = 6.dp)
}

@Preview
@Composable
fun PreviewConversation() {
    MaceTemplateTheme {
        Conversation(false, SampleData.conversationSample)
    }
}

@Composable
fun MessageCard(msg: Message) {
    Column {
        Row(modifier = Modifier.padding(all = 8.dp)) {
            Image(
                painter = painterResource(R.drawable.profile_picture),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column {
                androidx.compose.material.Text(
                    text = msg.author,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(shape = MaterialTheme.shapes.medium, elevation = 1.dp) {
                    androidx.compose.material.Text(
                        text = msg.body,
                        modifier = Modifier.padding(all = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {
    MaceTemplateTheme {
        Surface {
            MessageCard(
                msg = Message("Lexi", "Hey, take a look at Jetpack Compose, it's great!")
            )
        }
    }
}