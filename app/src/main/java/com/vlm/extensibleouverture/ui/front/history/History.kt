package com.vlm.extensibleouverture.ui.front.history

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Greeting() {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val color = if (isPressed) Color.Blue else Color.Green

    Column(
        modifier = Modifier.fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {},
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(backgroundColor = color),
            modifier = Modifier.fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                "Button",
                color = Color.White
            )
        }
    }
}

@Composable
fun History(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp)
    ) {

        Greeting()
    }
}



//        Image(
//            painterResource(R.drawable.empty_state_search),
//            contentDescription = null
//        )
//        Spacer(Modifier.height(24.dp))
//        Text(
//            text =  "히스토리 테스트",
//            style = MaterialTheme.typography.subtitle1,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(16.dp))
//        Text(
//            text =  "히스토리 테스트2",
//            style = MaterialTheme.typography.body2,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.fillMaxWidth()
//        )

