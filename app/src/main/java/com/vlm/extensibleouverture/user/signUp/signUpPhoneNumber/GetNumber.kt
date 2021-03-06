package com.vlm.extensibleouverture.user.signUp.signUpPhoneNumber

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vlm.vlmlibrary.compose.button.DefaultInputButton
import com.vlm.vlmlibrary.compose.editText.SimpleEditTextDefault

import com.vlm.vlmlibrary.compose.notosanFontFamily
import com.vlm.vlmlibrary.compose.theme.ColorWhite


@Preview(showBackground = true, name = "Signup-page-PhoneNumber")
@ExperimentalAnimationApi
@Composable
fun GetNumber_(){
    val rememberTextLength = rememberSaveable{ mutableStateOf(0) }
    val arg1 = rememberSaveable{ mutableStateOf("") }
    GetNumber(
        initAnimation = true,
        phoneNumber = arg1,
        isError = true,
        backPage = null,
        rememberTextLength = rememberTextLength,
        getCodeNumber = null
    )
}

@ExperimentalAnimationApi
@Composable
fun GetNumber(
    initAnimation: Boolean,
    phoneNumber: MutableState<String>,
    isError : Boolean = false,
    backPage: (() -> Unit)?,
    rememberTextLength: MutableState<Int>,
    getCodeNumber : (() -> Unit)?,
) {

    BackHandler(enabled = true){
        backPage?.let { it() }
    }

    AnimatedVisibility(
        visibleState = remember {
            MutableTransitionState(initAnimation).apply {
                targetState = true
            }
        },
        enter =
        fadeIn(animationSpec = tween(1000, 0)),
        exit = fadeOut()
    ) {

        Box {
            Row(
                Modifier
                    .padding(bottom = 150.dp)
                    .fillMaxSize()
                    .background(ColorWhite)
            )
            {
                Column(Modifier.align(Alignment.CenterVertically)) {

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "???????????? ??????",
                        fontSize = 25.sp,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "???????????? ????????? ???????????? ??????????????? ????????? ?????? ???????????? ????????? \n ??????????????? ?????? ????????? ??????????????? ???????????? ???????????? ???????????????",
                        fontSize = 12.sp,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SimpleEditTextDefault(
                        text = phoneNumber.value,
                        hint = "????????? ?????? ??????????????????",
                        errorMsg = "?????????????????? ??????????????? ????????? ??? ????????????\n????????? ????????? ??????????????????",
                        isError = isError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(onSend = {
                            getCodeNumber?.let { it() }
                        }),
                        onTextChanged = { response ->
                            phoneNumber.value = response
//                            if (phoneNumber.value.length > 15)
//                            {
//                                Toast.makeText(context,"15?????? ????????? ??????????????????", Toast.LENGTH_SHORT).show()
//                                phoneNumber.value = response.substring(0 until 15)
//                            }
                        })



                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            DefaultInputButton(
                                backgroundColor = Color.Transparent,
                                textColor = Color.Black,
                                modifier = Modifier
                                    .padding(top = 50.dp, start = 35.dp)
                                    .align(Alignment.Start),
                                title = "????????????"
                            ) {
                                if (backPage != null) {
                                    backPage()
                                }
                            }
                        }

                        Column {
                            if (rememberTextLength.value in 1..15) {
                                DefaultInputButton(
                                    backgroundColor = Color.Transparent,
                                    textColor = Color.Black,
                                    modifier = Modifier
                                        .padding(top = 50.dp, end = 35.dp)
                                        .align(Alignment.End),
                                    title = "???????????? ??????"
                                ) {

                                    getCodeNumber?.let { it() }

                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
