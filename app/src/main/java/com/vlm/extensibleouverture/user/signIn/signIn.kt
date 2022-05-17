package com.vlm.extensibleouverture.user.signIn

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.common.api.ApiException
import com.vlm.extensibleouverture.viewmodels.FakeParkGolfUserViewModel
import com.vlm.extensibleouverture.viewmodels.UserViewModelParkGolf
import com.vlm.vlmlogin.LoginKit
import com.vlm.vlmlogin.data.UserPrivate
import com.vlm.vlmlogin.data.UserPublic
import com.vlm.vlmlogin.modules.viewmodels.UserViewModel
import com.vlm.vlmlogin.modules.viewmodels.UserViewModelInterface
import com.vlm.vlmlogin.signIn.KakaoLogin
import com.vlm.vlmlibrary.compose.button.VlmKakaoLoginButton
import com.vlm.vlmlibrary.compose.dialog.ConfirmDialogWithContext
import kotlinx.coroutines.launch


enum class SignInState {
    InvalidUser,
    InvalidCredential,
    FirebaseDataNull, //Firebase의 public, private가 아무것도 없고 FirebaseAuth만 인증이 되어있는 아이디
    DataNull, // 핸드폰 인증과 이메일은 있으나 데이터는 없는 아이디 -> 미완료한 회원가입
    Login,
    False,
    Else
}

enum class SignUpState {
    SignUp,
    ReSignUp,
    SignIn,
}

@Preview(showBackground = true)
@Composable
fun SignInPreview(modifier: Modifier = Modifier) {
    SignIn(userViewModel = FakeParkGolfUserViewModel){

    }
}
@Composable
fun <userPublic : UserPublic, userPrivate : UserPrivate>
    SignIn(userViewModel : UserViewModelInterface<userPublic, userPrivate>, stateResponse : (SignUpState) -> Unit)
{

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val confirmShower = remember { mutableStateOf(false) }

    if(confirmShower.value)
    {
        ConfirmDialogWithContext(
            title = "로그인 계정 생성",
            context = "온전한 서비스 이용을 위해 계정정보를 입력하시겠습니까?",
            leftText = "아니요",
            leftClick = {
                confirmShower.value = false
            },
            rightText = "예",
            rightClick = {
                confirmShower.value = false
                stateResponse(SignUpState.SignUp)
            },
            onDismissClicked = {
                confirmShower.value = false
            }
        )
    }

    val reConfirmShower = remember { mutableStateOf(false) }
    if(reConfirmShower.value)
    {
        ConfirmDialogWithContext(
            title = "로그인 계정 생성",
            context = "계정 정보 입력 도중 중단된 기록이 있습니다 \n 계정 정보를 업데이트 하시겠습니까?",
            leftText = "아니요",
            leftClick = {
                reConfirmShower.value = false
            },
            rightText = "예",
            rightClick = {
                reConfirmShower.value = false
                stateResponse(SignUpState.ReSignUp)
            },
            onDismissClicked = {
                reConfirmShower.value = false
            }
        )
    }


    Column {

        VlmKakaoLoginButton(text = "카카오로 로그인"){

            KakaoLogin.kakaoLogin(context = context){ response, result ->
                when(response)
                {
                    KakaoLogin.KakaoResponse.Success -> {
                        val getResult = result as Pair<*, *>
                        val email = getResult.first.toString()
                        val password = getResult.second.toString()

                        /**토큰 정보 얻기**/
                        val prefer = context.getSharedPreferences("basicBox", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = prefer.edit()
                        editor.putString("email", email)
                        editor.putString("userPassword", password)
                        editor.apply()

                        firebaseEmailLoginRenewal(context = context, userViewModel = userViewModel as UserViewModelParkGolf, email = email, password = password){ loginResult ->

                            when (loginResult) {
                                SignInState.InvalidUser -> {
                                    confirmShower.value = true
                                }
                                SignInState.InvalidCredential -> {//firebase에 계정이 존재할 경우 로그인 password가 바꼇을경우? 이럴 경우가 있나
                                    Toast.makeText(context, "다른 경로로 가입이 이미 되어있는 이메일 입니다.", Toast.LENGTH_SHORT).show()
                                }
                                SignInState.FirebaseDataNull -> {
                                    confirmShower.value = true
                                }
                                SignInState.DataNull -> {
                                    reConfirmShower.value = true
                                }
                                SignInState.Login -> { //로그인 해서 바로 메인스트림 가면됨

                                    coroutineScope.launch(/*Dispatchers.Main*/) {
                                        LoginKit<userPublic, userPrivate>().getEssentialData(userViewmodel = userViewModel as UserViewModel<userPublic, userPrivate>) { success ->
                                            if(success){ // 성공
                                                stateResponse(SignUpState.SignIn)
//                                                (context as Activity).finish()
                                            }else{ // 실패
                                                Thread(kotlinx.coroutines.Runnable {
                                                    Toast.makeText(
                                                        context,
                                                        "기본 정보를 설정하는데 실패했습니다. 다시 로그인 해주세요",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                })
                                            }
                                        }
                                    }
                                }
                                else -> {


                                }
                            }
                        }


                    }
                    KakaoLogin.KakaoResponse.Failure -> {
                        val getResult = result as Throwable
                    }
                    KakaoLogin.KakaoResponse.Error -> {
                        val getResult = result as ApiException
                    }

                }

            }
        }

    }


}


//                                    lifecycleScope.launch {
//                                        LoginKit().getEssentialData { success ->
//                                            if(success)
//                                            { // 성공
//                                                val intent = Intent(requireContext(), M1Mainstream::class.java)
//                                                loginSuccess(true)
//                                                startActivity(intent)
//                                                requireActivity().finish()
//                                            }
//                                            else
//                                            { // 실패
//                                                this@LoginMainFragment.lifecycleScope.launch(Dispatchers.Main){
//                                                    Toast.makeText(requireContext(),"기본 정보 불러오기에 실패했습니다. 다시 로그인 해주세요",Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    val requestWork = OneTimeWorkRequestBuilder<GetEssentialUserDataWorker>().build()
//                                    WorkManager.getInstance(requireContext()).enqueue(requestWork)
//                                    WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(requestWork.id).observe(viewLifecycleOwner) {
//                                        if(it.state == WorkInfo.State.SUCCEEDED){
//                                            val intent = Intent(requireContext(), M1Mainstream::class.java)
//                                            loginSuccess(true)
//                                            startActivity(intent)
//                                            requireActivity().finish()
//                                            WorkManager.getInstance(requireContext())
//                                                .getWorkInfoByIdLiveData(requestWork.id).removeObservers(viewLifecycleOwner)
//                                        }else if(it.state == WorkInfo.State.FAILED){
//                                            Toast.makeText(requireContext(),"기본 정보를 로드하는데 실패했습니다. 다시 로그인 해주세요",Toast.LENGTH_SHORT).show()
//
//                                        }
//                                    }