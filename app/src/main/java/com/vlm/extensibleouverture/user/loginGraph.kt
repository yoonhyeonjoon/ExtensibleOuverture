package com.vlm.extensibleouverture.user

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vlm.extensibleouverture.ui.user.LoginFront
import com.vlm.extensibleouverture.user.signUp.SignUpProcess
import com.vlm.extensibleouverture.viewmodels.SignUpLibraryViewModel
import com.vlm.extensibleouverture.viewmodels.UserViewModelParkGolf
import com.vlm.vlmlibrary.compose.button.DefaultInputButton


@OptIn(
    ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    kotlinx.coroutines.ExperimentalCoroutinesApi::class,
    coil.annotation.ExperimentalCoilApi::class
)
fun NavGraphBuilder.loginGraph(
    signinLibraryViewModel : SignUpLibraryViewModel,
    userViewModel : UserViewModelParkGolf,
    signResponse : (String, NavBackStackEntry) -> Unit,
    modifier : Modifier = Modifier,
){

    composable(
        LoginFront.FRONT.route,
//        arguments = listOf( navArgument("userId") { defaultValue = "me" })
    ){ from ->

        Column {

//            SignIn(userViewModel = userViewModel){ state ->
//                when(state){
//                    SignUpState.SignUp -> {
//                        signResponse("signup",from)
//                    }
//                    SignUpState.ReSignUp -> {
//                        signResponse("resignup",from)
//                    }
//                    SignUpState.SignIn -> {
//                        signResponse("signin",from)
//                    }
//                }
//            }

            DefaultInputButton(modifier = Modifier
                .width(80.dp)
                .padding(vertical = 8.dp),"프로필파일 바로 가기")
            {
                signResponse("temporary",from)
            }

        }
    }

    composable(LoginFront.SIGN_UP.route + "/{type}"){ from ->
        val signType = if(from.arguments?.getString("type") == "resignup") 1 else 0
        SignUpProcess(signType  = signType,
            signinLibraryViewModel = signinLibraryViewModel,
            userViewModel = userViewModel
        )
        { response ->
            if(response == "front"){
                signResponse("upPress",from)
            }
        }

    }
}

