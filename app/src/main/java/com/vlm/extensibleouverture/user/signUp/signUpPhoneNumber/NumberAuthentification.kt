package com.vlm.extensibleouverture.user.signUp.signUpPhoneNumber

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
fun SignupPhoneNumberAuthentification_(){
    val rememberTextLength = rememberSaveable{ mutableStateOf(0) }
    val authCodeNumber = rememberSaveable{ mutableStateOf("") }
    val resultMsg = rememberSaveable{ mutableStateOf("") }
    val authentificationTimeout = rememberSaveable{ mutableStateOf(false) }

    NumberAuthentification(
        authCodeNumber, authentificationTimeout, "",
        rememberTextLength = rememberTextLength, resultMsg = resultMsg, initAnimation = true
    )
}


@ExperimentalAnimationApi
@Composable
fun NumberAuthentification(
    authCodeNumber : MutableState<String>,
    authentificationTimeout : MutableState<Boolean>,
    remainTime : String,
    resultMsg : MutableState<String>,
    rememberTextLength : MutableState<Int>,
    initAnimation : Boolean = false,
    verificatingCode : (() -> Unit)? = null,
    backPage : (() -> Unit)? = null,
    nextPage : (() -> Unit)? = null,
) {
    //텍스트 길이를 기억
    rememberTextLength.value = authCodeNumber.value.length

    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(initAnimation).apply { targetState = true } },
        enter =
        fadeIn(animationSpec = tween(1000, 0)),
        exit = fadeOut()
    ) {

        Box {
            Row(
                Modifier
                    .padding(bottom = 150.dp)
                    .fillMaxSize()
                    .background(ColorWhite))
            {
                Column(Modifier.align(Alignment.CenterVertically)) {

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "인증코드를 입력해주세요",
                        fontSize = 25.sp,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = remainTime,
                        fontSize = 17.sp,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                        textAlign = TextAlign.Center,
                        text = resultMsg.value,
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontFamily = notosanFontFamily,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SimpleEditTextDefault(
                        authCodeNumber.value,
//                        hint = "예(+8210- 혹은 010-)",
//                        errorMsg = "올바른 번호를 입력해주세요",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            verificatingCode?.let { it() }
                        }),
                        onTextChanged = { response ->
                            authCodeNumber.value = response
                            if (authCodeNumber.value.length > 10)
                            {
//                                Toast.makeText(context,"15자리 이하로 입력해주세요", Toast.LENGTH_SHORT).show()
                                authCodeNumber.value = response.substring(0 until 10)
                            }
                        })

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween){
                        Column {
                            DefaultInputButton(
                                backgroundColor = Color.Transparent,
                                textColor = Color.Black,
                                modifier = Modifier
                                    .padding(top = 50.dp, start = 35.dp)
                                    .align(Alignment.Start),
                                title = "돌아가기"
                            ) {
                                if (backPage != null)
                                {
                                    backPage()
                                }
                            }
                        }

                        Column {
                            if (rememberTextLength.value in 1..15) {
                                DefaultInputButton(
                                    enabled = !authentificationTimeout.value,
                                    backgroundColor = Color.Transparent,
                                    textColor = if(!authentificationTimeout.value) Color.Black else Color.Red,
                                    modifier = Modifier
                                        .padding(top = 50.dp, end = 35.dp)
                                        .align(Alignment.End),
                                    title = if(!authentificationTimeout.value) "인증하기" else "인증실패"
                                ) {
                                    verificatingCode?.let { it() }
                                }
                            }
                        }
                    }

                }
            }
        }

    }
}

