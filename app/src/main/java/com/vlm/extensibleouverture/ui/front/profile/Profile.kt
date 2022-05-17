package com.vlm.extensibleouverture.ui.front.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vlm.extensibleouverture.user.signIn.SignIn
import com.vlm.extensibleouverture.user.signIn.SignUpState
import com.vlm.extensibleouverture.viewmodels.FakeParkGolfUserViewModel
import com.vlm.vlmlibrary.compose.notosanFontFamily
import com.vlm.vlmlogin.data.UserPrivate
import com.vlm.vlmlogin.data.UserPublic
import com.vlm.vlmlogin.modules.viewmodels.UserViewModelInterface

@Preview(showBackground = true)
@Composable
fun ProfilePreview(){
    FakeParkGolfUserViewModel.userBasicData.value?.fid = "NotInvalid"
    Profile(
        userViewModel = FakeParkGolfUserViewModel,
        content = { ProfileContext(FakeParkGolfUserViewModel) })
    {

    }
}

@Composable
fun <userPublic : UserPublic, userPrivate : UserPrivate>
        Profile(modifier: Modifier = Modifier, userViewModel
        : UserViewModelInterface<userPublic, userPrivate>,
                content: @Composable () -> Unit,
                profileSignResponse : (String) -> Unit) {

    var signinState by rememberSaveable{ mutableStateOf("invalid") }
    if(userViewModel.userBasicData.value?.fid != "" && userViewModel.userBasicData.value?.fid != null)
    {
        signinState = userViewModel.userBasicData.value?.fid.toString()
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
        verticalArrangement = Arrangement.Center
    ){

        Text(
            modifier = Modifier.fillMaxWidth()/*.padding(start = 20.dp, end = 1.dp)*//*.width(100.dp)*/ /*.background(Lime200)*/,
            text = "마이 페이지",
            fontSize = 23.sp,
            textAlign = TextAlign.Center,
            fontFamily = notosanFontFamily,
            fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(30.dp))

        if(signinState == "invalid")
        {
            Text(
                modifier = Modifier.fillMaxWidth()/*.padding(start = 20.dp, end = 1.dp)*//*.width(100.dp)*/ /*.background(Lime200)*/,
                text = "로그인 정보 없음",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontFamily = notosanFontFamily,
                fontWeight = FontWeight.Normal)
        }
        else{
            content()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp)
    )
    {
        if(signinState == "invalid")
        {
            SignIn(userViewModel = userViewModel){ state ->
                when(state){
                    SignUpState.SignUp -> {
                        profileSignResponse("signup")
                    }
                    SignUpState.ReSignUp -> {
                        profileSignResponse("resignup")
                    }
                    SignUpState.SignIn -> {
                        signinState = userViewModel.userFid
                        //profileSignResponse("signin") 상위 컨트롤러에서 관리하기 위해선  이를 사용하면됨
                    }
                }
            }
        }


    }
}