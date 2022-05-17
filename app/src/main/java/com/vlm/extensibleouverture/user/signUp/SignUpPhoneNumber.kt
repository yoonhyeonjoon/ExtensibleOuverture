package com.vlm.extensibleouverture.user.signUp

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.vlm.extensibleouverture.user.signUp.signUpPhoneNumber.GetNumber
import com.vlm.extensibleouverture.user.signUp.signUpPhoneNumber.NumberAuthentification
import com.vlm.extensibleouverture.user.signUp.signUpPhoneNumber.methods.PhoneAuthorizer
import com.vlm.extensibleouverture.user.signUp.signUpPhoneNumber.methods.PhoneResponseCode
import com.vlm.extensibleouverture.user.signUp.signUpPhoneNumber.methods.verifyProcess
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Preview(showBackground = true, name = "Signup-page-PhoneNumber")
@ExperimentalAnimationApi
@Composable
fun SignupPhoneNumber_()
{
    val rememberTextLength = rememberSaveable{ mutableStateOf(0) }
    val phoneNumber = rememberSaveable{ mutableStateOf("") }
    val resultMsg = rememberSaveable{ mutableStateOf("") }
    val phonenumberInfo = PhoneNumberComposeInfo(fid = "testfid", eid = "twojo@kore.ac.kr", password = "20asdf", phoneVerificationID = "asdvv", phoneToken = null)
    SignupPhoneNumber(phonenumberInfo = phonenumberInfo, phoneNumber = phoneNumber,
        rememberTextLength = rememberTextLength, resultMsg = resultMsg, initAnimation = true)
}

enum class SignUpPhoneNumberPage{
    GetTheNumber, Authentification
}

data class PhoneNumberComposeInfo (
    val fid : String,
    val eid : String,
    var password: String,
    var phoneVerificationID : String = "none",
    var phoneToken : PhoneAuthProvider.ForceResendingToken? = null,
)


@ExperimentalAnimationApi
@Composable
fun SignupPhoneNumber(
    phonenumberInfo : PhoneNumberComposeInfo,
    phoneNumber: MutableState<String>,
    resultMsg: MutableState<String>,
    rememberTextLength: MutableState<Int>,
    initAnimation : Boolean = false,
    backPage : (() -> Unit)? = null,
    nextPage : (() -> Unit)? = null,
) {

    val context = LocalContext.current
    val phoneAuthorizer = PhoneAuthorizer()

    val selectedPage = rememberSaveable { mutableStateOf(SignUpPhoneNumberPage.GetTheNumber) }

    var currentTime by remember { mutableStateOf(100000L) } //2분
    val authentificationTimeout = remember { mutableStateOf(false) } //2분
    var isTimerRunning by remember { mutableStateOf(false) }
    val authCodeNumber  = rememberSaveable { mutableStateOf("") }
    val rememberAuthNumberLength = rememberSaveable{ mutableStateOf(0) }


    //Verifying Response
    fun responseProcess(responseCode : PhoneResponseCode)
    {
        when(responseCode)
        {
            PhoneResponseCode.Success -> {
                authentificationTimeout.value = false
                isTimerRunning = false
                resultMsg.value = "인증성공"
                /**인증성공시 다음페이지로 이동한다**/
                nextPage?.let { it() }
            }
            PhoneResponseCode.EMAIL_ALREADY_EXIST -> {
                authentificationTimeout.value = true
                isTimerRunning = false
                resultMsg.value = "이메일 계정이 이미 존재합니다. \n 이전에 가입한 적이 있거나 이메일 인증 후 다음 과정을 진행하지 않아 발생한 오류일 수 있습니다"
            }
            PhoneResponseCode.EMAIL_UPDATE_FAILURE -> {
                authentificationTimeout.value = true
                isTimerRunning = false
                resultMsg.value = "이메일 정보를 업데이트하는 것에 실패했습니다. \n 올바른 이메일이 아니기에 진행할 수 없습니다"
            }
            PhoneResponseCode.FIREBASEAUTH_CREDENTIAL_ERROR -> {
                resultMsg.value = "올바른 인증번호가 아니거나 인증 자격을 상실한 번호이므로 \n 자격을 증명하는데 실패하였습니다"
//                isTimerRunning = false
//                authentificationTimeout.value = true
            }
        }
    }

    fun timeTranser(remainTime : Long) : String{
        return if(currentTime <= 50L){
            "인증시간 초과"
        }
        else{
            "${remainTime / 1000}초"
        }
    }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if(currentTime > 0 && isTimerRunning)
        {
            authentificationTimeout.value = false
            delay(100L)
            currentTime -= 100L
//            println(currentTime)
        }
        else{
            authentificationTimeout.value = true
        }
    }

    //텍스트 길이를 기억
    rememberTextLength.value = phoneNumber.value.length
    val isErrorPhonenumber = rememberSaveable{ mutableStateOf(false) }

    BackHandler(enabled = true){
        when(selectedPage.value)
        {
            SignUpPhoneNumberPage.GetTheNumber -> {

            }
            SignUpPhoneNumberPage.Authentification -> {
                selectedPage.value = SignUpPhoneNumberPage.GetTheNumber
            }

        }
    }

    /** 번호 인증 콜백*/
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            phoneAuthorizer.glboalPhoneAuthCredential = credential
            currentTime = 100000L
            isTimerRunning = false
            phoneAuthorizer.verifyPhoneNumberWithCredential(credential) { response ->
                verifyProcess(phonenumberInfo= phonenumberInfo, getResponse = response){ phoneResponseCode ->
                    responseProcess(phoneResponseCode)
                }
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//            eee("phonepart", "onVerificationFailed")
            selectedPage.value = SignUpPhoneNumberPage.GetTheNumber
            isErrorPhonenumber.value = true
//            //인증실패시 타이머를 오프한다
            currentTime = 100000L
            isTimerRunning = false
            authentificationTimeout.value = true

            when (e) {
                is FirebaseTooManyRequestsException -> {
                    resultMsg.value = "요청회수가 초과되었습니다. \n 번호인증 발송 메세지는 하루 5회로 제한 됩니다" }
                is FirebaseAuthException -> {
                    resultMsg.value = "AuthException : 자격증명에 오류입니다" }
                is FirebaseAuthActionCodeException -> {
                    resultMsg.value = "ActionCodeException : 만료되었거나 유효하지 않은 대역 외 코드입니다" }
                is FirebaseAuthEmailException -> {
                    resultMsg.value = "EmailException : 인증하는 과정에서 해당 이메일에 대한 오류가 있어 전송을 실패했습니다 (예 : 비밀번호 재설정 이메일)." }
                is FirebaseAuthInvalidCredentialsException -> {
                    resultMsg.value = "InvalidCredentialsException : 사용자의 자격을 증명하는데 실패하였습니다 인증을 계속할 수 없습니다" }
                is FirebaseAuthInvalidUserException -> {
                    resultMsg.value = "InvalidUserException : 사용자에게 전달된 토큰을 찾을수 없거나 만료된 상태이기에 인증을 계속할 수 없습니다" }
                is FirebaseAuthMultiFactorException -> {
                    resultMsg.value = "MultiFactorException : 다른 경로로 이미 사용자 자격을 증명이 진행된 상태입니다" }
                is FirebaseAuthRecentLoginRequiredException -> {
                    resultMsg.value = "RecentLoginRequiredException : 사용자의 기기는 전화인증 서비스의 보안 조건을 충족하지 않습니다. 시스템의 버전 혹은 다른 어플리케이션의 동작에 의해 어플리케이션의 사용이 제한되고 있습니다" }
                is FirebaseAuthUserCollisionException -> {
                    resultMsg.value = "UserCollisionException : (사용자가 직접적으로 가입한적이 없지만)기존의 다른 사용자의 정보와 충돌이 있어 인증에 실패하였습니다. 개발자에게 문의부탁드립니다" }
                is FirebaseAuthWebException -> {
                    resultMsg.value = "AuthWebException : 웹에서 인증을 진행하는 경우에 대한 오류메세지입니다. 다른 경로를 통해 인증을 진행해야합니다" }
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
//            Log.d("phonepart", "onCodeSent:$verificationId")
            // Save verification ID and resending token so we can use them later
            phonenumberInfo.phoneVerificationID = verificationId
            phonenumberInfo.phoneToken = token
        }
    }


    fun getCodeNumber(){
        try{

            if( phoneNumber.value.substring(0,3) == "010" &&
                (phoneNumber.value.length == 10 || phoneNumber.value.length == 11 || phoneNumber.value.length == 12))
            {
                currentTime = 100000L
                isTimerRunning = true
                isErrorPhonenumber.value = false
                authentificationTimeout.value = false
                val tranPhoneNumber = String.format("+8210%s", phoneNumber.value.substring(3, phoneNumber.value.length))

                val auth = FirebaseAuth.getInstance()
                //auth.setLanguageCode("ko-KR")
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(tranPhoneNumber)       // Phone number to verify
                    .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(context as Activity)                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)

                selectedPage.value = SignUpPhoneNumberPage.Authentification
            }
            else
            {
                isErrorPhonenumber.value = true
            }
        }
        catch (e:Exception){
            isErrorPhonenumber.value = true
        }
    }


    when(selectedPage.value)
    {

        SignUpPhoneNumberPage.GetTheNumber -> {
            GetNumber(
                initAnimation = initAnimation,
                phoneNumber = phoneNumber,
                isError = isErrorPhonenumber.value,
                backPage = backPage,
                rememberTextLength = rememberTextLength,
                getCodeNumber = { getCodeNumber() }
            )
        }


        SignUpPhoneNumberPage.Authentification -> {
            NumberAuthentification(
                authCodeNumber = authCodeNumber,
                authentificationTimeout = authentificationTimeout,
                remainTime = timeTranser(currentTime),
                resultMsg = resultMsg,
                rememberTextLength = rememberAuthNumberLength,
                verificatingCode = {
                    val verificationCode = authCodeNumber.value
                    if (verificationCode.isNotBlank() && phonenumberInfo.phoneVerificationID != "") { //phoneVerificationID != "" 조건은 인증요청과 인증확인을 거의 동시에 눌렀을때 발생하는 문제를 막기 위해 추가하였다.
                        phoneAuthorizer.verifyPhoneNumberWithCode(
                            phonenumberInfo.phoneVerificationID,
                            verificationCode
                        ) { response ->
                            verifyProcess(phonenumberInfo = phonenumberInfo, getResponse = response) { phoneResponseCode ->
                                responseProcess(phoneResponseCode)
                            }
                        }
                    }
                    else {
                        resultMsg.value = "인증 경로가 올바르지 않습니다"
                    }
                },
                //백페이지 버튼은 상위로 올리지않고 SinginPhoneNumber에서 해결한다
                backPage = {
                    selectedPage.value = SignUpPhoneNumberPage.GetTheNumber
                },
            )

        }

    }



}

